package com.about.mantle.model.extended;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public class AuthorArticlesEx {

	private final AuthorEx author;
	private final SliceableListEx<BaseDocumentEx> articles;

	public AuthorArticlesEx(AuthorEx author, SliceableListEx<BaseDocumentEx> articles) {
		this.author = author;
		this.articles = articles;
	}

	public AuthorEx getAuthor() {
		return author;
	}

	public SliceableListEx<BaseDocumentEx> getArticles() {
		return articles;
	}

}
