package com.about.mantle.model.services.impl;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.refresh.Refreshable;
import com.about.globe.core.refresh.exception.GlobeRefreshException;
import com.about.mantle.model.services.BusinessOwnedVerticalDataService;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

/**
 * Provides connection to and periodic pull of git repository as additional resource store.
 */
public class BusinessOwnedVerticalDataServiceImpl implements BusinessOwnedVerticalDataService, Refreshable {

	private static final Logger logger = LoggerFactory.getLogger(BusinessOwnedVerticalDataServiceImpl.class);

	private Git git;
	private UsernamePasswordCredentialsProvider user;
	private final String gitBranch;
	private final String gitRoot;
	private final String gitPath;
	private final Boolean bovdEnabled;

	private String refName;

	private volatile boolean refreshing;
	private long refreshCount;
	private long lastRefreshTime;
	private long lastRefreshDuration;

	private PropertyChangeSupport propertyChangeSupport;

	public BusinessOwnedVerticalDataServiceImpl(String gitPath, String gitBranch, String gitRoot, String username, String password,
												Boolean bovdEnabled) {
		this.gitBranch = gitBranch;
		this.gitRoot = gitRoot;
		this.gitPath = gitPath;
		this.bovdEnabled = bovdEnabled;
		this.user = new UsernamePasswordCredentialsProvider(username, password);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		initRepo();
	}

	private void initRepo() {
        if (this.bovdEnabled) {
            try {
                logger.info("Initializing BOVD repository");

                String refBranchName = "refs/heads/" + gitBranch;

                // Clone remote repository
                CloneCommand cloneCommand = Git.cloneRepository()
                        .setURI(gitPath)
                        .setDirectory(Files.createTempDir())
                        .setCredentialsProvider(this.user)
                        .setTimeout(60);
                cloneCommand.setBranchesToClone(ImmutableSet.of(refBranchName))
                        .setBranch(refBranchName);
                this.git = cloneCommand.call();
                this.refName = Constants.HEAD;
            } catch (Exception e) {
                logger.error("Unable to start bovd service. Running with local resources only.", e);
            }
        }
	}

	/**
	 * Returns a resource from git repo first. If repo not available or resource not found, resource will be pulled
	 * from local storage.
	 * @param path
	 * @return
	 * @throws GlobeException
	 */
	@Override
	public byte[] getResource(String path) throws GlobeException {
		try {
			byte[] fileBytes = null;

			if (git != null) {
				Repository repository = git.getRepository();
				ObjectId ref = repository.resolve(this.gitBranch);
				// Resolve commit hash
				RevWalk revWalk = new RevWalk(repository);
				RevCommit revCommit = revWalk.parseCommit(ref);
				TreeWalk tree = new TreeWalk(repository);
				tree.addTree(revCommit.getTree());
				tree.setRecursive(true);
				// Set path filter, to show files on matching path
				tree.setFilter(PathFilter.create(gitRoot + "/" + path));

				try {
					ObjectReader objectReader = repository.newObjectReader();
					while (tree.next()) {
						fileBytes = objectReader.open(tree.getObjectId(0)).getBytes();
					}
				} catch (Exception e) {
					logger.error("Unable to retrieve requested bovd resource at path [{" + path + "}]", e);
				}
			}

			if (fileBytes == null) {
				logger.warn("Bovd resource not found in repo at path [{}], returning local resource.", path);
				fileBytes = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("business-owned-vertical-data/" + path));
			}
			return fileBytes;
		} catch (Exception e) {
			throw new GlobeException("Unable to retrieve bovd resource at path [" + path + "]", e);
		}
	}

	@Override
	public void addPropertyChangeLister(PropertyChangeListener propertyChangeListener) {
		this.propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public boolean isRefreshing() {
		return refreshing;
	}

	private String getLatestVersion() throws GlobeException {
		try {
			Ref branch = git.getRepository().getRefDatabase().getRef(this.refName);
			return branch.getObjectId().name();
		} catch (IOException e) {
			throw new GlobeException("Unable to get repository version", e);
		}
	}

	@Override
	public void refresh() throws GlobeRefreshException {
		if (git == null) {
			// If repo could not be started, try connect to repo again
			initRepo();
			if (git == null) {
				logger.warn("Ignoring git refresh, repo not available");
				return;
			}
		}

		if (refreshing) {
			logger.warn("Ignoring Git refresh, one is already in progress");
			return;
		}

		String latestVersion = getLatestVersion();

		long start = System.currentTimeMillis();

		try {
			refreshing = true;

			try {
				PullResult result = git.pull().setRebase(true).setCredentialsProvider(this.user).call();

				if (!result.isSuccessful()) {
					logger.error("Git refresh failed");
					return;
				}

				FetchResult fetchResult = result.getFetchResult();
				if (fetchResult != null && !fetchResult.getTrackingRefUpdates().isEmpty()) {
					propertyChangeSupport.firePropertyChange(GIT_PROPERTY, null, git);
				}
			} catch (Exception e) {
			    throw new GlobeRefreshException("Error refreshing git from version " + latestVersion, e);
			}

			refreshCount += 1;
			lastRefreshTime = System.currentTimeMillis();
			lastRefreshDuration = lastRefreshTime - start;
		} finally {
			refreshing = false;
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

}
