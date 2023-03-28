package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.NotificationEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.responses.NotificationListResponse;
import com.about.mantle.model.services.NotificationService;

public class NotificationServiceImpl extends AbstractHttpServiceClient implements NotificationService {
	
	public static final String SELENE_PATH = "/notification/list";
	
	public NotificationServiceImpl(HttpServiceClientConfig config) {
		super(config);
	}

	@Override
	public SliceableListEx<NotificationEx> getNotificationList(Long docId) {
		return getNotificationList(docId, NotificationListResponse.class).getData();
	}
	
	public <T extends BaseResponse<?>> T getNotificationList(Long docId, Class<T> bindToTarget) {
		if (docId == null) {
			throw new IllegalArgumentException("notification list: invalid operation");
		}
		
		WebTarget webTarget = baseTarget.path(SELENE_PATH).path(docId.toString());

		return readResponse(webTarget, bindToTarget);
	}

}
