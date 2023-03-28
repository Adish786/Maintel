package com.about.mantle.utils.dbutils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.utils.ConfigProperties;
import com.about.mantle.utils.servicediscovery.NebulaServiceDiscovery;

public class DBUtils {
	private static final Logger logger = LoggerFactory.getLogger(DBUtils.class);

	public static synchronized Connection nebulaDBConnect() {

		// Variables Declaration
		Properties properties = ConfigProperties.loadFileProperties(ConfigProperties.NEBULA_PROPERTIES_FILE);
		Connection connection = null;
		String driver = properties.getProperty(ConfigProperties.NEBULA_DB_DRIVER);
		String username = properties.getProperty(ConfigProperties.NEBULA_DB_USERNAME);
		String password = properties.getProperty(ConfigProperties.NEBULA_DB_PASSWORD);
		String dbTitle = properties.getProperty(ConfigProperties.NEBULA_DB_TITLE);
		NebulaServiceDiscovery serviceDiscovery = new NebulaServiceDiscovery();
		driver = serviceDiscovery.postgresConnectionConfiguration().getProperty(driver);
		username = serviceDiscovery.postgresConnectionConfiguration().getProperty(username);
		password = "T3H6QR94=r&b4&RN";//serviceDiscovery.postgresConnectionConfiguration().getSecuredProperty(password);
		dbTitle = serviceDiscovery.postgresConnectionConfiguration().getProperty(dbTitle);
		String dbUrl = serviceDiscovery.getJDBCUrl();

		try {
			// Load PostgreSql driver
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// Connect to AWS visual test DB
			connection = DriverManager.getConnection(dbUrl, username, password);
			logger.info(String.format("Successfully connected to database : %s with URL : %s", dbTitle, dbUrl));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

	public static synchronized boolean deleteTableRows(Connection connection, String table) {

		boolean flag = false;
		try (Statement statement = connection.createStatement(); )
		{
			statement.executeUpdate(String.format("TRUNCATE TABLE \"%s\"", table));
			logger.info(String.format("Deleted all rows from table : %s", table));
			flag = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	public static synchronized boolean dropTable(Connection connection, String table) {

		boolean flag = false;
		try (Statement statement = connection.createStatement(); )
		{
			statement.executeUpdate(String.format("DROP TABLE \"%s\"", table));
			logger.info(String.format("Dropped a table from database : %s", table));
			flag = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	public static synchronized boolean deleteTableColumn(Connection connection, String table, String column) {
		boolean flag = false;

		try (Statement statement = connection.createStatement(); )
		{
			statement.executeUpdate(String.format("ALTER TABLE \"%s\" DROP COLUMN %s", table, column));
			logger.info(String.format("Dropped a column %s from table : %s", column, table));
			flag = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return flag;
	}

	public static synchronized boolean executeInsertQuery(Connection connection, String table, LinkedHashMap<String, Object> entitiesMap) {
		boolean flag = false;
		List<Object> valuesList = new ArrayList<Object>();
		String query = "INSERT INTO \"" + table + "\" (";
		int size = entitiesMap.size();
		int counter = 0;
		for(Map.Entry<String, Object> entity : entitiesMap.entrySet()) {
			if(counter != size - 1) {
				query += entity.getKey() + ", ";
				counter++;
			} else {
				query += entity.getKey() + ")";
			}
			valuesList.add(entity.getValue());
		}
		query += " VALUES (";

		for(counter = 0; counter < size; counter++) {
			if(counter != size - 1) {
				query += "?, ";
			} else {
				query += "?)";
			}
		}

		try (PreparedStatement prepStatement = connection.prepareStatement(query);) {

			for(counter = 0; counter < size; counter++) {
				if(valuesList.get(counter) instanceof String) {
					prepStatement.setString(counter + 1,(String) valuesList.get(counter));
				} else if(valuesList.get(counter) instanceof InputStream) {
					prepStatement.setBinaryStream(counter + 1,(InputStream) valuesList.get(counter));
				} else if(valuesList.get(counter) instanceof Integer) {
					prepStatement.setInt(counter + 1,(Integer) valuesList.get(counter));
				} else if(valuesList.get(counter) instanceof Date) {
					prepStatement.setDate(counter + 1,(Date) valuesList.get(counter));
				} else if(valuesList.get(counter) instanceof Double) {
					prepStatement.setDouble(counter + 1, (Double) valuesList.get(counter));
				} else if(valuesList.get(counter) instanceof LocalDateTime) {
					prepStatement.setObject(counter + 1, (LocalDateTime) valuesList.get(counter));
				}
			}
			prepStatement.executeUpdate();
			logger.info("Successfully inserted a row in table: " + table);
			flag = true;
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
		return flag;
	}

	public static synchronized boolean executeUpdateQuery(Connection connection, String table, LinkedHashMap<String, Object> entitiesMap, LinkedHashMap<String, Object> whereConditionMap) {
		boolean flag = false;
		List<Object> valuesList = new ArrayList<Object>();
		List<Object> whereValuesList = new ArrayList<Object>();
		String query = "UPDATE \"" + table + "\" SET";
		int size = entitiesMap.size();
		int counter = 0;
		for(Map.Entry<String, Object> entity : entitiesMap.entrySet()) {
			if(counter != size - 1) {
				query += " " + entity.getKey() + "=?,";
				counter++;
			} else {
				query += " " + entity.getKey() + "=?";
			}
			valuesList.add(entity.getValue());
		}
		if(whereConditionMap != null) {
			query += " WHERE";
			counter = 0;
			size = whereConditionMap.size();
			for(Map.Entry<String, Object> condition : whereConditionMap.entrySet()) {
				if(counter != size - 1) {
					query += " " + condition.getKey() + "=?,";
					counter++;
				} else {
					query += " " + condition.getKey() + "=?";
				}
				whereValuesList.add(condition.getValue());
			}
		}

		try (PreparedStatement prepStatement = connection.prepareStatement(query);) {
			for(counter = 0; counter < entitiesMap.size(); counter++) {
				if(valuesList.get(counter) instanceof String) {
					prepStatement.setString(counter + 1,(String) valuesList.get(counter));
				} else if(valuesList.get(counter) instanceof InputStream) {
					prepStatement.setBinaryStream(counter + 1,(InputStream) valuesList.get(counter));
				} else if(valuesList.get(counter) instanceof Integer) {
					prepStatement.setInt(counter + 1,(Integer) valuesList.get(counter));
				} else if(valuesList.get(counter) instanceof Date) {
					prepStatement.setDate(counter + 1,(Date) valuesList.get(counter));
				} else if(valuesList.get(counter) instanceof Double) {
					prepStatement.setDouble(counter + 1, (Double) valuesList.get(counter));
				} else if(valuesList.get(counter) instanceof LocalDateTime) {
					prepStatement.setObject(counter + 1, (LocalDateTime) valuesList.get(counter));
				}
			}

			if(whereConditionMap != null) {
				for(int inLoop = 0; inLoop < whereConditionMap.size(); inLoop++) {
					if(whereValuesList.get(inLoop) instanceof String) {
						prepStatement.setString(counter + 1,(String) whereValuesList.get(inLoop));
					} else if(whereValuesList.get(inLoop) instanceof InputStream) {
						prepStatement.setBinaryStream(counter + 1,(InputStream) whereValuesList.get(inLoop));
					} else if(whereValuesList.get(inLoop) instanceof Integer) {
						prepStatement.setInt(counter + 1,(Integer) whereValuesList.get(inLoop));
					} else if(whereValuesList.get(inLoop) instanceof Date) {
						prepStatement.setDate(counter + 1,(Date) whereValuesList.get(inLoop));
					} else if(whereValuesList.get(inLoop) instanceof Double) {
						prepStatement.setDouble(counter + 1, (Double) whereValuesList.get(inLoop));
					} else if(whereValuesList.get(inLoop) instanceof LocalDateTime) {
						prepStatement.setObject(counter + 1, (LocalDateTime) whereValuesList.get(inLoop));
					}
				}
			}

			prepStatement.executeUpdate();
			if(whereConditionMap != null) {
				logger.info("Successfully updated a row in table: " + table);
			} else {
				logger.info("Successfully updated all rows in table: " + table);
			}
			flag = true;
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
		return flag;
	}

	public static synchronized boolean executeDeleteQuery(Connection connection, String table, LinkedHashMap<String, Object> entitiesMap) {
		boolean flag = false;
		String query = "DELETE FROM \"" + table +"\"";
		List<Object> valuesList = new ArrayList<Object>();
		int counter = 0;

		if(entitiesMap != null) {
			query += " WHERE";
			int size = entitiesMap.size();
			for(Map.Entry<String, Object> entity : entitiesMap.entrySet()) {
				if(counter != size - 1) {
					query += " " + entity.getKey() + "=?,";
					counter++;
				} else {
					query += " " + entity.getKey() + "=?)";
				}
				valuesList.add(entity.getValue());
			}
		}

		try (PreparedStatement prepStatement = connection.prepareStatement(query);) {
			if(entitiesMap != null) {
				for(counter = 0; counter < entitiesMap.size(); counter++) {
					if(valuesList.get(counter) instanceof String) {
						prepStatement.setString(counter + 1,(String) valuesList.get(counter));
					} else if(valuesList.get(counter) instanceof InputStream) {
						prepStatement.setBinaryStream(counter + 1,(InputStream) valuesList.get(counter));
					} else if(valuesList.get(counter) instanceof Integer) {
						prepStatement.setInt(counter + 1,(Integer) valuesList.get(counter));
					} else if(valuesList.get(counter) instanceof Date) {
						prepStatement.setDate(counter + 1,(Date) valuesList.get(counter));
					} else if(valuesList.get(counter) instanceof Double) {
						prepStatement.setDouble(counter + 1, (Double) valuesList.get(counter));
					} else if(valuesList.get(counter) instanceof LocalDateTime) {
						prepStatement.setObject(counter + 1, (LocalDateTime) valuesList.get(counter));
					}
				}
			}
			prepStatement.executeUpdate();
			if(entitiesMap != null) {
				logger.info("Successfully deleted a row in table: " + table);
			} else {
				logger.info("Successfully deleted all rows from table: " + table);
			}
			flag = true;
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
		return flag;
	}

	public static synchronized ResultSet executeSelectQuery(Connection connection, String query) {
		ResultSet resultSet = null;
		try (Statement statement = connection.createStatement();) {
			resultSet = statement.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultSet;
	}
}
