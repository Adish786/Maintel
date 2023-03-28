package com.about.mantle.model.descriptive_taxonomy_terms;

import com.about.hippodrome.models.media.VersionedMediaTypes;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.docv2.MetaDataEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.document.preprocessor.DescriptiveTaxonomyTermPreprocessor;
import com.about.mantle.model.services.impl.DescriptiveTaxonomyTermServiceImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Including this file in our test structure because these are good things to test, but we should not include the
 * @test annotation. These tests rely on an external API and that should not block mantle from building.
 *
 * You will have to add these five lines to {@link DescriptiveTaxonomyTermPreprocessor#hydrateMTaxonomyIds(MetaDataEx, DescriptiveTaxonomyTermParsedData)} to make this work
 * 		// todo: remove these lines because they are just for testing
 * 		if (descriptiveTaxonomyTermService == null) return metaData;
 * 		DescriptiveTaxonomyTermParsedData descriptiveTaxonomyTermData = descriptiveTaxonomyTermService.generateDescriptionTaxonomyTermParsedData();
 * 		if (descriptiveTaxonomyTermData == null) return metaData;
 * 		if (!mapCreatedSuccessfully(descriptiveTaxonomyTermParsedData.getDescriptiveTaxonomyTermMap())) return metaData;
 */
public class DescriptiveTaxonomyTermPreprocessorTest {

    private static DescriptiveTaxonomyTermPreprocessor preprocessor = null;
    private static DescriptiveTaxonomyTermServiceImpl service = null;

    // Uncomment to test
    // @BeforeClass
    public static void setup() {
        HttpServiceClientConfig.Builder httpConfigBuilder = new HttpServiceClientConfig.Builder();
        httpConfigBuilder.setBaseUrl("https://ci.taxonomy.meredith.services/v1");
        httpConfigBuilder.setMediaType(VersionedMediaTypes.DEFAULT_APPLICATION_JSON);

        // Get API key from configs
        String apiKey = "";
        service = new DescriptiveTaxonomyTermServiceImpl(httpConfigBuilder.build(), apiKey);
        preprocessor = new DescriptiveTaxonomyTermPreprocessor(service);
    }

    public void testDescriptiveTaxonomyTermPreprocessor(List<String> startTaxonomyIdList, List<String> expectedMTaxonomyIdList, List<String> expectedADTaxonomyIdList) {
        MetaDataEx metaData = new MetaDataEx();
        metaData.setmTaxonomyIds(SliceableListEx.of(startTaxonomyIdList));

        MetaDataEx newMetaData = preprocessor.hydrateMTaxonomyIds(metaData, service.generateDescriptionTaxonomyTermParsedData());

        List<String> actualMTaxonomyIds = newMetaData.getmTaxonomyIds() != null ? newMetaData.getmTaxonomyIds().getList() : new ArrayList<>();
        List<String> actualADTaxonomyIds = newMetaData.getAdTaxonomyIds()!= null ? newMetaData.getAdTaxonomyIds().getList() : new ArrayList<>();

        Assert.assertTrue("mTaxonomys need to be equal", expectedMTaxonomyIdList.size() == actualMTaxonomyIds.size() && expectedMTaxonomyIdList.containsAll(actualMTaxonomyIds) && actualMTaxonomyIds.containsAll(expectedMTaxonomyIdList));
        Assert.assertTrue("adTaxonomys need to be equal", expectedADTaxonomyIdList.size() == actualADTaxonomyIds.size() && expectedADTaxonomyIdList.containsAll(actualADTaxonomyIds) && actualADTaxonomyIds.containsAll(expectedADTaxonomyIdList));
    }

    // Uncomment to test
    // @Test
    public void testMissingAdgGroup() {
        List<String> startTaxonomyIdList = new ArrayList<>(Arrays.asList(new String[] {"15852"}));
        List<String> expectedMTaxonomyIdList = new ArrayList<>(Arrays.asList(new String[] {"15852","11013","15836"}));
        List<String> expectedADTaxonomyIdList = new ArrayList<>(Arrays.asList(new String[] {"11013","15836"}));
        testDescriptiveTaxonomyTermPreprocessor(startTaxonomyIdList, expectedMTaxonomyIdList, expectedADTaxonomyIdList);
    }

    // Uncomment to test
    // @Test
    public void testLongChain() {
        List<String> startTaxonomyIdList = new ArrayList<>(Arrays.asList(new String[] {"16509"}));
        List<String> expectedADTaxonomyIdList = new ArrayList<>(Arrays.asList(new String[] {"16509","11362","17353","11482","11277","11035","16537"}));
        List<String> expectedMTaxonomyIdList = List.copyOf(expectedADTaxonomyIdList);
        testDescriptiveTaxonomyTermPreprocessor(startTaxonomyIdList, expectedMTaxonomyIdList, expectedADTaxonomyIdList);
    }

    // Uncomment to test
    // @Test
    public void testLongChainAndMissingAdgAndParentInStartList() {
        List<String> startTaxonomyIdList = new ArrayList<>(Arrays.asList(new String[] {"16509","15852","13674","11013"}));
        List<String> expectedADTaxonomyIdList = new ArrayList<>(Arrays.asList(new String[] {"11362","17353","11482","16509","11047","13674","11013","11277","11035","15836","16537"}));
        List<String> expectedMTaxonomyIdList = new ArrayList<>(expectedADTaxonomyIdList);
        expectedMTaxonomyIdList.add(4, "15852");
        testDescriptiveTaxonomyTermPreprocessor(startTaxonomyIdList, expectedMTaxonomyIdList, expectedADTaxonomyIdList);
    }
}
