package com.lifeForce.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlobStorageServiceImplementation implements BlobStorageService {

	Connection conn = null;
	Statement stmt = null;


	public BlobStorageProfile createBlobStorage(BlobStorage blob)
			throws Exception {
		
		conn = getConnection();

		stmt = conn.createStatement();
		PreparedStatement ps = null;

		try {

			conn.setAutoCommit(false);
			String sql = "INSERT INTO blobStorage (`uuid`,`caption`,`img`,`contentLength`,`createdBy`,`createdDate`,`lastModifiedBy`,`lastModifiedDate`) VALUES(?,?,?,?,?,?,?,?)";

			ps = conn.prepareStatement(sql);
			ps.setString(1, blob.getUuid());
			ps.setString(2, blob.getCaption());
			ps.setBytes(3, blob.getImageData());
			ps.setLong(4, blob.getContentLength());
			ps.setString(5, blob.getCreatedBy());
			ps.setDate(6, (Date) blob.getCreatedDate());
			ps.setString(7, blob.getLastModifiedBy());
			ps.setDate(8, (Date) blob.getLastModifiedDate());

			ps.executeUpdate();
			conn.commit();

		} finally {
			ps.close();
			conn = null;
		}

		return findByUuid(blob.getUuid());
	}

	public Boolean deleteBlobStorage(Long blobStorageId) throws Exception {

		PreparedStatement ps = null;
		Boolean success = false;
		conn = getConnection();
		
		try {

			String sql = "DELETE FROM blobStorage where blobStorage.blobStorageId = ?;";

			ps = conn.prepareStatement(sql);
			ps.setLong(1, blobStorageId);

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

	public BlobStorageProfile findByUuid(String uuid) throws Exception {

		PreparedStatement ps = null;
		BlobStorage blob = null;
		conn = getConnection();
		
		try {

			String sqlSelect = "SELECT * FROM blobStorage where blobStorage.uuid = ?;";

			ps = conn.prepareStatement(sqlSelect);
			ps.setString(1, uuid);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				blob = new BlobStorage();
				blob.setBlobStorageId(rs.getLong("blobStorageId"));
				blob.setUuid(rs.getString("uuid"));
				blob.setCaption(rs.getString("caption"));
				blob.setContentLength(rs.getInt("contentLength"));
				blob.setImageData(rs.getBytes("img"));
				blob.setCreatedBy(rs.getString("createdBy"));
				blob.setCreatedBy(rs.getString("lastModifiedBy"));
				blob.setCreatedDate(rs.getDate("createdDate"));
				blob.setLastModifiedDate(rs.getDate("lastModifiedDate"));
			}

			return new BlobStorageProfile(blob);
		} finally {
			ps.close();
			blob = null;
			conn = null;
		}
	}

	public List<BlobStorageProfile> findByCreatedBy(String createdBy)
			throws Exception {

		PreparedStatement ps = null;
		BlobStorage blob = null;
		List<BlobStorageProfile> blobs = new ArrayList<BlobStorageProfile>();
		conn = getConnection();

		try {

			String sqlSelect = "SELECT * FROM blobStorage where blobStorage.createdBy = ?;";

			ps = conn.prepareStatement(sqlSelect);
			ps.setString(1, createdBy);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				blob = new BlobStorage();
				blob.setBlobStorageId(rs.getLong("blobStorageId"));
				blob.setUuid(rs.getString("uuid"));
				blob.setCaption(rs.getString("caption"));
				blob.setContentLength(rs.getInt("contentLength"));
				blob.setImageData(rs.getBytes("img"));
				blob.setCreatedBy(rs.getString("createdBy"));
				blob.setCreatedBy(rs.getString("lastModifiedBy"));
				blob.setCreatedDate(rs.getDate("createdDate"));
				blob.setLastModifiedDate(rs.getDate("lastModifiedDate"));

				blobs.add(new BlobStorageProfile(blob));
			}

			return blobs;
		} finally {
			ps.close();
			blob = null;
			blobs = null;
			conn = null;
		}
	}

	@Override
	public Boolean deleteBlobStorageByUuid(String uuid) throws Exception {
		PreparedStatement ps = null;
		Boolean success = false;
		conn = getConnection();

		try {

			String safeSql = "SET SQL_SAFE_UPDATES=0;";
			ps = conn.prepareStatement(safeSql);
			ps.executeQuery();
			
			
			String sql = "DELETE FROM blobStorage where blobStorage.uuid = ?";

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
	
	private Connection getConnection(){
		
		Connection localConn=null;
		try {

			Class.forName(DbConfigurations.getJdbcDriver());
			 localConn = DriverManager.getConnection(
					com.lifeForce.storage.DbConfigurations.getLocalDbUrl(),
					com.lifeForce.storage.DbConfigurations.getLocalDbUser(),
					com.lifeForce.storage.DbConfigurations.getLocalDbPass());

			return localConn;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return localConn;
	}

}
