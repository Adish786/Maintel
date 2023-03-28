package com.about.mantle.model.services.commerce;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.task.annotation.TaskParameter;
import com.about.mantle.amazon.productAdvertisingApi.model.ResponseGroup;
import com.about.mantle.amazon.productAdvertisingApi.services.AmazonProductAdvertisingApiFacade;
import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.model.commerce.AmazonCommerceModel;
import com.about.mantle.model.commerce.CommerceConfiguration;
import com.about.mantle.model.commerce.GenericCommerceModel;
import com.about.mantle.model.extended.docv2.CommerceInfoEx;
import com.about.mantle.model.services.commerce.util.ASINExtractor;
import com.amazonaws.services.kms.model.UnsupportedOperationException;

import static com.about.mantle.model.commerce.CommerceConfiguration.IMAGE_SIZE_LARGE;

public class AmazonCommerceApi implements CommerceApi {

	private static final String defaultImageSize = IMAGE_SIZE_LARGE;
	private final AmazonProductAdvertisingApiFacade amazonProductSvc;
	private static Logger logger = LoggerFactory.getLogger(AmazonCommerceApi.class);

	public AmazonCommerceApi(AmazonProductAdvertisingApiFacade amazonProductSvc) {
		this.amazonProductSvc = amazonProductSvc;
	}

	@Override
	public void gatherAndUpdateCommerceInfo(List<CommerceInfoEx> listOfCommerceInfoEx) {
		gatherAndUpdateCommerceInfo(listOfCommerceInfoEx, defaultImageSize);
	}

	@Override
	public void gatherAndUpdateCommerceInfo(List<CommerceInfoEx> listOfCommerceInfoEx, CommerceConfiguration config) {
		gatherAndUpdateCommerceInfo(listOfCommerceInfoEx,
				config.getImageSize() != null ? config.getImageSize() : defaultImageSize);
	}

	private void gatherAndUpdateCommerceInfo(List<CommerceInfoEx> listOfCommerceInfoEx, String imageSize) {
		Map<String, List<CommerceInfoEx>> mapOfAsinToCommerceInfo = new HashMap<>();

		for (CommerceInfoEx commerce : listOfCommerceInfoEx) {
			String url = commerce.getId();
			String asin = ASINExtractor.get(url);
			if (asin != null) {
				if (!mapOfAsinToCommerceInfo.containsKey(asin)) {
					mapOfAsinToCommerceInfo.put(asin, new ArrayList<>());
				}
				mapOfAsinToCommerceInfo.get(asin).add(commerce);
			} else {
				logger.error("asin not found on url: " + url);
			}
		}

		Map<String, AmazonCommerceModel> amazonResults = amazonProductPriceAndImage(
				new ArrayList<String>(mapOfAsinToCommerceInfo.keySet()), imageSize);

		for (String asin : mapOfAsinToCommerceInfo.keySet()) {
			List<CommerceInfoEx> commerceInfoList = mapOfAsinToCommerceInfo.get(asin);

			commerceInfoList.forEach(commerceInfo -> {
				if (amazonResults.get(asin) != null) {
					commerceInfo.setCommerceModel(amazonResults.get(asin));
				} else { // handles the case where the asin is wrong or the api doesn't find it
					commerceInfo.setCommerceModel(new GenericCommerceModel(commerceInfo.getId(), "amazon"));
				}
			});
		}
	}

	/**
	 * This method is used to fetch data for {@link ResponseGroup} OFFERS, and IMAGES from amazon product advertising
	 * API for each itemIds
	 *
	 * @param asins
	 *            List of Asins
	 * @param imageSize
	 *            string for image size (small, medium, or large)
	 * @return a map that contains itemId as key and {@link AmazonCommerceModel} as value
	 */
	private Map<String, AmazonCommerceModel> amazonProductPriceAndImage(
			@TaskParameter(name = "asins") List<String> asins, @TaskParameter(name = "imageSize") String imageSize) {

		// hardcoding locale = US until we support other locales
		Locale locale = Locale.US;

		Collection<AmazonCommerceModel> items = amazonProductSvc.lookupItems(asins,
				EnumSet.of(ResponseGroup.ITEMATTRIBUTES, ResponseGroup.OFFERFULL, ResponseGroup.IMAGES), locale,
				imageSize);

		Map<String, AmazonCommerceModel> answer = items.stream()
				.collect(Collectors.toMap(AmazonCommerceModel::getAsin, Function.identity()));

		return answer;
	}

	@Override
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers) {
		throw new UnsupportedOperationException("Amazon Commerce API is not supported by InfoCat use V5");	
	}

	@Override
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers, CommerceConfiguration config) {
		throw new UnsupportedOperationException("Amazon Commerce API is not supported by InfoCat use V5");	
	}
}
