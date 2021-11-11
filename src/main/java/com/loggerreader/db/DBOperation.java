package com.loggerreader.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loggerreader.Event;


public class DBOperation implements AutoCloseable {

	 private static Logger log = LoggerFactory.getLogger(DBOperation.class);
	 private Connection connection;
	 private final static String sql = "INSERT INTO event (id, duration, type, host, alert)  VALUES (?, ?, ?, ?, ?)";

	 public DBOperation(Connection connection) {
	        this.connection = connection;
	    }
	 
	 public boolean InsertRecordsIntoTable(Event event) {
		 
		 try {
	            PreparedStatement statement = connection.prepareStatement(sql);
	            statement.setString(1, event.getId());
	            statement.setLong(2, event.getDuration());
	            statement.setString(3, event.getType());
	            statement.setString(4, event.getHost());
	            statement.setBoolean(5, event.isAlert());
	            return statement.executeUpdate() > 0;
	        } catch (Exception e) {
	            log.error("insert log fail", e);
	            return false;
	        }
		 
	 }
	 
	 public void close() {
	        try {
	            connection.close();
	        } catch (SQLException e) {
	            log.error("Database Con close fail",  e);
	        }
	    }
	 
	 
}