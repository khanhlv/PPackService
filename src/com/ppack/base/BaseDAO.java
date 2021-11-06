package com.ppack.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.ppack.bean.Link;

public abstract class BaseDAO {

	private final static Logger logger = Logger.getLogger(BaseDAO.class.getSimpleName());
	public static List<String> listFinish = new Vector<String>();
	
	public String loadValueConfig(String key) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream("config.properties"));
		String value = properties.getProperty(key);
		return value.trim();
	}

	public Connection getConnection() throws SQLException, IOException, ClassNotFoundException {
		try {
			String classJDBC = loadValueConfig("classJDBC");
			String driverJDBC = loadValueConfig("driverJDBC");
			String usernameJDBC = loadValueConfig("usernameJDBC");
			String passwordJDBC = loadValueConfig("passwordJDBC");

			Class.forName(classJDBC);
			return DriverManager.getConnection(driverJDBC, usernameJDBC, passwordJDBC);
		} catch (SQLException e) {
			// TODO: handle exception
			throw e;
		}
	}

	public ConcurrentLinkedQueue<String> readerFileRPC() {

		BufferedReader bufferedReader = null;
		ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<String>();
		try {

			bufferedReader = new BufferedReader(new FileReader(new File("data/RPC.txt")));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				list.add(line.trim());
			}
		} catch (Exception e) {
			logger.error(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
			}
		}

		return list;
	}

	private ArrayList<String> readerFileSQL() {
		StringBuilder strBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		ArrayList<String> list = new ArrayList<String>();
		try {

			bufferedReader = new BufferedReader(new FileReader(new File("data/SQL.txt")));
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				
				if (line.trim().equals("GO")) {
					list.add(strBuilder.toString().trim());
					strBuilder = new StringBuilder();
					continue;
				}
				strBuilder.append(line.trim());
			}
		} catch (Exception e) {
			logger.error(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
			}
		}

		return list;
	}

	public ArrayList<Link> listLinkPing() {
		ArrayList<Link> list = new ArrayList<Link>();

		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			
			ArrayList<String> listSQL = readerFileSQL();
			if(listSQL.size() > 0) {
				conn = getConnection();
				for(int i=0;i<listSQL.size();i++) {
					System.out.println("Loading data to SQL...");
					pStmt = conn.prepareStatement(listSQL.get(i));
					rs = pStmt.executeQuery();

					while (rs.next()) {
						Link link = new Link();
						link.setName(rs.getString(1));
						link.setUrl(rs.getString(2));
						list.add(link);
					}
				}
			}
			System.out.println("Loading data SUCCESS...");
		} catch (Exception e) {
			logger.error(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pStmt != null) {
					pStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
			}
		}
		return list;

	}
}
