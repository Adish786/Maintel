package com.about.mantle.model.services.commerce;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.about.hippodrome.url.ExternalUrlData;
import com.about.hippodrome.url.PlatformUrlDataFactory;

public class VendorLookupService {

	private static Logger logger = LoggerFactory.getLogger(VendorLookupService.class);
	private PlatformUrlDataFactory urlDataFactory;

	public VendorLookupService() {};

	public VendorLookupService(PlatformUrlDataFactory urlDataFactory) {
		this.urlDataFactory = urlDataFactory;
	}

	private static final Map<String, String> COMMERCE_PARTNER_DOMAIN_TO_DISPLAYNAME;
	static {
		COMMERCE_PARTNER_DOMAIN_TO_DISPLAYNAME = new HashMap<String, String>() {
			private static final long serialVersionUID = 67821981926826794L;

			{
				put("1800contacts.com", "1-800 Contacts");
				put("1800petmeds.com", "1-800-PetMeds");
				put("4sleep.com", "4Sleep");
				put("acehardware.com", "Ace Hardware");
				put("adobe.com", "Adobe");
				put("amazon.com", "Amazon");
				put("anker.com", "Anker");
				put("anthropologie.com", "Anthropologie");
				put("ao.com", "Appliances Online");
				put("apple.com", "Apple");
				put("art.com", "Art.com");
				put("asos.com", "Asos");
				put("ballarddesigns.com", "Ballard Designs");
				put("balooliving.com", "Baloo");
				put("bearmattress.com", "Bear");
				put("bedbathandbeyond.com", "Bed Bath & Beyond");
				put("bestbuy.com", "Best Buy");
				put("bhldn.com", "Bhldn");
				put("bhphotovideo.com", "B&H Photo Video");
				put("bloomingdales.com", "Bloomingdales");
				put("boostmobile.com", "Boost Mobile");
				put("brilliantearth.com", "Brilliant Earth");
				put("brooklinen.com", "Brooklinen");
				put("buffy.co", "Buffy");
				put("build.com", "Build.com");
				put("burrow.com", "Burrow");
				put("buybuybaby.com", "buybuy BABY");
				put("caskers.com", "Caskers");
				put("casper.com", "Casper");
				put("cb2.com", "CB2");
				put("chewy.com", "Chewy");
				put("cleanorigin.com", "Clean Origin");
				put("cocktailkingdom.com", "Cocktail Kingdom");
				put("containerstore.com", "The Container Store");
				put("cozyearth.com", "Cozy Earth");
				put("craftsy.com", "Craftsy");
				put("crateandbarrel.com", "Crate & Barrel");
				put("credobeauty.com", "Credo Beauty");
				put("cvs.com", "CVS");
				put("davidsbridal.com", "David's Bridal");
				put("deandeluca.com", "Dean & Deluca");
				put("dell.com", "Dell");
				put("dermstore.com", "Dermstore");
				put("dickssportinggoods.com", "Dick's");
				put("drizly.com", "Drizly");
				put("dwr.com", "Design Within Reach");
				put("ebay.com", "eBay");
				put("eleyhosereels.com", "Eley");
				put("etsy.com", "Etsy");
				put("ettitude.com", "Ettitude");
				put("fabric.com", "Fabric.com");
				put("flaviar.com", "Flaviar");
				put("flooringsuperstore.com", "Flooring Superstore");
				put("food52.com", "Food52");
				put("freshdirect.com", "FreshDirect");
				put("frontgate.com", "Frontgate");
				put("giftbasket.com", "Gift Basket");
				put("gifttree.com", "Gift Tree");
				put("gourmetgiftbaskets.com", "Gourmet Gift Baskets");
				put("grampasweeder.com", "Grampa's Gardenware Co.");
				put("greenworkstools.com", "Greenworks");
				put("grokker.com", "Grokker");
				put("harryanddavid.com", "Harry & David");
				put("helixsleep.com", "Helix");
				put("hellofresh.com", "HelloFresh");
				put("homechef.com", "Home Chef");
				put("homedepot.com", "Home Depot");
				put("houzz.com", "Houzz");
				put("hp.com", "HP");
				put("huckberry.com", "Huckberry");
				put("hydrow.com", "Hydrow");
				put("igourmet.com", "iGourmet");
				put("iherb.com", "iHerb");
				put("ikea.com", "Ikea");
				put("iobit.com", "Iobit");
				put("jet.com", "Jet");
				put("joann.com", "JOANN");
				put("knix.com", "Knix");
				put("leesa.com", "Leesa");
				put("lenovo.com", "Lenovo");
				put("llbean.com", "L.L.Bean");
				put("lowes.com", "Lowe's");
				put("shop.lululemon.com", "Lululemon");
				put("lulus.com", "Lulus");
				put("macys.com", "Macy's");
				put("michaels.com", "Michaels");
				put("microsoft.com", "Microsoft");
				put("minibardelivery.com", "Minibar Delivery");
				put("minted.com", "Minted");
				put("mouth.com", "Mouth");
				put("myxfitness.com", "Myx Fitness");
				put("neimanmarcus.com", "Neiman Marcus");
				put("nestbedding.com", "Nest Bedding");
				put("net-a-porter.com", "Net-a-Porter");
				put("nordstrom.com", "Nordstrom");
				put("oculus.com", "Oculus");
				put("officedepot.com", "Office Depot");
				put("ososleep.com", "Oso");
				put("overstock.com", "Overstock");
				put("oxo.com", "Oxo");
				put("packagefreeshop.com", "Package Free Shop");
				put("parachutehome.com", "Parachute");
				put("pbteen.com", "PBteen");
				put("petco.com", "PETCO");
				put("petsmart.com", "PetSmart");
				put("pier1.com", "Pier 1 Imports");
				put("potterybarn.com", "Pottery Barn");
				put("purple.com", "Purple");
				put("getquip.com", "quip");
				put("qvc.com", "QVC");
				put("rei.com", "REI");
				put("rejuvenation.com", "Rejuvenation");
				put("reservebar.com", "ReserveBar");
				put("restduvet.com", "Rest Duvet");
				put("revolve.com", "Revolve");
				put("ritani.com", "Ritani");
				put("saatva.com", "Saatva");
				put("saksfifthavenue.com", "Saks Fifth Avenue");
				put("samsung.com", "Samsung");
				put("sephora.com", "Sephora");
				put("serenaandlily.com", "Serena And Lily");
				put("sheex.com", "Sheex");
				put("shethinx.com", "Thinx");
				put("sijohome.com", "Sijo");
				put("slumbercloud.com", "Slumber Cloud");
				put("spanx.com", "Spanx");
				put("sprint.com", "Sprint");
				put("staples.com", "Staples");
				put("sweavebedding.com", "Sweave");
				put("surlatable.com", "Sur La Table");
				put("target.com", "Target");
				put("thecompanystore.com", "The Company Store");
				put("thedetoxmarket.com", "The Detox Market");
				put("thefruitcompany.com", "The Fruit Company");
				put("thegrommet.com", "The Grommet");
				put("thenorthface.com", "The North Face");
				put("thepaintstore.com", "ThePaintStore");
				put("therelaxedgardener.com", "The Relaxed Gardener");
				put("thereformation.com", "Reformation");
				put("theshadestore.com", "The Shade Store");
				put("thrivemarket.com", "Thrive Market");
				put("tjmaxx.tjx.com", "TJ Maxx");
				put("toysrus.com", "Toys R Us");
				put("tractorsupply.com", "Tractor Supply Co.");
				put("tuftandneedle.com", "Tuft & Needle");
				put("ulta.com", "Ulta");
				put("uncommongoods.com", "Uncommon Goods");
				put("urbanoutfitters.com", "Urban Outfitters");
				put("verizon.com", "Verizon");
				put("violetgrey.com", "Violet Grey");
				put("vitacost.com", "Vitacost");
				put("vitaminshoppe.com", "Vitamin Shoppe");
				put("vivino.com", "Vivino");
				put("vrai.com", "Vrai");
				put("walgreens.com", "Walgreens");
				put("wallpaperdirect.com", "Wallpaperdirect");
				put("walmart.com", "Walmart");
				put("waterford.com", "Waterford");
				put("wayfair.com", "Wayfair");
				put("westelm.com", "West Elm");
				put("williams-sonoma.com", "Williams-Sonoma");
				put("wine.com", "Wine.com");
				put("winechateau.com", "Wine Chateau");
				put("wineenthusiast.com", "Wine Enthusiast");
				put("worldmarket.com", "World Market");
				put("zappos.com", "Zappos");
				put("zola.com", "Zola");
			}
		};
	}

