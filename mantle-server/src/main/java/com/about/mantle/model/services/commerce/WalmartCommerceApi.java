package com.about.mantle.model.services.commerce;

import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.model.commerce.CommerceConfiguration;
import com.about.mantle.model.commerce.GenericCommerceModel;
import com.about.mantle.model.commerce.WalmartCommerceModel;
import com.about.mantle.model.extended.docv2.CommerceInfoEx;
import com.amazonaws.services.kms.model.UnsupportedOperationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WalmartCommerceApi implements CommerceApi {

    private final WalmartProductLookupApiServiceFacade walmart;
    // http://www.regexplanet.com/share/index.html?share=yyyyya0xt4r
    private static final String ID_REGEX = ".*walmart.com.*/ip/.*/(\\d+).*";
    private static final Pattern regexForWalmartId = Pattern.compile(ID_REGEX);
    private static final CommerceConfiguration defaultCommerceConfig = buildDefaultCommerceConfig();

    private Logger logger = LoggerFactory.getLogger(WalmartCommerceApi.class);

    public WalmartCommerceApi(WalmartProductLookupApiServiceFacade walmart) {
        this.walmart = walmart;
    }

    /**
     * Takes a list of CommerceInfo and calls the subsequent api to gather and update the commerce info this method
     * takes advantage of by reference to update the items in the list.  Defaults size to medium.
     *
     * @param listOfCommerceInfoEx
     */
    @Override
    public void gatherAndUpdateCommerceInfo(List<CommerceInfoEx> listOfCommerceInfoEx) {
        gatherAndUpdateCommerceInfo(listOfCommerceInfoEx, defaultCommerceConfig);
    }

    /**
     * Takes a list of CommerceInfo and calls the subsequent api to gather and update the commerce info this method
     * takes advantage of by reference to update the items in the list. A map of configs can be passed in by the app to
     * open the services for ab testing.
     *
     * @param listOfCommerceInfoEx
     * @param config
     */
    @Override
    public void gatherAndUpdateCommerceInfo(List<CommerceInfoEx> listOfCommerceInfoEx, CommerceConfiguration config) {

        Map<Integer, CommerceInfoEx> mapOfIdsToCommerceInfoEx = new HashMap<>();

        // Only add to the map if we could parse the URL
        for (CommerceInfoEx commerce : listOfCommerceInfoEx) {
            Matcher idMatcher = regexForWalmartId.matcher(commerce.getId());
            if (idMatcher.find()) {
                String idAsStr = idMatcher.group(1);
                int id = -1;
                try {
                    id = Integer.parseInt(idAsStr);
                } catch (NumberFormatException e) {
                    logger.error("ID on Walmart url was not an integer.  URL: " + commerce.getId());
                }

                if (id != -1) {
                    mapOfIdsToCommerceInfoEx.put(id, commerce);
                }

            } else {
                logger.error("ID not found on Walmart url: " + commerce.getId());
            }
        }

        Map<Integer, WalmartCommerceModel> walmartResults = mapOfIdsToCommerceInfoEx.isEmpty() || walmart == null ?
                Collections.emptyMap() :
                walmart.lookupItems(mapOfIdsToCommerceInfoEx.keySet(), config.getImageSize());

        for (Integer id : mapOfIdsToCommerceInfoEx.keySet()) {
            CommerceInfoEx commerceInfo = mapOfIdsToCommerceInfoEx.get(id);

            if (walmartResults.get(id) != null) {
                commerceInfo.setCommerceModel(walmartResults.get(id));
            } else { // handles the case where the id is wrong or the api doesn't find it
                commerceInfo.setCommerceModel(new GenericCommerceModel(commerceInfo.getId(), "walmart"));
            }
        }
    }

    private static CommerceConfiguration buildDefaultCommerceConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put(CommerceConfiguration.CONFIG_IMAGE_SIZE, CommerceConfiguration.IMAGE_SIZE_MEDIUM);
        return new CommerceConfiguration(configMap);
    }

	@Override
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers) {
		throw new UnsupportedOperationException("Walmart API is not supported by InfoCat");	
	}

	@Override
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers, CommerceConfiguration config) {
		throw new UnsupportedOperationException("Walmart API is not supported by InfoCat");	
	}

}
