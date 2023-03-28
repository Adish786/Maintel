package com.about.mantle.model.tasks;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.htmlslicing.HtmlSlice;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.FlexibleArticleDocumentEx;
import com.about.mantle.model.extended.docv2.PageEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.ListStructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentFaqEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHeadingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx.StructuredContentProductRecordDataEx;
import com.about.mantle.model.extended.docv2.sc.recipesc.RecipeStructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentQuestionAndAnswerEx;
import com.about.mantle.render.MantleRenderUtils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

@Tasks
public class TableOfContentsTask {

	private static final String ANCHOR_CLASS = "heading-toc";
	private static final Pattern H3 = Pattern.compile("<h3( [^>]+)?>(.+?)</h3>");
	private static final Pattern H3_WITH_ANCHOR = Pattern.compile("<a class=\""+ ANCHOR_CLASS + "\" id=\"([^>]+)\"></a><h3( [^>]+)?>(.+?)</h3>");
	private static final String TOC_FAQ_MANTLE_DEFAULT_HEADING = "Frequently Asked Questions";
	protected static String tocFaqDefaultHeading = null;

	@Deprecated
	public TableOfContentsTask() {
	}

	public TableOfContentsTask(String tocFaqDefaultHeadingFromConfig) {
		tocFaqDefaultHeading = StringUtils.defaultIfBlank(tocFaqDefaultHeadingFromConfig, TOC_FAQ_MANTLE_DEFAULT_HEADING);
	}

	@Task(name = "tableOfContents")
	@TimedComponent(category = "task")
	public TableOfContents getTableOfContents(@RequestContextTaskParameter RequestContext requestContext,
											  @TaskParameter(name = "document") BaseDocumentEx document) {
		TableOfContents tableOfContents = null;
		Stream<AbstractStructuredContentContentEx<?>> blocksStream = Stream.empty();

		if (hasTableOfContents(document)) {
			tableOfContents = new TableOfContents();
			switch (document.getTemplateType()) {
				case FLEXIBLEARTICLE:
					generateFlexToc(document, tableOfContents);
					break;
				case REVIEWSC:
					// We want to skip intro for review sc document - getContentsStream does include intro in it but
					// getContents doesn't; thus using getContents here for review sc
					//discussion - https://dotdash.slack.com/archives/CET5ZMWNN/p1589209537071400
					blocksStream = ((StructuredContentDocumentEx) document).getContents().stream()
							.map(block -> (AbstractStructuredContentContentEx<?>)block);
					tableOfContents = getTableOfContentsFromBlocks(blocksStream);
					break;
				case LISTSC:
					// for list sc we just want to include intro and outro only. CMS didn't have support for showing/hiding items' headings block in TOC when this code was written.
					ListStructuredContentDocumentEx listScDoc = ((ListStructuredContentDocumentEx) document);
					blocksStream = Stream.of(listScDoc.ensureSCStream.apply(listScDoc.getIntro()), listScDoc.ensureSCStream.apply(listScDoc.getOutro()))
							.flatMap(Function.identity())
							.filter(listScDoc.onNonNullSCData);
					tableOfContents = getTableOfContentsFromBlocks(blocksStream);
					break;
				case RECIPESC:
					RecipeStructuredContentDocumentEx recipeScDoc = (RecipeStructuredContentDocumentEx) document;
					blocksStream = Stream.of(recipeScDoc.ensureSCStream.apply(recipeScDoc.getIntro()),
					                         recipeScDoc.ensureSCStream.apply(recipeScDoc.getFromTheEditors()),
					                         recipeScDoc.ensureSCStream.apply(recipeScDoc.getInstruction()))
					                     .flatMap(Function.identity())
					                     .filter(recipeScDoc.onNonNullSCData);
					tableOfContents = getTableOfContentsFromBlocks(blocksStream);
					break;
				case ENTITYREFERENCE:
				case HOWTO:
				case TERMSC:
				case STRUCTUREDCONTENT:
					blocksStream = ((StructuredContentBaseDocumentEx) document).getContentsStream();
					tableOfContents = getTableOfContentsFromBlocks(blocksStream);
					break;
				default:
					break;
			}

		}
		return tableOfContents;
	}

	@Task(name = "tocId")
	@TimedComponent(category = "task")
	public String getAnchorId(@RequestContextTaskParameter RequestContext requestContext,
							  @TaskParameter(name = "text") String text) {
		return generateAnchorId(text);
	}

