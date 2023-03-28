package com.about.mantle.utils.selene.utils;

import com.about.mantle.utils.selene.api.common.DataWithStatus;

public abstract class UpdateAndRestore<T> {

	protected abstract void getDataBeforeUpdate();

	protected abstract DataWithStatus update(T data);

	protected abstract void restore();

	public void runTest(T updateData, Runnable test) throws InterruptedException {
		getDataBeforeUpdate();
		update(updateData);
		try {
			Thread.sleep(2000);
			test.run();
		} finally {
			restore();
		}
	}


}
