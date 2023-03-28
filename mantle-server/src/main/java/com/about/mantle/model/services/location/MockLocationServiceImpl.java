package com.about.mantle.model.services.location;

import com.about.mantle.model.googleplace.GooglePlaceModel;
import com.google.maps.model.AddressType;

public class MockLocationServiceImpl implements LocationService{

    /**
     * Returns a mock {@link com.about.mantle.model.googleplace.GooglePlaceModel}
     * for when there is no {@code com.about.globe.googlemaps.api.apiKey} found.
     * @param placeId the place ID of a location in Google Maps.
     * @return {@link com.about.mantle.model.googleplace.GooglePlaceModel} a mock location.
     */
    @Override
    public GooglePlaceModel getGooglePlaceModel(String placeId) {
        return new GooglePlaceModel.Builder()
                .formattedAddress("Times Square Studios, 1500 Broadway, New York, NY 10036, USA")
				.adrAddress(
						"\\u003cspan class=\\\"extended-address\\\"\\u003eTimes Square Studios\\u003c/span\\u003e, \\u003cspan class=\\\"street-address\\\"\\u003e1500 Broadway\\u003c/span\\u003e, \\u003cspan class=\\\"locality\\\"\\u003eNew York\\u003c/span\\u003e, \\u003cspan class=\\\"region\\\"\\u003eNY\\u003c/span\\u003e \\u003cspan class=\\\"postal-code\\\"\\u003e10036\\u003c/span\\u003e, \\u003cspan class=\\\"country-name\\\"\\u003eUSA\\u003c/span\\u003e")
                .phone("212.204.1496")
                .website("https://www.dotdash.com/")
                .url("https://maps.google.com/?cid=1455335883113928959")
                .placeId(placeId)
                .lat(40.75676439999999d)
                .lng(-73.98571919999999d)
                .name("The Times Square Studios")
                .types(new AddressType[]{AddressType.HAIR_CARE})
                .build();
    }
}
