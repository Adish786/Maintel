package com.about.mantle.model.tasks;

import java.io.File;
import java.time.Instant;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.RequestContextImpl;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.extended.NotificationEx;
import com.about.mantle.model.extended.NotificationEx.NotificationItem;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.NotificationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationTaskTest {

	private NotificationService notificationService;
	private NotificationTask notificationTask;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String jsonFilePath = "./src/test/resources/tasks/notificationTaskTest/notification.json";
	
	@Before
	public void setUp() throws Exception {
		notificationService = mock(NotificationService.class);
		notificationTask = new NotificationTask(notificationService, null, List.of(5938292L, 6749636L));
		
		SliceableListEx<NotificationEx> notificationList = objectMapper.readValue(new File(jsonFilePath), new TypeReference<SliceableListEx<NotificationEx>>(){});
		for(NotificationEx notification : notificationList.getList()) {
			for(NotificationItem item : notification.getNotificationItems()) {
				if(!item.getHeader().equals("Expired Deal")) item.setEndDate(Instant.now().plusSeconds(60L).toEpochMilli());
			}
		}
		when(notificationService.getNotificationList(any())).thenReturn(notificationList);
	}
	
	@Test
	public void testNotificationList_specificType() throws Exception {
		List<NotificationItem> notificationList = notificationTask.getNotificationList(null, 4589346L, "PROMO");
		
		for(NotificationItem item : notificationList) {
			Assert.assertEquals("All types should be PROMO", "PROMO", item.getType());
		}
	}
	
	@Test
	public void testNotificationList_typeAndExpiry() throws Exception {
		List<NotificationItem> notificationList = notificationTask.getNotificationList(null, 4589346L, "PROMO", 2L);
		
		Assert.assertEquals("Only PROMO and non-expired notifications should be returned", 2, notificationList.size());
	}
	
	@Test
	public void testNotificationList_orderSpecificType() throws Exception {
		List<NotificationItem> notificationList = notificationTask.getNotificationList(null, 4589346L, "PROMO", 2L);
		Assert.assertEquals(2, notificationList.size());
		Assert.assertEquals("Parent Taxonomy Notification should be first", "Product Review Taxonomy Deal", notificationList.get(0).getHeader());
		Assert.assertEquals("Child node notification should be second","Motorola Deal", notificationList.get(1).getHeader());
	}
	
	@Test
	public void testNotificationList_orderAnyType() throws Exception {
		List<NotificationItem> notificationList = notificationTask.getNotificationList(null, 4589346L, null, 4L);
		Assert.assertEquals(4, notificationList.size());
		Assert.assertEquals("Parent Taxonomy Notification should be first", "Product Review Taxonomy Deal", notificationList.get(0).getHeader());
		Assert.assertEquals("Parent Taxonomy Notification should be second", "Other Parent Taxonomy Deal", notificationList.get(1).getHeader());
		Assert.assertEquals("Child node notification should be third", "Motorola Deal", notificationList.get(2).getHeader());
		Assert.assertEquals("Child node notification should be fourth", "Other Motorola Deal", notificationList.get(3).getHeader());
	}
	
	@Test
	public void testNotificationList_testDefaultSize() throws Exception {
		List<NotificationItem> notificationList = notificationTask.getNotificationList(null, 4589346L, null);
		Assert.assertEquals(1, notificationList.size());
		Assert.assertEquals("Parent Taxonomy Notification should be first", "Product Review Taxonomy Deal", notificationList.get(0).getHeader());
	}

	@Test
	public void testSitewideNotification_testContent() {
		RequestContext.Builder requestContextBuilder = new RequestContextImpl.Builder();
		requestContextBuilder.setUrlData(new VerticalUrlData(VerticalUrlData.builder("people","people.com").docId(5324742L)));
		NotificationItem notificationItem = notificationTask.getSiteWideNotification(requestContextBuilder.build());
		Assert.assertEquals("This is sitewide alert", notificationItem.getContent());
	}

	@Test
	public void testSitewideNotification_testCurrentDocIdExclusion() {
		RequestContext.Builder requestContextBuilder = new RequestContextImpl.Builder();
		requestContextBuilder.setUrlData(new VerticalUrlData(VerticalUrlData.builder("people","people.com").docId(5938292L)));
		NotificationItem notificationItem = notificationTask.getSiteWideNotification(requestContextBuilder.build());
		Assert.assertNull(notificationItem);
	}

}
