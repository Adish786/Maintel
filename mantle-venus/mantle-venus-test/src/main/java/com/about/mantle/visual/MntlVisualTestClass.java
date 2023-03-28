package com.about.mantle.visual;

import com.about.mantle.commons.MntlVisualCommonTestMethods;
import com.about.mantle.utils.ConfigProperties;
import com.about.mantle.utils.dbutils.DBUtils;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.selection.Browser;
import com.about.venus.core.driver.selection.Device;
import com.about.venus.core.driver.selection.DriverSelection.Matcher;
import com.about.venus.core.test.TestContext;
import com.about.venus.core.utils.CaptureScreen;
import com.about.venus.core.utils.ConfigurationProperties;
import io.restassured.response.Response;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;


public class MntlVisualTestClass extends MntlVenusTest implements MntlVisualCommonTestMethods{
	
	
	private  String path = null;
	protected  WebDriverExtended driver;
	protected  Matcher deviceType;
	private int tolerance = 100;
	private static final Logger logger = LoggerFactory.getLogger(MntlVisualTestClass.class);
	private static final Properties properties = ConfigProperties.loadFileProperties(ConfigProperties.NEBULA_PROPERTIES_FILE);
	private static final String actualFolder = "target/screenshot/current/";
	private static final String baseFolder = "target/screenshot/baseline/";
	private static final String diffFolder = "target/screenshot/difference/";

	public List<String> devEnvs = new ArrayList<String>();

	public MntlVisualTestClass(String path, Matcher device) {
		super();
		this.path = path;
		this.driver = classTest(device);
		this.deviceType = getVisualDeviceType();
	}
	
	public void visualTestMethod(List<VisualTestData> data, ErrorCollector collector) {
		
		// Variables declaration
		String baselinePage = properties.getProperty(ConfigProperties.NEBULA_BASELINE_PAGE);
		String updaterPage = properties.getProperty(ConfigProperties.NEBULA_UPDATER_PAGE);
		String imageFormat = properties.getProperty(ConfigProperties.NEBULA_IMAGE_FORMAT);
		String imageTitleColumn = properties.getProperty(ConfigProperties.NEBULA_DB_IMAGE_TITLE_COLUMN);
		String targetProject = ConfigurationProperties.getTargetProject(null);
		String testPlanTitle = TestContext.getInstance().getTestPlanTitle();
		String testCaseTitle = TestContext.getInstance().getTestCaseTitle();
		Runner runner = processPage(startBaseUrl(this.path, this.driver),data.get(0));
		String pageUrl = this.driver.currentUrlWithoutQuery();

		for (VisualTestData datum : data) {
			CaptureScreen screen   = new CaptureScreen(datum.getComponentUnderTest() + "-" + ConfigurationProperties.getDevice(Device.PC));
			screen.setScreenshotTitle(testPlanTitle, testCaseTitle, datum.getComponentUnderTest(), ConfigurationProperties.getSquadronEnvironment(null), ConfigurationProperties.getBrowser(Browser.Chrome).name().toLowerCase(), ConfigurationProperties.getDevice(Device.PC).name().toLowerCase().replace("_chromeemulator", ""), imageFormat);
			screen.actualScreenshot = testElement(runner.withCaptureScreen(screen), datum, screen);
			
			// This method is used to set Baseline and Current image in S3 bucket and inserting image details in DB
			screen = setBaseAndCurrent(screen, pageUrl);

			boolean notDevEnvNoBaseline = (!isDevEnv() && screen.baseScreenshot==null);
			if (notDevEnvNoBaseline){
				collector.checkThat(screen.getScreenshotTitle() + " is missing in baseline, please run visual test against dev env first!", notDevEnvNoBaseline, is(false));
			}
			else{
			// This method is used to compare screenshots irrespective of creation of image file. Used for Nebula
			// Setting image pixel difference tolerance, by default value is 100 otherwise the value which is passed from the vertical
			boolean hasDiff = screen.compareScreenshots(tolerance);
			String message = "Please click on the link to check Baseline image for the mentioned test case: " + properties.getProperty(ConfigProperties.NEBULA_APP_URL) + "/" + ConfigurationProperties.getTargetProject(null) + "/" + baselinePage + "?" + imageTitleColumn + "=" + screen.getScreenshotTitle().replace(imageFormat, "") +
					"\nScreenshots has been captured on the page: " + pageUrl;
    	
			// If difference would be there, add relevant image difference details in Database and mark it as failed test
			// update image only in last round
			if(hasDiff) {
				writeDataInUpdaterTable(screen, pageUrl);
				message = "Please check the link to get more details: " + properties.getProperty(ConfigProperties.NEBULA_APP_URL) + "/" + ConfigurationProperties.getTargetProject(null) + "/" + updaterPage + "?" + imageTitleColumn + "=" + screen.getScreenshotTitle().replace(imageFormat, "") + 
						"\nScreenshots has been captured on the page: " + pageUrl;
			}
			logger.info(message);
			collector.checkThat(message + "\nVisual test for "+ datum.getComponentUnderTest() +" failed. Diff Location is "
										+ "\n https://s3.amazonaws.com/dd-qa-tests-output/visualTest/BUILD_NUMBER/"+ConfigurationProperties.getTargetProject(null)
										+"/screenshots/diff/"+datum.getComponentUnderTest()+"-"+ ConfigurationProperties.getDevice(Device.PC).name().toLowerCase() + imageFormat
					, hasDiff, is(false));
			}
		}
	}
	
