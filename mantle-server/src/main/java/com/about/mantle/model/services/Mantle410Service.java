package com.about.mantle.model.services;

import java.util.Set;

/**
 * For taking the page gone handling out of fastly and moving to mantle
 *
 * See GLBE-8284
 */
public interface Mantle410Service {

    /**
     * Checks against a uri to see if it is page gone
     *
     * @param uri
     * @return
     */
    public boolean is410(String uri);

    /**
     * Gets the set of urls from BVOD, made public for caching purposes
     * @return
     */
    public Set<String> getBVODMap();
}
