package com.about.mantle.venus.model.components.ads;

import static com.about.venus.core.driver.selection.Device.Mobile_ChromeEmulator;
import static com.about.venus.core.driver.selection.Device.PC;
import static com.about.venus.core.driver.selection.Device.SmartPhone;
import static com.about.venus.core.driver.selection.Device.Tablet;
import static com.about.venus.core.driver.selection.Device.Tablet_ChromeEmulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.about.venus.core.driver.selection.Device;

public class AdCallExpectedValues {

	private final Map<Device, List<Map<String, List<String>>>> params;
	private final List<Map<String, List<String>>> valuesPC; 
	private final List<Map<String, List<String>>> valuesTablet;
	private final List<Map<String, List<String>>> valuesTabletEmulator;
	private final List<Map<String, List<String>>> valuesMobile;
	private final List<Map<String, List<String>>> valuesMobileEmulator;
	private Map<String, List<String>> commonBillboardValues;
	
	public AdCallExpectedValues() {
		this.params = new HashMap<>();
		this.valuesPC = new ArrayList<>();
		this.valuesTablet = new ArrayList<>();
		this.valuesTabletEmulator = new ArrayList<>();
		this.valuesMobile = new ArrayList<>();
		this.valuesMobileEmulator = new ArrayList<>();
		this.commonBillboardValues = new HashMap<>();
	}
	
	public void addToValuesPC(Map<String, List<String>> valuesPC) {
		this.valuesPC.add(valuesPC);
	}
	
	public void addToValuesMobile(Map<String, List<String>> valuesMobile) {
		this.valuesMobile.add(valuesMobile);
	}
	
	public void addToValuesMobileEmulator(Map<String, List<String>> valuesMobileEmulator) {
		this.valuesMobileEmulator.add(valuesMobileEmulator);
	}
	
	public void addToValuesTablet(Map<String, List<String>> valuesTablet) {
		this.valuesTablet.add(valuesTablet);
	}
	
	public void addToValuesTabletEmulator(Map<String, List<String>> valuesTabletEmulator) {
		this.valuesTabletEmulator.add(valuesTabletEmulator);
	}
	
	/*
	 * this method must be called after populating values for devices being tested
	 */
	public void setValues() {
		this.params.put(PC, this.valuesPC);
		this.params.put(Mobile_ChromeEmulator, this.valuesMobileEmulator);
		this.params.put(Tablet, this.valuesTablet);
		this.params.put(Tablet_ChromeEmulator, this.valuesTabletEmulator);
		this.params.put(SmartPhone, this.valuesMobile);
	}
	
	public Map<Device, List<Map<String, List<String>>>> params() {
		return this.params;
	}
	
	/*
	 * @param tax0 
	 * 		tax0 value for the given ad call
	 * 		these values can be overridden in vertical implementation
	 */
	public void setCommonBillboardValues(String tax0) {
		setCommonBillboardValues(tax0, true, true);
	}
	
	public void setCommonBillboardValues(String tax0, boolean jny, boolean chapters) {
		if(chapters) {
			setEntry(this.commonBillboardValues, "entryType","direct");
		}	
		setEntry(this.commonBillboardValues, "vid","0");
		setEntry(this.commonBillboardValues, "pc","1");
		setEntry(this.commonBillboardValues, "tax0", tax0);
		setEntry(this.commonBillboardValues, "ugc","0");
		if(jny) {
			setEntry(this.commonBillboardValues, "jny","0");
			setEntry(this.commonBillboardValues, "jnyroot", "rootJourneyDocIDValue");
		}
		
	}
	
	/*
	 * @param commonValues
	 * 		map of ad call values that needs to be set as common values
	 */
	public void setCommonBillboardValues(Map<String, List<String>> commonValues) {
		this.commonBillboardValues = commonValues;
	}
	
	public Map<String, List<String>> getCommonBillboardValues() {
		return this.commonBillboardValues;
	}
	
	/*
	 * @param key
	 * 		key value to which new entry needs to be added
	 * @param strings
	 * 		list of values for the given key 
	 */
	public void setEntry(Map<String, List<String>> values, String key, String... strings) {
		List<String> entries = new ArrayList<>();
		entries.addAll(Arrays.asList(strings));
		values.put(key, entries);
	}

	/*
	 * @param device
	 * 		device on which current test is running
	 * @param valuesMap
	 * 		map to which we want to add entry
	 * @param key
	 * 		key that we want to add to valuesMap
	 * @param values
	 * 		array of strings as values for given key
	 */
	public void setPageSpecificValue(Device device, Map<String, List<String>> valuesMap, String key, String... values) {
		List<Map<String, List<String>>> existingValues = null;
		Consumer<Map<String, List<String>>> function = null;
		switch(device) {
		case PC:
			existingValues = this.valuesPC;
			function = (map) -> addToValuesPC(map);
			break;
		case Mobile_ChromeEmulator:
			existingValues = this.valuesMobileEmulator;
			function = (map) -> addToValuesMobileEmulator(map);
			break;
		case SmartPhone:
			existingValues = this.valuesMobile;
			function = (map) -> addToValuesMobile(map);
			break;	
		case Tablet_ChromeEmulator:
			existingValues = this.valuesTabletEmulator;
			function = (map) -> addToValuesTabletEmulator(map);
			break;	
		case Tablet:
			existingValues = this.valuesTablet;
			function = (map) -> addToValuesTablet(map);
			break;	
		default:
			throw new IllegalArgumentException(device.toString() + " is not supported yet!");
		}
		setEntry(valuesMap, key, values);
		if(existingValues.contains(valuesMap)) {
			existingValues.remove(valuesMap);
		}
		function.accept(valuesMap);
	}
}
