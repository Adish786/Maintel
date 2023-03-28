package com.about.mantle.htmlslicing;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.FlexibleArticleDocumentEx;
import com.about.mantle.model.extended.docv2.PageEx;
import com.about.mantle.model.extended.docv2.RecipeDocumentEx;
import com.about.mantle.model.extended.docv2.StepByStepDocumentEx;
import com.about.mantle.model.extended.docv2.TaxonomyDocumentEx;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractHtmlSlicer implements HtmlSlicer {

	private ObjectMapper objectMapper;

	public AbstractHtmlSlicer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/*
	 * Needed for slicing legacy templates so as not to modify the document in the cache.
	 */
	private BaseDocumentEx cloneDocument(BaseDocumentEx document) {
		BaseDocumentEx answer = document;
		//long startTime = System.currentTimeMillis();
		try {
			answer = objectMapper.readValue(objectMapper.writeValueAsBytes(document), BaseDocumentEx.class);
		} catch (Exception e) {
			throw new GlobeException("Failed to clone document [" + document.getUrl() + "]", e);
		}
		//long duration = System.currentTimeMillis() - startTime;
		//System.out.println("TIME TO CLONE DOCUMENT " + duration);
		//NOTE that on @mmanashirov's MacBook Air (Early 2015) this takes about
		// ~300 ms on a fresh server start and ~5 ms on subsequent requests
		//These number must be doubled because it is done twice for each request:
		//once with the rendered slice config and once with a blank slice config.
		//The rendered slice config is obvious but the blank slice config is for
		//the purpose of getting the total character/word counts for GTM reporting.
		return answer;
	}

	@Override
	public BaseDocumentEx slice(HtmlSlicerConfig htmlSlicerConfig, BaseDocumentEx document) {

		if (document instanceof FlexibleArticleDocumentEx) {
			document = cloneDocument(document);
			for (PageEx page : ((FlexibleArticleDocumentEx) document).getPages()) {
				page.getContent().setList(applyFormatting(page.getContent().getList(), htmlSlicerConfig));
			}
		} else if (document instanceof TaxonomyDocumentEx) {
			document = cloneDocument(document);
			for (PageEx page : ((TaxonomyDocumentEx) document).getPages()) {
				page.getContent().setList(applyFormatting(page.getContent().getList(), htmlSlicerConfig));
			}
		} else if (document instanceof RecipeDocumentEx) {
			document = cloneDocument(document);
			RecipeDocumentEx recipeDocument = (RecipeDocumentEx) document;
			recipeDocument.getInstruction()
					.setList(applyFormatting(recipeDocument.getInstruction().getList(), htmlSlicerConfig));
		} else if (document instanceof StepByStepDocumentEx) {
			document = cloneDocument(document);
			for (PageEx page : ((StepByStepDocumentEx) document).getPages()) {
				page.getIntro().setList(applyFormatting(page.getIntro().getList(), htmlSlicerConfig));
				page.getContent().setList(applyFormatting(page.getContent().getList(), htmlSlicerConfig));
			}
		}

		// content block formatting for Structured Content (StructuredContentDocumentEx)
		// is handled by {@link StructuredContentDocumentProcessor}

		return document;
	}

}
