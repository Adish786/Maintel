package com.about.mantle.model.extended.responses;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.mantle.model.extended.attribution.Attribution;
import com.about.mantle.model.extended.docv2.SliceableListEx;

import java.io.Serializable;

public class AttributionListResponse extends BaseResponse<SliceableListEx<Attribution>> implements Serializable {

	private static final long serialVersionUID = 1L;

}
