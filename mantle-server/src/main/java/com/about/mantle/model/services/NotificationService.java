package com.about.mantle.model.services;

import com.about.mantle.model.extended.NotificationEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;


public interface NotificationService {
	
	/**
	 * This Service is for Editor created notifications e.g. promotions.
	 * These notifications are inherited from a node's ancestors and contain display information and view type.
	 * These notifications are temporary and have start and end dates associated with when they should be displayed. 
	 *
	 * @param docId
	 */
	SliceableListEx<NotificationEx> getNotificationList(Long docId);

}
