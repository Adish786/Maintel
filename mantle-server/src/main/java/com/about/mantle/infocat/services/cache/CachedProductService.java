package com.about.mantle.infocat.services.cache;

import com.about.globe.core.cache.GenericCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.infocat.services.ProductService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class CachedProductService implements ProductService {

    private final ProductService productService;
    private final CacheTemplate<Product> productByIdCache;

    public CachedProductService(ProductService productService, CacheTemplate<Product> productByIdCache) {
        this.productService = productService;
        this.productByIdCache = productByIdCache;
    }

    /**
     * @see ProductService#getProduct(String)
     */
    @Override
    public Product getProduct(String id) {
        GenericCacheKey<String> key = new GenericCacheKey<>(id);

        return productByIdCache.get(key, () -> productService.getProduct(id));
    }

    /**
     * @see ProductService#getProducts(List)
     */
    @Override
    public Map<String, Product> getProducts(List<String> ids) {
        if (isEmpty(ids)) return null;

        return ids.stream().distinct().map(id -> getProduct(id)).filter(product -> product != null)
                .collect(Collectors.toMap(product -> product.getId(), product -> product));
    }

}
