package com.about.mantle.venus.utils;

import com.about.venus.core.driver.proxy.VenusHarRequest;
import com.about.venus.core.utils.Url;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class UrlParams  {
	private ListMultimap<String, String> urlParams;
	private ListMultimap<String, String> custParams;
	private ListMultimap<String, String> scpParams;

	public UrlParams(VenusHarRequest request) {
		urlParams = request.url().parameters();
		custParams = custParams();
		scpParams = scpParams();
	}
	public UrlParams(Url url) {
		urlParams = url.parameters();
	}

	static public class BaseParams {
		public ListMultimap<String, String> params;
		private BaseParams(ListMultimap<String, String> params) { this.params = params; }

		protected String getParam(String name) {
			if(params != null) {
				List<String> value = params.get(name);
				if(value != null && value.size() != 0) {
					return value.get(0);
				}
			}
			return null;
		}

		public String getParamWithEncoding(String name) {
			String value = getParam(name);
			if (value != null) {
				List<NameValuePair> nvp = URLEncodedUtils.parse(value, Charset.forName("UTF-8"));
				if (nvp != null) {
					return nvp.get(0).getName();
				}
			}
			return null;
		}
	}
	static public class CustomParams extends BaseParams {
		public CustomParams(ListMultimap<String, String> params) { super(params); }
		public String au() { return getParam("au"); }
		public String chpg() { return getParam("chpg"); }
		public String custom() { return getParam("custom"); }
		public String customSeries() { return getParam("customSeries"); }
		public String dcRef() { return getParamWithEncoding("dc_ref"); }
		public String docId() { return getParam("docId"); }
		public String entryType() { return getParam("entryType"); }
		public String hgt() { return getParam("hgt"); }
		public String inf() { return getParam("inf"); }
		public String pc() { return getParam("pc"); }
		public String ptax() { return getParam("ptax"); }
		public String rid() { return getParam("rid"); }
		public String sid() { return getParam("sid"); }
		public String slot() { return getParam("slot"); }
		public String syn() { return getParam("syn"); }
		public String t() { return getParam("t"); }
		public String tax0() { return getParam("tax0"); }
		public String tax1() { return getParam("tax1"); }
		public String tax2() { return getParam("tax2"); }
		public String tax3() { return getParam("tax3"); }
		public String ugc() { return getParam("ugc"); }
		public String vid() { return getParam("vid"); }
		public String w() { return getParam("w"); }
		public String wdth() { return getParam("wdth"); }
	}
	static public class ScpParams extends BaseParams {
		public ScpParams(ListMultimap<String, String> params) {
			super(params);
		}
		public String aid() { return getParam("aid"); }
		public String amzn_vid() { return getParam("amzn_vid"); }
		public String amznbid() { return getParam("amznbid"); }
		public String amzniid() { return getParam("amzniid"); }
		public String amznp() { return getParam("amznp"); }
		public String amznslots() { return getParam("amznslots"); }
		public String amznsz() { return getParam("amznsz"); }
		public String aolbid() { return getParam("aolbid"); }
		public String au() { return getParam("au"); }
		public String ccaud() { return getParam("ccaud"); }
		public String chan() { return getParam("chan"); }
		public String chanid() { return getParamWithEncoding("chanid"); }
		public String chop() { return getParam("chop"); }
		public String chpg() { return getParam("chpg"); }
		public String docId() { return getParam("docId"); }
		public String entryType() { return getParam("entryType"); }
		public String inf() { return getParam("inf"); }
		public String jnyroot() { return getParam("jnyroot"); }
		public String leaid() { return getParam("leaid"); }
		public String leuid() { return getParam("leuid"); }
		public String lpid() { return getParam("lpid"); }
		public String mpalias() { return getParam("mpalias"); }
		public String mpt() { return getParam("mpt"); }
		public String ocode() { return getParam("o"); }
		public String pc() { return getParam("pc"); }
		public String pos() { return getParam("pos"); }
		public String priority() { return getParam("priority"); }
		public String ptax() { return getParam("ptax"); }
		public String sbj() { return getParam("sbj"); }
		public String slot() { return getParam("slot"); }
		public String t() { return getParam("t"); }
		public String tax0() { return getParam("tax0"); }
		public String tax1() { return getParam("tax1"); }
		public String tax2() { return getParam("tax2"); }
		public String tax3() { return getParam("tax3"); }
		public String tile() { return getParam("tile"); }
		public String vid() { return getParam("vid"); }
		public String floor() { return getParam("floor"); }
		public String floor_id() { return getParam("floor_id"); }
		public String gridId() { return getParam("gridId"); }
		public String gridPb() { return getParam("gridPb"); }
		public String gridSize() { return getParam("gridSize"); }

	}
	static public class UrlParameters extends BaseParams {
		public UrlParameters(ListMultimap<String, String> params) { super(params); }
		public String channel() { return getParam("channel"); }
		public String client() { return getParam("client"); }
		public String enc_prev_ius() { return getParam("enc_prev_ius"); }
		public String hints() { return getParam("hints"); }
		public String iu_parts() { return getParam("iu_parts"); }
		public String iu() { return getParam("iu"); }
		public String max_radlink_len() { return getParam("max_radlink_len"); }
		public String num_ads() { return getParam("num_ads"); }
		public String num_radlinks() { return getParam("num_radlinks"); }
		public String pc() { return getParam("pc"); }
		public String prev_iu_szs() { return getParam("prev_iu_szs"); }
		public String prev_scp() { return getParam("prev_scp"); }
		public String q() { return getParam("q"); }
		public String r() { return getParam("r"); }
		public String rurl() { return getParam("rurl"); }
		public String scp() { return getParam("scp"); }
		public JSONArray slotsArray() { String slots = getParam("slots");
			if(slots != null) { return new JSONArray(slots); }
			return null;
		}
		public String sz() { return getParam("sz"); }
		public String utm_campaign() { return getParam("utm_campaign"); }
		public String utm_medium() { return getParam("utm_medium"); }
		public String utm_source() { return getParam("utm_source"); }
	}
	public CustomParams custParam() { return new CustomParams(custParams); }
	public ScpParams scpParam() { return new ScpParams(scpParams); }
	public UrlParameters urlParam() { return new UrlParameters(urlParams); }

	/**
	 * Will get all of the params under the key
	 * @param key (scp or cust_params)
	 * @return
	 */
	private ListMultimap<String, String> getParams(String key) {
		ListMultimap<String, String> adcallCustomParameters = ArrayListMultimap.create();
		if (urlParams.containsKey(key)) {
			List<NameValuePair> customParameters = URLEncodedUtils.parse(urlParams.get(key).get(0),
				Charset.forName("UTF-8"));
			customParameters.stream().forEach(param -> adcallCustomParameters.put(param.getName(), param.getValue()));
			return adcallCustomParameters;
		}
		return null;
	}
	
	public ListMultimap<String, String> custParams() {
		return getParams("cust_params");
	}
	
	private String getUrlParam(ListMultimap<String, String> param, String key) {
		if (!param.containsKey(key)) {
			throw new Error("Missing urlParam key: " + key);
		}
		if (param.get(key).size() == 0) {
			throw new Error("urlParam key: " + key + " does not have any items, expected > 0");
		}
		return param.get(key).get(0);
	}

	public ListMultimap<String, String> scpParams() {
		if (getParams("scp") != null) {
			return getParams("scp");
		}
		return prevScpParams();
	}

	/** @deprecated use urlParam.slots */
	@Deprecated
	public JSONArray slotsArray() {
		return new JSONArray(urlParams.get("slots").get(0).toString());
	}
	
	public ListMultimap<String, String> prevScpParams() {
		ListMultimap<String, String> adcallprevScpParameters = ArrayListMultimap.create();
		if (urlParams.containsKey("prev_scp")) {
			List<String> slotParams = Arrays.asList(urlParams.get("prev_scp").get(0).split("\\|"));
			for (String slot : slotParams) {
				List<NameValuePair> params = URLEncodedUtils.parse(slot, Charset.forName("UTF-8"));
				params.stream().forEach(param -> adcallprevScpParameters.put(param.getName(), param.getValue()));
			}
			return adcallprevScpParameters;
		}
		return null;
	}

	public List<String> getUrlParamList(String param) {
		return urlParams.get(param);
	}

	public List<String> getCustomParamList(String param) {
		return custParams.get(param);
	}

	public List<String> getScpParamList(String param) {
		return scpParams.get(param);
	}

	/** @deprecated use urlParam.sz() */
	@Deprecated
	public String sz() {
		return urlParams.get("sz").get(0);
	}

	/** @deprecated use urlParam.iu() */
	@Deprecated
	public String iu() {
		return urlParams.get("iu").get(0);
	}

	/** @deprecated use urlParam.q() */
	@Deprecated
	public String q() {
		return urlParams.get("q").get(0);
	}

	/** @deprecated use urlParam.client() */
	@Deprecated
	public String client() {
		return urlParams.get("client").get(0);
	}

	/** @deprecated use urlParam.channel() */
	@Deprecated
	public String channel() {
		return urlParams.get("channel").get(0);
	}

	/** @deprecated use urlParam.scp() */
	@Deprecated
	public String scp() {
		return urlParams.get("scp").get(0);
	}

	/** @deprecated use urlParam.hints() */
	@Deprecated
	public String hints() {
		return urlParams.get("hints").get(0);
	}

	/** @deprecated use urlParam.rurl() */
	@Deprecated
	public String rurl() {
		return urlParams.get("rurl").get(0);
	}

	/** @deprecated use urlParam.r() */
	@Deprecated
	public String r() {
		return urlParams.get("r").get(0);
	}
		 
	/*
	 * below are the custom params methods
	 */
	/** @deprecated use scpParam.inf() */
	@Deprecated
	public String inf() {
		return scpParams.get("inf").get(0);
	}

	/** @deprecated use custParam.w() */
	@Deprecated
	public String w() {
		return custParams.get("w").get(0);
	}

	/** @deprecated use custParam.wdth() */
	@Deprecated
	public String wdth() {
		return custParams.get("wdth").get(0);
	}

	/** @deprecated use custParam.rid() */
	@Deprecated
	public String rid() {
		return custParams.get("rid").get(0);
	}

	/** @deprecated use custParam.sid() */
	@Deprecated
	public String sid() {
		return custParams.get("sid").get(0);
	}

	/** @deprecated use scpParam.pc() */
	@Deprecated
	public String pc() {
		return scpParams.get("pc").get(0);
	}

	/** @deprecated use scpParam.amznbid() */
	@Deprecated
	public String amznbid() {
		return scpParams.get("amznbid").get(0);
	}

	/** @deprecated use scpParam.amzniid() */
	@Deprecated
	public String amzniid() {
		return scpParams.get("amzniid").get(0);
	}

	/** @deprecated use scpParam.amznsz() */
	@Deprecated
	public String amznsz() {
		return scpParams.get("amznsz").get(0);
	}

	/** @deprecated use scpParam.amznp() */
	@Deprecated
	public String amznp() {
		return scpParams.get("amznp").get(0);
	}

	/** @deprecated use scpParam.au() */
	@Deprecated
	public String au() {
		return scpParams.get("au").get(0);
	}

	/** @deprecated use scpParam.chanid() */
	@Deprecated
	public String chanid() {
		return URLEncodedUtils.parse(scpParams.get("chanid").get(0), Charset.forName("UTF-8")).get(0).getName();
	}

	/** @deprecated use scpParam.chan() */
	@Deprecated
	public String chan() {
		return scpParams.get("chan").get(0);
	}

	/** @deprecated use scpParam.aid() */
	@Deprecated
	public String aid() {
		return scpParams.get("aid").get(0);
	}

	/** @deprecated use scpParam.t() */
	@Deprecated
	public String t() {
		return scpParams.get("t").get(0);
	}

	/** @deprecated use scpParam.dcRef() */
	@Deprecated
	public String dcRef() {
		return URLEncodedUtils.parse(custParams.get("dc_ref").get(0), Charset.forName("UTF-8")).get(0).getName();
	}

	/** @deprecated use scpParam.tax1() */
	@Deprecated
	public String tax1() {
		return scpParams.get("tax1").get(0);
	}

	/** @deprecated use scpParam.tax0() or custParam.tax0 */
	@Deprecated
	public String tax0() {
		return tax0(false);
	}

	/** @deprecated use scpParam.tax0() or custParam.tax0 */
	@Deprecated
	public String tax0(boolean vertical) {
		String tax0 = vertical ? custParams.get("tax0").get(0) : scpParams.get("tax0").get(0);
		return tax0;
	}

	/** @deprecated use scpParam.tax2() */
	@Deprecated
	public String tax2() {
		return scpParams.get("tax2").get(0);
	}

	/** @deprecated use scpParam.sbj() */
	@Deprecated
	public String sbj() {
		return scpParams.get("sbj").get(0);
	}

	/** @deprecated use scpParam.ptax() */
	@Deprecated
	public String ptax() {
		return scpParams.get("ptax").get(0);
	}

	/** @deprecated use custParam.ugc() */
	@Deprecated
	public String ugc() {
		return custParams.get("ugc").get(0);
	}

	/** @deprecated use custParam.syn() */
	@Deprecated
	public String syn() {
		return custParams.get("syn").get(0);
	}

	/** @deprecated use custParam.hgt() */
	@Deprecated
	public String hgt() {
		return custParams.get("hgt").get(0);
	}

	/** @deprecated use scpParam.ccaud() */
	@Deprecated
	public String ccaud() {
		return scpParams.get("ccaud").get(0);
	}

	/** @deprecated use scpParam.lpid() */
	@Deprecated
	public String lpid() {
		return scpParams.get("lpid").get(0);
	}

	/** @deprecated use scpParam.lpmptid() */
	@Deprecated
	public String mpt() {
		return scpParams.get("mpt").get(0);
	}

	/** @deprecated use scpParam.ocode() */
	@Deprecated
	public String ocode() {
		return scpParams.get("o").get(0);
	}

	/** @deprecated use scpParam.amzn_vid() */
	@Deprecated
	public String amznVid() {
		return scpParams.get("amzn_vid").get(0);
	}

	/** @deprecated use scpParam.amznslots() */
	@Deprecated
	public String amznslots() {
		return scpParams.get("amznslots").get(0);
	}

	/** @deprecated use scpParam.vid() */
	@Deprecated
	public String vid() {
		return scpParams.get("vid").get(0);
	}

	/** @deprecated use scpParam.chpg() */
	@Deprecated
	public String chpg() {
		return scpParams.get("chpg").get(0);
	}

	/** @deprecated use scpParam.entryType() */
	@Deprecated
	public String entryType() {
		return scpParams.get("entryType").get(0);
	}

	/** @deprecated use scpParam.pos() */
	@Deprecated
	public List<String> pos() {
		return scpParams.get("pos");
	}

	/** @deprecated use scpParam.tile() */
	@Deprecated
	public List<String> tile() {
		return scpParams.get("tile");
	}

	/** @deprecated use scpParam.chop() */
	@Deprecated
	public List<String> chop() {
		return scpParams.get("chop");
	}

	/** @deprecated use scpParam.priority() */
	@Deprecated
	public List<String> priority() {
		return scpParams.get("priority");
	}

	/** @deprecated use scpParam.aolbid() */
	@Deprecated
	public String aolbid() {
		return scpParams.get("aolbid").get(0);
	}

	/** @deprecated use scpParam.mpalias() */
	@Deprecated
	public String mpalias() {
		return scpParams.get("mpalias").get(0);
	}

	/** @deprecated use scpParam.jnyroot() */
	@Deprecated
	public String jnyRoot() {
		return scpParams.get("jnyroot").get(0);
	}
	/*
	 * below are the social share params methods
	 */

	/** @deprecated use urlParam.utm_campaign() */
	@Deprecated
	public String utmCampaign() {
		return urlParams.get("utm_campaign").get(0);
	}

	/** @deprecated use urlParam.utm_medium() */
	@Deprecated
	public String utmMedium() {
		return urlParams.get("utm_medium").get(0);
	}

	/** @deprecated use urlParam.utmSource() */
	@Deprecated
	public String utmSource() {
		return urlParams.get("utm_source").get(0);
	}

	/** @deprecated use urlParam.maxRadLink() */
	@Deprecated
	public String maxRadLink() {
		return urlParams.get("max_radlink_len").get(0);
	}

	/** @deprecated use urlParam.num_radlinks() */
	@Deprecated
	public String numRadLink() {
		return urlParams.get("num_radlinks").get(0);
	}

	/** @deprecated use urlParam.num_ads() */
	@Deprecated
	public String numAds() {
		return urlParams.get("num_ads").get(0);
	}

	/** @deprecated use urlParam.prev_iu_szs() */
	@Deprecated
	public String prevIuSzs() {
		return urlParams.get("prev_iu_szs").get(0);
	}

	/** @deprecated use urlParam.prev_scp() */
	@Deprecated
	public String prevScp() {
		return urlParams.get("prev_scp").get(0);
	}

	/** @deprecated use urlParam.enc_prev_ius() */
	@Deprecated
	public String encPrevIus() {
		return urlParams.get("enc_prev_ius").get(0);
	}

	/** @deprecated use urlParam.iu_parts() */
	@Deprecated
	public String iuParts() {
		return urlParams.get("iu_parts").get(0);
	}

	/** @deprecated use custParam.customSeries() */
	@Deprecated
	public String customSeries() {
		if (custParams.get("customSeries")!= null && (custParams.get("customSeries").size()!=0)) {
			return custParams.get("customSeries").get(0);

		}			
		return null;
	}

	/** @deprecated use scpParam.slot() */
	@Deprecated
	public String scpslot() {
		if (scpParams.get("slot")!= null && (scpParams.get("slot").size()!=0)) {
			return scpParams.get("slot").get(0);
		}
		return null;
	}

	/** @deprecated use custParam.slot() */
	@Deprecated
	public String custParamsSlot() {
		if (custParams.get("slot")!= null && (custParams.get("slot").size()!=0)) {
			return custParams.get("slot").get(0);
		}
		return null;
	}
}
