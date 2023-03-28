package com.about.mantle.amazon.productAdvertisingApi.tasks;

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
import com.about.mantle.amazon.productAdvertisingApi.model.ResponseGroup;
import com.about.mantle.amazon.productAdvertisingApi.services.AmazonProductAdvertisingApiFacade;
import com.about.mantle.model.commerce.AmazonCommerceModel;

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
	 * This method is used to fetch data for {@link ResponseGroup} OFFERS, and IMAGES from amazon product advertising
	 * API for each itemIds
	 *
	 * @param itemIds
	 *            comma separated item Ids
	 * @param imageSize
	 *            string for image size (small, medium, or large)
	 * @return a map that contains itemId as key and {@link AmazonCommerceModel} as value
	 */
	@Task(name = "retrieveAmazonProductPriceAndImage")
	public Map<String, AmazonCommerceModel> amazonProductPriceAndImage(@TaskParameter(name = "itemIds") String itemIds,
			@TaskParameter(name = "imageSize") String imageSize) {

		List<String> asins = Arrays.asList(StringUtils.split(itemIds, " ,"));

		// hardcoding locale = US until we support other locales
		Locale locale = Locale.US;

		Collection<AmazonCommerceModel> items = amazonProductSvc.lookupItems(asins,
				EnumSet.of(ResponseGroup.ITEMATTRIBUTES, ResponseGroup.OFFERFULL, ResponseGroup.IMAGES), locale,
				imageSize);

		Map<String, AmazonCommerceModel> answer = items.stream()
				.collect(Collectors.toMap(AmazonCommerceModel::getAsin, Function.identity()));

		return answer;
	}
}
