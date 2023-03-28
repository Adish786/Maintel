package com.about.mantle.model.commerce;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.Image;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.Item;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.Offer;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.OfferListing;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.Price;

import static com.about.mantle.model.commerce.CommerceConfiguration.IMAGE_SIZE_LARGE;
import static com.about.mantle.model.commerce.CommerceConfiguration.IMAGE_SIZE_MEDIUM;
import static com.about.mantle.model.commerce.CommerceConfiguration.IMAGE_SIZE_SMALL;

/**
 * Wrapper around AmazonProductAdvertisingApi Item that provides the subset of what's needed by Mantle
 */
public class AmazonCommerceModel extends CommerceModel implements Serializable {

	public AmazonCommerceModel(String url, String retailerName) {
		super(url, retailerName);
	}

	public AmazonCommerceModel() {
		super();
		retailerName = "amazon";
	}

	// Inc this number for any non-backwards-compatible (ie non-additive) interface changes.
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(AmazonCommerceModel.class);

	@JsonProperty("eligibleForPrime")
	private boolean isEligibleForPrime;
	private String asin;
	private String regularPrice;
	private String reducedPrice;
	private String percentSaved;
	private String amountSaved;

	public static AmazonCommerceModel buildAmazonCommerceModel(Item item, String imageSize, Locale locale) {

		AmazonCommerceModel answer = new AmazonCommerceModel(item.getDetailPageURL(), "amazon");

		// answer is modified by reference. Necessary if I want to use answer later as a lambda.
		// Don't like this, revisit.
		addOfferBasedInfo(answer, item, locale);
		addImageBasedInfo(answer, item, imageSize);

		answer.asin = item.getASIN();
		answer.url = item.getDetailPageURL();

		return answer;

	}

	private static void addImageBasedInfo(AmazonCommerceModel answer, Item item, String imageSize) {

		Optional<Image> maybeImage = Optional.empty();

		if (imageSize.equalsIgnoreCase(IMAGE_SIZE_SMALL)) {
			maybeImage = Optional.ofNullable(item.getSmallImage());
		} else if (imageSize.equalsIgnoreCase(IMAGE_SIZE_MEDIUM)) {
			maybeImage = Optional.ofNullable(item.getMediumImage());
		} else if (imageSize.equalsIgnoreCase(IMAGE_SIZE_LARGE)) {
			maybeImage = Optional.ofNullable(item.getLargeImage());
		} else {
			throw new GlobeException("Amazon product image size `" + imageSize + "` not supported", null);
		}

		maybeImage.ifPresent(image -> answer.imageUrl = image.getURL());

		if (!(maybeImage.isPresent())) {
			logger.error("Amazon item `{}` did not have image content for size `{}`.  Not including image.",
					item.getASIN(), imageSize);
		}
	}

	// Per Amazon (http://docs.aws.amazon.com/AWSECommerceService/latest/DG/ItemsThatDoNotHaveOffers.html), all
	// 'Regular' products have Offers. If we're not getting offers, then all offer-based data will be null.
	private static void addOfferBasedInfo(AmazonCommerceModel itemFacade, Item item, Locale locale) {

		Optional<Stream<Offer>> maybeNullOfferStream = maybeGet(() -> item.getOffers().getOffer().stream());

		Optional<OfferListing> bestNewOfferListing = maybeNullOfferStream
				.flatMap(
						offerStream -> offerStream
								.filter(offer -> offer.getOfferAttributes().getCondition().equals("New"))
								.flatMap((Function<Offer, Stream<OfferListing>>) offer -> (offer
										.getOfferListing() == null) ? null : offer.getOfferListing().stream())
								.filter(offerListing -> !(Objects.isNull(offerListing.getPrice())))
								.filter(offerListing -> !(Objects.isNull(offerListing.getPrice().getAmount())))
								.filter(offerListing -> !(Objects.isNull(offerListing.getPrice().getCurrencyCode())))
								.reduce(AmazonCommerceModel::pickBestOfferListing));

		bestNewOfferListing.ifPresent(offerListing -> {

			Price amazonsOfferPrice = getBestOfferListingPrice(offerListing);
			Price ourOfferPrice = formatAndRoundUpPrice(locale, amazonsOfferPrice);

			Price amazonsAmountSaved = offerListing.getAmountSaved();

			if (amazonsAmountSaved == null) {
				// Not on sale. The offer price given is the regular price.
				itemFacade.regularPrice = ourOfferPrice.getFormattedPrice();
			} else {
				// On sale! The offer price given is the reduced price.

				// calc the regular price given the reduced + saved
				Price ourRegularPrice = calculateAndFormatRegularPrice(amazonsOfferPrice, amazonsAmountSaved, locale);

				// We can't use amazon's amount saved or percentage saved because of our rounding.
				BigInteger ourAmountSaved = ourRegularPrice.getAmount().subtract(ourOfferPrice.getAmount());

				// Calculate % saved using our numbers
				BigDecimal percentSaved = calculatePercentSaved(ourAmountSaved, ourRegularPrice.getAmount());

				// Sometimes amazon will put something on 'sale' even if there's no savings. If the % saved is
				// zero, we'll treat it as though it's not on sale.
				if (percentSaved.compareTo(BigDecimal.ZERO) != 0) {
					itemFacade.percentSaved = percentSaved.toString() + "%";
					itemFacade.reducedPrice = ourOfferPrice.getFormattedPrice();
					itemFacade.amountSaved = formatAmazonPrice(ourAmountSaved, locale,
							Currency.getInstance(amazonsAmountSaved.getCurrencyCode()));
				}
				itemFacade.regularPrice = ourRegularPrice.getFormattedPrice();
			}

			itemFacade.isEligibleForPrime = offerListing.isIsEligibleForPrime();

		});

		if (!(bestNewOfferListing.isPresent())) {
			logger.error("Amazon item `{}` did not have any usable Offer data.  Returning null for price, etc.",
					item.getASIN());
		}
	}

