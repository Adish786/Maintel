package com.about.mantle.model.services.impl;

import static com.about.mantle.model.extended.docv2.SliceableListEx.isEmpty;

import javax.ws.rs.client.WebTarget;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.PageRequest;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListEx;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListOfListEx;
import com.about.mantle.model.extended.curatedlist.ImageCuratedListEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.responses.DocumentCuratedListExPageResponse;
import com.about.mantle.model.extended.responses.DocumentCuratedListOfListExPageResponse;
import com.about.mantle.model.extended.responses.ImageCuratedListExPageResponse;
import com.about.mantle.model.services.CuratedListService;

public class CuratedListServiceImpl extends AbstractHttpServiceClient implements CuratedListService {

	public static final String SELENE_CURATED_LIST_PATH = "/curatedlist";
	public static final String SELENE_IMAGE_PATH = "/image";
	public static final String SELENE_TAXONOMY_PATH = "/taxonomy";
	public static final String SELENE_TAXONOMY_LIST_OF_LIST_PATH = "/taxonomy/listoflist";
	protected static final String SELENE_DOCUMENT_LIST_PATH = "/document";
	protected static final String SELENE_DOCUMENT_LIST_OF_LIST_PATH = "/document/listoflist";

	
	public CuratedListServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
	}

	private String createProjection(Integer pageNum, Integer itemsPerPage) {
		if (pageNum != null && itemsPerPage != null) {
			int start = (pageNum - 1) * itemsPerPage;
			int end = start + itemsPerPage - 1;
			return "{(list[0](itemCount,items(list[" + start + "," + end + "])))}";
		}
		return null;
	}

	@Override
	public SliceableListEx<ImageCuratedListEx> getImageListByName(String listName, Boolean activeOnly, Integer pageNum,
			Integer itemsPerPage, Long activeDate) {
		String projection = createProjection(pageNum, itemsPerPage);

		ImageCuratedListExPageResponse response;
		if (activeDate != null) {
			response = fetchByListNameAndType(SELENE_IMAGE_PATH,
					new CuratedListRequest.Builder().setActiveOnly(true).setListName(listName)
							.setActiveDateMillis(activeDate).build(),
					PageRequest.fromPageNumberAndSize(1, 1), projection, ImageCuratedListExPageResponse.class);
		} else {
			response = fetchByListNameAndType(SELENE_IMAGE_PATH,
					new CuratedListRequest.Builder().setActiveOnly(activeOnly).setListName(listName).build(),
					PageRequest.fromPageNumberAndSize(1, 1), projection, ImageCuratedListExPageResponse.class);
		}

		return response.getData();
	}
	
	@Override
	public SliceableListEx<DocumentCuratedListEx> getDocumentSummaryListHistoryByName(String listName, Integer historyDepth,
			Integer pageNum, Integer itemsPerPage) {

		int start = (pageNum - 1) * itemsPerPage;
		int end = start + itemsPerPage - 1;
		
		String projection = "{(list[0, " + historyDepth + "](name,displayName,itemCount,activeDate,items(list[" + start + "," + end + "])))}";

		DocumentCuratedListExPageResponse documentSummaryCuratedLists;

		documentSummaryCuratedLists = fetchByListNameAndType(SELENE_DOCUMENT_LIST_PATH,
					new CuratedListRequest.Builder().setActiveOnly(false).setListName(listName).build(),
					null, projection, DocumentCuratedListExPageResponse.class);

		if (documentSummaryCuratedLists == null || isEmpty(documentSummaryCuratedLists.getData()))
			return null;

		return documentSummaryCuratedLists.getData();
	}

	@Override
	public SliceableListEx<DocumentCuratedListEx> getDocumentSummaryListByName(String listName, Boolean activeOnly,
			Integer pageNum, Integer itemsPerPage, Long activeDate) {

		return getDocumentSummaryListByName(listName, activeOnly, pageNum, itemsPerPage, activeDate, null);
	}

	@Override
	public SliceableListEx<DocumentCuratedListEx> getDocumentSummaryListByName(String listName, Boolean activeOnly,
		   Integer pageNum, Integer itemsPerPage,
		   Long activeDate, String projection) {

		if(projection == null) {
			projection = createProjection(pageNum, itemsPerPage);
		}

		DocumentCuratedListExPageResponse documentSummaryCuratedLists;

		if (activeDate != null) {
			documentSummaryCuratedLists = fetchByListNameAndType(SELENE_DOCUMENT_LIST_PATH,
					new CuratedListRequest.Builder().setActiveOnly(true).setListName(listName)
							.setActiveDateMillis(activeDate).build(),
					PageRequest.fromPageNumberAndSize(1, 1), projection, DocumentCuratedListExPageResponse.class);
		} else {
			documentSummaryCuratedLists = fetchByListNameAndType(SELENE_DOCUMENT_LIST_PATH,
					new CuratedListRequest.Builder().setActiveOnly(activeOnly).setListName(listName).build(),
					PageRequest.fromPageNumberAndSize(1, 1), projection, DocumentCuratedListExPageResponse.class);
		}

		if (documentSummaryCuratedLists == null || isEmpty(documentSummaryCuratedLists.getData()))
			return null;

		return documentSummaryCuratedLists.getData();
	}

	@Override
	public SliceableListEx<DocumentCuratedListOfListEx> getDocumentSummaryListOfListByName(String listName,
		Boolean activeOnly, Integer pageNum, Integer itemsPerPage, Long activeDate) {

		return getDocumentSummaryListOfListByName(listName, activeOnly, pageNum, itemsPerPage, activeDate, null);
	}

	@Override
	public SliceableListEx<DocumentCuratedListOfListEx> getDocumentSummaryListOfListByName(String listName,
			Boolean activeOnly, Integer pageNum, Integer itemsPerPage, Long activeDate, String projection) {

		if(projection == null) {
			projection = createProjection(pageNum, itemsPerPage);
		}

		DocumentCuratedListOfListExPageResponse documentSummaryCuratedListOfList;

		if (activeDate != null) {
			documentSummaryCuratedListOfList = fetchByListNameAndType(SELENE_DOCUMENT_LIST_OF_LIST_PATH,
					new CuratedListRequest.Builder().setActiveOnly(true).setListName(listName)
							.setActiveDateMillis(activeDate).build(),
					PageRequest.fromPageNumberAndSize(1, 1), projection, DocumentCuratedListOfListExPageResponse.class);
		} else {
			documentSummaryCuratedListOfList = fetchByListNameAndType(SELENE_DOCUMENT_LIST_OF_LIST_PATH,
					new CuratedListRequest.Builder().setActiveOnly(activeOnly).setListName(listName).build(),
					PageRequest.fromPageNumberAndSize(1, 1), projection, DocumentCuratedListOfListExPageResponse.class);
		}

		if (documentSummaryCuratedListOfList == null || isEmpty(documentSummaryCuratedListOfList.getData()))
			return null;

		return documentSummaryCuratedListOfList.getData();
	}

	public <T extends BaseResponse<?>> T fetchByListNameAndType(String basePath, CuratedListRequest key,
			PageRequest pageRequest, String projection, Class<T> bindToTarget) {

		WebTarget webTarget = baseTarget.path(SELENE_CURATED_LIST_PATH).path(basePath).path("listname")
				.path(key.getListName());

		webTarget = webTarget.queryParam("activeOnly", key.isActiveOnly());
		
		if (key.getActiveDate() != null) {
			webTarget = webTarget.queryParam("activeDate", key.getActiveDate());
		}

		if (pageRequest != null) {
			webTarget = webTarget.queryParam("limit", pageRequest.getLimit());
			webTarget = webTarget.queryParam("offset", pageRequest.getOffset());
		}

		if (projection != null) {
			webTarget = webTarget.queryParam("projection", "{p}").resolveTemplate("p", projection);
		}

		T response = readResponse(webTarget, bindToTarget);
		return response;
	}

}
