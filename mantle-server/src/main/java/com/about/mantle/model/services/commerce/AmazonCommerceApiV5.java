package com.about.mantle.model.services.commerce;

import static com.about.mantle.model.commerce.CommerceConfiguration.IMAGE_SIZE_LARGE;

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
import com.about.mantle.amazon.productAdvertisingApi.v5.services.AmazonProductAdvertisingApiFacade;
import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.model.commerce.AmazonCommerceModelV5;
import com.about.mantle.model.commerce.CommerceConfiguration;
import com.about.mantle.model.commerce.GenericCommerceModel;
import com.about.mantle.model.extended.docv2.CommerceInfoEx;
import com.about.mantle.model.services.commerce.util.ASINExtractor;
import com.amazon.paapi5.v1.GetItemsResource;

public class AmazonCommerceApiV5 implements CommerceApi {

	private static final String defaultImageSize = IMAGE_SIZE_LARGE;
	private final AmazonProductAdvertisingApiFacade amazonProductSvc;
	private static Logger logger = LoggerFactory.getLogger(AmazonCommerceApiV5.class);

	public AmazonCommerceApiV5(AmazonProductAdvertisingApiFacade amazonProductSvc) {
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
				logger.warn("asin not found on url: " + url);
			}
		}

		Map<String, AmazonCommerceModelV5> amazonResults = amazonProductPriceAndImage(
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
	
	@Override
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers) {
		gatherAndUpdateRetailers(listOfRetailers, defaultImageSize);
	}

	@Override
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers, CommerceConfiguration config) {
		gatherAndUpdateRetailers(listOfRetailers,
				config.getImageSize() != null ? config.getImageSize() : defaultImageSize);
	}

	private void gatherAndUpdateRetailers(List<Retailer> listOfRetailers, String imageSize) {
		Map<String, List<Retailer>> mapOfAsinToRetailer = new HashMap<>();

		for (Retailer retailer : listOfRetailers) {
			String url = retailer.getUrl();
			String asin = ASINExtractor.get(url);
			if (asin != null) {
				if (!mapOfAsinToRetailer.containsKey(asin)) {
					mapOfAsinToRetailer.put(asin, new ArrayList<>());
				}
				mapOfAsinToRetailer.get(asin).add(retailer);
			} else {
				logger.warn("asin not found on url: " + url);
			}
		}

		Map<String, AmazonCommerceModelV5> amazonResults = amazonProductPriceAndImage(
				new ArrayList<String>(mapOfAsinToRetailer.keySet()), imageSize);

		for (String asin : mapOfAsinToRetailer.keySet()) {
			List<Retailer> retailerList = mapOfAsinToRetailer.get(asin);

			retailerList.forEach(retailer -> {
				if (amazonResults.get(asin) != null) {
					retailer.setCommerceModel(amazonResults.get(asin));
				} else { // handles the case where the asin is wrong or the api doesn't find it
					retailer.setCommerceModel(new GenericCommerceModel(retailer.getUrl(), "amazon"));
				}
			});
		}
	}

	/**
	 * This method is used to fetch data for {@link GetItemsResource} from amazon product advertising
	 * API for each itemId. For the full list of resource parameters goto https://webservices.amazon.com/paapi5/documentation/get-items.html#resources-parameter
	 *
	 * @param asins
	 *            List of Asins
	 * @param imageSize
	 *            string for image size (small, medium, or large)
	 * @return a map that contains itemId as key and {@link AmazonCommerceModelV5} as value
	 */
	private Map<String, AmazonCommerceModelV5> amazonProductPriceAndImage(
			@TaskParameter(name = "asins") List<String> asins, @TaskParameter(name = "imageSize") String imageSize) {

		// hardcoding locale = US until we support other locales
		Locale locale = Locale.US;

		Collection<AmazonCommerceModelV5> items = amazonProductSvc.getItems(asins,
				EnumSet.of(GetItemsResource.IMAGES_PRIMARY_LARGE, GetItemsResource.IMAGES_PRIMARY_MEDIUM, GetItemsResource.IMAGES_PRIMARY_SMALL,
						GetItemsResource.OFFERS_LISTINGS_PRICE, GetItemsResource.OFFERS_LISTINGS_CONDITION, GetItemsResource.OFFERS_LISTINGS_DELIVERYINFO_ISPRIMEELIGIBLE,
						GetItemsResource.OFFERS_LISTINGS_SAVINGBASIS),
				locale,
				imageSize);

		Map<String, AmazonCommerceModelV5> answer = items.stream()
				.collect(Collectors.toMap(AmazonCommerceModelV5::getAsin, Function.identity()));

		return answer;
	}
}
