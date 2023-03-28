package com.about.mantle.model.services;

import com.about.mantle.model.extended.attribution.Attribution;
import com.about.mantle.model.extended.attribution.AttributionType;
import com.about.mantle.model.extended.docv2.SliceableListEx;

import java.util.Map;

public interface AttributionService {

	/**
	 * Gets a singular attribution based off a unique ID in selene
	 * @param id - The attribution id. ex: f066e2152759477ca47008038405ac69
	 * @return
	 */
	Attribution getById(String id);

	Map<String, AttributionType> getAttributionTypes();

	/**
	 * Gets all the different attributions an author might have.
	 * If this author is both an `AUTHOR` and `MEDICAL_REVIEWER` then it will return both of those
	 * attribution objects.
	 * @param id - The author id. ex: 230271
	 * @return
	 */
	SliceableListEx<Attribution> getAttributionsByAuthorId(String id);

}
