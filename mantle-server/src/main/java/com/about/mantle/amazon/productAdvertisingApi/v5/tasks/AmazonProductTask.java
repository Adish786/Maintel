package com.about.mantle.amazon.productAdvertisingApi.v5.tasks;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.amazon.productAdvertisingApi.v5.services.AmazonProductAdvertisingApiFacade;
import com.about.mantle.model.commerce.AmazonCommerceModel;
import com.about.mantle.model.commerce.AmazonCommerceModelV5;
import com.amazon.paapi5.v1.GetItemsResource;

/**
 * This class responsible for doing operations on Amazon Products
 */
@Deprecated  // Remove this as a part of GLBE-5916
@Tasks
public class AmazonProductTask {

	private static Logger logger = LoggerFactory.getLogger(AmazonProductTask.class);

	private final AmazonProductAdvertisingApiFacade amazonProductSvc;

	/**
	 * Creates the AmazonProductTask
	 *
	 * @param amazonProductService
	 */
	public AmazonProductTask(AmazonProductAdvertisingApiFacade amazonProductService) {
		amazonProductSvc = amazonProductService;
	}

	/**
	 * This method is used to fetch data for {@link GetItemsResource} from amazon product advertising
	 * API for each itemIds. For the full list of resource parameters goto https://webservices.amazon.com/paapi5/documentation/get-items.html#resources-parameter
	 *
	 *
	 * @param itemIds
	 *            comma separated item Ids
	 * @param imageSize
	 *            string for image size (small, medium, or large)
	 * @return a map that contains itemId as key and {@link AmazonCommerceModel} as value
	 */
	@Task(name = "retrieveAmazonProductPriceAndImage")
	public Map<String, AmazonCommerceModelV5> amazonProductPriceAndImage(@TaskParameter(name = "itemIds") String itemIds,
			@TaskParameter(name = "imageSize") String imageSize) {

		List<String> asins = Arrays.asList(StringUtils.split(itemIds, " ,"));

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
