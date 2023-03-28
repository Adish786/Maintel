package com.about.mantle.model.tasks;

import com.about.globe.core.exception.GlobeInvalidTaskParameterException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameters;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.infocat.model.InfoCatRecordPair;
import com.about.mantle.model.extended.DeionSearchFullResultEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx;
import com.about.mantle.model.services.DeionSearchFullDocumentService;
import com.about.mantle.model.services.DeionSearchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Tasks
public class ProductRecordTask {
	public static final Logger logger = LoggerFactory.getLogger(ProductRecordTask.class);

	protected final DeionSearchFullDocumentService deionSearchFullDocumentService;
	protected final Vertical vertical;

	public ProductRecordTask(DeionSearchFullDocumentService deionSearchFullDocumentService, Vertical vertical) {
		this.deionSearchFullDocumentService = deionSearchFullDocumentService;
		this.vertical = vertical;
	}

	@Task(name = "documentsByProductId")
	@TimedComponent(category = "task")
	public List<DocumentProductReference> getDocumentsByProductId(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameters DocumentsByProductIdModel model) throws GlobeInvalidTaskParameterException {
		// Vertical should use model vertical if present, then class vertical if present
		String vertical = model.getVertical() != null ? model.getVertical() :
				(this.vertical != null ? this.vertical.toString() : null);

		if (model.getDocument() == null && StringUtils.isEmpty(model.getProductId())) {
			throw new GlobeInvalidTaskParameterException("document and productId are null");
		} else if (model.getDocument() != null && !StringUtils.isEmpty(model.getProductId())) {
			throw new GlobeInvalidTaskParameterException("document and productId cannot both be set");
		}

		String tempProductId = model.getProductId();
		if (model.getDocument() != null) {
			List<InfoCatRecordPair> products = model.getDocument().getInfoCatProductRecords();
			tempProductId = (products != null && products.size() > 0) ? products.get(0).getProduct().getId() : null;
		}
		final String productId = tempProductId;

		List<String> filterQueries = new ArrayList<>();
		filterQueries.add("productId:" + productId);
		if (vertical != null) {
			filterQueries.add("vertical:" + vertical);
		}

		if (model.getTemplateType() != null) {
			filterQueries.add("templateType:" + model.getTemplateType());
		}

		DeionSearchService.DeionSearchRequestContext.Builder builder = new DeionSearchService.DeionSearchRequestContext.Builder();
		builder = builder.setQuery("*")
				.setFilterQueries(filterQueries)
				.setNoCache(false)
				.setSort("displayed desc")
				.setIncludeDocumentSummaries(true);
		if (model.getLimit() != null) {
			builder = builder.setLimit(model.getLimit());
		}

		List<DocumentProductReference> results = new ArrayList<>();

		DeionSearchFullResultEx searchResults = deionSearchFullDocumentService.searchFullResults(builder.build());
		for (BaseDocumentEx document : searchResults.getItems()) {
			// Only structured content documents should be returned, but ensures safe
			// casting
			if (!(document instanceof StructuredContentBaseDocumentEx)) {
				continue;
			}

			// Assumes a matching ProductRecord block exists, but this should
			// be assured by the deion search
			StructuredContentBaseDocumentEx scDoc = (StructuredContentBaseDocumentEx) document;
			List<StructuredContentProductRecordEx> productBlocks = scDoc.getContentsStreamOfType("PRODUCTRECORD")
					.filter(block -> productId
							.equals(((StructuredContentProductRecordEx) block).getData().getProductId()))
					.map(StructuredContentProductRecordEx.class::cast)
					.collect(Collectors.toCollection(ArrayList::new));

			results.add(new DocumentProductReference(scDoc, productBlocks));
		}

		return results;
	}

	public static class DocumentProductReference {
		private BaseDocumentEx document;
		private List<StructuredContentProductRecordEx> blocks;

		public DocumentProductReference(BaseDocumentEx document, List<StructuredContentProductRecordEx> blocks) {
			this.document = document;
			this.blocks = blocks;
		}

		public BaseDocumentEx getDocument() {
			return document;
		}

		public List<StructuredContentProductRecordEx> getBlocks() {
			return blocks;
		}
	}

	public static class DocumentsByProductIdModel {

		private BaseDocumentEx document;
		private Integer limit;
		private String productId;
		private String templateType;
		private String vertical;

		public BaseDocumentEx getDocument() {
			return document;
		}

		public void setDocument(BaseDocumentEx document) {
			this.document = document;
		}

		public Integer getLimit() {
			return limit;
		}

		public void setLimit(Integer limit) {
			this.limit = limit;
		}

		public String getProductId() {
			return productId;
		}

		public void setProductId(String productId) {
			this.productId = productId;
		}

		public String getTemplateType() {
			return templateType;
		}

		public void setTemplateType(String templateType) {
			this.templateType = templateType;
		}

		public String getVertical() {
			return vertical;
		}

		public void setVertical(String vertical) {
			this.vertical = vertical;
		}
	}

}
