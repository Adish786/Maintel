package com.about.mantle.venus.model.components.ads;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.about.venus.core.driver.selection.Device.Mobile_ChromeEmulator;
import static com.about.venus.core.driver.selection.Device.SmartPhone;

public class AdCallExpectedMobile {
	protected final AdCallExpectedValues adCallValues;
	protected Map<String, List<String>> valuesBillboardMobile;
	protected Map<String, List<String>> valuesBillboard1Mobile;
	protected Map<String, List<String>> valuesBillboard2Mobile;
	protected Map<String, List<String>> valuesBillboard3Mobile;
	protected Map<String, List<String>> valuesBillboard4Mobile;
	protected Map<String, List<String>> valuesBillboard5Mobile;
	protected Map<String, List<String>> valuesBillboard6Mobile;
	protected Map<String, List<String>> valuesDynamic1Mobile;
	protected Map<String, List<String>> valuesDynamic2Mobile;
	protected Map<String, List<String>> valuesDynamic3Mobile;
	protected Map<String, List<String>> valuesDynamic4Mobile;
	protected Map<String, List<String>> valuesDynamic5Mobile;
	protected Map<String, List<String>> valuesBillboardFooter;
	protected Map<String, List<String>> valuesBillboardFooter2;

	public AdCallExpectedMobile(AdCallExpectedValues adCallValues) {
		this.adCallValues = adCallValues;
	}

	public void setCommonvaluesBillboardMobile() {
		this.valuesBillboardMobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesMobile(this.valuesBillboardMobile);
		adCallValues.addToValuesMobileEmulator(this.valuesBillboardMobile);
	}

	public void setCommonvaluesBillboard1Mobile() {
		this.valuesBillboard1Mobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesMobile(this.valuesBillboard1Mobile);
		adCallValues.addToValuesMobileEmulator(this.valuesBillboard1Mobile);
	}

	public Map<String, List<String>> getvaluesBillboard1Mobile() {
		return this.valuesBillboard1Mobile;
	}

	public void setCommonvaluesBillboard2Mobile() {
		this.valuesBillboard2Mobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesMobile(this.valuesBillboard2Mobile);
		adCallValues.addToValuesMobileEmulator(this.valuesBillboard2Mobile);
	}

	public Map<String, List<String>> getvaluesBillboard2Mobile() {
		return this.valuesBillboard2Mobile;
	}

	public void setCommonvaluesBillboard3Mobile() {
		this.valuesBillboard3Mobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesMobile(this.valuesBillboard3Mobile);
		adCallValues.addToValuesMobileEmulator(this.valuesBillboard3Mobile);
	}

	public Map<String, List<String>> getvaluesBillboard3Mobile() {
		return this.valuesBillboard3Mobile;
	}

	public void setCommonvaluesBillboard4Mobile() {
		this.valuesBillboard4Mobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesMobile(this.valuesBillboard4Mobile);
		adCallValues.addToValuesMobileEmulator(this.valuesBillboard4Mobile);
	}

	public Map<String, List<String>> getvaluesBillboard4Mobile() {
		return this.valuesBillboard4Mobile;
	}

	public void setCommonvaluesBillboard5Mobile() {
		this.valuesBillboard5Mobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesMobile(this.valuesBillboard5Mobile);
		adCallValues.addToValuesMobileEmulator(this.valuesBillboard5Mobile);
	}

	public Map<String, List<String>> getvaluesBillboard5Mobile() {
		return this.valuesBillboard5Mobile;
	}

	public void setCommonvaluesBillboard6Mobile() {
		this.valuesBillboard5Mobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesMobile(this.valuesBillboard6Mobile);
		adCallValues.addToValuesMobileEmulator(this.valuesBillboard6Mobile);
	}

	public Map<String, List<String>> getvaluesBillboard6Mobile() {
		return this.valuesBillboard6Mobile;
	}

