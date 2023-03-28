package com.about.mantle.model.services.impl;

import com.about.globe.core.http.GeoData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OneTrustConsentBannerServiceTest {

    @Test
    public void getStateCodeFromGeoDataTest_InUS_InCA() {

        // when the country is US and the state is "CA"

        //given
        GeoData geoData = new GeoData("US", "CA", false);
        //when
        String expectedStateCode = "CA";
        String actualStateCode = OneTrustConsentBannerService.getStateCodeFromGeoData(geoData);
        //then
        assertEquals(expectedStateCode, actualStateCode);
    }

    @Test
    public void getStateCodeFromGeoDataTest_InUS_NotCA() {

        // when the country is US and the state is not "CA"

        //given
        GeoData geoData = new GeoData("US", "TX", false);
        //when
        String expectedStateCode = "TX";
        String actualStateCode = OneTrustConsentBannerService.getStateCodeFromGeoData(geoData);
        //then
        assertEquals(expectedStateCode, actualStateCode);
    }

    @Test
    public void getStateCodeFromGeoDataTest_InUS_UnknownState() {

        // test for when the state is not known (i.e. "?")

        //given
        GeoData geoData = new GeoData("US", "?", false);
        //when
        String expectedStateCode = "CA";
        String actualStateCode = OneTrustConsentBannerService.getStateCodeFromGeoData(geoData);
        //then
        assertEquals(expectedStateCode, actualStateCode);
    }

    @Test
    public void getStateCodeFromGeoDataTest_NotUS_InEU() {

        // test for when the country is EU and the state is not known (i.e. "?")

        //given
        GeoData geoData = new GeoData("ES", "?", true);
        //when
        String expectedStateCode = null;
        String actualStateCode = OneTrustConsentBannerService.getStateCodeFromGeoData(geoData);
        //then
        assertEquals(expectedStateCode, actualStateCode);
    }

    @Test
    public void getStateCodeFromGeoDataTest_NotUS_NotEU() {

        // test for when the country is not US or EU

        //given
        GeoData geoData = new GeoData("CA", "ON", false);
        //when
        String expectedStateCode = null;
        String actualStateCode = OneTrustConsentBannerService.getStateCodeFromGeoData(geoData);
        //then
        assertEquals(expectedStateCode, actualStateCode);
    }




}