	private static final Map<String, String> AFFILIATE_LINK_QUERY_PARAM;
	static {
		AFFILIATE_LINK_QUERY_PARAM = new HashMap<String, String>() {
			{
				put("www.awin1.com", "p");
				put("www.avantlink.com", "url");
				put("click.linksynergy.com", "murl");
				put("shareasale.com", "urllink");
				put("www.pjtra.com", "url");
				put("www.pntra.com", "url");
				put("www.gopjn.com", "url");
				put("www.pjatr.com", "url");
				put("www.pntrac.com", "url");
				put("www.pntrs.com", "url");
			}
		};
	}

	public String getRetailerName(String url) {

		// uncomment once CMS does HN-5289
		if (StringUtils.isBlank(url)) {
			// logger.error("Got a blank url / id from Commerce list item");
			return null;
		}

		String displayName;
		try {
			URL parseableUrl = new URL(url);
			String host = parseableUrl.getHost();
			String truncatedHost = getRetailerHost(url, host);
			displayName = truncatedHost;
			if (COMMERCE_PARTNER_DOMAIN_TO_DISPLAYNAME.containsKey(truncatedHost)) {
				displayName = COMMERCE_PARTNER_DOMAIN_TO_DISPLAYNAME.get(truncatedHost);
			} else if (truncatedHost.indexOf("tjx.com") > -1) {
				// Special exception for tjmaxx, whose key has subdomain.toplevel.com
				displayName = COMMERCE_PARTNER_DOMAIN_TO_DISPLAYNAME.get("tjmaxx.tjx.com");
			} else if (truncatedHost.indexOf("lululemon.com") > -1) {
				displayName = COMMERCE_PARTNER_DOMAIN_TO_DISPLAYNAME.get("shop.lululemon.com");
			}
		} catch (MalformedURLException e) {
			Integer start = url.indexOf("://") + 3;
			Integer end = url.substring(start).indexOf("/");
			String host = url.substring(start).substring(0, end);
			String truncatedHost = getTruncatedHost(host);

			displayName = truncatedHost;
		}

		return displayName;
	}

