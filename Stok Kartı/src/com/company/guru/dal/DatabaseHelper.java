package com.company.guru.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.company.guru.entity.StockCard;

public class DatabaseHelper {
	private String username = "root";
	private String password = "12345";
	private String dbURL = "jdbc:mysql://localhost:3306/?";
	private String dbName = "gurudb";
	private Connection connection;
	private Statement statement;
	private PreparedStatement preparedStatement;
	
	public Connection connectToDatabase() throws SQLException {
		connection = DriverManager.getConnection(dbURL, username, password);
		
		statement = connection.createStatement();
		statement.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
		
		return connection;
	}
	
	public void createTable() throws SQLException {
			
		try { 
			connection = connectToDatabase();
			
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + dbName + ".stockcard "
					+ "(stockcode VARCHAR(50) NOT NULL,"
					+ "stockname VARCHAR(100) NOT NULL,"
					+ "stocktype INT(2) NOT NULL,"
					+ "unit VARCHAR(10) NOT NULL,"
					+ "barcode VARCHAR(30) NOT NULL,"
					+ "vattype FLOAT NOT NULL,"
					+ "description TEXT NOT NULL,"
					+ "createdat DATETIME)");
		} catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
    		statement.close();
    		connection.close();        
    	}
	}
	
	public void add(StockCard stockCard) throws SQLException {
		try {
			connection = connectToDatabase();
			preparedStatement = connection.prepareStatement(
					"INSERT INTO gurudb.stockcard (stockcode, stockname, stocktype, unit, barcode, vattype, description, createdat) VALUES(?,?,?,?,?,?,?,?)"
					);
			preparedStatement.setString(1, stockCard.getStockCode());
			preparedStatement.setString(2, stockCard.getStockName());
			preparedStatement.setInt(3, stockCard.getStockType());
			preparedStatement.setString(4, stockCard.getUnit());
			preparedStatement.setString(5, stockCard.getBarcode());
			preparedStatement.setDouble(6, stockCard.getVatType());
			preparedStatement.setString(7, stockCard.getDescription());
			preparedStatement.setString(8, stockCard.getCreatedAt().toString());
			
			int result = preparedStatement.executeUpdate();
			System.out.println(result);
		} catch(SQLException e) {
            System.err.println("Error: " + e.getMessage());
		} finally {
			connection.close();
		}
	}
	
	public List<StockCard> getAllStockCards() throws SQLException {
		try {
			connection = connectToDatabase();
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM gurudb.stockcard");
			List<StockCard> stockCards = new ArrayList<StockCard>();
			
			while(resultSet.next()) {
				stockCards.add(new StockCard(
						resultSet.getString("stockcode"),
						resultSet.getString("stockname"),
						resultSet.getInt("stocktype"),
						resultSet.getString("unit"),
						resultSet.getString("barcode"),
						resultSet.getFloat("vattype"),
						resultSet.getString("description"),
						resultSet.getString("createdat")
				));
			}
			
			return stockCards;
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
		
		return null;
	}
	
	public StockCard getCardByStockCode(String code) throws SQLException {
		try {
			connection = connectToDatabase();
			statement = connection.createStatement();
			StockCard stockCard = null;
			
			ResultSet resultSet = statement.executeQuery("SELECT * FROM " + dbName +".stockcard s WHERE s.stockcode = '" + code + "'");
			if(resultSet.next()) {
				stockCard = new StockCard(
						resultSet.getString("stockcode"),
						resultSet.getString("stockname"),
						resultSet.getInt("stocktype"),
						resultSet.getString("unit"),
						resultSet.getString("barcode"),
						resultSet.getFloat("vattype"),
						resultSet.getString("description"),
						resultSet.getString("createdat")
					);
			}
			
			return stockCard;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
		
		return null;
	}
	
	public void updateStockCard(StockCard stockCard) throws SQLException {
		try {
			connection = connectToDatabase();
			
			preparedStatement = connection.prepareStatement(
					"UPDATE " + dbName + ".stockcard SET stockname=?, stocktype=?, "
					+ "unit=?, barcode=?, vattype=?, description=?, createdat=? WHERE stockcode=?");
			
			preparedStatement.setString(1, stockCard.getStockName());
			preparedStatement.setInt(2, stockCard.getStockType());
			preparedStatement.setString(3, stockCard.getUnit());
			preparedStatement.setString(4, stockCard.getBarcode());
			preparedStatement.setDouble(5, stockCard.getVatType());
			preparedStatement.setString(6, stockCard.getDescription());
			preparedStatement.setString(7, stockCard.getCreatedAt());
			preparedStatement.setString(8, stockCard.getStockCode());
			
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}
	
	public void deleteStockCard(String code) throws SQLException {
		try {
			connection = connectToDatabase();
			statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM " + dbName + ".stockcard s WHERE s.stockcode = '" + code + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}
	
	public void copyStockCard(StockCard stockCard) throws SQLException {
		try {
			connection = connectToDatabase();
			preparedStatement = connection.prepareStatement(
					"INSERT INTO gurudb.stockcard (stockcode, stockname, stocktype, unit, barcode, vattype, description, createdat) VALUES(?,?,?,?,?,?,?,?)"
					);
			preparedStatement.setString(1, stockCard.getStockCode() + "_copy");
			preparedStatement.setString(2, stockCard.getStockName());
			preparedStatement.setInt(3, stockCard.getStockType());
			preparedStatement.setString(4, stockCard.getUnit());
			preparedStatement.setString(5, stockCard.getBarcode());
			preparedStatement.setDouble(6, stockCard.getVatType());
			preparedStatement.setString(7, stockCard.getDescription());
			preparedStatement.setString(8, stockCard.getCreatedAt().toString());
			
			int result = preparedStatement.executeUpdate();
			System.out.println(result);
		} catch(SQLException e) {
            System.err.println("Error: " + e.getMessage());
		} finally {
			connection.close();
		}
	}
	
}
