package com.lifeForce.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlobStorageServiceImplementation implements BlobStorageService {

	private static BlobStorageServiceImplementation instance = null;
	private static final Object lock = new Object();

	Connection conn = null;

	public static BlobStorageServiceImplementation getInstance() {

		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new BlobStorageServiceImplementation();
				}
			}
		}
		return instance;
	}

	// private BlobStorageServiceImplementation() {
	// conn = openDbConnection();
	// }

	public BlobStorageProfile createBlobStorage(BlobStorage blob)
			throws Exception {

		if (null == conn) {
			conn = openDbConnection();
		}

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
			if (null != conn || !conn.isClosed()) {
				closeDbConnection();
				conn = null;
			}

			ps.close();

		}

		return findByUuid(blob.getUuid());
	}

	public Boolean deleteBlobStorage(Long blobStorageId) throws Exception {

		PreparedStatement ps = null;
		Boolean success = false;

		try {

			if (null == conn) {
				conn = openDbConnection();
			}

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
			if (null != conn || !conn.isClosed()) {
				closeDbConnection();
				conn = null;
			}
			ps.close(); 
		}
	}

	public BlobStorageProfile findByUuid(String uuid) throws Exception {

		PreparedStatement ps = null;
		BlobStorage blob = null;

		try {

			if (null == conn) {
				conn = openDbConnection();
			}

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
			if (null != conn || !conn.isClosed()) {
				closeDbConnection();
				conn = null;
			}
			ps.close();
			blob = null;
			
		}
	}

	public List<BlobStorageProfile> findByCreatedBy(String createdBy)
			throws Exception {

		PreparedStatement ps = null;
		BlobStorage blob = null;
		List<BlobStorageProfile> blobs = new ArrayList<BlobStorageProfile>();

		try {

			if (null == conn) {
				conn = openDbConnection();
			}

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
			if (null != conn || !conn.isClosed()) {
				closeDbConnection();
				conn = null;
			}
			ps.close();
			blob = null;
			blobs = null; 
		}
	}

	@Override
	public Boolean deleteBlobStorageByUuid(String uuid) throws Exception {
		PreparedStatement ps = null;
		Boolean success = false;

		try {

			if (null == conn) {
				conn = openDbConnection();
			}

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
			if (null != conn || !conn.isClosed()) {
				closeDbConnection();
				conn = null;
			}
			ps.close();
			
		}

	}

	private Connection openDbConnection() {

		Connection localConn = null;
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

	private void closeDbConnection() {

		try {
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
