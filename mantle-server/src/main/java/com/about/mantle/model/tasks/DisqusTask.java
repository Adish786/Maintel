package com.about.mantle.model.tasks;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.disqus.DisqusPost;
import com.about.mantle.model.disqus.DisqusThreadDetails;
import com.about.mantle.model.services.DisqusService;

/**
 * Gets models for the vertical from the disqus api
 */
@Tasks
public class DisqusTask {

    private final DisqusService disqusService;
    private final String defaultMaxAgeForPopularComments;
    private final int defaultMaxLimitForPopularComments; //Max limit is 100
    
    private static final int MAX_LIMIT = 100;

    /**
     * @param disqusService - service for making disqus api calls
     * @param defaultMaxAgeForPopularComments - Default max age for popular comments
     *                  Options: 30s, 1h, 6h, 12h, 1d, 3d, 7d, 30d, 60d, 90d
     * @param defaultMaxLimitForPopularComments - Default maximum number of popular comments that can come back, absolute max is 100
     */
    public DisqusTask(DisqusService disqusService,  String defaultMaxAgeForPopularComments, int defaultMaxLimitForPopularComments){
        this.disqusService = disqusService;
        this.defaultMaxAgeForPopularComments = defaultMaxAgeForPopularComments;
        this.defaultMaxLimitForPopularComments = defaultMaxLimitForPopularComments;
    }

    @Task(name = "disqusThreadDetails")
    public DisqusThreadDetails getDisqusThreadDetails(@TaskParameter(name = "docId") Long docId){
        return disqusService.getThreadDetails(docId);
    }
    
    /**
     * @param docId Selene docId
     * @param numberOfOriginalComments required number of original comments 
     * @return list of original comments (excluding threads)
     */
    @Task(name = "disqusOriginalComments")
    public List<DisqusPost> getOriginalComments(@TaskParameter(name = "docId") Long docId, @TaskParameter(name = "numberOfOriginalComments") Integer numberOfOriginalComments) {
    	DisqusThreadDetails disqusThreadDetails = disqusService.getThreadDetails(docId);
    	
    	if(disqusThreadDetails == null) return null;
    	
    	//Here we are requesting with max limit as we don't know if it can suffice requested numberOfOriginalComments with any lesser limits 
    	List<DisqusPost> originalComments = disqusService.getOriginalPosts(disqusThreadDetails.getId(), MAX_LIMIT, "popular");
    	
    	List<DisqusPost> topNOriginalComments = Collections.emptyList();
    	if(originalComments != null) {
    		topNOriginalComments = originalComments.stream()
    	    		.limit(numberOfOriginalComments)
    	    		.collect(Collectors.toList());
    	}
    	
    	return topNOriginalComments;
    }

}
