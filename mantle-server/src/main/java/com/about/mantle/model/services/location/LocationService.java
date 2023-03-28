package com.about.mantle.model.services.location;

import com.about.mantle.model.googleplace.GooglePlaceModel;

public interface LocationService {

    /**
     * Fetches a {@link com.google.maps.model.PlaceDetails} object ID'd with a
     * unique {@code placeId} and returns the associated {@link com.about.mantle.model.googleplace.GooglePlaceModel}.
     * Returns null if there is an {@code Exception} in the API call.
     * @param placeId the place ID of a location in Google Maps.
     * @return {@link com.about.mantle.model.googleplace.GooglePlaceModel} the object tied to the place ID.
     */
    public GooglePlaceModel getGooglePlaceModel(String placeId);
}