	private TableOfContents getTableOfContentsFromBlocks(Stream<AbstractStructuredContentContentEx<?>> blocksStream) {
		List<TableOfContents.Item> tocItems = blocksStream
				.map(block -> getTocItem(block))
				.filter(item -> item != null)
				.collect(Collectors.toCollection(ArrayList::new));
		TableOfContents tableOfContents = new TableOfContents();
		tableOfContents.addItems(tocItems);
		return tableOfContents;
	}

	protected TableOfContents.Item getTocItem(AbstractStructuredContentContentEx<?> block) {
		return getTocItem(block, null);
	}

	protected TableOfContents.Item getTocItem(AbstractStructuredContentContentEx<?> block, String headingOverride) {
		TableOfContents.Item answer = null;

		switch (block.getType()) {
			case "QUESTIONANDANSWER":
				StructuredContentQuestionAndAnswerEx questionAndAnswerBlock = (StructuredContentQuestionAndAnswerEx) block;
				StructuredContentQuestionAndAnswerEx.StructuredContentQuestionAndAnswerDataEx questonAndAnswerData = questionAndAnswerBlock.getData();
				answer = createTocItem(questonAndAnswerData.getHideOnTOC(),
									   generateAnchorId(questionAndAnswerBlock),
									   questonAndAnswerData.getShortText(),
									   questonAndAnswerData.getQuestion(),
									   headingOverride);
				break;
			case "HEADING":
				StructuredContentHeadingEx headingBlock = (StructuredContentHeadingEx) block;
				StructuredContentHeadingEx.StructuredContentHeadingDataEx headingData = headingBlock.getData();
				answer = createTocItem(headingData.getHideOnTOC(),
									   generateAnchorId(headingBlock),
									   headingData.getShortText(),
									   headingData.getText(),
									   headingOverride);
				break;
			case "PRODUCTRECORD":
				StructuredContentProductRecordEx productRecordBlock = (StructuredContentProductRecordEx) block;
				StructuredContentProductRecordEx.StructuredContentProductRecordDataEx productRecordData = productRecordBlock.getData();
				String heading = stripToEmpty(productRecordBlock.getData().getSuperlative()) + " "
						+ stripToEmpty(productRecordBlock.getData().getProduct().getLongTitle());
				answer = createTocItem(productRecordData.getHideOnTOC(),
									   generateAnchorId(productRecordBlock),
									   null,
									   heading,
									   headingOverride);
				break;
			case "FAQ":
				StructuredContentFaqEx faqBlock = (StructuredContentFaqEx) block;
				answer = createTocItem(faqBlock.getData().getHideOnTOC(),
						generateAnchorId(faqBlock),
						null,
						generateFaqHeader(faqBlock),
						headingOverride);
				break;
			default:
				break;
		}
		return answer;
	}

	private TableOfContents.Item createTocItem(Boolean hideOnTOCBlockValue, String id, String shortTextBlockValue, String headingBlockValue, String headingOverride) {
		TableOfContents.Item answer = null;
		boolean hideOnTOC = Boolean.TRUE.equals(hideOnTOCBlockValue);
		if (!hideOnTOC) {
			String effectiveHeading = isNotBlank(headingOverride) ? headingOverride : isNotBlank(shortTextBlockValue)
					? shortTextBlockValue : headingBlockValue;
			if (effectiveHeading != null & id != null) {
				String heading = extractHeadingTextOnly(effectiveHeading);
				answer = new TableOfContents.Item(heading, id);
			}
		}
		return answer;
	}


	//TODO: Remove once flex articles have been converted to structured content
	@Deprecated
	private void generateFlexToc(BaseDocumentEx document, TableOfContents tableOfContents) {
		for (PageEx page : ((FlexibleArticleDocumentEx) document).getPages()) {
			for (HtmlSlice htmlSlice : page.getContent()) {
				Matcher m = H3_WITH_ANCHOR.matcher(htmlSlice.getContent());
				while (m.find()) {
					String id = m.group(1);
					String heading = extractHeadingTextOnly(m.group(3));
					tableOfContents.addItem(heading, id);
				}
			}
		}
	}

	//TODO: Remove once flex articles have been converted to structured content and verticals no longer inject anchor ids into the document
	/**
	 * Usage: a vertical should override the documentServiceWrapper bean and override the getDocument method.
	 * The vertical service wrapper should fetch the document and inject anchor ids using TableOfContentsTask.injectTableOfContentsAnchorIds.
	 * Example: https://bitbucket.prod.aws.about.com/projects/FRON/repos/health/browse/health-server/src/main/java/com/about/health/model/services/impl/HealthDocumentServiceWrapper.java
	 * @param document
	 */
	@Deprecated
	public static void injectTableOfContentsAnchorIds(BaseDocumentEx document) {
		if (hasTableOfContents(document)) {
			switch (document.getTemplateType()) {
			case FLEXIBLEARTICLE:
				for (PageEx page : ((FlexibleArticleDocumentEx) document).getPages()) {
					for (HtmlSlice htmlSlice : page.getContent()) {
						StringBuffer newContent = new StringBuffer();
						Matcher m = H3.matcher(htmlSlice.getContent());
						while (m.find()) {
							final String h3 = m.group(0);
							StringBuffer newHeading = new StringBuffer("<a class=\"" + ANCHOR_CLASS + "\" id=\"");
							newHeading.append(generateAnchorId(h3)).append("\"></a>").append(h3);
							m.appendReplacement(newContent, newHeading.toString());
						}
						m.appendTail(newContent);
						htmlSlice.setContent(newContent.toString());
					}
				}
				break;
			default:
				break;
			}
		}
	}

