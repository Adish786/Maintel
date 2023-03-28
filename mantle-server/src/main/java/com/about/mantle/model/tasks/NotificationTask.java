package com.about.mantle.model.tasks;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.extended.NotificationEx;
import com.about.mantle.model.extended.NotificationEx.NotificationItem;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.NotificationService;

@Tasks
public class NotificationTask {

	private final NotificationService notificationService;
	private Set<Long> taxonomyDocIdsToBeExcludedFromSitewideNotification;

	private Set<Long> docIdsToBeExcludedFromSitewideNotification;

	public NotificationTask(NotificationService notificationService, List<Long> taxonomyDocIdsToBeExcludedFromSitewideNotification, List<Long> docIdsToBeExcludedFromSitewideNotification) {
		this.notificationService = notificationService;
		if(taxonomyDocIdsToBeExcludedFromSitewideNotification != null) {
			this.taxonomyDocIdsToBeExcludedFromSitewideNotification = new HashSet<>(taxonomyDocIdsToBeExcludedFromSitewideNotification);
		}
		if(docIdsToBeExcludedFromSitewideNotification != null) {
			this.docIdsToBeExcludedFromSitewideNotification = new HashSet<>(docIdsToBeExcludedFromSitewideNotification);
		}
	}

	/**
	 * Returns a List of NotificationItem. This list has been flattened and originating document information has been removed.
	 * This task filters any notification in which the current time is not between the start and end date of the notification.
	 * 
	 * @param requestContext
	 */
	@Task(name = "notificationList")
	public List<NotificationItem> getNotificationList(@RequestContextTaskParameter RequestContext requestContext) {
		return getNotificationList(requestContext, null);
	}
	
	/**
	 * Returns a List of NotificationItem. This list has been flattened and originating document information has been removed.
	 * List is filtered by type and filters any notification in which the current time is not between the start and end date of the notification.
	 * 
	 * @param requestContext
	 * @param type
	 */
	@Task(name = "notificationList")
	@TimedComponent(category = "task")
	public List<NotificationItem> getNotificationList(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "type") String type) {

		Long docId = ((VerticalUrlData)requestContext.getUrlData()).getDocId();
		
		return getNotificationList(requestContext, docId, type);
	}
	
	/**
	 * Returns a List of NotificationItem. This list has been flattened and originating document information has been removed.
	 * List is filtered by type and filters any notification in which the current time is not between the start and end date of the notification.
	 * 
	 * @param requestContext
	 * @param docId
	 * @param type
	 */
	@Task(name = "notificationList")
	@TimedComponent(category = "task")
	public List<NotificationItem> getNotificationList(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "docId") Long docId,
			@TaskParameter(name = "type") String type) {
		
		//Request Notification List with a default limit of 1
		return getNotificationList(requestContext, docId, type, 1L);
	}
	
	/**
	 * Returns a List of NotificationItem. This list has been flattened and originating document information has been removed.
	 * List is filtered by type and filters any notification in which the current time is not between the start and end date of the notification.
	 * 
	 * @param requestContext
	 * @param docId
	 * @param type
	 * @param limit
	 */
	@Task(name = "notificationList")
	@TimedComponent(category = "task")
	public List<NotificationItem> getNotificationList(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "docId") Long docId,
			@TaskParameter(name = "type") String type,
			@TaskParameter(name = "limit") Long limit) {
		
		SliceableListEx<NotificationEx> fullNotificationList = notificationService.getNotificationList(docId);
		
		long currentEpoch = Instant.now().toEpochMilli();
		Predicate<NotificationItem> isInTimeRange = notificationItem -> ((notificationItem.getStartDate() <= currentEpoch) && (notificationItem.getEndDate() > currentEpoch));
		Predicate<NotificationItem> isType = notificationItem -> type == null || type.equals(notificationItem.getType());
		
		List<NotificationItem> filteredNotificationList = 
				fullNotificationList.getList().stream() // stream of List<NotificationEx>
					.flatMap(notification -> notification.getNotificationItems().stream()) // stream of List<NotificationItem>
					.filter(isType.and(isInTimeRange)) //filtered by type and time range
					.limit(limit)
					.collect(Collectors.toList()); 
		
		return filteredNotificationList;
	}

	@Task(name = "siteWideNotification")
	public NotificationItem getSiteWideNotification(@RequestContextTaskParameter RequestContext requestContext) {
		return getSiteWideNotification(requestContext, null);
	}

	@Task(name = "siteWideNotification")
	public NotificationItem getSiteWideNotification(@RequestContextTaskParameter RequestContext requestContext,
													  @TaskParameter(name = "ancestors") List<TaxeneNodeEx> ancestors) {

		Long docId = null;
		if(requestContext.getUrlData() instanceof VerticalUrlData) {
			docId = ((VerticalUrlData)requestContext.getUrlData()).getDocId();
		}

		if(isCurrentDocToBeExcludedFromSitewideNotification(docId)) return null;

		//This excludes taxonomy document as well as it's children if taxonomyDocIdsToBeExcludedFromSitewideNotification contains its docId
		if(isAncestorToBeExcludedFromSitewideNotification(ancestors)) return null;

		NotificationItem notificationItem = null;

		if(docId != null) {
			List<NotificationItem> filteredNotificationList = getNotificationList(requestContext, docId, "SITEWIDE", 1L);
			if(CollectionUtils.isNotEmpty(filteredNotificationList)) notificationItem = filteredNotificationList.get(0);
		}

		return notificationItem;
	}

	private boolean isCurrentDocToBeExcludedFromSitewideNotification(Long docId) {
		if(docIdsToBeExcludedFromSitewideNotification != null && docId != null) {
			return docIdsToBeExcludedFromSitewideNotification.contains(docId);
		}
		return false;
	}

	private boolean isAncestorToBeExcludedFromSitewideNotification(List<TaxeneNodeEx> ancestors) {
		if(taxonomyDocIdsToBeExcludedFromSitewideNotification != null && ancestors != null) {
			return ancestors.stream().anyMatch(taxeneNodeEx -> taxonomyDocIdsToBeExcludedFromSitewideNotification.contains(taxeneNodeEx.getDocId()));
		}
		return false;
	}
	
}
