/*
 * *
 *  * ${PROJECT_NAME}
 *  * Copyright (c) ${YEAR} Dennis Suchomsky <dennis.suchomsky@gmail.com>
 *  *
 *  *  This program is free software: you can redistribute it and/or modify
 *  *  it under the terms of the GNU General Public License as published by
 *  *  the Free Software Foundation, either version 3 of the License, or
 *  *  (at your option) any later version.
 *  *
 *  *  This program is distributed in the hope that it will be useful,
 *  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License
 *  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.suchomsky;

import java.io.File;
import java.sql.*;

/**
 * The type Persistent preferences.
 */
public class PersistentPreferences {


	/**
	 * The Filename default.
	 */
	static final String FILENAME_DEFAULT = "persistent_preferences.db";
	static final String TABLENAME_DEFAULT = "persistent_preferences";
	private static volatile Connection connection;
	private static volatile PersistentPreferences instance;

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("Error loading jdbc driver");
			e.printStackTrace();
		}
	}

	private String tableName;
	private String dbPath;

	/**
	 * Instantiates a new Persistent preferences.
	 */
	private PersistentPreferences() {
		this(TABLENAME_DEFAULT);
	}

	/**
	 * Instantiates a new Persistent preferences.
	 *
	 * @param tableName the table name
	 */
	private PersistentPreferences(String tableName) {
		this(tableName, FILENAME_DEFAULT);
	}

	/**
	 * Instantiates a new Persistent preferences.
	 *
	 * @param tableName the table name
	 * @param dbName    the db name
	 */
	private PersistentPreferences(String tableName, String dbName) {
		this.dbPath = System.getProperty("user.home") + "/" + dbName;
		this.tableName = tableName;

		initDBConnection();
		createTable();
	}

	/**
	 * Get Singleton instance
	 *
	 * @return instance of the class
	 */
	public static PersistentPreferences getInstance() {
		final PersistentPreferences currentInstance;
		if (instance == null) {
			synchronized (PersistentPreferences.class) {
				if (instance == null) {
					instance = new PersistentPreferences();
				}
				currentInstance = instance;
			}
		} else {
			currentInstance = instance;
		}
		return currentInstance;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (!connection.isClosed() && connection != null) {
			connection.close();
			if (connection.isClosed())
				System.out.println("Connection to Database closed");
		}
	}

	private void initDBConnection() {
		try {
			if (connection != null)
				return;
			System.out.println("Creating Connection to Database...");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
			if (!connection.isClosed())
				System.out.println("...Connection established");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					if (!connection.isClosed() && connection != null) {
						connection.close();
						if (connection.isClosed())
							System.out.println("Connection to Database closed");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private boolean createTable() {
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (keyfield, valuefield);");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Delete table.
	 */
	public boolean deleteTable() {
		try {
			File file = new File(dbPath);
			if (file.exists()) {
				file.delete();
			}
			return true;
		} catch (Exception e){
			return false;
		}
	}

	private boolean updateParam(String key, String value) {
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("UPDATE " + tableName + " SET valuefield='" + value + "' WHERE keyfield LIKE '" + key + "';");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	private boolean insertParam(String key, String value) {
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO " + tableName + " VALUES (?, ?);");
			ps.setString(1, key);
			ps.setString(2, value);
			ps.addBatch();
			connection.setAutoCommit(false);
			ps.executeBatch();
			connection.setAutoCommit(true);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Sets param.
	 *
	 * @param key   the key
	 * @param value the value
	 *
	 * @return the param
	 */
	public boolean setParam(String key, String value) {
		String oldValue = getParam(key);
		if (oldValue == null)
			return insertParam(key, value);
		else
			return updateParam(key, value);
	}

	/**
	 * Gets param.
	 *
	 * @param key the key
	 *
	 * @return the param
	 */
	public String getParam(String key) {
		String value = null;
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE keyfield='" + key + "';");
			rs.next();
			value = rs.getString("valuefield");
			rs.close();
			return value;
		} catch (SQLException e) {
			//System.err.println("Couldn't handle DB-Query" + e.getMessage());
			return value;
		}
	}
}