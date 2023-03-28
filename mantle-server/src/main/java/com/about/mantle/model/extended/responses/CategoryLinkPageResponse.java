package com.about.mantle.model.extended.responses;

import java.io.Serializable;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.mantle.model.extended.docv2.CategoryLinkEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public class CategoryLinkPageResponse extends BaseResponse<SliceableListEx<CategoryLinkEx>> implements Serializable {
	private static final long serialVersionUID = 1L;

}
