package com.about.mantle.venus.model.components.ads;

import static com.about.venus.core.driver.selection.Device.Tablet;
import static com.about.venus.core.driver.selection.Device.Tablet_ChromeEmulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdCallExpectedTablet {

	protected final AdCallExpectedValues adCallValues;
	protected Map<String, List<String>> valuesBillboard1Tablet;
	protected Map<String, List<String>> valuesBillboard2Tablet;
	protected Map<String, List<String>> valuesBillboard3Tablet;
	protected Map<String, List<String>> valuesBillboard4Tablet;
	protected Map<String, List<String>> valuesBillboard5Tablet;
	protected Map<String, List<String>> valuesBillboard6Tablet;

	public AdCallExpectedTablet(AdCallExpectedValues adCallValues) {
		this.adCallValues = adCallValues;
	}

	public void setCommonValuesNativeTablet() {
		Map<String, List<String>> valuesNativeTablet = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesNativeTablet, "prev_iu_szs","320x50|1x3");
		adCallValues.setEntry(valuesNativeTablet, "slot", "native");
		adCallValues.addToValuesTablet(valuesNativeTablet);
		adCallValues.addToValuesTabletEmulator(valuesNativeTablet);
	}

	public void setCommonvaluesBillboard1Tablet() {
		this.valuesBillboard1Tablet = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesTablet(this.valuesBillboard1Tablet);
		adCallValues.addToValuesTabletEmulator(this.valuesBillboard1Tablet);
	}

	public Map<String, List<String>> getvaluesBillboard1Tablet() {
		return this.valuesBillboard1Tablet;
	}

	public void setCommonvaluesBillboard2Tablet() {
		this.valuesBillboard2Tablet = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesTablet(this.valuesBillboard2Tablet);
	}

	public Map<String, List<String>> getvaluesBillboard2Tablet() {
		return this.valuesBillboard2Tablet;
	}

	public void setCommonvaluesBillboard3Tablet() {
		this.valuesBillboard3Tablet = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesTablet(this.valuesBillboard3Tablet);
	}

	public Map<String, List<String>> getvaluesBillboard3Tablet() {
		return this.valuesBillboard3Tablet;
	}

	public void setCommonvaluesBillboard4Tablet() {
		this.valuesBillboard4Tablet = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesTablet(this.valuesBillboard4Tablet);
	}

	public Map<String, List<String>> getvaluesBillboard4Tablet() {
		return this.valuesBillboard4Tablet;
	}

	public void setCommonvaluesBillboard5Tablet() {
		this.valuesBillboard4Tablet = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesTablet(this.valuesBillboard5Tablet);
	}

	public Map<String, List<String>> getvaluesBillboard5Tablet() {
		return this.valuesBillboard5Tablet;
	}

	public void setCommonvaluesBillboard6Tablet() {
		this.valuesBillboard4Tablet = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesTablet(this.valuesBillboard6Tablet);
	}

	public Map<String, List<String>> getvaluesBillboard6Tablet() {
		return this.valuesBillboard6Tablet;
	}

	/*
	 * @param billboardType
	 * 		billboard for which expected value is to be set
	 * @param key
	 * 		the key which needs to be set
	 * @param values
	 * 		array of values for the given key
	 */
	public void setPageSpecificValueBillboardTablet(String billboardType, String key, String... values) {
		switch(billboardType) {
			case "billboard1":
				adCallValues.setPageSpecificValue(Tablet_ChromeEmulator, this.valuesBillboard1Tablet, key, values);
				adCallValues.setPageSpecificValue(Tablet, this.valuesBillboard1Tablet, key, values);
				break;
			case "billboard2":
				adCallValues.setPageSpecificValue(Tablet_ChromeEmulator, this.valuesBillboard2Tablet, key, values);
				adCallValues.setPageSpecificValue(Tablet, this.valuesBillboard2Tablet, key, values);
				break;
			case "billboard3":
				adCallValues.setPageSpecificValue(Tablet_ChromeEmulator, this.valuesBillboard3Tablet, key, values);
				adCallValues.setPageSpecificValue(Tablet, this.valuesBillboard3Tablet, key, values);
				break;
			case "billboard4":
				adCallValues.setPageSpecificValue(Tablet_ChromeEmulator, this.valuesBillboard4Tablet, key, values);
				adCallValues.setPageSpecificValue(Tablet, this.valuesBillboard4Tablet, key, values);
				break;
			case "billboard5":
				adCallValues.setPageSpecificValue(Tablet_ChromeEmulator, this.valuesBillboard5Tablet, key, values);
				adCallValues.setPageSpecificValue(Tablet, this.valuesBillboard5Tablet, key, values);
				break;
			case "billboard6":
				adCallValues.setPageSpecificValue(Tablet_ChromeEmulator, this.valuesBillboard6Tablet, key, values);
				adCallValues.setPageSpecificValue(Tablet, this.valuesBillboard6Tablet, key, values);
				break;
			default:
				throw new IllegalArgumentException(billboardType + " is not supported yet!");
		}
	}
}