	public void visualTestMethod(List<VisualTestData> data, ErrorCollector collector, int tolerance) {
		this.tolerance = tolerance;
		visualTestMethod(data,collector);
	}
	
	protected Runner processPage(Runner runner ,VisualTestData data) {
		return runner
				.loadUrl(data.isProctorTest())
	       		.scrollTop()
	    		.fullPageScroll(data.isWithFullScroll()) // ideally you should not need to hide global element for a component // need work
	    		.deleteComponents(data.deleteGlobalComps)
	    		.dummyComponents(data.dummyGlobalComps)
	    		.hideBlocks(data.hideGlobalComps);		   			
	}
	
	protected Screenshot testElement(Runner runner ,VisualTestData data, CaptureScreen screen) {
		return runner
    			.clickElement(data.getElementToClick(),data.getEnableClick())
    			.deleteComponents(data.getDeleteComps())
    			.dummyComponents(data.getDummyComps())
    			.hideBlocks(data.getHideComps())
    			.hideAttribute(data.getHideAttributes())
    			.setAttribute(data.getUpdateAttributes(), data.getAttributeValues())
    			.locateElement(data.getElementLocator())
    			.captureElement();
	}

	public boolean isDevEnv(){
		devEnvs.add("k8s-lifeco-dev");
		devEnvs.add("k8s-beautytrain-dev");
		devEnvs.add("k8s-commerce-dev");
		devEnvs.add("k8s-finance-dev");
		devEnvs.add("k8s-spruce-dev");
		devEnvs.add("k8s-prm-dev");
		devEnvs.add("k8s-health-dev");
		devEnvs.add("k8s-food-dev");
		devEnvs.add("k8s-money-dev");
		String currentEnv = ConfigurationProperties.getSquadronEnvironment(null);
		if (currentEnv!= null && devEnvs.contains(currentEnv)) {return true;};
		return false;
	}
	

	public void tearDown() {
		closeDriver(this.driver);
	}

