package com.about.mantle.infocat.services.impl;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.infocat.model.TaggedImage;
import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.infocat.model.responses.ProductResponse;
import com.about.mantle.infocat.services.ProductService;
import com.about.mantle.model.extended.docv2.ImageEx;

public class ProductServiceImpl extends AbstractHttpServiceClient implements ProductService {
	private static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

	private static final String INFOCAT_RECORD_PATH = "/record";
	private final ExecutorService executor;

	public ProductServiceImpl(HttpServiceClientConfig httpServiceClientConfig, ExecutorService executor) {
		super(httpServiceClientConfig);
		this.executor = executor;
	}

	@Override
	public Product getProduct(String id) {
		ProductResponse response = getProduct(id, ProductResponse.class);
		return escapeFields(response.getData());
	}

	private Product escapeFields(Product product) {
		// Escape images
		List<TaggedImage> images = product != null ? product.getImages() : null;
		if (images != null) {
			for (TaggedImage taggedImage : images) {
				ImageEx image = taggedImage.getImage();
				image.setAlt(StringEscapeUtils.escapeHtml4(image.getAlt()));
				image.setCaption(StringEscapeUtils.escapeHtml4(image.getCaption()));
				/* Image owner is not escaped as that field may contain html code
				 * for a link or other formatting.
				 */
			}
		}
		return product;
	}

	@Override
	public Map<String, Product> getProducts(List<String> ids) {
		if (isEmpty(ids)) return null;

		return ids.stream().distinct().map(id -> CompletableFuture.supplyAsync(() -> getProduct(id), executor)
				.exceptionally(ex -> {
					logger.error("An error occurred while retrieving a product with id " + id, ex);
					return null;
				}))
				.map(productFuture -> productFuture.join()).filter(product -> product != null)
				.collect(Collectors.toMap(product -> product.getId(), product -> product));
	}

	private <T extends BaseResponse<?>> T getProduct(String id, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.path(INFOCAT_RECORD_PATH).path(id)
				//Filter out future events
				.queryParam("promotionalEventFilter", "ONGOING")
				.queryParam("includeMetadata", true);

		T response = readResponse(webTarget, bindToTarget);
		return response;
	}

}
