package com.about.mantle.htmlslicing;

import java.util.List;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;

public interface HtmlSlicer {

	List<HtmlSlice> applyFormatting(List<HtmlSlice> blocks, HtmlSlicerConfig config);

	List<HtmlSlice> applyFormatting(String blockContent, HtmlSlicerConfig config);

	List<HtmlSlice> removeFormatting(List<HtmlSlice> blocks, HtmlSlicerConfig config);

	/**
	 * This method splits/format the document into content blocks based on given htmlSlicerConfig
	 * 
	 * @param htmlSlicerConfig
	 * @param document
	 * @return Document with html sliced up
	 */
	BaseDocumentEx slice(HtmlSlicerConfig htmlSlicerConfig, BaseDocumentEx document);

}
