package com.about.mantle.model.services;

import java.util.Set;

/**
 * Service that allows the creation of lists containing some number of domains.
 * This service provides the lists of domains to be marked as "nofollow" and/or "sponsored".
 */
public interface CuratedDomainService {

	public static final String REVGROUP_SAFELIST = "REVGROUP_SAFELIST";
	public static final String SEO_SAFELIST = "SEO_SAFELIST";
	public static final String REVGROUP_SPONSOREDLIST = "REVGROUP_SPONSOREDLIST";
	public static final String CAES_SPONSOREDLIST = "CAES_SPONSOREDLIST";

	/**
	 * Returns domains present in the list identified by the given combination of type
	 * and subType fields.
	 * @param type The primary field used to identify a list. Determines what subTypes
	 *             are valid. (e.g. COMMERCE is a valid subtype of REVGROUP_SAFELIST).
	 *             Valid types are defined in Selene by an enumeration; arbitrary values
	 *             for type are not allowed.
	 * @param subType The secondary field used to identify a list. A type may allow only
	 *                specific subtypes. (e.g. NOTAGROUP is not a valid subtype of
	 *                REVGROUP_SAFELIST).
	 * @return
	 */
	Set<String> getDomainsBySource(String type, String subType);
}
