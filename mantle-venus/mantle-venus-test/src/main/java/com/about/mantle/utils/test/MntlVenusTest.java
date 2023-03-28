package com.about.mantle.utils.test;

import com.about.mantle.utils.test.data.RemoteTestDataExtension;
import com.about.mantle.utils.test.data.TestDataRule;
import com.about.mantle.utils.url.URLUtils;
import com.about.mantle.venus.utils.MntlConfigurationProperties;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.factory.WebDriverFactory;
import com.about.venus.core.driver.factory.feature.Feature;
import com.about.venus.core.driver.selection.Browser;
import com.about.venus.core.driver.selection.DriverSelection;
import com.about.venus.core.test.VenusTest;
import com.about.venus.core.utils.ConfigurationProperties;
import org.junit.Assume;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EnableRuleMigrationSupport
@ExtendWith(TestDataRule.class)
@ExtendWith(RemoteTestDataExtension.class)
public class MntlVenusTest extends VenusTest {

	private static final Logger logger = LoggerFactory.getLogger(MntlVenusTest.class);

	/**
	 * Will check if the current BaseSeleneUrl is production or dev env
	 * @return boolean
	 */
	public boolean isProdSelene() {
		return new URLUtils(MntlConfigurationProperties.getTargetSelene(null)).isProdSelene();
	}

	/**
	 * Will check if the current BaseUrl is production or dev env
	 * @return boolean
	 */
	public boolean isProd() {
		return new URLUtils(MntlConfigurationProperties.getTargetProjectBaseUrl(null)).isProd();
	}

	protected static WebDriverExtended classTest(DriverSelection.Matcher driverMatcher, Feature... features) {
		WebDriverExtended driver = null;
		if (ConfigurationProperties.getBrowser(Browser.Chrome).is(Browser.IE)
				|| ConfigurationProperties.getBrowser(Browser.Chrome).is(Browser.Edge)) {
			Assume.assumeTrue("Skipping test ...IE/Edge driver does not support proxy test", false);
		} else {
			driver = getClassDriver(driverMatcher, features);
			logger.info("driverObjectInstance is  " + driver.toString());
		}
		return driver;
	}

	protected static WebDriverExtended getClassDriver(DriverSelection.Matcher driverMatcher, Feature... features) {
		WebDriverExtended driver = (new WebDriverFactory()).getDriver(driverMatcher, features);
		Assume.assumeTrue("Skipping no driver matches for selection", driver != null);
		return driver;
	}

	protected static void closeDriver(WebDriverExtended driver) {
		if (driver != null)
			driver.quit();
	}
}
