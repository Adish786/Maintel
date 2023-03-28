package com.about.mantle.model.tasks;

import com.about.globe.core.http.GeoData;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.ua.DeviceCategory;
import com.about.globe.core.http.ua.UserAgent;

import com.about.globe.core.model.extended.Configs;
import com.about.hippodrome.models.media.VersionedMediaTypes;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.AuctionFloorConfig;
import com.about.mantle.model.extended.AuctionFloorInfoFactors;
import com.about.mantle.model.extended.AuctionFloorInfoListItem;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.AuctionFloorMappingConfigService;
import com.about.mantle.model.services.AuctionFloorService;
import com.about.mantle.model.services.impl.AuctionFloorMappingConfigServiceImpl;

import com.about.mantle.model.services.impl.AuctionFloorServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuctionFloorTaskTest {

    private static HttpServiceClientConfig.Builder httpConfigBuilder;
    private static AuctionFloorMappingConfigService mockAuctionFloorMappingConfigService;

    private static AuctionFloorService mockAuctionFloorService;

    private static AuctionFloorConfig mockFloorConfig;

    private static RequestContext requestContext;


    @BeforeClass
    public static void setup() throws Exception {
        httpConfigBuilder = new HttpServiceClientConfig.Builder();
        httpConfigBuilder.setMediaType(VersionedMediaTypes.V2_JSON_WITH_REDUCED_WEIGHT);
        httpConfigBuilder.setBaseUrl("http://auction-floor-test");

        mockAuctionFloorListRequestContext();
        requestContext = getMockRequestContext(DeviceCategory.PERSONAL_COMPUTER);

        mockFloorConfig = mockAuctionFloorConfigResponse();
    }

    @Before
    public void setAuctionFloorMappingConfigService() {
        mockAuctionFloorMappingConfigService = Mockito.spy(new AuctionFloorMappingConfigServiceImpl(httpConfigBuilder.build()));
        Mockito.doReturn(mockFloorConfig).when(mockAuctionFloorMappingConfigService).getAuctionFloorConfig();
    }

    @Before
    public void setAuctionFloorService() {
        mockAuctionFloorService = Mockito.spy(new AuctionFloorServiceImpl(httpConfigBuilder.build(), "allrecipes"));
    }

    @Test
    public void testSingleSlotConfig() {
        ArrayList<AuctionFloorInfoListItem> itemList = new ArrayList<>();

        AuctionFloorInfoListItem item = createAuctionFloorInfoListItem("test-id-1", 5, "square-fixed-2");

        itemList.add(item);

        SliceableListEx<AuctionFloorInfoListItem> sliceableList = createAuctionFloorSliceableList(itemList);

        Mockito.doReturn(sliceableList).when(mockAuctionFloorService).getAuctionFloorInfoList(Mockito.any(), Mockito.any());

        AuctionFloorTask task = new AuctionFloorTask(mockAuctionFloorMappingConfigService, mockAuctionFloorService);

        Map<String, Map<String, String>> getAuctionFloors = task.auctionFloors(requestContext);

        assertEquals(getAuctionFloors.get("square-fixed-2").get("floor"), "5");
        assertEquals(getAuctionFloors.get("square-fixed-2").get("id"), "test-id-1");
    }

    @Test
    public void testMultipleSlotConfig() {
        ArrayList<AuctionFloorInfoListItem> itemList = new ArrayList<>();

        AuctionFloorInfoListItem leaderboardFlexListItem = createAuctionFloorInfoListItem("test-id-1", 10, "leaderboard-flex-1");
        AuctionFloorInfoListItem squareFlex1 = createAuctionFloorInfoListItem("test-id-2", 10, "square-flex-1");
        AuctionFloorInfoListItem squareFixed1 = createAuctionFloorInfoListItem("test-id-3", 5, "square-fixed-1");

        itemList.add(leaderboardFlexListItem);
        itemList.add(squareFlex1);
        itemList.add(squareFixed1);

        SliceableListEx<AuctionFloorInfoListItem> sliceableList = createAuctionFloorSliceableList(itemList);

        Mockito.doReturn(sliceableList).when(mockAuctionFloorService).getAuctionFloorInfoList(Mockito.any(), Mockito.any());

        AuctionFloorTask task = new AuctionFloorTask(mockAuctionFloorMappingConfigService, mockAuctionFloorService);

        Map<String, Map<String, String>> getAuctionFloors = task.auctionFloors(requestContext);

        assertEquals(getAuctionFloors.get("leaderboard-flex-1").get("floor"), "10");
        assertEquals(getAuctionFloors.get("leaderboard-flex-1").get("id"), "test-id-1");

        assertEquals(getAuctionFloors.get("square-flex-1").get("floor"), "10");
        assertEquals(getAuctionFloors.get("square-flex-1").get("id"), "test-id-2");

        assertEquals(getAuctionFloors.get("square-fixed-1").get("floor"), "5");
        assertEquals(getAuctionFloors.get("square-fixed-1").get("id"), "test-id-3");
    }


    @Test
    public void fallbackToEmptyFloors() {

        Mockito.doReturn(null).when(mockAuctionFloorMappingConfigService).getAuctionFloorConfig();

        AuctionFloorTask task = new AuctionFloorTask(mockAuctionFloorMappingConfigService, mockAuctionFloorService);

        assertEquals(task.auctionFloors(requestContext), Collections.emptyMap());
    }


    private static UserAgent mockUserAgent(DeviceCategory deviceCategory) {
        UserAgent mockUserAgent = mock(UserAgent.class);
        when(mockUserAgent.getDeviceCategory()).thenReturn(deviceCategory);
        when(mockUserAgent.getOsName()).thenReturn("ios");

        return mockUserAgent;
    }

    private static GeoData mockGeoData() {
        GeoData mockGeoData = mock(GeoData.class);
        when(mockGeoData.getIsoCode()).thenReturn("US");

        return mockGeoData;
    }

    private static Configs mockConfigs() {
        Configs mockConfigs = mock(Configs.class);

        when(mockConfigs.getValue("tier", String.class )).thenReturn("l");
        when(mockConfigs.getValue("vertical", String.class )).thenReturn("allrecipes");
        return mockConfigs;
    }

    private static RequestContext getMockRequestContext(DeviceCategory deviceCategory) {
        RequestContext mockRequestContext = mock(RequestContext.class);
        UserAgent mockUserAgent = mockUserAgent(deviceCategory);
        GeoData mockGeoData = mockGeoData();
        Configs mockConfigs = mockConfigs();

        when(mockRequestContext.getUserAgent()).thenReturn(mockUserAgent);
        when(mockRequestContext.getGeoData()).thenReturn(mockGeoData);
        when(mockRequestContext.getConfigs()).thenReturn(mockConfigs);

        return mockRequestContext;
    }

    private static AuctionFloorService.AuctionFloorListRequestContext mockAuctionFloorListRequestContext() {
        AuctionFloorService.AuctionFloorListRequestContext mockRequestContext = mock(AuctionFloorService.AuctionFloorListRequestContext.class);

        when(mockRequestContext.getVertical()).thenReturn("allrecipes");

        return mockRequestContext;
    }

    private static AuctionFloorConfig mockAuctionFloorConfigResponse() throws Exception {
        String folderPath = "./src/test/resources/tasks/auctionFloorTaskTest";
        String fileName = "floorConfig.json";

        String fullPath = folderPath + File.separator + fileName;
        ObjectMapper objectMapper = new ObjectMapper();

        AuctionFloorConfig response = objectMapper.readValue(new File(fullPath), AuctionFloorConfig.class);

        return response;
    }

    private AuctionFloorInfoListItem createAuctionFloorInfoListItem (String floorId, Integer floorPrice, String slotName) {

        AuctionFloorInfoListItem item = new AuctionFloorInfoListItem();
        AuctionFloorInfoFactors factors = new AuctionFloorInfoFactors();


        item.setFloor(floorPrice);
        item.setId(floorId);
        factors.setSlot(slotName);
        factors.setGeoLocation("us");
        factors.setOs("ios");
        factors.setDeviceCategory("desktop");
        factors.setTier("l");
        factors.setVertical("Allrecipes");

        item.setAuctionFloorFactors(factors);


        return item;
    }

    private SliceableListEx<AuctionFloorInfoListItem> createAuctionFloorSliceableList(List<AuctionFloorInfoListItem> listOfAuctionFloorItems) {
        SliceableListEx<AuctionFloorInfoListItem> auctionFloorSliceableList = new SliceableListEx<>();

        auctionFloorSliceableList.setList(listOfAuctionFloorItems);

        return auctionFloorSliceableList;
    }

}
