package com.about.mantle.model.services;

import com.about.mantle.model.PageRequest;
import com.about.mantle.model.extended.docv2.CategoryLinkEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public interface NavigationLinkService {

	SliceableListEx<CategoryLinkEx> getNavigationLinks(String url, PageRequest pageRequest);
}
