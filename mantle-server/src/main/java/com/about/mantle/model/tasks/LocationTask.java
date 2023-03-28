package com.about.mantle.model.tasks;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.services.location.LocationService;
import com.about.mantle.model.googleplace.GooglePlaceModel;

@Tasks
public class LocationTask {

    private LocationService service;

    public LocationTask(LocationService service) {
        this.service = service;
    }

	/**
	 * For a given {@code placeId}, fetch comprehensive place information provided by the
	 * Google Places API. The retrieved information is mapped into a
     * {@link com.about.mantle.model.googleplace.GooglePlaceModel} object and returned.
     * Returns null if there is an {@code Exception} in the API call.
     *
     * @param placeId the place ID of a location in Google Maps.
     * @return {@link com.about.mantle.model.googleplace.GooglePlaceModel} the object tied to the {@code placeId}.
	 */
	@Task(name = "getLocationData")
	@TimedComponent(category = "task")
	public GooglePlaceModel getLocationData(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "placeId") String placeId) {
        return service.getGooglePlaceModel(placeId);
	}
}
