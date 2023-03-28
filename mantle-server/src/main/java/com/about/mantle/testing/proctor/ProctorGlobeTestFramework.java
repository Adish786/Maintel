package com.about.mantle.testing.proctor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.refresh.exception.ProctorSupplierException;
import com.about.globe.core.testing.GlobeBucket;
import com.about.globe.core.testing.GlobeTestFramework;
import com.about.globe.core.testing.exception.GlobeTestingException;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.tasks.DocumentTask;
import com.about.mantle.model.tasks.TaxeneConfigurationTask;
import com.about.mantle.model.tasks.TemplateNameResolveTask;
import com.google.common.collect.ImmutableMap;
import com.indeed.proctor.common.Identifiers;
import com.indeed.proctor.common.Proctor;
import com.indeed.proctor.common.ProctorResult;
import com.indeed.proctor.common.model.ConsumableTestDefinition;
import com.indeed.proctor.common.model.TestBucket;

public class ProctorGlobeTestFramework implements GlobeTestFramework {

	public static final String TRACKING_IDS = "trackingIds";

	private final ProctorSupplier proctorSupplier;
	private final ProctorSupplier globalProctorSupplier;
	private final IdentifiersExtractor identifiersExtractor;
	private final TemplateNameResolveTask templateNameResolver;
	private final TaxeneConfigurationTask taxeneConfigurationTask;
	private final DocumentTask documentTask;

	public ProctorGlobeTestFramework(ProctorSupplier proctorSupplier,  ProctorSupplier globalProctorSupplier, IdentifiersExtractor identifiersExtractor,
									 TemplateNameResolveTask templateNameResolver, TaxeneConfigurationTask taxeneConfigurationTask, DocumentTask documentTask) {
		this.proctorSupplier = proctorSupplier;
		this.globalProctorSupplier = globalProctorSupplier;
		this.identifiersExtractor = identifiersExtractor;
		this.templateNameResolver = templateNameResolver;
		this.taxeneConfigurationTask = taxeneConfigurationTask;
		this.documentTask = documentTask;
	}

	@Override
	public Map<String, GlobeBucket> getAssignments(RequestContext requestContext) throws GlobeTestingException {

		Identifiers identifiers = identifiersExtractor.extract(requestContext);
		Map<String, Object> inputContext = getInputContext(requestContext);
		Map<String, Integer> forceBuckets = requestContext.getForcedTestBuckets();

		Proctor proctor;
		Proctor globalProctor;
		try {
			proctor = proctorSupplier.supply();
			globalProctor = globalProctorSupplier.supply();
		} catch (ProctorSupplierException e) {
			throw new GlobeTestingException("Error getting proctor instance", e);
		}

		ProctorResult proctorResult = proctor.determineTestGroups(identifiers, inputContext, forceBuckets);
		ProctorResult globalProctorResult = globalProctor.determineTestGroups(identifiers, inputContext, forceBuckets);

		return convertAndMergeProctorResult(proctorResult, globalProctorResult);
	}
    
	/**
	 * This method merges global proctor tests with vertical's tests. Vertical's tests will take precedence if there
	 * is a clash with test names. Special case: If vertical test is inactive and global test with same name is active, final merged results
	 * will not have that test; If a vertical has to fall-back to global test then they have to delete its test.
	 * TL;DR vertical has a final say on what is active and inactive.
	 */
	protected Map<String, GlobeBucket> convertAndMergeProctorResult(ProctorResult proctorResult, ProctorResult globalProctorResult) throws GlobeTestingException {

		Map<String, GlobeBucket> resultHolder = new HashMap<>();

		for (Entry<String, TestBucket> entry : globalProctorResult.getBuckets().entrySet()) {
			// Do not add the inactive bucket, we dont want the test to appear in map request is not in test
			if (entry.getValue().getValue() != -1) {
				ConsumableTestDefinition testDefinition = globalProctorResult.getTestDefinitions().get(entry.getKey());
				resultHolder.put(entry.getKey(), convertProctorTestBucket(entry.getValue(), entry.getKey(), testDefinition));
			}
		}

		for (Entry<String, TestBucket> entry : proctorResult.getBuckets().entrySet()) {
			if (entry.getValue().getValue() != -1) {
				ConsumableTestDefinition testDefinition = proctorResult.getTestDefinitions().get(entry.getKey());
				resultHolder.put(entry.getKey(), convertProctorTestBucket(entry.getValue(), entry.getKey(), testDefinition));
			}else{
				resultHolder.remove(entry.getKey());
			}
		}
		ImmutableMap.Builder<String, GlobeBucket> builder = ImmutableMap.builder();
		return builder.putAll(resultHolder).build();
	}

	protected GlobeBucket convertProctorTestBucket(TestBucket proctorBucket, String testName,
			ConsumableTestDefinition testDefinition) throws GlobeTestingException {

		String name = proctorBucket.getName();
		String description = proctorBucket.getDescription();
		int value = proctorBucket.getValue();
		Object payload = proctorBucket.getPayload() == null ? null : proctorBucket.getPayload().fetchAValue();

		@SuppressWarnings("unchecked")
		Map<String, String> trackingIds = (Map<String, String>) testDefinition.getConstants().get(TRACKING_IDS);

		String trackingId = trackingIds.get(name);

		return new GlobeBucket(name, description, value, payload, trackingId);
	}

	protected Map<String, Object> getInputContext(RequestContext requestContext) {
		ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

		builder.put("requestContext", requestContext);
		
		if (templateNameResolver != null) {
			String templateType = templateNameResolver.getTemplateName(requestContext);
			if (templateType != null) {
				builder.put("documentTemplateType", templateType);
			}
		}
		
		if(documentTask != null && (requestContext.getUrlData() instanceof VerticalUrlData)) {
			Long docId = ((VerticalUrlData)requestContext.getUrlData()).getDocId();
			//Check that the request has a docId and is not /404
			//Checking docId here because otherwise the document would be requested for resources as well
			if (docId != null && !requestContext.getRequestUri().equals("/404")) {
				BaseDocumentEx document = documentTask.fetchDocument(requestContext);
				if (document != null) {
					builder.put("document", document);
				}
			}
		}
		
		builder.put("geo", requestContext.getGeoData());
		builder.put("taxeneConfig", taxeneConfigurationTask.getNodeConfigs(requestContext));

		return builder.build();
	}

}
