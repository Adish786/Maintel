package com.about.mantle.venus.model.components.ads;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.about.venus.core.driver.selection.Device.PC;

public class AdCallExpectedPC {

	protected final AdCallExpectedValues adCallValues;
	protected Map<String, List<String>> valuesBillboardPC;
	protected Map<String, List<String>> valuesBillboard1PC;
	protected Map<String, List<String>> valuesBillboard2PC;
	protected Map<String, List<String>> valuesBillboard3PC;
	protected Map<String, List<String>> valuesBillboard4PC;
	protected Map<String, List<String>> valuesBillboard5PC;
	protected Map<String, List<String>> valuesBillboard6PC;
	protected Map<String, List<String>> valuesBillboard7PC;
	protected Map<String, List<String>> valuesBillboard8PC;

	public AdCallExpectedPC(AdCallExpectedValues adCallValues) {
		this.adCallValues = adCallValues;
	}

	public void setCommonValuesNativePC() {
		Map<String, List<String>> valuesNativePC = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesNativePC, "prev_iu_szs","320x50|1x3");
		adCallValues.setEntry(valuesNativePC, "slot", "native");
		adCallValues.addToValuesPC(valuesNativePC);
	}

	public void setCommonValuesBillboardPC() {
		this.valuesBillboardPC = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.addToValuesPC(this.valuesBillboardPC);
	}

	public void setCommonValuesBillboard1PC() {
		this.valuesBillboard1PC = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(this.valuesBillboard1PC, "fluid","height");
		adCallValues.addToValuesPC(this.valuesBillboard1PC);
	}

	public Map<String, List<String>> getValuesBillboard1PC() {
		return this.valuesBillboard1PC;
	}

	public void setCommonValuesBillboard2PC() {
		this.valuesBillboard2PC = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesBillboard2PC, "fluid","height");
		adCallValues.addToValuesPC(this.valuesBillboard2PC);
	}

	public Map<String, List<String>> getValuesBillboard2PC() {
		return this.valuesBillboard2PC;
	}

	public void setCommonValuesBillboard3PC() {
		this.valuesBillboard3PC = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesBillboard3PC, "fluid","height");
		adCallValues.addToValuesPC(this.valuesBillboard3PC);
	}

	public Map<String, List<String>> getValuesBillboard3PC() {
		return this.valuesBillboard3PC;
	}

	public void setCommonValuesBillboard4PC() {
		this.valuesBillboard4PC = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesBillboard4PC, "fluid","height");
		adCallValues.addToValuesPC(this.valuesBillboard4PC);
	}

	public Map<String, List<String>> getValuesBillboard4PC() {
		return this.valuesBillboard4PC;
	}

	public void setCommonValuesBillboard5PC() {
		this.valuesBillboard5PC = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesBillboard5PC, "fluid","height");
		adCallValues.addToValuesPC(this.valuesBillboard5PC);
	}

	public Map<String, List<String>> getValuesBillboard5PC() {
		return this.valuesBillboard5PC;
	}

	public void setCommonValuesBillboard6PC() {
		this.valuesBillboard6PC = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesBillboard6PC, "fluid","height");
		adCallValues.addToValuesPC(this.valuesBillboard6PC);
	}

	public Map<String, List<String>> getValuesBillboard6PC() {
		return this.valuesBillboard6PC;
	}

	public void setCommonValuesBillboard7PC() {
		this.valuesBillboard7PC = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesBillboard7PC, "fluid","height");
		adCallValues.addToValuesPC(this.valuesBillboard7PC);
	}

	public Map<String, List<String>> getValuesBillboard7PC() {
		return this.valuesBillboard7PC;
	}

	public void setCommonValuesBillboard8PC() {
		this.valuesBillboard7PC = new HashMap<>(adCallValues.getCommonBillboardValues());
		adCallValues.setEntry(valuesBillboard8PC, "fluid","height");
		adCallValues.addToValuesPC(this.valuesBillboard8PC);
	}

	public Map<String, List<String>> getValuesBillboard8PC() {
		return this.valuesBillboard8PC;
	}

	/*
	 * @param billboardType
	 * 		billboard for which expected value is to be set
	 * @param key
	 * 		the key which needs to be set
	 * @param values
	 * 		array of values for the given key
	 */
	public void setPageSpecificValueBillboardPC(String billboardType, String key, String... values) {
		switch(billboardType) {
			case "billboard":
				adCallValues.setPageSpecificValue(PC, this.valuesBillboardPC, key, values);
				break;
			case "billboard1":
				adCallValues.setPageSpecificValue(PC, this.valuesBillboard1PC, key, values);
				break;
			case "billboard2":
				adCallValues.setPageSpecificValue(PC, this.valuesBillboard2PC, key, values);
				break;
			case "billboard3":
				adCallValues.setPageSpecificValue(PC, this.valuesBillboard3PC, key, values);
				break;
			case "billboard4":
				adCallValues.setPageSpecificValue(PC, this.valuesBillboard4PC, key, values);
				break;
			case "billboard5":
				adCallValues.setPageSpecificValue(PC, this.valuesBillboard5PC, key, values);
				break;
			case "billboard6":
				adCallValues.setPageSpecificValue(PC, this.valuesBillboard6PC, key, values);
				break;
			case "billboard7":
				adCallValues.setPageSpecificValue(PC, this.valuesBillboard7PC, key, values);
				break;
			case "billboard8":
				adCallValues.setPageSpecificValue(PC, this.valuesBillboard8PC, key, values);
				break;
			default:
				throw new IllegalArgumentException(billboardType + " is not supported yet!");
		}
	}
}