	private static BigDecimal calculatePercentSaved(BigInteger amountSaved, BigInteger regularPrice) {

		BigDecimal dividend = new BigDecimal(amountSaved);
		BigDecimal divisor = new BigDecimal(regularPrice);

		return dividend.divide(divisor, 2, RoundingMode.UP).movePointRight(2);
	}

	/**
	 * Given sale price and amount saved, calc the regular price. Regular Price is not included in the OfferListing for
	 * some reason.
	 *
	 * @param offerPrice
	 * @param amountSaved
	 * @return
	 */
	private static Price calculateAndFormatRegularPrice(Price offerPrice, Price amountSaved, Locale locale) {

		Price answer = new Price();
		Assert.isTrue(offerPrice.getCurrencyCode().equals(amountSaved.getCurrencyCode()));

		answer.setCurrencyCode(offerPrice.getCurrencyCode());
		answer.setAmount(offerPrice.getAmount().add(amountSaved.getAmount()));

		answer = formatAndRoundUpPrice(locale, answer);

		return answer;

	}

	/**
	 * Amazon returns prices w/ no decimal points. EG $5.99 = 599. Need to use the Currency passed in to determine
	 * number of decimal places.
	 *
	 * @param locale
	 * @param price
	 *            A price with the amount rounded and the formatted price in locale friendly format.
	 * @return
	 */
	private static Price formatAndRoundUpPrice(Locale locale, Price price) {

		Currency currency = Currency.getInstance(price.getCurrencyCode());

		Price answer = new Price();
		BigInteger roundedAmount = roundUpAmazonPrice(price.getAmount(), currency);

		answer.setCurrencyCode(price.getCurrencyCode());
		answer.setAmount(roundedAmount);
		answer.setFormattedPrice(formatAmazonPrice(roundedAmount, locale, currency));

		return answer;
	}

	/**
	 * Rounds price up to the closest non-decimal value. Amazon returns prices w/ no decimal points. EG $5.99 = 599.
	 * Need to use the Currency passed in to determine number of decimal places.
	 *
	 * @param price
	 * @param currency
	 * @return
	 */
	private static BigInteger roundUpAmazonPrice(BigInteger price, Currency currency) {

		int fractionDigits = currency.getDefaultFractionDigits();
		BigDecimal bd = new BigDecimal(price);

		bd = bd.movePointLeft(fractionDigits);
		bd = bd.setScale(0, RoundingMode.UP);

		return bd.toBigIntegerExact();

	}

