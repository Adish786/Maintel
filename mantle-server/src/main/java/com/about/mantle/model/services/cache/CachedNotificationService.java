package com.about.mantle.model.services.cache;

import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.NotificationEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.NotificationService;

public class CachedNotificationService implements NotificationService {

	private final NotificationService notificationService;
	private final CacheTemplate<SliceableListEx<NotificationEx>> notificationCache;

	public CachedNotificationService(NotificationService notificationService,
			CacheTemplate<SliceableListEx<NotificationEx>> notificationCache) {
		this.notificationService = notificationService;
		this.notificationCache = notificationCache;
	}
	
	@Override
	public SliceableListEx<NotificationEx> getNotificationList(Long docId) {

		return notificationCache.get(docId, () -> notificationService.getNotificationList(docId));
	}

}
