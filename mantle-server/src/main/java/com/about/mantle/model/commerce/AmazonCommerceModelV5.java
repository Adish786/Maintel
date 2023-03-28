package com.about.mantle.model.commerce;

import static com.about.mantle.model.commerce.CommerceConfiguration.IMAGE_SIZE_LARGE;
import static com.about.mantle.model.commerce.CommerceConfiguration.IMAGE_SIZE_MEDIUM;
import static com.about.mantle.model.commerce.CommerceConfiguration.IMAGE_SIZE_SMALL;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.amazon.paapi5.v1.ImageSize;
import com.amazon.paapi5.v1.Images;
import com.amazon.paapi5.v1.Item;
import com.amazon.paapi5.v1.OfferListing;
import com.amazon.paapi5.v1.OfferPrice;
import com.amazon.paapi5.v1.OfferSavings;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wrapper around AmazonProductAdvertisingApi Item that provides the subset of what's needed by Mantle
 */
public class AmazonCommerceModelV5 extends CommerceModel implements Serializable {

	public AmazonCommerceModelV5(String url, String retailerName) {
		super(url, retailerName);
	}

	public AmazonCommerceModelV5() {
		super();
		retailerName = "amazon";
	}

	// Inc this number for any non-backwards-compatible (ie non-additive) interface changes.
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(AmazonCommerceModelV5.class);

	@JsonProperty("eligibleForPrime")
	private boolean isEligibleForPrime;
	private String asin;
	private String regularPrice;
	private String reducedPrice;
	private String percentSaved;
	private String amountSaved;

	public static AmazonCommerceModelV5 buildAmazonCommerceModel(Item item, String imageSize, Locale locale) {

		AmazonCommerceModelV5 answer = new AmazonCommerceModelV5(item.getDetailPageURL(), "amazon");

		// answer is modified by reference. Necessary if I want to use answer later as a lambda.
		// Don't like this, revisit.
		addOfferBasedInfo(answer, item, locale);
		addImageBasedInfo(answer, item, imageSize);

		answer.asin = item.getASIN();
		answer.url = item.getDetailPageURL();

		return answer;

	}

	private static void addImageBasedInfo(AmazonCommerceModelV5 answer, Item item, String imageSize) {

		Optional<ImageSize> maybeImage = Optional.empty();
		Images images = item.getImages();
		if (imageSize.equalsIgnoreCase(IMAGE_SIZE_SMALL)) {
			maybeImage = maybeGet(() -> images.getPrimary().getSmall());
		} else if (imageSize.equalsIgnoreCase(IMAGE_SIZE_MEDIUM)) {
			maybeImage = maybeGet(() -> images.getPrimary().getMedium());
		} else if (imageSize.equalsIgnoreCase(IMAGE_SIZE_LARGE)) {
			maybeImage = maybeGet(() -> images.getPrimary().getLarge());
		} else {
			throw new GlobeException("Amazon product image size `" + imageSize + "` not supported", null);
		}

		maybeImage.ifPresent(image -> answer.imageUrl = image.getURL());

		if (!(maybeImage.isPresent())) {
			logger.error("Amazon item `{}` did not have image content for size `{}`.  Not including image.",
					item.getASIN(), imageSize);
		}
	}