	/**
	 * Formats price for locale and currency. Amazon returns prices w/ no decimal points. EG $5.99 = 599. Need to use
	 * the Currency passed in to determine number of decimal places.
	 *
	 * @param price
	 * @param locale
	 * @param currency
	 * @return
	 */
	private static String formatAmazonPrice(BigInteger price, Locale locale, Currency currency) {

		NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
		formatter.setCurrency(currency);
		int fractionDigits = currency.getDefaultFractionDigits();
		BigDecimal priceBigD = new BigDecimal(price);

		String formattedPrice = formatter.format(priceBigD);

		// Strip off the decimal and everything past it.
		// TODO this is not compatible w/ currency number formats that put the currency symbol after the number
		formattedPrice = StringUtils.left(formattedPrice, formattedPrice.length() - (fractionDigits + 1));

		return formattedPrice;

	}

	/**
	 * Picks the OfferListing with the lowest price
	 *
	 * @param bestSoFar
	 * @param offerListing
	 * @return
	 */
	private static OfferListing pickBestOfferListing(OfferListing bestSoFar, OfferListing offerListing) {
		if (bestSoFar == null) {
			return offerListing;
		} else {
			BigInteger bestPriceSoFar = getBestOfferListingPrice(bestSoFar).getAmount();
			BigInteger thisOffersPrice = getBestOfferListingPrice(offerListing).getAmount();
			return bestPriceSoFar.compareTo(thisOffersPrice) < 0 ? bestSoFar : offerListing;
		}
	}

	private static Price getBestOfferListingPrice(OfferListing offerListing) {
		return offerListing.getSalePrice() != null ? offerListing.getSalePrice() : offerListing.getPrice();
	}

	/**
	 * Percentage saved, if applicable. Formatted (% included!). Will return null if the item does not currently have a
	 * reduced price.
	 *
	 * @return
	 */
	public String getPercentSaved() {
		return percentSaved;
	}

	public void setPercentSaved(String percentSaved) {
		this.percentSaved = percentSaved;
	}

	/**
	 * Amount saved, if applicable. Formatted for currency / locale. Will return null the item does not currently have a
	 * reduced price.
	 *
	 * @return
	 */
	public String getAmountSaved() {
		return amountSaved;
	}

	public void setAmountSaved(String amountSaved) {
		this.amountSaved = amountSaved;

	}

	/**
	 * Regular price of the item. Formatted for currency / locale. Will return null if we could not pull the price from
	 * Amazon.
	 *
	 * @return
	 */
	public String getRegularPrice() {
		return regularPrice;
	}

	public void setRegularPrice(String regularPrice) {
		this.regularPrice = regularPrice;
	}

	/**
	 * Reduced price of the item, if applicable. Formatted for currency / locale. Will return null the item does not
	 * currently have a reduced price.
	 *
	 * @return
	 */
	public String getReducedPrice() {
		return reducedPrice;
	}

	public void setReducedPrice(String reducedPrice) {
		this.reducedPrice = reducedPrice;
	}

	/**
	 * Returns the item's reduced price if applicable. If not, returns its regular price.
	 *
	 * @return
	 */
	@Override
	public String getBestPrice() {
		return (getReducedPrice() != null) ? getReducedPrice() : getRegularPrice();
	}

	@JsonProperty("eligibleForPrime")
	public boolean isEligibleForPrime() {
		return isEligibleForPrime;
	}

	@JsonProperty("eligibleForPrime")
	public void setIsEligibleForPrime(boolean isEligibleForPrime) {
		this.isEligibleForPrime = isEligibleForPrime;
	}

	@Override
	public String getImageUrl() {
		return imageUrl;
	}

	@Override
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	/**
	 * The detail URL provided by amazon. This URL is custom made for us to allow any revenue generated from clicks /
	 * purchases to be associated with our account.
	 * 
	 * @return
	 */
	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "AmazonCommerceModel{" + "imageUrl='" + imageUrl + '\'' + ", asin='" + asin + '\'' + ", regularPrice='"
				+ regularPrice + '\'' + ", reducedPrice='" + reducedPrice + '\'' + ", percentSaved='" + percentSaved
				+ '\'' + ", amountSaved='" + amountSaved + '\'' + ", url='" + url + '\'' + ", bestPrice='" + getBestPrice()
				+ '\'' + ", eligibleForPrime=" + isEligibleForPrime() + '}';
	}

	/**
	 * Quick and dirty way of handling chained null checks. This little gem should really be in hippodrome
	 * 
	 * @param supplier
	 * @param <T>
	 * @return
	 */
	public static <T> Optional<T> maybeGet(Supplier<T> supplier) {

		Optional<T> answer;

		try {
			answer = Optional.ofNullable(supplier.get());
		} catch (NullPointerException e) {
			answer = Optional.empty();
		}

		return answer;

	}
}
