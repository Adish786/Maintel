package com.about.mantle.model.services.commerce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.model.commerce.CommerceConfiguration;
import com.about.mantle.model.extended.docv2.CommerceInfoEx;
import org.slf4j.MDC;

/**
 * Service for handling commerce methods.
 */
public class CommerceServiceImpl implements CommerceService {

	private CommerceMappingService commerceMappingService;
	private Logger logger = LoggerFactory.getLogger(CommerceService.class);
	private ExecutorService executorService;
	
	public CommerceServiceImpl(CommerceMappingService commerceMappingService, ExecutorService executorService) {
		this.commerceMappingService = commerceMappingService;
		this.executorService = executorService;
	}

	@Override
	public List<List<CommerceInfoEx>> getCommerceModel(List<List<CommerceInfoEx>> listOfListCommerceInfo,
			Map<String, String> config) {

		Map<String, List<CommerceInfoEx>> mapOfApiToCommerceInfo = new HashMap<>();

		// Build Mapping of Api to CommerceInfo Items
		listOfListCommerceInfo.forEach(commerceInfoAtI -> commerceInfoAtI.forEach(commerceInfoEx -> {
			if (!mapOfApiToCommerceInfo.containsKey(commerceInfoEx.getType())
					&& StringUtils.isNotBlank(commerceInfoEx.getType())) {
				mapOfApiToCommerceInfo.put(commerceInfoEx.getType(), new ArrayList<>());
			}

			// Protection against cms convention where "" type objects are added
			if (StringUtils.isNotBlank(commerceInfoEx.getType()))
				mapOfApiToCommerceInfo.get(commerceInfoEx.getType()).add(commerceInfoEx);
		}));

		CommerceConfiguration configuration = new CommerceConfiguration(config);

		// For all of the types call the commerce mapping service to get the wrappers and run them on the lists
		if (executorService == null) {
			Map<String, String> mdc = MDC.getCopyOfContextMap();
			mapOfApiToCommerceInfo.entrySet().parallelStream().forEach(entry -> {
			    MDC.setContextMap(mdc);
				try {
					commerceMappingService.getService(entry.getKey()).gatherAndUpdateCommerceInfo(entry.getValue(),
							configuration);
				} catch (Exception e) {
					logger.error("Error running api - " + entry + " with error: ", e);
				}
			});
		} else {
			mapOfApiToCommerceInfo.entrySet().stream().map(e -> {
				return CompletableFuture.runAsync(() -> {
					commerceMappingService.getService(e.getKey()).gatherAndUpdateCommerceInfo(e.getValue(),
							configuration);
				},executorService).handle( (v,ex) -> {
					if (ex != null) {
						logger.error("Error running api - " + e + " with error: ", ex);
					}
					return v;
				});
			}).collect(Collectors.toList()).forEach(f -> f.join());
		}
		// Return the mapOfCommerce Info
		return listOfListCommerceInfo;
	}

	@Override
	public List<List<Retailer>> getCommerceModelForRetailers(List<List<Retailer>> listOfListRetailers,
			Map<String, String> config) {
		Map<String, List<Retailer>> mapOfApiToRetailer = new HashMap<>();

		// Build Mapping of Api to Retailer Items
		listOfListRetailers.forEach(retailerAtI -> retailerAtI.forEach(retailer -> {
			if (!mapOfApiToRetailer.containsKey(retailer.getType())
					&& StringUtils.isNotBlank(retailer.getType())) {
				mapOfApiToRetailer.put(retailer.getType(), new ArrayList<>());
			}

			// Protection against cms convention where "" type objects are added
			if (StringUtils.isNotBlank(retailer.getType()))
				mapOfApiToRetailer.get(retailer.getType()).add(retailer);
		}));

		CommerceConfiguration configuration = new CommerceConfiguration(config);

		// For all of the types call the commerce mapping service to get the wrappers and run them on the lists
		if (executorService == null) {
			Map<String, String> mdc = MDC.getCopyOfContextMap();
			mapOfApiToRetailer.entrySet().parallelStream().forEach(entry -> {
			    MDC.setContextMap(mdc);
				try {
					commerceMappingService.getService(entry.getKey()).gatherAndUpdateRetailers(entry.getValue(),
							configuration);
				} catch (Exception e) {
					logger.error("Error running api - " + entry + " with error: ", e);
				}
			});
		} else {
			mapOfApiToRetailer.entrySet().stream().map(e -> {
				return CompletableFuture.runAsync(() -> {
					commerceMappingService.getService(e.getKey()).gatherAndUpdateRetailers(e.getValue(),
							configuration);
				},executorService).handle( (v,ex) -> {
					if (ex != null) {
						logger.error("Error running api - " + e + " with error: ", ex);
					}
					return v;
				});
			}).collect(Collectors.toList()).forEach(f -> f.join());
		}
		// Return the mapOfCommerce Info
		return listOfListRetailers;
	}

}
