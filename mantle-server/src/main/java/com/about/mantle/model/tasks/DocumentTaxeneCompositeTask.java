package com.about.mantle.model.tasks;

import java.util.List;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.cache.hash.DocumentCollectionHasher;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.DocumentTaxeneComposite;
import com.about.mantle.model.services.DocumentTaxeneService;
import com.about.mantle.model.services.TaxeneRelationService;
import com.about.mantle.model.services.impl.DocumentTaxeneServiceImpl;

/**
 * Tasks related to returning instances of {@link DocumentTaxeneComposite}.  Primarily a wrapper around the
 * {@link DocumentTaxeneService}
 */
@Tasks
public class DocumentTaxeneCompositeTask {

	private final DocumentTaxeneService documentTaxeneService;

	public DocumentTaxeneCompositeTask(DocumentTaxeneService documentTaxeneService) {
		this.documentTaxeneService = documentTaxeneService;
	}

	/**
	 * Same as {@link #getDocumentTaxeneComposite(BaseDocumentEx)} but does not limit the Taxene doc with a projection
	 * @param document
	 * @param <T>
	 * @return
	 */
	@Task(name = "documentTaxene")
	@TimedComponent(category = "task")
	public<T extends BaseDocumentEx> DocumentTaxeneComposite<T> getDocumentTaxeneComposite(
			@TaskParameter(name = "document") T document) {
		return getDocumentTaxeneComposite(document, null);
	}

	/**
	 * Wrapper around {@link DocumentTaxeneService#getDocumentTaxeneComposite(BaseDocumentEx, String)}
	 * @param document
	 * @param projection
	 * @param <T>
	 * @return
	 */
	@Task(name = "documentTaxene")
	@TimedComponent(category = "task")
	public <T extends BaseDocumentEx> DocumentTaxeneComposite<T> getDocumentTaxeneComposite(
			@TaskParameter(name = "document") T document, @TaskParameter(name = "projection") String projection) {
		return documentTaxeneService.getDocumentTaxeneComposite(document, projection);
	}

	/**
	 * Same as {@link #getDocumentTaxeneCompositeList(List, String)} but does not limit the Taxene doc with a projection
	 * @param documentList
	 * @param <T>
	 * @return
	 */
	@Task(name = "documentTaxeneList")
	@TimedComponent(category = "task")
	public <T extends BaseDocumentEx> List<DocumentTaxeneComposite<T>> getDocumentTaxeneCompositeList(
			@TaskParameter(name = "documentList", hasher = DocumentCollectionHasher.class) List<T> documentList) {
		return getDocumentTaxeneCompositeList(documentList, null);
	}

	/**
	 * Wrapper for {@link DocumentTaxeneService#getDocumentTaxeneCompositeList(List, String)}
	 * @param documentList
	 * @param projection
	 * @param <T>
	 * @return
	 */
	@Task(name = "documentTaxeneList")
	@TimedComponent(category = "task")
	public <T extends BaseDocumentEx> List<DocumentTaxeneComposite<T>> getDocumentTaxeneCompositeList(
			@TaskParameter(name = "documentList", hasher = DocumentCollectionHasher.class) List<T> documentList,
			@TaskParameter(name = "projection") String projection) {

		return documentTaxeneService.getDocumentTaxeneCompositeList(documentList, projection);
	}
}
