package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

public interface Linkboxed {

	SliceableListEx<LinkboxEx> getLinkboxes();

	List<LinkboxLinkEx> getLinkboxesAsList();

	default List<LinkboxLinkEx> transformLinkboxes() {
		if (getLinkboxes().getList().isEmpty()) return ImmutableList.of();

		List<LinkboxLinkEx> result = new ArrayList<>();
		for (LinkboxEx linkbox : getLinkboxes().getList()) {
			for (LinkEx link : linkbox.getLinks().getList()) {
				if (isNotEmpty(link.getUri())) {
					result.add(new LinkboxLinkEx(linkbox.getHeading(), link.getText(), link.getUri(), link
							.getExternal(), link.getDocument()));
				}
			}
		}
		return result;
	}
}
