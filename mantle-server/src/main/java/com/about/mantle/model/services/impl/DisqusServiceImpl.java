package com.about.mantle.model.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.model.disqus.DisqusPost;
import com.about.mantle.model.disqus.DisqusThreadDetails;
import com.about.mantle.model.disqus.DisqusThreadDetailsResponse;
import com.about.mantle.model.disqus.DisqusThreadPopularPostsResponse;
import com.about.mantle.model.services.DisqusService;


public class DisqusServiceImpl implements DisqusService {
    public final static String THREAD_API = "https://disqus.com/api/3.0/threads/details.json";
    public final static String POPULAR_API = "https://disqus.com/api/3.0/posts/listPopular.json";
    public final static String THREADED_POSTS_API = "https://disqus.com/api/3.0/threads/listPostsThreaded.json";
    private final String apiKey;
    private final String forumName;

    // This client will be used for all requests to Disqus
    private static final Client client = ClientBuilder.newClient();

    private static final Logger logger = LoggerFactory.getLogger(DisqusServiceImpl.class);

    /**
     * DisqusServiceImpl
     *
     * @param apiKey    - The Disqus API Key, can be found in the dotdash@dotdash.com admin account for disqus
     * @param forumName - All sites with disqus are forums, forum name is not a 1 to 1 with the vertical name,
     *                  individual pages are referred to as threads.
     */
    public DisqusServiceImpl(String apiKey, String forumName) {
        this.apiKey = apiKey;
        this.forumName = forumName;
    }

    /**
     * Gets the thread details from https://disqus.com/api/3.0/threads/details.json
     * Such as the number of comments and the disqus id for the thread
     *
     * @param docId - the id of the vertical document
     * @return the thread details from the api response
     */
    @Override
    public DisqusThreadDetails getThreadDetails(Long docId) {

        Response response = null;
        try {
            WebTarget webTarget = buildWebTarget(THREAD_API, "ident:" + docId.toString());

            response = webTarget.request().get();

            DisqusThreadDetailsResponse threadDetailsResponse = response.readEntity(DisqusThreadDetailsResponse.class);

            return threadDetailsResponse.getResponse();

        } catch (Exception e) {
            logger.error("Failed to read count response", e);
            return null;
        } finally {
            if (response != null) response.close();
        }
    }

    /**
     * Gets the list of popular comments for a given thread, or in dotdash terms document.
     *
     * @param threadId - The internal disqus id, can be obtained from getThreadDetails
     * @param limit    - Limit to the number of popular comments pulled back maximum of 100
     * @param interval - Max age of a popular comment that can be returned, Choices: 30s, 1h, 6h, 12h, 1d, 3d,
     *                 7d, 30d, 60d, 90d
     * @return list of popular comments
     */
    @Override
    public List<DisqusPost> getPopularPosts(String threadId, int limit, String interval) {
        Response response = null;
        try {
            WebTarget webTarget = buildWebTarget(POPULAR_API, threadId)
                    .queryParam("limit", limit)
                    .queryParam("interval", interval);

            response = webTarget.request().get();

            DisqusThreadPopularPostsResponse popularPostsResponse = response.readEntity(DisqusThreadPopularPostsResponse.class);

            return popularPostsResponse.getResponse();

        } catch (Exception e) {
            logger.error("Failed to read popular comment response", e);
            return null;
        } finally {
            if (response != null) response.close();
        }
    }

    //Used to build webTarget so we can avoid having mostly the same code replicated between the api calls
    private WebTarget buildWebTarget(String disqusUrl, String thread) {

        return client.target(disqusUrl).queryParam("api_key", apiKey).queryParam("forum", forumName).queryParam("thread", thread);
    }

    @Override
    public List<DisqusPost> getOriginalPosts(String threadId, int limit, String order) {
        Response response = null;
        try {
            WebTarget webTarget = buildWebTarget(THREADED_POSTS_API, threadId)
                    .queryParam("limit", limit)
                    .queryParam("order", order);

            logger.info("Disqus request Uri to get threaded posts/comments: {}", webTarget.getUri());

            response = webTarget.request().get();

            DisqusThreadPopularPostsResponse popularPostsResponse = response.readEntity(DisqusThreadPopularPostsResponse.class);

            return popularPostsResponse.getResponse() != null ? popularPostsResponse.getResponse()
                    .stream().filter(disqusPost -> disqusPost != null && disqusPost.getParent() == null)
                    .collect(Collectors.toList()) : null;

        } catch (Exception e) {
            logger.error("Failed to get lists of threaded posts from API url: {}", THREADED_POSTS_API, e);
            return null;
        } finally {
            if (response != null) response.close();
        }
    }
}
