package com.about.mantle.model.services;

import java.beans.PropertyChangeListener;

public interface BusinessOwnedVerticalDataService {
	String GIT_PROPERTY = "git";

	/**
	 * Retrieves a resource from a business owned data repo
	 *
	 * @param path
	 * @return
	 */
	byte[] getResource(String path);

	/**
	 * Add a listener that gets PropertyChangeEvents dispatched to them
	 *
	 * @param propertyChangeListener
	 * @return
	 */
	void addPropertyChangeLister(PropertyChangeListener propertyChangeListener);
}
