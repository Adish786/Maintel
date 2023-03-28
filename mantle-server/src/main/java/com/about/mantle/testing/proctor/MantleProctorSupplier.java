package com.about.mantle.testing.proctor;

import static com.indeed.proctor.store.FileBasedProctorStore.DEFAULT_TEST_DEFINITIONS_DIRECTORY;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.indeed.proctor.common.el.LibraryFunctionMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.refresh.Refreshable;
import com.about.globe.core.refresh.exception.GlobeRefreshException;
import com.about.globe.core.refresh.exception.ProctorSupplierException;
import com.indeed.proctor.common.PayloadSpecification;
import com.indeed.proctor.common.Proctor;
import com.indeed.proctor.common.ProctorLoadResult;
import com.indeed.proctor.common.ProctorUtils;
import com.indeed.proctor.common.RuleEvaluator;
import com.indeed.proctor.common.TestSpecification;
import com.indeed.proctor.common.model.ConsumableTestDefinition;
import com.indeed.proctor.common.model.TestBucket;
import com.indeed.proctor.common.model.TestDefinition;
import com.indeed.proctor.common.model.TestMatrixArtifact;
import com.indeed.proctor.common.model.TestMatrixVersion;
import com.indeed.proctor.store.GitProctor;
import com.indeed.proctor.store.ProctorStore;
import com.indeed.proctor.store.StoreException;

public class MantleProctorSupplier implements ProctorSupplier, Refreshable {
	
	private static final Logger logger = LoggerFactory.getLogger(MantleProctorSupplier.class);

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock readLock = readWriteLock.readLock();
	private final Lock writeLock = readWriteLock.writeLock();

	private final String name;
	private final ProctorStore proctorStore;
	private final String sourceId;

	private Proctor activeProctor;
	private TestMatrixVersion activeTestMatrix;

	private volatile boolean refreshing;
	private long refreshCount;
	private long lastRefreshTime;
	private long lastRefreshDuration;
	
	public static final MantleProctorSupplier getEmptyProctorSupplierWithName(String name) {
		String supplierName = StringUtils.isNotBlank(name) ? name : "emptyProctorSupplier";
		return new MantleProctorSupplier(supplierName) {
			@Override
			public Proctor supply() throws ProctorSupplierException {
				return Proctor.EMPTY;
			}

			@Override
			public void refresh() {
				logger.debug("Empty [{}], there is nothing to refresh", supplierName);
			}
		};
	}
	
	/**
	 * Use one of the static factory methods `fromSVN` or `fromGit` to get an instance of `MantleProctorSupplier`.
	 * @param proctorStore
	 * @param sourceId NOTE that this is used for logging purposes so do NOT include any sensitive information
	 */
	private MantleProctorSupplier(ProctorStore proctorStore, String sourceId, String name) {
		this.proctorStore = proctorStore;
		this.sourceId = sourceId;
		this.name = name;
	}

	private MantleProctorSupplier(String name) {
		this.proctorStore = null;
		this.sourceId = null;
		this.name = name;
	}

	public static MantleProctorSupplier fromGit(String gitPath, String gitBranch, String username, String password, String name) {
		MantleProctorSupplier answer;
		try {
			GitProctor gitProctor = new GitProctor(gitPath, username, password,
					"matrices/" + DEFAULT_TEST_DEFINITIONS_DIRECTORY, gitBranch);
			answer = new MantleProctorSupplier(gitProctor, String.format("gitPath: %s, gitBranch: %s", gitPath, gitBranch), name);
		} catch (Exception e) {
			logger.error(String.format("Couldn't instantiate %s", name), e);
			answer = MantleProctorSupplier.getEmptyProctorSupplierWithName(name);
		}
		return answer;
	}

