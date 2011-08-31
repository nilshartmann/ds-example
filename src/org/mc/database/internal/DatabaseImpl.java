package org.mc.database.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Map;

import org.mc.database.Database;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.metatype.Configurable;

@Component(configurationPolicy = ConfigurationPolicy.require)
public class DatabaseImpl implements Database {

	private Connection _connection;

	interface Config {

		String classname();

		String url();

		String user();

		String password();

	}

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		System.out.printf("DatabaseImpl activated with: %s%n", properties);
		Config config = Configurable.createConfigurable(Config.class,
				properties);
		Class.forName(config.classname());
		_connection = DriverManager.getConnection(config.url(), config.user(),
				config.password());

		try {
			_connection
					.createStatement()
					.execute(
							"CREATE TABLE customer(id INTEGER, firstname VARCHAR(256), familyname VARCHAR(256))");
			System.out.println("Database created");
			
		} catch (Exception ex) {
			// ignore
		}
		try {
		_connection.createStatement().execute(
				"INSERT INTO customer VALUES(1, 'Nils', 'Hartmann')");
		System.out.println("Database populated");
		} catch (Exception ex) {
			ex.printStackTrace();
			// ignore
		}
		System.out.println("Database Service ready to use");

	}

	@Deactivate
	protected void shutdown() throws Exception {
		System.out.printf("Closing JDBC Database");
		_connection.close();
	}

	@Override
	public Map<String, String> getSingeResult(String sqlQuery) {
		try {
			System.out.printf("Executing query: '%s'%n", sqlQuery);
			ResultSet resultSet = _connection.createStatement().executeQuery(
					sqlQuery);
			if (resultSet.next() == false) {
				System.out.println("no hit");
				return new Hashtable<String, String>();
			}

			Hashtable<String, String> result = new Hashtable<String, String>();
			int columns = resultSet.getMetaData().getColumnCount();
			
			for (int i=0;i<columns;i++) {
				String columnName = resultSet.getMetaData().getColumnName(i+1);
				result.put(columnName.toLowerCase(), String.valueOf(resultSet.getObject(columnName)));
			}

//			result.put("firstname", resultSet.getString("firstname"));
//			result.put("familyname", resultSet.getString("familyname"));

			System.out.printf("SQL result: %s%n", result);
			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

}