	// This function gets the truncated hostname of retailer link
	private String getRetailerHost(String networkUrl, String host) {
		String networkHost =  getTruncatedHost(host);
		try {
			String decodedNetworkUrl = URLDecoder.decode(networkUrl, StandardCharsets.UTF_8);
			String retailerURL = null;

			// to fetch retailer link from aff network url params
			if(AFFILIATE_LINK_QUERY_PARAM.containsKey(host)) {
				Map<String, List<String>> queryParamsMap = ExternalUrlData.builder(urlDataFactory)
					.from(decodedNetworkUrl).build().getQueryParams();
					String merchantNameParam = AFFILIATE_LINK_QUERY_PARAM.get(host);
				List<String> merchantParamValue =  queryParamsMap != null ? queryParamsMap.get(merchantNameParam) : null;
				if(merchantParamValue != null && !merchantNameParam.isEmpty()) {
					retailerURL = merchantParamValue.get(0);
				}
			} else if (networkHost.equals("anrdoezrs.net")) {
				// to fetch retailer url from CJ url e.g https://www.anrdoezrs.net/links/8029122/type/dlg/sid/PEORevealedThe20BestJeansforCurvyWomenAccordingtoReviewsjmastropStyAff12135902202005I/https://www.madewell.com/the-petite-curvy-perfect-vintage-straight-jean-in-hoye-wash-NC528.html
				int indexOfRetailer = StringUtils.lastIndexOf(decodedNetworkUrl, "/http");
				retailerURL = StringUtils.substring(decodedNetworkUrl, (indexOfRetailer + 1));
			} else if ((host.equals("partnerize.com")) && (decodedNetworkUrl.indexOf("/destination:") != -1)) {
				// to fetch retailer url from partnerize url: https://partnerize.com/click/camref:{PID}/pubref:{DID}%7C{RID}%7C%7C{CID}%7C{CHID}/destination:{PURL}
				// fetch retailer url by substringing after /destination:
				retailerURL = StringUtils.substringAfterLast(decodedNetworkUrl, "/destination:");
			} else {
				Map<String, List<String>> networkQueryParams = ExternalUrlData.builder(urlDataFactory).from(decodedNetworkUrl).build().getQueryParams();
				if (networkQueryParams != null && networkQueryParams.containsKey("u") ) {
					// for impactRadius links that may have retailer URL coming from query param u , if u param isn't present then networkHost is returned
					List<String> impactRadiusRetailerParam = networkQueryParams.get("u");
					if(impactRadiusRetailerParam != null && !impactRadiusRetailerParam.isEmpty() ) {
						retailerURL = impactRadiusRetailerParam.get(0);
					}
				}
			}

			// get retailer host if retailer link is not the network url
			if(retailerURL != null && !retailerURL.isEmpty()) {
				String retailerHost = new URI(retailerURL).getHost();
				String truncatedRetailer = getTruncatedHost(retailerHost);
				return truncatedRetailer;
			}	
		} catch (UnsupportedEncodingException | URISyntaxException | MalformedURLException e) {
			logger.warn("Unable to parse retailerURL from networkUrl {}", networkUrl, e);
		}
		// otherwise return host of network url
		return networkHost;
	}

	private String getTruncatedHost(String host) {
		Integer lastDot = host.lastIndexOf(".");
		Integer secondToLastDot = host.substring(0, lastDot).lastIndexOf(".") + 1;
		return host.substring(secondToLastDot, host.length());
	}

}