	// Per Amazon (https://webservices.amazon.com/paapi5/documentation/use-cases/using-offer-information/items-that-do-not-have-offers.html), all
	// 'Regular' products have Offers. If we're not getting offers, then all offer-based data will be null.
	private static void addOfferBasedInfo(AmazonCommerceModelV5 itemFacade, Item item, Locale locale) {

		Optional<Stream<OfferListing>> maybeNullOfferStream = maybeGet(() -> item.getOffers().getListings().stream());

		Optional<OfferListing> bestNewOfferListing = maybeNullOfferStream
				.flatMap(
						offerStream -> offerStream
								.filter(offerListing -> !(Objects.isNull(offerListing.getPrice())))
								.filter(offerListing -> !(Objects.isNull(offerListing.getPrice().getAmount())))
								.filter(offerListing -> !(Objects.isNull(offerListing.getPrice().getCurrency())))
								.reduce(AmazonCommerceModelV5::pickBestOfferListing));

		bestNewOfferListing.ifPresent(offerListing -> {

			OfferPrice amazonsOfferPrice = offerListing.getPrice();
			OfferPrice ourOfferPrice = formatAndRoundUpPrice(locale, amazonsOfferPrice);

			OfferSavings amazonsAmountSaved = offerListing.getPrice().getSavings();

			if (amazonsAmountSaved == null) {
				// Not on sale. The offer price given is the regular price.
				itemFacade.regularPrice = ourOfferPrice.getDisplayAmount();
			} else {
				// On sale! The offer price given is the reduced price.

				// round up the savings basis. Savings Basis will exist if there is a savings price
				OfferPrice ourRegularPrice = formatAndRoundUpPrice(locale, offerListing.getSavingBasis());

				// We can't use amazon's amount saved or percentage saved because of our rounding.
				BigDecimal ourAmountSaved = ourRegularPrice.getAmount().subtract(ourOfferPrice.getAmount());

				// Calculate % saved using our numbers
				BigDecimal percentSaved = calculatePercentSaved(ourAmountSaved, ourRegularPrice.getAmount());

				// Sometimes amazon will put something on 'sale' even if there's no savings. If the % saved is
				// zero, we'll treat it as though it's not on sale.
				if (percentSaved.compareTo(BigDecimal.ZERO) != 0) {
					itemFacade.percentSaved = percentSaved.toString() + "%";
					itemFacade.reducedPrice = ourOfferPrice.getDisplayAmount();
					itemFacade.amountSaved = formatAmazonPrice(ourAmountSaved, locale,
							Currency.getInstance(amazonsAmountSaved.getCurrency()));
				}
				itemFacade.regularPrice = ourRegularPrice.getDisplayAmount();
			}

			itemFacade.isEligibleForPrime = offerListing.getDeliveryInfo().isIsPrimeEligible();

		});

		if (!(bestNewOfferListing.isPresent())) {
			logger.warn("Amazon item `{}` did not have any usable Offer data.  Returning null for price, etc.",
					item.getASIN());
		}
	}

	private static BigDecimal calculatePercentSaved(BigDecimal amountSaved, BigDecimal regularPrice) {
		BigDecimal divide = amountSaved.divide(regularPrice, 2, RoundingMode.UP);
		BigDecimal move = divide.movePointRight(2);
		return move;
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
	private static OfferPrice formatAndRoundUpPrice(Locale locale, OfferPrice price) {

		Currency currency = Currency.getInstance(price.getCurrency());

		OfferPrice answer = new OfferPrice();
		BigDecimal roundedAmount = roundUpAmazonPrice(price.getAmount());

		answer.setCurrency(price.getCurrency());
		answer.setAmount(roundedAmount);
		answer.setDisplayAmount(formatAmazonPrice(roundedAmount, locale, currency));

		return answer;
	}

	/**
	 * Rounds price up to the closest non-decimal value
	 *
	 * @param price
	 * @param currency
	 * @return
	 */
	private static BigDecimal roundUpAmazonPrice(BigDecimal price) {

		price = price.setScale(0, RoundingMode.UP);

		return price;

	}

	/**
	 * Formats price for locale and currency.
	 *
	 * @param price
	 * @param locale
	 * @param currency
	 * @return
	 */
	private static String formatAmazonPrice(BigDecimal price, Locale locale, Currency currency) {

		NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
		formatter.setCurrency(currency);
		int fractionDigits = currency.getDefaultFractionDigits();

		String formattedPrice = formatter.format(price);

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
			BigDecimal bestPriceSoFar = getBestOfferListingPrice(bestSoFar).getAmount();
			BigDecimal thisOffersPrice = getBestOfferListingPrice(offerListing).getAmount();
			return bestPriceSoFar.compareTo(thisOffersPrice) < 0 ? bestSoFar : offerListing;
		}
	}

	private static OfferPrice getBestOfferListingPrice(OfferListing offerListing) {
		return offerListing.getPrice();
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