	@Override
	public Proctor supply() throws ProctorSupplierException {
		readLock.lock();
		try {
			if (activeProctor == null) {
				throw new ProctorSupplierException("No active version of proctor found at " + sourceId, null);
			}
			return activeProctor;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean isRefreshing() {
		return refreshing;
	}

	@Override
	public void refresh() throws GlobeRefreshException {
		if (refreshing) {
			logger.warn("Ignoring Proctor refresh, one is already in progress for [{}]", name);
			return;
		}

		String latestVersion;
		try {
			proctorStore.refresh();
			latestVersion = proctorStore.getLatestVersion();
		} catch (StoreException e) {
			throw new GlobeRefreshException(String.format("Unable to get latest test matrix of %s, version: %s ", name, sourceId), e);
		}
		// No update needed
		if (activeTestMatrix != null && activeTestMatrix.getVersion().equals(latestVersion)) {
			logger.debug("Ignoring proctor refresh, already at version [{}] for [{}]", latestVersion, name);
			return;
		}

		long start = System.currentTimeMillis();

		if (activeTestMatrix == null) {
			logger.debug("Activating [{}] version [{}]", name, latestVersion);
		} else {
			logger.info("Switching [{}] version from [{}] to [{}]", name, activeTestMatrix.getVersion(), latestVersion);
		}

		try {
			refreshing = true;
			
			TestMatrixVersion testMatrix = null;
			Proctor proctor = null;

			try {				
				testMatrix = getTestMatrix(latestVersion);
				validateTestMatrix(testMatrix);
				TestMatrixArtifact matrixArtifact = getTestMatrixArtifact(testMatrix);
				LibraryFunctionMapper functionMapper = RuleEvaluator.defaultFunctionMapperBuilder()
						.add("mntl", MantleRuleFunctions.class)
						.build();
				// Since we aren't using specification config we need to generate one on the fly
				Map<String, TestSpecification> specifications = createSpecifications(matrixArtifact);
				
				ProctorLoadResult loadResult = ProctorUtils.verifyAndConsolidate(matrixArtifact, sourceId,
						specifications, functionMapper);
				proctor = Proctor.construct(matrixArtifact, loadResult, functionMapper);
			} catch (Exception e) {
			    throw new GlobeRefreshException(String.format("Error refreshing %s from version %s", name, latestVersion), e);
			}

			try{
				writeLock.lock();
				// Make active
				if (proctor != null && testMatrix != null) {				
					activeProctor = proctor;
					activeTestMatrix = testMatrix;
				}

				refreshCount += 1;
				lastRefreshTime = System.currentTimeMillis();
				lastRefreshDuration = lastRefreshTime - start;
			}finally {
				writeLock.unlock();
			}
			
		} finally {
			refreshing = false;
		}
	}

	private void validateTestMatrix(TestMatrixVersion testMatrix) throws GlobeRefreshException {
		Map<String, TestDefinition> tests = testMatrix.getTestMatrixDefinition().getTests();
		for (Entry<String, TestDefinition> testEntry : tests.entrySet()) {
			try {
				validateTestDefinition(testEntry.getKey(), testEntry.getValue());
			} catch (GlobeRefreshException e) {
				throw new GlobeRefreshException("Test validation failed for test " + testEntry.getKey() + " version "
						+ testMatrix.getVersion() + " in " + name + " at " + sourceId, e);
			}

		}
	}

	private void validateTestDefinition(String testName, TestDefinition test) throws GlobeRefreshException {

		@SuppressWarnings("unchecked")
		Map<String, String> trackingIds = (Map<String, String>) test.getConstants().get(
				ProctorGlobeTestFramework.TRACKING_IDS);

		if (isEmpty(trackingIds)) {
			logger.error("Tracking ids not set in constants for test [{}] in [{}] at [{}]", testName, name, sourceId);
		}
		
		for (TestBucket bucket : test.getBuckets()) {
			// Skip inactive bucket
			if (bucket.getValue() == -1) continue;
			
			String trackingId = trackingIds.get(bucket.getName());
			if (isBlank(trackingId)) {
				logger.error("Bucket [{}] does not have tracking id mapped in constants for test [{}] in [{}] at [{}]",
					bucket.getName(), testName, name, sourceId);
			}
		}
	}

	private TestMatrixArtifact getTestMatrixArtifact(TestMatrixVersion testMatrix) {
		return ProctorUtils.convertToConsumableArtifact(testMatrix);
	}

	private Map<String, TestSpecification> createSpecifications(TestMatrixArtifact matrixArtifact) {

		Map<String, ConsumableTestDefinition> tests = matrixArtifact.getTests();
		if (tests == null || tests.isEmpty()) return Collections.emptyMap();

		Map<String, TestSpecification> specifications = new HashMap<>(tests.size());
		for (Entry<String, ConsumableTestDefinition> entry : tests.entrySet()) {
			specifications.put(entry.getKey(), createSpecification(entry.getValue()));
		}
		return specifications;
	}

	private TestSpecification createSpecification(ConsumableTestDefinition definition) {

		TestSpecification specification = new TestSpecification();

		String payloadType = null;
		Map<String, Integer> buckets = new HashMap<>();
		for (TestBucket bucket : emptyIfNull(definition.getBuckets())) {
			buckets.put(bucket.getName(), bucket.getValue());
			payloadType = bucket.getPayload() == null ? null : bucket.getPayload().fetchType();
		}
		specification.setBuckets(buckets);

		PayloadSpecification payload = null;
		if (payloadType != null && !"none".equals(payloadType)) {
			payload = new PayloadSpecification();
			payload.setType(payloadType);
		}

		specification.setPayload(payload);

		return specification;
	}

	private TestMatrixVersion getTestMatrix(String fetchRevision) throws GlobeRefreshException {
		try {
			return proctorStore.getTestMatrix(fetchRevision);
		} catch (StoreException e) {
			throw new GlobeRefreshException("Unable to get current test matrix from " + sourceId, e);
		}
	}

	@Override
	public long getRefreshCount() {
		return refreshCount;
	}

	@Override
	public long getLastRefreshTime() {
		return lastRefreshTime;
	}

	@Override
	public long getLastRefreshDuration() {
		return lastRefreshDuration;
	}

	public String getSupplierName() {
		return name;
	}
	
}
