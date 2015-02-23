/**
 * 
 */
package com.lifeForce.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author arun_malik
 *
 */
public class ReplicatedDbServiceImplementation {
	
	private static ReplicatedDbServiceImplementation instance = null;
	private static final Object lock = new Object();
	
	Connection conn = null;

	public static ReplicatedDbServiceImplementation getInstance() {
		
		if(instance == null) {
			synchronized (lock) {
				if(instance == null) {
					instance = new ReplicatedDbServiceImplementation();
				}
			}
		}
		return instance;
	}
	
	public void createMapperStorage(MapperStorage mapper)
			throws Exception {
		
		PreparedStatement ps = null;

		try {

			if (null == conn) {
				conn = openDbConnection();
			}
			
			conn.setAutoCommit(false);
			String sql = " INSERT INTO `mapper`(`nodeid`,`uuid`)VALUES(?,?)";

			ps = conn.prepareStatement(sql);
			ps.setLong(1, mapper.getNodeId());
			ps.setString(2, mapper.getUuid());

			ps.executeUpdate();
			conn.commit();

		} finally {
			if (null != conn || !conn.isClosed()) {
				closeDbConnection();
				conn = null;
			}
			ps.close();
			
		} 
	}
	
	
	public MapperStorage findNodeIdByUuid(String uuid)
			throws Exception {
		
		PreparedStatement ps = null;
		MapperStorage mapper = null;

		try {

			if (null == conn) {
				conn = openDbConnection();
			}
			
			String sqlSelect = "SELECT * FROM mapper where mapper.uuid = ?;";

			ps = conn.prepareStatement(sqlSelect);
			ps.setString(1, uuid);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				mapper = new MapperStorage();
				mapper.setMapperId(rs.getLong("idmapper"));
				mapper.setUuid(rs.getString("uuid"));
				mapper.setNodeId(rs.getLong("nodeid"));
			}

			return mapper;
		} finally {
			if (null != conn || !conn.isClosed()) {
				closeDbConnection();
				conn = null;
			}
			ps.close();
			mapper = null;
			
		}
	}

	public Boolean deleteMapper(String uuid) throws Exception {

		PreparedStatement ps = null;
		Boolean success = false;
		
		
		try {

			if (null == conn) {
				conn = openDbConnection();
			}
			
			String safeSql = "SET SQL_SAFE_UPDATES=0;";
			ps = conn.prepareStatement(safeSql);
			ps.executeQuery();
			
			
			String sql = "DELETE FROM mapper where mapper.uuid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);

			ps.executeQuery();
			success = true;
			return success;

		} catch (Exception ex) {
			success = false;
			return success;
		} finally {
			if (null != conn || !conn.isClosed()) {
				closeDbConnection();
				conn = null;
			}
			
			ps.close();
			
		}
	}
	
	public Connection openDbConnection() {
		
		try {

			Class.forName(DbConfigurations.getJdbcDriver());
			Connection mainMapperConn = DriverManager.getConnection(
					DbConfigurations.getMapperMainDbUrl(),
					DbConfigurations.getMapperMainDbUser(),
					DbConfigurations.getMapperMainDbPass());
			
			return mainMapperConn;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException exMain) {

			exMain.printStackTrace();
			System.out.println("Connection Backup replicated Db");

			try {
				Connection mainRepConn = DriverManager.getConnection(
						DbConfigurations.getMapperReplicatedDbUrl(),
						DbConfigurations.getMapperReplicatedDbUser(),
						DbConfigurations.getMapperReplicatedDbPass());

				return mainRepConn;
				
			} catch (SQLException exRep) {
				exRep.printStackTrace(); 
			}

		}
		return null;
	}
	
	private void closeDbConnection() {

		try {
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
