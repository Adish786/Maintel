package com.about.mantle.model.services.location;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.model.googleplace.GooglePlaceModel;
import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest.FieldMask;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.AddressType;

public class LocationServiceImpl implements LocationService{
   
    private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);
    private final GeoApiContext context;

    public LocationServiceImpl(String apiKey) {
        this.context = isBlank(apiKey) ? null : new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    @Override
    public GooglePlaceModel getGooglePlaceModel(String placeId) {
        return buildModelFromPlaceDetails(getPlaceDetails(placeId));
    }

    /**
     * Builds a {@link com.about.mantle.model.googleplace.GooglePlaceModel} using a
     * {@link com.google.maps.model.PlaceDetails} object from the Google Maps API Client.
     * Returns {@code null} if the response is null, indicating there was an {@code Exception}.
     * @param response {@link com.google.maps.model.PlaceDetails} response
     * @return {@link com.about.mantle.model.googleplace.GooglePlaceModel} object
     */
    private GooglePlaceModel buildModelFromPlaceDetails(PlaceDetails response) {
        GooglePlaceModel placeDetails = null;

        if(response != null) {
            placeDetails = new GooglePlaceModel.Builder()
                    .formattedAddress(Optional.ofNullable(response.formattedAddress).orElse(""))
                    .adrAddress(Optional.ofNullable(response.adrAddress).orElse(""))
                    .phone(Optional.ofNullable(response.internationalPhoneNumber).orElse(""))
                    .website(response.website != null ? response.website.toString() : "")
                    .url(response.url.toString())
                    .placeId(response.placeId)
                    .lat(response.geometry.location.lat)
                    .lng(response.geometry.location.lng)
					.name(StringUtils.isNotBlank(response.name) ? response.name : "")
                    .types(Optional.ofNullable(response.types).orElse(new AddressType[0]))
                    .build();
        }
        
        return placeDetails;
    }

    /**
     * Ensures that the service has an API key for Google Maps before having the
     * client make an API call
     */
    private void ensureContext() {
        if(this.context == null) {
            throw new GlobeException("No API key found for Google Maps");
        }
    }

    /**
     * Fetch the details of a location based on its {@code placeId} using the 
     * Google Maps API client and return the associated {@link com.google.maps.model.PlaceDetails}
     * object.
     * @param  placeId the place ID of a location in Google Maps.
     * @return {@link com.google.maps.model.PlaceDetails} the details of the place.
     */
    private PlaceDetails getPlaceDetails(String placeId) {
        PlaceDetails response = null;

        ensureContext();

        try {
            response = PlacesApi.placeDetails(this.context, placeId)
                    .fields(
                        FieldMask.FORMATTED_ADDRESS,
                        FieldMask.ADR_ADDRESS,
                        FieldMask.INTERNATIONAL_PHONE_NUMBER,
                        FieldMask.WEBSITE,
                        FieldMask.URL,
                        FieldMask.PLACE_ID,
                        FieldMask.GEOMETRY,
                        FieldMask.NAME,
                        FieldMask.TYPES)
                    .await();
        } catch(Exception e) {
            logger.error("Failed to fetch Google Place ID Details for " + placeId, e);
        }

        return response;
    }
}
