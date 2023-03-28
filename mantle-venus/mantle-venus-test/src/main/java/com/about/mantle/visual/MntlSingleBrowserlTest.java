package com.about.mantle.visual;

import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.driver.WebDriverExtended;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import javax.annotation.concurrent.NotThreadSafe;

import static com.about.venus.core.driver.selection.Device.Any;
import static com.about.venus.core.driver.selection.DriverSelection.Matcher.devices;

@NotThreadSafe
@FixMethodOrder(MethodSorters.DEFAULT)
public class MntlSingleBrowserlTest extends MntlVenusTest {
	private static WebDriverExtended driver = null;

	@BeforeClass
	public static void setUp() {
		driver = classTest(devices(Any));
		driver.proxy();
	}

	@AfterClass
	public static void tesrDown() {
		closeDriver(driver);

	}

}
