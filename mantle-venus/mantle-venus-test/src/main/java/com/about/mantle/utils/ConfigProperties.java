package com.about.mantle.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {
	
	// Variables declaration
	public static final String NEBULA_PROPERTIES_FILE = "nebula.properties";
	public static final String NEBULA_APP_TITLE = "nebulaAppTitle";
	public static final String NEBULA_SQUADRON_DB_RESOURCE = "nebulaSquadronDBResouce";
	public static final String NEBULA_JDBC_DRIVER = "nebulaJDBCDriver";
	public static final String NEBULA_DB_DRIVER = "nebulaDBDriver";
	public static final String NEBULA_DB_PORT = "nebulaDBPort";
	public static final String NEBULA_DB_TITLE = "nebulaDBTitle";
	public static final String NEBULA_DB_USERNAME = "nebulaDBUsername";
	public static final String NEBULA_DB_PASSWORD = "nebulaDBPassword";
	public static final String NEBULA_QUERY_PARAM = "nebulaQueryParam";
	public static final String NEBULA_BASELINE_PAGE = "nebulaBaselinePage";
	public static final String NEBULA_UPDATER_PAGE = "nebulaUpdaterPage";
	public static final String NEBULA_BASELINE_TABLE_SUFFIX = "nebulaBaselineTableSuffix";
	public static final String NEBULA_UPDATER_TABLE_SUFFIX = "nebulaUpdaterTableSuffix";
	public static final String NEBULA_IMAGE_FORMAT = "nebulaImageFormat";
	public static final String NEBULA_DB_TEST_PLAN_COLUMN = "nebulaDBTestPlanColumn";
	public static final String NEBULA_DB_TEST_CASE_COLUMN = "nebulaDBTestCaseColumn"; 
	public static final String NEBULA_DB_TEST_COMPONENT_COLUMN = "nebulaDBTestComponentColumn";
	public static final String NEBULA_DB_IMAGE_TITLE_COLUMN = "nebulaDBImageTitleColumn"; 
	public static final String NEBULA_DB_BASELINE_IMAGE_COLUMN = "nebulaDBBaselineImageColumn"; 
	public static final String NEBULA_DB_CURRENT_IMAGE_COLUMN = "nebulaDBCurrentImageColumn"; 
	public static final String NEBULA_DB_DIFFERENCE_IMAGE_COLUMN = "nebulaDBDifferenceImageColumn"; 
	public static final String NEBULA_DB_VERTICAL_COLUMN = "nebulaDBVerticalColumn";
    public static final String NEBULA_DB_ENVIRONMENT_COLUMN = "nebulaDBEnvironmentColumn";
	public static final String NEBULA_DB_BROWSER_COLUMN = "nebulaDBBrowserColumn"; 
	public static final String NEBULA_DB_DEVICE_COLUMN = "nebulaDBDeviceColumn"; 
	public static final String NEBULA_DB_PAGE_URL_COLUMN = "nebulaDBPageUrlColumn";
	public static final String NEBULA_DB_UPDATED_ON_COLUMN = "nebulaDBUpdatedOnColumn";
	public static final String NEBULA_DB_UPDATED_BY_COLUMN = "nebulaDBUpdatedByColumn";
	public static final String NEBULA_BASELINE_FOLDER = "nebulaBaselineFolder";
	public static final String NEBULA_CURRENT_FOLDER = "nebulaCurrentFolder";
	public static final String NEBULA_DIFFERENCE_FOLDER = "nebulaDifferenceFolder";
	public static final String NEBULA_SQUADRON_ENVIRONMENT = "nebulaSquadronEnvironment";
	public static final String NEBULA_CONSUL_ENABLED = "nebulaConsulEnabled";
	public static final String NEBULA_CONSUL_URL = "nebulaConsulUrl";
	public static final String NEBULA_CONSUL_TOKEN = "nebulaConsulToken";
	public static final String NEBULA_AWS_PROFILE = "nebulaAWSProfile";
	public static final String NEBULA_AWS_REGION = "nebulaAWSRegion";
	public static final String NEBULA_APP_URL = "nebulaAppUrl";
	public static final String NEBULA_S3_BUCKET_TITLE = "nebulaS3BucketTitle";
	public static final String NEBULA_S3_BUCKET_URL = "nebulaS3BucketUrl";
	public static final String NEBULA_S3_API_IMAGE_URL_PARAM = "nebulaS3ApiImageUrlParam";
	public static final String NEBULA_S3_API_FOLDER_PATH_PARAM = "nebulaS3ApiFolderPathParam";
	public static final String NEBULA_S3_IMAGE_API_SERVLET = "nebulaS3ImageApiServlet";
	
	public static Properties loadFileProperties(String file) {
		Properties properties = new Properties();
		InputStream inStream = ClassLoader.getSystemResourceAsStream(file);
		
		try {
			properties.load(inStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return properties;
	}
}