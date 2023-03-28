package com.about.mantle.model.tasks;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.figcaption;
import static j2html.TagCreator.figure;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.img;
import static j2html.TagCreator.p;
import static j2html.TagCreator.video;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.commerce.amazonrss.AmazonRssItem;
import com.about.mantle.model.commerce.amazonrss.AmazonRssProduct;
import com.about.mantle.model.extended.AuthorEx;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.JwPlayerVideoDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.TaggedImage.UsageFlag;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AmazonOSPDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AmazonOSPDocumentEx.ProductItem;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHeadingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHtmlEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentImageEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentVideoBlockEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentSubheadingEx;
import com.about.mantle.model.services.commerce.AmazonRssService;
import com.about.mantle.render.MantleRenderUtils;

import j2html.Config;
import j2html.tags.ContainerTag;

@Tasks
public class AmazonRssTask {

	protected final AmazonRssService amazonRssService;
	protected final AuthorTask authorTask;
	protected final AttributionTask attributionTask;
	protected final String domain;
	protected final long intervalModeStart;
	protected final int intervalModeSpan;
	protected final MantleRenderUtils renderUtils;
	
	//j2html config to disable html escaping while building html from HTML sc blocks
	static {
		Config.textEscaper = text -> text;
	}

	/**
	 * Amazon RSS task
	 * @param amazonRssService
	 * @param domain                 e.g. lifewire.com
	 * @param intervalModeStart      number of hours from server start to transition from bulk mode to interval mode
	 * @param intervalModeSpan       number of hours from now documents were last published to include in the feed
	 */
	@Deprecated
	public AmazonRssTask(AmazonRssService amazonRssService, AuthorTask authorTask, String domain, int intervalModeStart, int intervalModeSpan) {
		this(amazonRssService, authorTask, domain, intervalModeStart, intervalModeSpan, null, null);
	}

	/**
	 * Amazon RSS task
	 * @param amazonRssService
	 * @param domain                 e.g. lifewire.com
	 * @param intervalModeStart      number of hours from server start to transition from bulk mode to interval mode
	 * @param intervalModeSpan       number of hours from now documents were last published to include in the feed
	 * @param attributionTask
	 * @param renderUtils
	 */
	public AmazonRssTask(AmazonRssService amazonRssService, AuthorTask authorTask, String domain, int intervalModeStart,
						 int intervalModeSpan, AttributionTask attributionTask, MantleRenderUtils renderUtils) {
		this.amazonRssService = amazonRssService;
		this.authorTask = authorTask;
		this.domain = domain;
		this.intervalModeStart = (intervalModeStart * 60L * 60L * 1000L) + System.currentTimeMillis();
		this.intervalModeSpan = intervalModeSpan;
		this.attributionTask = attributionTask;
		this.renderUtils = renderUtils;
	}

	@Task(name = "amazonRssItems")
	@TimedComponent(category = "task")
	public List<AmazonRssItem> getAmazonRssItems(@RequestContextTaskParameter RequestContext requestContext) {
		List<AmazonRssItem> items = amazonRssService.getDocumentsForRssFeed(domain, isIntervalMode() ? intervalModeSpan : -1).stream()
				.map(document -> itemFromDocument(document, requestContext))
				.filter(amazonRssItem -> Objects.nonNull(amazonRssItem))
				.collect(Collectors.toList());
		return items;
	}

