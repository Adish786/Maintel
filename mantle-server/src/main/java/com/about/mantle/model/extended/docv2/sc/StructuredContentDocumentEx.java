package com.about.mantle.model.extended.docv2.sc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.liveblogpost.LiveBlogPost;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A document made up of only Structured Content blocks.
 */
public class StructuredContentDocumentEx extends StructuredContentBaseDocumentEx {
	private static final long serialVersionUID = 1L;

	private SliceableListEx<StructuredContentPageEx> pages;

	private LiveBlogPost liveBlogPost;

	/**
     * Returns all of the 'pages' in the structured document.  Currently CMS supports only
     * one page per document, so it's recommended that you use the `getContents` method
     * which will flatten the page for you.
     *
     * @return
     */
    public SliceableListEx<StructuredContentPageEx> getPages() {
        return pages;
    }

    public void setPages(SliceableListEx<StructuredContentPageEx> pages) {
        this.pages = pages;
    }

	public LiveBlogPost getLiveBlogPost() {
		return liveBlogPost;
	}

	public void setLiveBlogPost(LiveBlogPost liveBlogPost) {
		this.liveBlogPost = liveBlogPost;
	}

    /**
     * Convenience method that returns all content from all pages in the correct order.  Filters out contents that do not contain
     * any data.  Note that by using this you'll lose any page-related metadata, however as of now since there is only
     * one page per document this will not be an issue.
     *
     * @return
     */
	@JsonIgnore
	@Override
	@SuppressWarnings("rawtypes")
    public Stream<AbstractStructuredContentContentEx<?>> getContentsStream() {

		Stream<AbstractStructuredContentContentEx<?>> answer;

		if (pages != null) {

			answer = pages.stream()
					.flatMap(structuredContentPageEx -> structuredContentPageEx.getContents().stream())
					.filter(abstractStructuredContentContentEx -> abstractStructuredContentContentEx.getData() != null);

		} else {
			answer = Stream.empty();
		}

		return answer;
    }

	@SuppressWarnings("rawtypes")
	@JsonIgnore
	public List<AbstractStructuredContentContentEx> getContents() {
		return pages.stream()
				.flatMap(structuredContentPageEx -> structuredContentPageEx.getContents().stream())
				.filter(abstractStructuredContentContentEx -> abstractStructuredContentContentEx.getData() != null).collect(Collectors.toList());
	}


    @Override
    public String toString() {
        return "StructuredContentDocumentEx{parent=" + super.toString() + "}";
    }

}
