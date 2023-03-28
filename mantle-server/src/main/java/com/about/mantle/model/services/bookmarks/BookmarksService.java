package com.about.mantle.model.services.bookmarks;

import com.about.mantle.model.extended.bookmarks.BookmarksRequestContext;

/**
 * Used for the "Save" feature on Carbon V2 (e.g. allrecipes) which allows
 * users to save pages to their user profile.
 */
public interface BookmarksService {

	boolean isBookmarked(BookmarksRequestContext reqCtx);

	boolean deleteBookmark(BookmarksRequestContext reqCtx);

	boolean saveBookmark(BookmarksRequestContext reqCtx);

}