	@Task(name = "amazonRssItem")
	@TimedComponent(category = "task")
	public AmazonRssItem getAmazonRssItem(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "document") BaseDocumentEx document) {
		return itemFromDocument(document, requestContext);
	}

	
	/**
	 * Amazon OSP elements: https://m.media-amazon.com/images/G/01/osp-ZXCVBNM/RSS_Specs_A21TJRUUN4KGV.pdf
	 *
	 * Amazon OSP elements mapping:
	 *
	 *  1.3.1 title                      => BaseDocument.title								[Required]
	 *  1.3.2 link                       => BaseDocument.url/docId							[Required]
	 *  1.3.3 pubDate                    => BaseDocument.dates.lastPublished				[Required]
	 *  1.3.4 author                     => BaseDocument.authorKey/bylines (left)			[Required]
	 *  1.3.6 amzn:heroImage             => BaseDocument.taggedImages
	 *  1.3.7 amzn:heroImageCaption      => BaseDocument.taggedImages
	 *
	 *  1.3.5 content:encoded            => Generated HTML from 
	 *  										(AmazonOSPDocument.intro 
	 *  											+ AmazonOSPDocument.productItems 
	 *  												+ AmazonOSPDocument.outro)			[Required]
	 *  1.3.8 amzn:subtitle              => AmazonOSPDocument.subheading
	 *  1.3.9 amzn:introText             => AmazonOSPDocument.summary.description
	 *  1.3.10 amzn:products             => AmazonOSPDocument.productItems					[Required]
	 *  1.3.22 amzn:indexContent         => Hardcode to False on front-end					[Required]
	 *
	 *
	 *  1.3.12 amzn:productURL           => Pulled from InfoCat								[Required]
	 *  1.3.13 amzn:productHeadline      => ProductRecord.headline
	 *  1.3.14 amzn:productSummary       => ProductRecord.quote								[Required]
	 *  1.3.15 amzn:rank                 => To be inferred based on the List Item sequence	[Required]
	 *  1.3.16 amzn:award                => ProductRecord.superlative						[Required]
	 *  1.3.17 amzn:rating
	 *  1.3.18    amzn:ratingValue       => ProductRecord.rating
	 *  1.3.19    amzn:applyToVariants   => ProductRecord.showRating
	 *  1.3.20    amzn:bestRating        => Hardcode to 5
	 *  1.3.21    amzn:worstRating       => Hardcode to 1
	 */
	protected AmazonRssItem itemFromDocument(BaseDocumentEx document, RequestContext requestContext) {
		if(document instanceof AmazonOSPDocumentEx) {
			AmazonOSPDocumentEx amazonOSPDocument = (AmazonOSPDocumentEx) document;
			AmazonRssItem.Builder builder = new AmazonRssItem.Builder();
			
			builder.title(amazonOSPDocument.getTitle());
			builder.link(amazonOSPDocument.getUrl());
			builder.pubDate(amazonOSPDocument.getDates().getLastPublished());
			
			String authorString = getItemAuthorString(amazonOSPDocument);
			if(StringUtils.isBlank(authorString)) return null;
			builder.author(authorString);
			
			builder.heroImage(amazonOSPDocument.getImageForUsage(UsageFlag.PRIMARY)); // use primaryImage instead of bestIntroImage because we don't want the first item image
			
			String heroImgAccreditationString = amazonOSPDocument.getImageForUsage(UsageFlag.PRIMARY).getOwner();
			if (StringUtils.isNotBlank(heroImgAccreditationString)) {
				builder.heroImageCaption(renderUtils.stripOuterTag(heroImgAccreditationString));
			}

			builder.htmlContent(getHtmlContent(amazonOSPDocument.getIntro(), amazonOSPDocument.getOutro(), amazonOSPDocument.getProductItems(), requestContext));
			
			builder.subheading(amazonOSPDocument.getSubheading());

			builder.introText(amazonOSPDocument.getSummary().getDescription());
			
			builder.products(amazonRssProductsFromDocument(amazonOSPDocument));
			
			return builder.build();
		}

		return null;
	}

	/**
	 *	&lt;content:encoded&gt; tag on for an rss item on front-end looks like below:
	 * 
	 * <p>
	 *	{@code
	 *	 <content:encoded>
	 *		<![CDATA[
	 *			<p>Full article HTML markup along with div elements if using product cards Ex: I want my
	 *	 		product card to show below this text so I will add div here. <div data-itemtype="product"><a href="Amazon product
	 *	 		URL"></a></div></p>
	 *		]]>
	 *	</content:encoded>
	 * }
	 * </p>
	 * 
	 *	This method builds the {@code <p>} tag used under [CDDATA] and then renders/returns the html for it
	 * 
	 * @param intro 		Intro content well
	 * @param outro 		Outro content well
	 * @param productItems 	ProductItems that has "product" and "contents" representing product and corresponding content well
	 * @param requestContext 
	 * @return HTML markup for {@code <p>} tag under [CDDATA]
	 */
	private String getHtmlContent(SliceableListEx<AbstractStructuredContentContentEx<?>> intro,
			SliceableListEx<AbstractStructuredContentContentEx<?>> outro, SliceableListEx<ProductItem> productItems, RequestContext requestContext) {
		//p tag is a root element which is being built in this method
		ContainerTag pTag = p();
		
		intro.stream().forEach(block -> buildPTag(block, pTag, requestContext));
		productItems.stream().forEach(productItem -> {
			buildPTag(productItem.getProduct(), pTag, requestContext);
			productItem.getContents().forEach(productItemContentBlock -> buildPTag(productItemContentBlock, pTag, requestContext));
		});
		outro.stream().forEach(block -> buildPTag(block, pTag, requestContext));
		
		//render eventually builds & returns HTML string using StringBuilder underneath
		return pTag.render();
	}

	/**
	 * @param block SC block on the document
	 * @param pTag {@code <p>} tag under [CDDATA] that is being built for &lt;content:encoded&gt; field on RSS 
	 * 
	 * <p> This method builds {@code <p>} tag by adding siblings based on type of SC block </p>
	 * @param requestContext 
	 */
	private void buildPTag(AbstractStructuredContentContentEx<?> block, ContainerTag pTag, RequestContext requestContext) {
		if(block instanceof StructuredContentHtmlEx) {
			pTag.withText(((StructuredContentHtmlEx)block).getData().getHtml());
		}else if(block instanceof StructuredContentHeadingEx) {
			pTag.with(h2(((StructuredContentHeadingEx)block).getData().getText()));
		}else if(block instanceof StructuredContentSubheadingEx) {
			pTag.with(h3(((StructuredContentSubheadingEx)block).getData().getText()));
		}else if(block instanceof StructuredContentImageEx) {
			ImageEx image = ((StructuredContentImageEx)block).getData().getImage();
			String imageAlt = image.getAlt();

			String imageOwner = image.getOwner();
			if (imageOwner != null) {
				imageOwner = renderUtils.stripOuterTag(imageOwner);
			}

			String imageUrl = renderUtils.getThumborUrl(image, image.getWidth(), image.getHeight(), "", false, requestContext, null, null);
			/*
			 	<figure>
					<img src='image.jpg' alt='missing' />
    				<figcaption>Owner goes here</figcaption>
				</figure>
			 */
			if(StringUtils.isNotEmpty(imageUrl)) {
				pTag.with(
						figure()
							.with(img().withSrc(imageUrl).withCondAlt(StringUtils.isNotBlank(imageAlt), imageAlt))
							.condWith(StringUtils.isNotBlank(imageOwner), figcaption(imageOwner))
						);
			}
		}else if(block instanceof StructuredContentProductRecordEx) {
			String amazonProductUrl = ((StructuredContentProductRecordEx) block).getData()
					.getProduct().getRetailerList().get(0).getUrl();
			
			/* <div data-itemtype="product">
					<a href="Amazon product URL"/>
				</div>
			*/	
			pTag.with(
					div().attr("data-itemtype", "product")
						.with(a().withHref(amazonProductUrl))
					);
		}else if(block instanceof StructuredContentVideoBlockEx) {
			StructuredContentVideoBlockEx videoBlock = (StructuredContentVideoBlockEx)block;
			BaseDocumentEx videoDoc = videoBlock.getData().getDocument();

			if (videoDoc != null) {
				TemplateTypeEx videoDocTemplate = videoDoc.getTemplateType();
				/*
					<video src="INSERT VID URL HERE" poster="INSERT SUPPORTED IMAGE FILE URL HERE"></video>
				*/
	
				switch(videoDocTemplate) {
					case JWPLAYERVIDEO:
						JwPlayerVideoDocumentEx jwPlayerVideo = (JwPlayerVideoDocumentEx)videoDoc;
						String vidUrl = jwPlayerVideo.getContentUrl();
						String thumbnailUrl = jwPlayerVideo.getThumbnailUrl();
	
						pTag.with(
							video().withSrc(vidUrl)
							.condAttr(StringUtils.isNotBlank(thumbnailUrl), "poster", thumbnailUrl)
						);
						break;
					default:
						break;
				}
			}
		}
	}

	private String getItemAuthorString(BaseDocumentEx document) {
		//If Guest Author exists use Guest Author; otherwise try to get author from attributions
		if (document.getGuestAuthor() != null && document.getGuestAuthor().getLink() != null &&
				document.getGuestAuthor().getLink().getText() != null) {
			
			return document.getGuestAuthor().getLink().getText();
		} else {
			if (attributionTask != null) {
				List<String> authorNames = attributionTask.fetchAttributionsByDocument(document,
						Collections.singletonList("AUTHOR"))
					.stream()
					.map(attribution -> attribution.getAuthor().getDisplayName())
					.distinct()
					.collect(Collectors.toList());

				return String.join(", ", authorNames);
			} else {
				// AuthorTask used for backwards compatibility
				AuthorEx author = authorTask.fetchAuthorByDocument(document);

				if (author != null && author.getDisplayName() != null) {
					return author.getDisplayName();
				}else {
					return null;
				}
			}
		}
	}

	protected List<AmazonRssProduct> amazonRssProductsFromDocument(AmazonOSPDocumentEx amazonOSPDocument) {
		List<AmazonOSPDocumentEx.ProductItem> productItems = amazonOSPDocument.getProductItems().getList();
		List<AmazonRssProduct> amazonRssProducts = new ArrayList<>();
		
		for(int i=0; i < productItems.size(); i++) {
			amazonRssProducts.add(amazonRssProductFromProductItem(productItems.get(i), i));
		}
		
		return amazonRssProducts;
	}
	
	protected AmazonRssProduct amazonRssProductFromProductItem(AmazonOSPDocumentEx.ProductItem productItem, int productRecordBlockOrder) {
		AmazonRssProduct.Builder builder = new AmazonRssProduct.Builder();
		
		StructuredContentProductRecordEx product = productItem.getProduct();
		
		if (!product.getData().getProduct().getRetailerList().isEmpty()) {
			String url = product.getData().getProduct().getRetailerList().get(0).getUrl();
			builder.url(url);
		}
		
		String award = product.getData().getSuperlative();
		builder.award(award);
		
		builder.summary(product.getData().getQuote());
		builder.headline(product.getData().getHeadline());
		builder.rank(productRecordBlockOrder + 1);

		//Set all rating data
		BigDecimal ratingOnProductRecord = product.getData().getRating();
		if(ratingOnProductRecord != null && ratingOnProductRecord.compareTo(BigDecimal.ZERO) != 0) {
			AmazonRssProduct.Rating rating = new AmazonRssProduct.Rating();
			rating.setRatingValue(ratingOnProductRecord);
			rating.setApplyToVariants(BooleanUtils.isTrue(product.getData().getShowRating()));
			rating.setBestRating(5);
			rating.setWorstRating(1);
			builder.rating(rating);
		}
		
		return builder.build();
	}

	@Task(name = "isAmazonIntervalMode")
	public boolean isIntervalMode() {
		return System.currentTimeMillis() > this.intervalModeStart;
	}

}
