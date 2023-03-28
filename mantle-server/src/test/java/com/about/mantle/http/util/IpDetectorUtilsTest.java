package com.about.mantle.http.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class IpDetectorUtilsTest {

	private MantleIpDetectorUtils ipDetectorUtils;

	@Before
	public void before() {
		String[] cidrArr = StringUtils.splitByWholeSeparator("64.125.212.0/23, 216.223.12.0/23, " +
				"209.66.78.0/24, 207.241.144.0/21, 127.0.0.0/8, 10.0.0.0/8, " +
				"2002:0000:0000:1234:0000:0000:0000:0000/64",
				", ");
		ipDetectorUtils = new MantleIpDetectorUtils(cidrArr);
	}

	@Test
	public void testValidIpv4Address() throws Exception {
		assertTrue(ipDetectorUtils.ipAddressExists("64.125.213.255"));
		assertTrue(ipDetectorUtils.ipAddressExists("216.223.12.10"));
	}

	@Test
	public void testInvalidIpv4Address() throws Exception {
		assertFalse(ipDetectorUtils.ipAddressExists("72.229.28.1"));
		assertFalse(ipDetectorUtils.ipAddressExists("93.449.23.1"));
	}

	@Test
	public void testValidIpv6Address() throws Exception {
		assertTrue(ipDetectorUtils.ipAddressExists("2002:0000:0000:1234:ABCD:0000:0400:0000"));
		assertTrue(ipDetectorUtils.ipAddressExists("2002:0000:0000:1234:ffff:8349:a32e:ffff"));
	}

	@Test
	public void testInvalidIpv6Address() throws Exception {
		assertFalse(ipDetectorUtils.ipAddressExists("3002:ABCD:0000:1234:ABCD:0000:0400:0000"));
		assertFalse(ipDetectorUtils.ipAddressExists("1234:5424:0000:1234:ABCD:0000:0400:0000"));
	}

	@Test
	public void testInvalidArgs() throws Exception {
		assertFalse(ipDetectorUtils.ipAddressExists(""));
		assertFalse(ipDetectorUtils.ipAddressExists(null));
		assertFalse(ipDetectorUtils.ipAddressExists("Not a IP address"));
	}
}
