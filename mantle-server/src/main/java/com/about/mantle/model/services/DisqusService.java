package com.about.mantle.model.services;

import com.about.mantle.model.disqus.DisqusThreadDetails;
import com.about.mantle.model.disqus.DisqusPost;

import java.util.List;

/**
 * Service for making calls to disqus' api
 */
public interface DisqusService {

    /**
     * Gets the thread details from https://disqus.com/api/3.0/threads/details.json
     * Such as the number of comments and the disqus id for the thread
     *
     * @param docId - the id of the vertical document
     * @return the thread details from the api response
     */
    public DisqusThreadDetails getThreadDetails(Long docId);

    /**
     * Gets the list of popular comments for a given thread, or in dotdash terms document.
     *
     * @param threadId - The internal disqus id, can be obtained from getThreadDetails
     * @param limit - Limit to the number of popular comments pulled back maximum of 100
     * @param interval - Max age of a popular comment that can be returned, Choices: 30s, 1h, 6h, 12h, 1d, 3d,
     *                                                                               7d, 30d, 60d, 90d
     * @return list of popular comments
     */
    public List<DisqusPost> getPopularPosts(String threadId, int limit, String interval);
    
    
    /**
     * Gets comments/posts that includes threads but then filters only for original comments/posts
     * 
     * @param threadId - The internal disqus id, can be obtained from {@linkplain #getThreadDetails(Long)}
     * @param limit - Limit to the number of threaded posts/comments pulled back maximum of 100
     * @param order - Order string e.g. popular
     * @return filtered list of original comments/posts
     * 
     * Note: this is undocumented API refer to AXIS-1210 for more details
     */
    public List<DisqusPost> getOriginalPosts(String threadId, int limit, String order);
}