	public void setCommonvaluesDynamic1Mobile() {
		this.valuesDynamic1Mobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesDynamic1Mobile, "prev_iu_szs","300x250");
		adCallValues.addToValuesMobile(this.valuesDynamic1Mobile);
		adCallValues.addToValuesMobileEmulator(this.valuesDynamic1Mobile);
	}

	public Map<String, List<String>> getvaluesDynamic1Mobile() {
		return this.valuesDynamic1Mobile;
	}

	public void setCommonvaluesDynamic2Mobile() {
		this.valuesDynamic2Mobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesDynamic2Mobile, "prev_iu_szs","300x250");
		adCallValues.setEntry(valuesDynamic2Mobile, "dord","1");
		adCallValues.addToValuesMobile(this.valuesDynamic2Mobile);
		adCallValues.addToValuesMobileEmulator(this.valuesDynamic2Mobile);
	}

	public Map<String, List<String>> getvaluesDynamic2Mobile() {
		return this.valuesDynamic2Mobile;
	}

	public void setCommonvaluesDynamic3Mobile() {
		this.valuesDynamic3Mobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesDynamic3Mobile, "prev_iu_szs","300x250");
		adCallValues.setEntry(valuesDynamic3Mobile, "dord","2");
		adCallValues.addToValuesMobile(this.valuesDynamic3Mobile);
		adCallValues.addToValuesMobileEmulator(this.valuesDynamic3Mobile);
	}

	public Map<String, List<String>> getvaluesDynamic3Mobile() {
		return this.valuesDynamic3Mobile;
	}

	public void setCommonvaluesDynamic4Mobile() {
		this.valuesDynamic4Mobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesDynamic4Mobile, "prev_iu_szs","300x250");
		adCallValues.setEntry(valuesDynamic4Mobile, "dord","3");
		adCallValues.addToValuesMobile(this.valuesDynamic4Mobile);
		adCallValues.addToValuesMobileEmulator(this.valuesDynamic4Mobile);
	}

	public Map<String, List<String>> getvaluesDynamic4Mobile() {
		return this.valuesDynamic4Mobile;
	}

	public void setCommonvaluesDynamic5Mobile() {
		this.valuesDynamic5Mobile = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesDynamic5Mobile, "prev_iu_szs","300x250");
		adCallValues.setEntry(valuesDynamic5Mobile, "dord","4");
		adCallValues.addToValuesMobile(this.valuesDynamic5Mobile);
		adCallValues.addToValuesMobileEmulator(this.valuesDynamic5Mobile);
	}

	public Map<String, List<String>> getvaluesDynamic5Mobile() {
		return this.valuesDynamic5Mobile;
	}

	public void setCommonvaluesBillboardFooter() {
		this.valuesBillboardFooter = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesBillboardFooter, "prev_iu_szs","300x250");
		adCallValues.addToValuesMobile(this.valuesBillboardFooter);
		adCallValues.addToValuesMobileEmulator(this.valuesBillboardFooter);
	}

	public Map<String, List<String>> getValuesBillboardFooter() {
		return this.valuesBillboardFooter;
	}

	public void setCommonvaluesBillboardFooter2() {
		this.valuesBillboardFooter2 = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesBillboardFooter2, "prev_iu_szs","300x250");
		adCallValues.addToValuesMobile(this.valuesBillboardFooter2);
		adCallValues.addToValuesMobileEmulator(this.valuesBillboardFooter2);
	}

	public Map<String, List<String>> getValuesBillboardFooter2() {
		return this.valuesBillboardFooter2;
	}

	/*
	 * @param billboardType
	 * 		billboard for which expected value is to be set
	 * @param key
	 * 		the key which needs to be set
	 * @param values
	 * 		array of values for the given key
	 */
	public void setPageSpecificValueBillboardMobile(String billboardType, String key, String... values) {
		switch(billboardType) {
			case "billboard":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesBillboardMobile, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesBillboardMobile, key, values);
				break;
			case "billboard1":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesBillboard1Mobile, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesBillboard1Mobile, key, values);
				break;
			case "billboard2":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesBillboard2Mobile, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesBillboard2Mobile, key, values);
				break;
			case "billboard3":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesBillboard3Mobile, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesBillboard3Mobile, key, values);
				break;
			case "billboard4":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesBillboard4Mobile, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesBillboard4Mobile, key, values);
				break;
			case "billboard5":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesBillboard5Mobile, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesBillboard5Mobile, key, values);
				break;
			case "billboard6":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesBillboard6Mobile, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesBillboard6Mobile, key, values);
				break;
			case "dynamic1":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesDynamic1Mobile, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesDynamic1Mobile, key, values);
				break;
			case "dynamic2":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesDynamic2Mobile, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesDynamic2Mobile, key, values);
				break;
			case "dynamic3":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesDynamic3Mobile, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesDynamic3Mobile, key, values);
				break;
			case "dynamic4":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesDynamic4Mobile, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesDynamic4Mobile, key, values);
				break;
			case "billboardfooter":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesBillboardFooter, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesBillboardFooter, key, values);
				break;
			case "billboardfooter2":
				adCallValues.setPageSpecificValue(Mobile_ChromeEmulator, this.valuesBillboardFooter2, key, values);
				adCallValues.setPageSpecificValue(SmartPhone, this.valuesBillboardFooter2, key, values);
				break;
			default:
				throw new IllegalArgumentException(billboardType + " is not supported yet!");
		}
	}

}