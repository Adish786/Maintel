package com.about.mantle.model.services.document.preprocessor;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentCalloutEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHtmlEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentTableEx;
import com.about.mantle.model.services.document.ElementRewriter;

/**
 * Parses HTML content from Documents and replaces with content modified by registered ElementRewriter beans.
 */
public class ElementRewriterPreprocessor extends AbstractStructuredContentBlockPreprocessor {
	private static final Logger logger = LoggerFactory.getLogger(ElementRewriterPreprocessor.class);

	private Map<String, List<ElementRewriter>> elementRewriters;

	public ElementRewriterPreprocessor(List<ElementRewriter> elementRewriters) {
		if (elementRewriters == null) {
			elementRewriters = new ArrayList<>();
		}
		// Mapping rewriters according to targeted elements
		this.elementRewriters = elementRewriters.stream()
				.flatMap(x -> x.getTargetElementNames().stream().distinct().map(y -> new SimpleEntry<>(x, y)))
				.collect(Collectors.groupingBy(
						Map.Entry::getValue,
						Collectors.mapping(Map.Entry::getKey, Collectors.toList())
				));
	}

	@Override
	public BaseDocumentEx preProcessDocument(BaseDocumentEx document) {
		// Documents only need to be processed if there LinkRewriter beans are registered
		if (elementRewriters == null || elementRewriters.isEmpty()) return document;

		return super.preProcessDocument(document);
	}

	/**
	 * Examines content block for applicable types and passes them along for examination for inline links.
	 * @param doc
	 * @param content
	 */
	@Override
	protected void processContentBlock(BaseDocumentEx doc, AbstractStructuredContentContentEx<?> content) {
		if (content instanceof StructuredContentHtmlEx) {
			StructuredContentHtmlEx htmlBlock = (StructuredContentHtmlEx) content;
			String html = htmlBlock.getData().getHtml();
			String newHtml = processHtml(doc, html);
			if (html != null && !html.equals(newHtml)) {
				htmlBlock.getData().setHtml(newHtml);
			}
		}else if(content instanceof StructuredContentCalloutEx) {
			StructuredContentCalloutEx calloutBlock = (StructuredContentCalloutEx) content;
			String html = calloutBlock.getData().getHtml();
			String newHtml = processHtml(doc, html);
			if (html != null && !html.equals(newHtml)) {
				calloutBlock.getData().setHtml(newHtml);
			}
		}else if(content instanceof StructuredContentTableEx) {
			StructuredContentTableEx tableBlock = (StructuredContentTableEx) content;
			tableBlock.getData().getTableData().getList().forEach(cellDataList -> {
				cellDataList.getList().forEach(cellData -> {
					String html = cellData.getValue();
					String newHtml = processHtml(doc, html);
					if (html != null && !html.equals(newHtml)) {
						cellData.setValue(newHtml);
					}
				});
			});
		}
	}

	/**
	 * Parses html content, extracts inline links, and returns new html block with modified link text.
	 * @param html
	 * @return
	 */
	public String processHtml(BaseDocumentEx baseDocument, String html) {
		org.jsoup.nodes.Document doc = null;
		
		if(StringUtils.isEmpty(html)) return html;

		// Check if any target elements are found (e.g. a -> "<a" exists)
		// This is to avoid parsing with JSoup when no work will be done.
		Set<String> targetElementNames = this.elementRewriters.keySet();
		boolean foundTargetElement = false;
		for (String targetElementName : this.elementRewriters.keySet()) {
			if (html.indexOf("<" + targetElementName) != -1) {
				foundTargetElement = true;
				break;
			}
		}

		if (!foundTargetElement) {
			return html;
		}

		try {
			doc = Jsoup.parseBodyFragment(html);
		} catch (Exception e) {
			logger.error("Error parsing html for link rewriting.  HTML was not processed.  Continuing.", e);
			return html;
		}

		for (String targetElementName : this.elementRewriters.keySet()) {
			doc.select(targetElementName).forEach((aNode) -> {
				JSoupElementWrapper elementWrapper = new JSoupElementWrapper(aNode);
				for (ElementRewriter elementRewriter : this.elementRewriters.get(targetElementName)) {
					try {
						elementRewriter.processElement(baseDocument, elementWrapper);
					} catch (Exception e) {
						logger.error("Error preprocessing element " +  targetElementName + " with elementRewriter " + elementRewriter, e);
					}
				}
			});
		}

		return doc.body().html();
	}
}
