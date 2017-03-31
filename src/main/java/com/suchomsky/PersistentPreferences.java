/**
 * 
 */
package com.suchomsky;

import java.io.File;
import java.sql.*;

class PersistentPreferences {

    private static Connection connection;
    private static final String DB_PATH = System.getProperty("user.home") + "/" + "persistent_preferences.db";
    private String tableName;

    static {
	try {
	    Class.forName("org.sqlite.JDBC");
	} catch (ClassNotFoundException e) {
	    System.err.println("Error loading jdbc driver");
	    e.printStackTrace();
	}
    }

    public PersistentPreferences(String tableName) {
        initDBConnection();
        this.tableName = tableName;
        createTable();
    }

    private void initDBConnection() {
	try {
	    if (connection != null)
		return;
	    System.out.println("Creating Connection to Database...");
	    connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
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

    public void deleteTable(){
		File file = new File(DB_PATH);
		if (file.exists()){
			file.delete();
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

    public boolean setParam(String key, String value) {
	String oldValue = getParam(key);
	if (oldValue == null)
	    return insertParam(key, value);
	else
	    return updateParam(key, value);
    }

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