	// This method is used for Nebula application to set Baseline and Current Image.
	public CaptureScreen setBaseAndCurrent(CaptureScreen screen, String pageUrl) {

		// Variables Declaration
		String screenshotTitle = screen.getScreenshotTitle();
		String baselineTableSuffix = properties.getProperty(ConfigProperties.NEBULA_BASELINE_TABLE_SUFFIX);
		String imageFormat = properties.getProperty(ConfigProperties.NEBULA_IMAGE_FORMAT);
		// Array split to get currently executing test details
		String[] titleArr = screenshotTitle.replace(imageFormat, "").split("_");
		String targetProject = ConfigurationProperties.getTargetProject(null);
		String table = targetProject + baselineTableSuffix;
		String testPlan = titleArr[0];
		String testCase = titleArr[1];
		String testComponent = titleArr[2];
		String browser = titleArr[3];
		String device = titleArr[4];
		String imageTitleColumn = properties.getProperty(ConfigProperties.NEBULA_DB_IMAGE_TITLE_COLUMN);
		String baselineImageColumn = properties.getProperty(ConfigProperties.NEBULA_DB_BASELINE_IMAGE_COLUMN);
		String updatedBy = "Automated Job";
		String nebulaAppUrl = properties.getProperty(ConfigProperties.NEBULA_APP_URL);
		String s3ImageApiServlet = properties.getProperty(ConfigProperties.NEBULA_S3_IMAGE_API_SERVLET);
		
		try (
				// Establish DB connection
				Connection connection = DBUtils.nebulaDBConnect();

				// Create and execute a statement to check whether baseline image details exist in baseline Table
				ResultSet resultSet = DBUtils.executeSelectQuery(connection, "Select " + baselineImageColumn + " from \"" + table + "\" where " + imageTitleColumn + " = '"+ screenshotTitle +"'");
			)
		{	
			baseURI = nebulaAppUrl + "/" + s3ImageApiServlet;
			// Iterating through a result set. It must will have only one record depending on image name which is Unique
			if(resultSet.next()) {
                // If baseline image record found in DB, retrieve the image from S3 bucket and store it in specified folder
				Screenshot baseScreenshot = null;
                byte[] baseScreenshotArr = fetchS3BucketImage(baseURI, resultSet.getString(1));
                
                // Convert byte array image object to Ashot screenshot
				try {
					baseScreenshot = new Screenshot(ImageIO.read(new ByteArrayInputStream(baseScreenshotArr)));
				} catch (IOException e) {
					e.printStackTrace();
				}

				if(baseScreenshot != null)
					screen.baseScreenshot = baseScreenshot;

				logger.info("Baseline screenshot '" + screenshotTitle + "' already exist in table '" + table + "'. Hence, using it as a baseline screenshot.");
				logger.info("Captured screenshot has been set to Current screenshot");
				screen.createImageFile(baseScreenshot.getImage(), baseFolder + screenshotTitle);
			} else {

				if (isDevEnv()) {
				// Assuming the test has been executed for first time
				String folderPath = ConfigurationProperties.getTargetProject(null) + "/" + properties.getProperty(ConfigProperties.NEBULA_BASELINE_FOLDER) + "/" + screenshotTitle;
				
				// If no baseline image details found, Baseline and Current Screenshot will be treated as same
				if(screen.actualScreenshot != null) {
					screen.baseScreenshot = screen.actualScreenshot;
				}
				logger.info("Baseline screenshot '" + screenshotTitle + "' doesn't exist in table '" + table + "'. Hence, inserting a captured screenshot details in table as a baseline screenshot details");
				screen.createImageFile(screen.baseScreenshot.getImage(), baseFolder + screenshotTitle);
				
				File imageFile = new File(baseFolder + screenshotTitle); 
				String baselineImageUrl = UploadImageToS3Bucket(baseURI, imageFile, folderPath);
				
				if(baselineImageUrl != null && !baselineImageUrl.equals("")) {
					// Variables declaration
					String testPlanColumn = properties.getProperty(ConfigProperties.NEBULA_DB_TEST_PLAN_COLUMN);
					String testCaseColumn = properties.getProperty(ConfigProperties.NEBULA_DB_TEST_CASE_COLUMN);
					String testComponentColumn = properties.getProperty(ConfigProperties.NEBULA_DB_TEST_COMPONENT_COLUMN);
					String verticalColumn = properties.getProperty(ConfigProperties.NEBULA_DB_VERTICAL_COLUMN);
	                String environmentColumn = properties.getProperty(ConfigProperties.NEBULA_DB_ENVIRONMENT_COLUMN);
					String browserColumn = properties.getProperty(ConfigProperties.NEBULA_DB_BROWSER_COLUMN);
					String deviceColumn = properties.getProperty(ConfigProperties.NEBULA_DB_DEVICE_COLUMN);
					String pageUrlColumn = properties.getProperty(ConfigProperties.NEBULA_DB_PAGE_URL_COLUMN);
					String updatedOnColumn = properties.getProperty(ConfigProperties.NEBULA_DB_UPDATED_ON_COLUMN);
					String updatedByColumn = properties.getProperty(ConfigProperties.NEBULA_DB_UPDATED_BY_COLUMN);
					
					// Insert captured screenshot in table as a baseline screenshot
					LinkedHashMap<String, Object> entities = new LinkedHashMap<String, Object>();
					entities.put(testPlanColumn, testPlan);
					entities.put(testCaseColumn, testCase);
					entities.put(testComponentColumn, testComponent);
					entities.put(imageTitleColumn, screenshotTitle);
					entities.put(baselineImageColumn, baselineImageUrl);
					entities.put(verticalColumn, ConfigurationProperties.getTargetProject(null));
					entities.put(environmentColumn, ConfigurationProperties.getSquadronEnvironment(null));
					entities.put(browserColumn, browser);
					entities.put(deviceColumn, device);
					entities.put(pageUrlColumn, pageUrl);
					LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("America/New_York"));
					entities.put(updatedOnColumn, localDateTime);
					entities.put(updatedByColumn, updatedBy);
					DBUtils.executeInsertQuery(connection, table, entities);
					logger.info("Successfully inserted captured screenshot as a baseline screenshot in the table '" + table + "'");
					logger.info("Screenshot title: " + screenshotTitle);
				}
				} else {
					logger.info("Please run the visual test for " + testCase + " against dev env first!");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return screen;
	}

	// This method is used for Nebula application to add the relevant image difference details in Database.
	public void writeDataInUpdaterTable(CaptureScreen screen, String pageUrl) {
	
		// Variables declaration
		String screenshotTitle = screen.getScreenshotTitle();
		String updaterTableSuffix = properties.getProperty(ConfigProperties.NEBULA_UPDATER_TABLE_SUFFIX);
		String imageTitleColumn = properties.getProperty(ConfigProperties.NEBULA_DB_IMAGE_TITLE_COLUMN);
		String table = ConfigurationProperties.getTargetProject(null) + updaterTableSuffix;
		String updated_by = "Automated Job";
		String nebulaAppUrl = properties.getProperty(ConfigProperties.NEBULA_APP_URL);
		String s3ImageApiServlet = properties.getProperty(ConfigProperties.NEBULA_S3_IMAGE_API_SERVLET);
		baseURI = nebulaAppUrl + "/" + s3ImageApiServlet;
		logger.info("Found difference in Baseline and Current screenshot");
		screen.createImageFile(screen.actualScreenshot.getImage(), actualFolder + screenshotTitle);
		screen.createImageFile(screen.imageDiff().getMarkedImage(), diffFolder + screenshotTitle);

		try (
				// Establish DB connection
				Connection connection = DBUtils.nebulaDBConnect();

				// Create and execute a statement to check whether a record already exists in updater table or not.
				// If exists, update the record. If not exists, insert the record.
				ResultSet resultSet = DBUtils.executeSelectQuery(connection, "Select " + imageTitleColumn + " from \"" + table + "\" where " + imageTitleColumn + " = '"+ screenshotTitle +"'");
			) 
		{
			LinkedHashMap<String, Object> entities = new LinkedHashMap<String, Object>();
			String currentImageColumn = properties.getProperty(ConfigProperties.NEBULA_DB_CURRENT_IMAGE_COLUMN);
			String differenceImageColumn = properties.getProperty(ConfigProperties.NEBULA_DB_DIFFERENCE_IMAGE_COLUMN);
			String updatedOnColumn = properties.getProperty(ConfigProperties.NEBULA_DB_UPDATED_ON_COLUMN);
			String updatedByColumn = properties.getProperty(ConfigProperties.NEBULA_DB_UPDATED_BY_COLUMN);
			String envColumn = properties.getProperty(ConfigProperties.NEBULA_DB_ENVIRONMENT_COLUMN);
			String currentENV = ConfigurationProperties.getSquadronEnvironment(null);

			// Upload Current and Difference images to S3 bucket
			String currentFolderPath = ConfigurationProperties.getTargetProject(null) + "/" + properties.getProperty(ConfigProperties.NEBULA_CURRENT_FOLDER) + "/" + screenshotTitle;
			File imageFile = new File(actualFolder + screenshotTitle); 
			String currentImageUrl = UploadImageToS3Bucket(baseURI, imageFile, currentFolderPath);
			
			String differenceFolderPath = ConfigurationProperties.getTargetProject(null) + "/" + properties.getProperty(ConfigProperties.NEBULA_DIFFERENCE_FOLDER) + "/" + screenshotTitle;
			imageFile = new File(diffFolder + screenshotTitle); 
			String differenceImageUrl = UploadImageToS3Bucket(baseURI, imageFile, differenceFolderPath);
			
			// If successful, insert the details in the DB
			if(currentImageUrl != null && !currentImageUrl.equals("") && differenceImageUrl != null && !differenceImageUrl.equals("")) {
			
				entities.put(currentImageColumn, currentImageUrl);
				entities.put(differenceImageColumn, differenceImageUrl);
				LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("America/New_York"));
				entities.put(updatedOnColumn, localDateTime);
				entities.put(updatedByColumn, updated_by);
				entities.put(envColumn, currentENV);
				// If condition: If result set is empty. It means there is no record present in updater table. Hence, insert all details in updater table
				// Else condition: Iterating through a result set. It must will have only one entry depending on image name which is Unique. Update image details in existing record
				if(!resultSet.next()) {
					entities.put(imageTitleColumn, screenshotTitle);
					DBUtils.executeInsertQuery(connection, table, entities);
					logger.info("Successfully inserted a record including Baseline, Current and Difference image Url in the table '" + table + "' for the Screenshot title: " + screenshotTitle);
				} else {
					LinkedHashMap<String, Object> whereConditionMap = new LinkedHashMap<String, Object>();
					whereConditionMap.put(imageTitleColumn, screenshotTitle);
					DBUtils.executeUpdateQuery(connection, table, entities, whereConditionMap);
					logger.info("Successfully updated a record in the table '" + table + "' for the Screenshot title: " + screenshotTitle);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private byte[] fetchS3BucketImage(String baseURI, String imageUrl) {
		Response response = given().
				param(properties.getProperty(ConfigProperties.NEBULA_S3_API_IMAGE_URL_PARAM), imageUrl).
				when().
				get(baseURI);
		
		// If response code is 200, convert the image object as byte array
		if(response.getStatusCode() == 200) {
			return response.asByteArray();
		} else {
			logger.info("Not able to fetch the image object from S3 bucket. Received response code as: " + response.getStatusCode());
			return null;
		}
	}
	
	private String UploadImageToS3Bucket(String baseURI, File imageFile, String folderPath) {
		Response response = given().
				multiPart(imageFile).
				param(properties.getProperty(ConfigProperties.NEBULA_S3_API_FOLDER_PATH_PARAM), folderPath).
				when().
				post(baseURI);
		
		if(response.getStatusCode() == 201) {
			return response.jsonPath().get("url");
		} else {
			logger.info("Not able to upload the image object to S3 bucket. Received response code as: " + response.getStatusCode());
			return null;
		}
	}
}
