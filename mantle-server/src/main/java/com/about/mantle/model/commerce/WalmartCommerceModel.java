package com.about.mantle.model.commerce;

import static com.about.mantle.model.commerce.CommerceConfiguration.IMAGE_SIZE_LARGE;
import static com.about.mantle.model.commerce.CommerceConfiguration.IMAGE_SIZE_MEDIUM;
import static com.about.mantle.model.commerce.CommerceConfiguration.IMAGE_SIZE_SMALL;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dotdash.walmart.model.Item;

public class WalmartCommerceModel extends CommerceModel {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(WalmartCommerceModel.class);

    private static final Currency usdCurrency = Currency.getInstance("USD");

    private String bestPrice;
    private int id;

    public WalmartCommerceModel(Item item, String imageSize) {
        this.retailerName = "walmart";
        this.bestPrice = determineBestPrice(item);
        // See GLBE-5534.  Waiting to actually pull URL from API.
        // this.url = item.getProductTrackingUrl();
        this.url = null;
        // See GLBE-5534.  Waiting to actually pull image from API.
        // this.imageUrl = determineImageUrl(item, imageSize);
        this.imageUrl = null;
        this.id = item.getItemId();
    }

    /**
     * The 'best price' for this item.  Returns the current sale price if available, otherwise returns the MSRP.
     *
     * @return
     */
    @Override
    public String getBestPrice() {
        return this.bestPrice;
    }

  
        /**
         * Returns the Walmart ID
         *
         * @return
         */
    public int getId() {
        return id;
    }

    private static String determineImageUrl(Item item, String imageSize) {

        String answer;

        if (IMAGE_SIZE_SMALL.equals(imageSize)) {
            answer = item.getThumbnailImage();
        } else if (IMAGE_SIZE_MEDIUM.equals(imageSize)) {
            answer = item.getMediumImage();
        } else if (IMAGE_SIZE_LARGE.equals(imageSize)) {
            answer = item.getLargeImage();
        } else {
            logger.warn("Image size {} not supported.  Defaulting to medium.", imageSize);
            answer = item.getMediumImage();
        }

        return answer;

    }

    private static String determineBestPrice(Item item) {
        Double bestPrice = (item.getSalePrice() != null) ? item.getSalePrice() : item.getMsrp();
        return formatAndRoundUpPrice(bestPrice);
    }

    /**
     * TODO much of this is cut and pasted from {@link AmazonCommerceModel}.  See what can be factored out.
     *
     * @param price
     * @return
     */
    private static String formatAndRoundUpPrice(Double price) {

        int fractionDigits = usdCurrency.getDefaultFractionDigits();
        BigDecimal bd = new BigDecimal(price);

        bd = bd.setScale(0, RoundingMode.UP);
        BigInteger rounded = bd.toBigIntegerExact();

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setCurrency(usdCurrency);

        BigDecimal priceBigD = new BigDecimal(rounded);

        String formattedPrice = formatter.format(priceBigD);

        // Strip off the decimal and everything past it.
        // TODO this is not compatible w/ currency number formats that put the currency symbol after the number
        formattedPrice = StringUtils.left(formattedPrice, formattedPrice.length() - (fractionDigits + 1));

        return formattedPrice;
    }
}
