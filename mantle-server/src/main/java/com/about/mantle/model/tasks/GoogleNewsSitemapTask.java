package com.about.mantle.model.tasks;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.TaggedImage.UsageFlag;
import com.about.mantle.model.news.sitemap.google.GoogleNews;
import com.about.mantle.model.news.sitemap.google.GoogleNews.Publication;
import com.about.mantle.model.news.sitemap.google.GoogleNewsItem;
import com.about.mantle.model.services.news.GoogleNewsSitemapService;

/**
 * 
 * See GLBE-7371 for the google news sitemap specs
 *
 */
@Tasks
public class GoogleNewsSitemapTask {

	private static final Logger logger = LoggerFactory.getLogger(GoogleNewsSitemapTask.class);
	
	private final GoogleNewsSitemapService googleNewsSitemapService;
	
	private final String domain;
	private final String siteName;
	
	private boolean isGoogleNewsSitemapEnabled;

	public GoogleNewsSitemapTask(GoogleNewsSitemapService googleNewsSitemapService, String siteName, String domain, boolean isGoogleNewsSitemapEnabled) {
		this.googleNewsSitemapService = googleNewsSitemapService;
		this.isGoogleNewsSitemapEnabled = isGoogleNewsSitemapEnabled;
		this.siteName = siteName;
		this.domain = domain;
	}

	@Task(name = "googleNewsSitemap")
	public List<GoogleNewsItem> googleNewsSitemap() {

		List<GoogleNewsItem> answer = new ArrayList<>();

		if (isGoogleNewsSitemapEnabled) {
			
			List<BaseDocumentEx> newsDocuments = googleNewsSitemapService.getGoogleNewsDocuments(domain);

			if(newsDocuments != null && !newsDocuments.isEmpty()) {
				answer = getGoogleNews(newsDocuments, domain);	
			}
		}
		return answer;
	}
	
	@Task(name = "getNewsItemFromDocument")
	public GoogleNewsItem getNewsItemFromDocument(@TaskParameter(name = "document") BaseDocumentEx document, @TaskParameter(name = "domain") String domain) {

		DateTime firstPublished = document.getDates().getFirstPublished();
		DateTime displayDate = document.getDates().getDisplayed();
		Publication publication = new Publication(siteName, "en");
		String title = document.getTitle();
		GoogleNews news = new GoogleNews(firstPublished, title, publication, displayDate);

		String url = document.getUrl();
		ImageEx image = getImage(document);

		GoogleNewsItem newsItem = new GoogleNewsItem(url, news, image);
		return newsItem;
	}

	private List<GoogleNewsItem> getGoogleNews(List<BaseDocumentEx> newsDocs, String domain) {
		List<GoogleNewsItem> answer = new ArrayList<>();
		for (BaseDocumentEx doc : newsDocs) {
			try {
				GoogleNewsItem newsItem = getNewsItemFromDocument(doc, domain);
				answer.add(newsItem);
			} catch (Exception e) {
				logger.error(String.format("Error occured while adding news item to Google News Sitemap for docId %s", doc.getDocumentId()), e);
			}
		}
		return answer;
	}

	private ImageEx getImage(BaseDocumentEx document) {
		ImageEx recircImage = document.getImageForUsage("RECIRC");
		recircImage = (recircImage != null || !ImageEx.EMPTY.equals(recircImage)) ? recircImage
				: document.getImageForUsage(UsageFlag.PRIMARY);
		return recircImage;
	}

}
