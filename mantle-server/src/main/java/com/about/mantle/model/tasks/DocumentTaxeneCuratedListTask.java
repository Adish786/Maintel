package com.about.mantle.model.tasks;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.extended.curatedlist.CuratedListEx;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListOfListEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentTaxeneComposite;
import com.about.mantle.model.extended.docv2.DocumentTaxeneCuratedListEx;
import com.about.mantle.model.extended.docv2.DocumentTaxeneCuratedListOfListEx;
import com.about.mantle.model.services.DocumentTaxeneService;
import com.about.mantle.model.services.TaxeneRelationService;
import com.about.mantle.model.services.impl.DocumentTaxeneServiceImpl;

@Tasks
public class DocumentTaxeneCuratedListTask {

	private final CuratedListTask curatedListTask;
	private final DocumentTaxeneService documentTaxeneService;
	private final ExecutorService executor;

	// TODO refactor so that CuratedListTask is not a dependency.
	public DocumentTaxeneCuratedListTask(CuratedListTask curatedListTask, DocumentTaxeneService documentTaxeneService,
			ExecutorService executor) {
		this.curatedListTask = curatedListTask;
		this.documentTaxeneService = documentTaxeneService;
		this.executor = executor;
	}

	@Task(name = "documentTaxeneCuratedList")
	public CuratedListEx<CuratedDocumentTaxeneComposite> documentTaxeneCuratedList(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName) {

		return documentTaxeneCuratedList(requestContext, listName, null);
	}

	@Task(name = "documentTaxeneCuratedList")
	public CuratedListEx<CuratedDocumentTaxeneComposite> documentTaxeneCuratedList(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName,
			@TaskParameter(name = "projection") String projection) {

		return documentTaxeneCuratedList(requestContext, listName, null, null, projection);
	}

	@Task(name = "documentTaxeneCuratedList")
	@TimedComponent(category = "task")
	public CuratedListEx<CuratedDocumentTaxeneComposite> documentTaxeneCuratedList(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName, @TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage) {

		return documentTaxeneCuratedList(requestContext, listName, pageNum, itemsPerPage, null);

	}

	@Task(name = "documentTaxeneCuratedList")
	@TimedComponent(category = "task")
	public CuratedListEx<CuratedDocumentTaxeneComposite> documentTaxeneCuratedList(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName, @TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage,
			@TaskParameter(name = "projection") String projection) {

		// Getting CuratedListEx using the curatedListTask
		CuratedListEx<CuratedDocumentEx> curatedList = curatedListTask.getDocumentSummaryListByName(requestContext,
				listName, pageNum, itemsPerPage, projection);

		if (curatedList == null)
			return null;

		return createDocumentTaxeneCuratedList(curatedList);
	}

	@Task(name = "documentTaxeneCuratedListOfList")
	public DocumentTaxeneCuratedListOfListEx<CuratedDocumentEx> documentTaxeneCuratedListOfList(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName) {

		return documentTaxeneCuratedListOfList(requestContext, listName, null);

	}

	@Task(name = "documentTaxeneCuratedListOfList")
	@TimedComponent(category = "task")
	public DocumentTaxeneCuratedListOfListEx<CuratedDocumentEx> documentTaxeneCuratedListOfList(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "listName") String listName,
			@TaskParameter(name = "projection") String projection) {

		// Getting DocumentCuratedListOfList using the curatedListTask
		DocumentCuratedListOfListEx documentCuratedListOfList = curatedListTask
				.documentCuratedListOfList(requestContext, listName, projection);

		if (documentCuratedListOfList == null)
			return null;

		List<DocumentTaxeneCuratedListEx<CuratedDocumentEx>> listOfDocumentTaxeneCuratedList;
		if (executor == null) {
			// Using the returned DocumentCuratedListOfList to form/create the
			// DocumentTaxeneCuratedListOfList
			listOfDocumentTaxeneCuratedList = documentCuratedListOfList.getData().parallelStream()
					.map(curatedList -> createDocumentTaxeneCuratedList(curatedList)).collect(Collectors.toList());
		} else {
			List<CompletableFuture<DocumentTaxeneCuratedListEx<CuratedDocumentEx>>> list = documentCuratedListOfList
					.getData().stream()
					.map(cl -> CompletableFuture.supplyAsync(() -> createDocumentTaxeneCuratedList(cl), executor))
					.collect(Collectors.toList());
			listOfDocumentTaxeneCuratedList = list.stream().map(f -> f.join()).collect(Collectors.toList());
		}
		// Creating a new DocumentTaxeneCuratedListOfList
		DocumentTaxeneCuratedListOfListEx<CuratedDocumentEx> documentTaxeneCuratedListOfList = new DocumentTaxeneCuratedListOfListEx<>();

		documentTaxeneCuratedListOfList.copyFrom(documentCuratedListOfList);
		documentTaxeneCuratedListOfList.setData(listOfDocumentTaxeneCuratedList);
		return documentTaxeneCuratedListOfList;
	}

	/*
	 * A method that creates a new DocumentTaxeneCuratedList from a given
	 * CuratedListEx
	 */
	private DocumentTaxeneCuratedListEx<CuratedDocumentEx> createDocumentTaxeneCuratedList(
			CuratedListEx<CuratedDocumentEx> curatedList) {
		DocumentTaxeneCuratedListEx<CuratedDocumentEx> documentTaxeneCuratedList = new DocumentTaxeneCuratedListEx<>();

		documentTaxeneCuratedList.copyFrom(curatedList);
		documentTaxeneCuratedList
				.setData(documentTaxeneService.getCuratedDocumentTaxeneCompositeList(curatedList.getData(), null));

		return documentTaxeneCuratedList;
	}
}