	public static String generateAnchorId(String heading) {
		return MantleRenderUtils.formatForHash(extractHeadingTextOnly(heading));
	}

	public static String generateAnchorId(StructuredContentHeadingEx headingBlock) {
		return generateAnchorId(headingBlock.getData().getText());
	}

	public static String generateAnchorId(StructuredContentProductRecordEx productRecordBlock) {
		return generateAnchorId(productRecordBlock.getData());
	}

	public static String generateAnchorId(StructuredContentQuestionAndAnswerEx questionAndAnswerBlock) {
		return generateAnchorId(questionAndAnswerBlock.getData().getQuestion());
	}

	/*
	 * Need the block data as the data is used at the document level and not always at the block level
	 */
	public static String generateAnchorId(StructuredContentProductRecordDataEx productRecordBlockData) {
		String heading = stripToEmpty(productRecordBlockData.getSuperlative()) + " " + stripToEmpty(productRecordBlockData.getProduct().getLongTitle());
		return generateAnchorId(heading);
	}

  	/*
  	 * Using block UUID in conjunction with the FAQ header because there can technically be more than
  	 * one FAQ block on the same page with the same header value
  	 */
	public static String generateAnchorId(StructuredContentFaqEx faqBlock) {
		return generateAnchorId(generateFaqHeader(faqBlock) + "-" + faqBlock.getUuid());
	}

	private static String generateFaqHeader(StructuredContentFaqEx faqBlock) {
		return StringUtils.defaultIfBlank(faqBlock.getData().getHeading(), tocFaqDefaultHeading);
	}

	private static String extractHeadingTextOnly(String heading) {
		return Jsoup.parseBodyFragment(heading).text();
	}

	//TODO: Remove metadata fallback and make hasTableOfContents in BaseDocumentEx default to false when hasTableOfContents in metadata is no longer supported (https://iacpublishing.atlassian.net/browse/GLBE-5717)
	/**
	 * This adds fallback logic for selene's migration of the hasTableOfContents field from metadata into the BaseDocument.
	 * We should check to see if it is returned in the document. If not return the value from metadata which defaults to false
	 * if the flag is not set in metadata.
	 * @param document
	 * @return true or false
	 */
	private static boolean hasTableOfContents(BaseDocumentEx document) {
		return (boolean) defaultIfNull(document.getHasTableOfContents(), document.getMetaData().getHasTableOfContents());
	}



	public static class TableOfContents {

		private List<Item> items = new ArrayList<>();

		public void addItem(String heading, String id) {
			this.items.add(new Item(heading, id));
		}

		public void addItems(List<Item> items) {
			this.items.addAll(items);
		}
		
		public List<Item> getItems() {
			return this.items;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((items == null) ? 0 : items.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof TableOfContents)) {
				return false;
			}
			TableOfContents other = (TableOfContents) obj;
			if (items == null) {
				if (other.items != null) {
					return false;
				}
			} else if (!items.equals(other.items)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "TableOfContents [items=" + items + "]";
		}
		
		public static class Item {
			private String heading;
			private String id;
			
			public Item() {
			}
			
			public Item(String heading, String id) {
				this.heading = heading;
				this.id = id;
			}
			
			public String getHeading() {
				return heading;
			}
			
			public String getId() {
				return id;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((heading == null) ? 0 : heading.hashCode());
				result = prime * result + ((id == null) ? 0 : id.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (!(obj instanceof Item)) {
					return false;
				}
				Item other = (Item) obj;
				if (heading == null) {
					if (other.heading != null) {
						return false;
					}
				} else if (!heading.equals(other.heading)) {
					return false;
				}
				if (id == null) {
					if (other.id != null) {
						return false;
					}
				} else if (!id.equals(other.id)) {
					return false;
				}
				return true;
			}

			@Override
			public String toString() {
				return "Item [heading=" + heading + ", id=" + id + "]";
			}

		}

	}

}
