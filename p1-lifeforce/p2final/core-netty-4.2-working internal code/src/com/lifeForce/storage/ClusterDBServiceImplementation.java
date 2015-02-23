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
import java.util.ArrayList;
import java.util.List;


public class ClusterDBServiceImplementation {

	Connection conn = null;
	Statement stmt = null;

	public ClusterDBServiceImplementation() {
		conn = getDbConnection();
	}
	
	public void createMapperStorage(ClusterMapperStorage clusterMapper)
			throws Exception {

		stmt = conn.createStatement();
		PreparedStatement ps = null;

		System.out.println("%%%%%%%%%%%%%%%%%% IN createMapperStorage %%%%%%%%%%%%%%");
		
		// select clusterMapper -> if found then call update else call insert
		try {

			String sqlSelect = "SELECT * FROM clusterMapper where clusterMapper.clusterId = ?;";

			ps = conn.prepareStatement(sqlSelect);
			ps.setInt(1, clusterMapper.getClusterID());

			ResultSet rs = ps.executeQuery();

			// Record exists in database then update ELSE insert it
			if(rs.next()) {
				System.out.println("%%%%%%%%%%% UPDATING Current List ^^^^^^^^^^^^^^^^^"+ clusterMapper.getPort());
				updateClusterMapper(clusterMapper.getClusterID(), clusterMapper.getLeaderHostAddress(), clusterMapper.getPort());
			} 
			else {
				
				conn.setAutoCommit(false);
				String sql = " INSERT INTO `clusterMapper`(`clusterId`,`leaderHostAddress`,`port`) VALUES (?,?,?)";

				ps = conn.prepareStatement(sql);
				ps.setInt(1, clusterMapper.getClusterID());
				ps.setString(2, clusterMapper.getLeaderHostAddress());
				ps.setInt(3, clusterMapper.getPort());
				
				ps.executeUpdate();
				conn.commit();
			}
		} finally {
			ps.close();
			conn = null;
		} 
	}
	
	
	public List<ClusterMapperStorage> getClusterList(int selfClusterId)
			throws Exception {
		
		List<ClusterMapperStorage> clusterMapperList = new ArrayList<ClusterMapperStorage>();
		PreparedStatement ps = null;

		try {

			String sqlSelect = "SELECT * FROM clusterMapper where clusterMapper.clusterId != ?;";

			ps = conn.prepareStatement(sqlSelect);
			ps.setInt(1, selfClusterId);

			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				ClusterMapperStorage clusterMapper = new ClusterMapperStorage();
				clusterMapper.setClusterID(rs.getInt("clusterId"));
				clusterMapper.setLeaderHostAddress(rs.getString("leaderHostAddress"));
				clusterMapper.setPort(rs.getInt("port"));
				
				clusterMapperList.add(clusterMapper);
			}

			return clusterMapperList;
		} finally {
			ps.close();
			clusterMapperList = null;
			conn = null;
		}
	}

	public Boolean updateClusterMapper(int clusterId, String host, int port) throws Exception {

		PreparedStatement ps = null;
		Boolean success = false;
		conn = getDbConnection();
		
		try {

			String sql = "UPDATE cmpe275.clusterMapper cm SET cm.leaderHostAddress = ?, cm.port = ? where cm.clusterId = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, host);
			ps.setInt(2, port);
			ps.setInt(3, clusterId);
			
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
					DbConfigurations.getClusterMapperMainDbUrl(),
					DbConfigurations.getClusterMapperMainDbUser(),
					DbConfigurations.getClusterMapperMainDbPass());
			
			return mainMapperConn;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException exMain) {

			exMain.printStackTrace();
			System.out.println("Connection Backup replicated Db");

//			try {
//				Connection mainRepConn = DriverManager.getConnection(
//						DbConfigurations.getMapperReplicatedDbUrl(),
//						DbConfigurations.getMapperReplicatedDbUser(),
//						DbConfigurations.getMapperReplicatedDbPass());
//
//				return mainRepConn;
//				
//			} catch (SQLException exRep) {
//				exRep.printStackTrace(); 
//			}

		}
		return null;
	}

}
