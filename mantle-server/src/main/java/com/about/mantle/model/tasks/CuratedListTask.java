package com.about.mantle.model.tasks;

import static com.about.mantle.model.extended.docv2.SliceableListEx.isEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.extended.curatedlist.CuratedListEx;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListEx;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListOfListEx;
import com.about.mantle.model.extended.curatedlist.ImageCuratedListEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.CuratedListService;
import com.google.common.collect.Lists;

@Tasks
public class CuratedListTask {

	protected final CuratedListService curatedListService;

	public CuratedListTask(CuratedListService curatedListService) {
		this.curatedListService = curatedListService;
	}

	@Task(name = "imageList")
	@TimedComponent(category = "task")
	public CuratedListEx<ImageEx> getImageByName(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName, @TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage) {

		Long activeDate = getDateFromRequestContext(requestContext);

		SliceableListEx<ImageCuratedListEx> lists = curatedListService.getImageListByName(listName, true, pageNum, itemsPerPage,
				activeDate);

		if (isEmpty(lists)) return null;

		return lists.getList().get(0);
	}
	
	@Task(name = "documentCuratedList")
    public CuratedListEx<CuratedDocumentEx> documentCuratedList(@RequestContextTaskParameter RequestContext requestContext, @TaskParameter(name = "listName") String listName) {
    	
    	return getDocumentSummaryListByName(requestContext, listName, null, null);
    }
	
	@Task(name = "documentCuratedListWithDate")
	@TimedComponent(category = "task")
	public CuratedListEx<CuratedDocumentEx> getDocumentSummaryListHistoryByName(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName, @TaskParameter(name = "historyDepth") Integer historyDepth, @TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage) {

		SliceableListEx<DocumentCuratedListEx> lists = curatedListService.getDocumentSummaryListHistoryByName(listName, historyDepth, pageNum,
				itemsPerPage);
		
		if (isEmpty(lists)) {
			return null;
		}

		// Go backwards through the versions of the CL, finding the first (oldest) occurrence
		// of each documentId.  The CL's corresponding "active date" should reflect when the
		// document was first added to the list, and used as the list item's "addedDate"
		Map<Long, DateTime> oldestAddedDateMap = new HashMap<>();
		for (CuratedListEx<CuratedDocumentEx> curatedListEx : Lists.reverse(lists.getList())) {
			for (CuratedDocumentEx curatedDocumentEx : curatedListEx.getItems().getList()) {
				if (!oldestAddedDateMap.containsKey(curatedDocumentEx.getDocumentId())) {
					oldestAddedDateMap.put(curatedDocumentEx.getDocumentId(), curatedListEx.getActiveDate());
				}
			}
		}
		for (CuratedDocumentEx curatedDocumentEx : lists.getList().get(0).getItems().getList()) {
			curatedDocumentEx.setAddedDate(oldestAddedDateMap.get(curatedDocumentEx.getDocumentId()));
		}
		
		return lists.getList().get(0);
	}

	@Task(name = "documentCuratedList")
	@TimedComponent(category = "task")
	public CuratedListEx<CuratedDocumentEx> getDocumentSummaryListByName(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName,
			@TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage) {

		return getDocumentSummaryListByName(requestContext, listName, pageNum, itemsPerPage, null);
	}

	@Task(name = "documentCuratedList")
	@TimedComponent(category = "task")
	public CuratedListEx<CuratedDocumentEx> getDocumentSummaryListByName(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName,
			@TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage,
			@TaskParameter(name = "projection") String projection) {

		Long activeDate = getDateFromRequestContext(requestContext);

		SliceableListEx<DocumentCuratedListEx> lists = curatedListService.getDocumentSummaryListByName(listName, true, pageNum,
				itemsPerPage, activeDate, projection);

		return isEmpty(lists) ? null : lists.getList().get(0);
	}

	@Task(name = "documentCuratedListOfList")
    public DocumentCuratedListOfListEx documentCuratedListOfList(@RequestContextTaskParameter RequestContext requestContext,
		 	@TaskParameter(name = "listName") String listName) {
		
        return documentCuratedListOfList(requestContext, listName, null);
    }

	@Task(name = "documentCuratedListOfList")
	public DocumentCuratedListOfListEx documentCuratedListOfList(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName,
			@TaskParameter(name = "projection") String projection) {

		return getDocumentSummaryListOfListByName(requestContext, listName, null, null, projection);
	}

	@Task(name = "documentCuratedListOfList")
	@TimedComponent(category = "task")
	public DocumentCuratedListOfListEx getDocumentSummaryListOfListByName(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName, @TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage) {

		return getDocumentSummaryListOfListByName(requestContext, listName, pageNum, itemsPerPage, null);
	}

	@Task(name = "documentCuratedListOfList")
	@TimedComponent(category = "task")
	public DocumentCuratedListOfListEx getDocumentSummaryListOfListByName(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName, @TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage,
			@TaskParameter(name = "projection") String projection) {

		Long activeDate = getDateFromRequestContext(requestContext);

		SliceableListEx<DocumentCuratedListOfListEx> lists = curatedListService.getDocumentSummaryListOfListByName(listName, true,
				pageNum, itemsPerPage, activeDate, projection);

		return isEmpty(lists) ? null : lists.getList().get(0);
	}

	/**
	 * This method takes a curated list of list and flattens all sublists into one list
	 * 
	 * @param documentCuratedListOfList
	 *            the curated list of list
	 *            
	 * @return a flattened list
	 */
	@Task(name = "flattenCuratedListOfList")
	public List<CuratedDocumentEx> flattenCuratedListofList(
			@TaskParameter(name = "documentCuratedListOfList") DocumentCuratedListOfListEx documentCuratedListOfList) {

		return documentCuratedListOfList.getData().stream().flatMap(list -> list.getData().stream())
				.collect(Collectors.toList());
	}

    protected Long getDateFromRequestContext(RequestContext requestContext) {

        Map<String, String[]> params = requestContext.getParameters();

        if (params.containsKey("et"))
            return Long.valueOf(params.get("et")[0]);

        return null;
    }
}
