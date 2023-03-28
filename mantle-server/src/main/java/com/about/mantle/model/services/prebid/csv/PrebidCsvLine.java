package com.about.mantle.model.services.prebid.csv;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.about.globe.core.http.ua.DeviceCategory;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

/**
 * Used for reading the prebid CSV file.
 */
public class PrebidCsvLine implements Serializable {

	private static final long serialVersionUID = 1L;

	// Column names
	public static final String DOMAIN = "domain";
	public static final String DEVICE = "device";
	public static final String SLOT = "slot";
	public static final String SIZE = "size";
	public static final String BIDDER = "bidder";
	public static final String ACCOUNT_ID = "account_id";
	public static final String DOMAIN_ID = "domain_id";
	public static final String SLOT_ID = "slot_id";
	public static final String AB_TESTS = "ab_tests";
	public static final String MEDIA_TYPE = "media_type";

	@CsvBindByName(column = DOMAIN, required = true)
	private String domain;

	@CsvBindByName(column = SLOT, required = true)
	private String slot;

	@CsvBindByName(column = BIDDER, required = true)
	private String bidder;

	@CsvBindAndSplitByName(column = DEVICE, required = true, elementType = DeviceCategory.class, converter = DeviceCategoryCsvConverter.class, splitOn = ",")
	private Set<DeviceCategory> deviceCategories;

	@CsvCustomBindByName(column = SIZE, converter = SizeCsvConverter.class)
	private List<Integer> size;

	@CsvBindByName(column = ACCOUNT_ID)
	private String accountId;

	@CsvBindByName(column = DOMAIN_ID)
	private String domainId;

	@CsvBindByName(column = SLOT_ID)
	private String slotId;

	@CsvBindByName(column = MEDIA_TYPE)
	private String mediaType;

	@CsvCustomBindByName(column = AB_TESTS, converter = ABTestCsvConverter.class)
	private Map<String, Set<String>> abTests;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSlot() {
		return slot;
	}

	public String getMediaType() {
		return (mediaType != null) ? mediaType : "display";
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getBidder() {
		return bidder;
	}

	public void setBidder(String bidder) {
		this.bidder = bidder;
	}

	public Set<DeviceCategory> getDeviceCategories() {
		return deviceCategories;
	}

	public void setDeviceCategories(Set<DeviceCategory> deviceCategories) {
		this.deviceCategories = deviceCategories;
	}

	public List<Integer> getSize() {
		return size;
	}

	public void setSize(List<Integer> size) {
		this.size = size;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getSlotId() {
		return slotId;
	}

	public void setSlotId(String slotId) {
		this.slotId = slotId;
	}

	public Map<String, Set<String>> getABTests() {
		return abTests;
	}

	public void setABTests(Map<String, Set<String>> abTests) {
		this.abTests = abTests;
	}

}