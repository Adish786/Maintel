package com.about.mantle.http.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

/**
 * This class is responsible for detecting About ip addresses
 */
public class MantleIpDetectorUtils {
	private static Logger logger = LoggerFactory.getLogger(MantleIpDetectorUtils.class);

	private List<IpAddressMatcher> ipAddressMatchers;

	public MantleIpDetectorUtils(String[] cidrArr) {
		initialize(cidrArr);
	}

	/**
	 *
	 * @param cidrArr
	 */
	private void initialize(String[] cidrArr) {
		ipAddressMatchers = new ArrayList<>();
		for (String address : cidrArr) {
			ipAddressMatchers.add(new IpAddressMatcher(address));
		}
	}

	/**
	 * @param ipAddress an ip address
	 * @return true if the given ip address falls within the range of a dotdash cidr, false otherwise
	 */
	public boolean ipAddressExists(String ipAddress) {
		if (StringUtils.isNotEmpty(ipAddress)) {
			for (IpAddressMatcher matcher : ipAddressMatchers) {
				try {
					if (matcher.matches(ipAddress))
						return true;
				} catch (IllegalArgumentException e) {
					break;
				}
			}
		}
		return false;
	}
}
