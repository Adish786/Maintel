package com.about.mantle.spring;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.about.globe.core.spring.GlobeThreadPoolExecutor;
import com.about.mantle.cache.clearance.CacheClearanceRequest;
import com.about.mantle.cache.clearance.CacheClearanceThreadLocalUtils;

/**
 * This thread pool executor copies task submitting threads' 
 *  cache clearing threadLocal flag's value to all child threads.
 *  PS it also clears out set flag once child thread completes given runnable so that 
 *  set threadLocal isn't stuck with child tread forever.
 */
public class MantleThreadPoolExecutor extends GlobeThreadPoolExecutor {

	public MantleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}

	@Override
	public void execute(Runnable task) {

		List<CacheClearanceRequest> clearCacheRequests = CacheClearanceThreadLocalUtils.getClearCacheRequests();

		Runnable wrappedTask = () -> {
			try {
				CacheClearanceThreadLocalUtils.setClearCacheRequest(clearCacheRequests);
				task.run();
			} finally {
				CacheClearanceThreadLocalUtils.setClearCacheRequest(null);
			}
		};
		super.execute(wrappedTask);
	}

}
