/**
 * 
 */
package com.lifeForce.storage;

import java.sql.Connection; 
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author arun_malik
 *
 */
public class ReplicatedDbServiceImplementation {

	Connection conn = null;
	Statement stmt = null;

	public ReplicatedDbServiceImplementation() {
		conn = getDbConnection();
	}
	
	public void createMapperStorage(MapperStorage mapper)
			throws Exception {

		stmt = conn.createStatement();
		PreparedStatement ps = null;

		try {

			conn.setAutoCommit(false);
			String sql = " INSERT INTO `mapper`(`nodeid`,`uuid`)VALUES(?,?)";

			ps = conn.prepareStatement(sql);
			ps.setLong(1, mapper.getNodeId());
			ps.setString(2, mapper.getUuid());

			ps.executeUpdate();
			conn.commit();

		} finally {
			ps.close();
			conn = null;
		} 
	}
	
	
	public MapperStorage findNodeIdByUuid(String uuid)
			throws Exception {
		
		PreparedStatement ps = null;
		MapperStorage mapper = null;

		try {

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
			ps.close();
			mapper = null;
			conn = null;
		}
	}

	public Boolean deleteMapper(String uuid) throws Exception {

		PreparedStatement ps = null;
		Boolean success = false;
		conn = getDbConnection();
		
		try {

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
			ps.close();
			conn = null;
		}
	}
	
	public Connection getDbConnection() {
		
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

}
