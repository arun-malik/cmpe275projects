package com.lifeForce.storage;

import java.util.Date;


public class BlobStorageProfile {
	 private Long blobStorageId;
	 private String uuid;
	 private String caption;
	 private byte[] imageData;
	 private Integer contentLength;
	 private String createdBy;
	 private Date createdDate;
	 private String lastModifiedBy;
	 private Date lastModifiedDate;
	 
	 public BlobStorageProfile()
	    {}
	 
	 public BlobStorageProfile(BlobStorage blob)
	    {
		 
		 this.blobStorageId = blob.getBlobStorageId();
		 this.caption = blob.getCaption();
		 this.contentLength = blob.getContentLength();
		 this.createdBy = blob.getCreatedBy();
		 this.createdDate = blob.getCreatedDate();
		 this.imageData = blob.getImageData();
		 this.lastModifiedBy=blob.getLastModifiedBy();
		 this.lastModifiedDate = blob.getLastModifiedDate();
		 this.uuid = blob.getUuid();
		 
	    }

	 
	 
	/**
	 * @return the blobStorageId
	 */
	public Long getBlobStorageId() {
		return blobStorageId;
	}
	/**
	 * @param blobStorageId the blobStorageId to set
	 */
	public void setBlobStorageId(Long blobStorageId) {
		this.blobStorageId = blobStorageId;
	}
	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	/**
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}
	/**
	 * @param caption the caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
	/**
	 * @return the imageData
	 */
	public byte[] getImageData() {
		return imageData;
	}
	/**
	 * @param imageData the imageData to set
	 */
	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}
	/**
	 * @return the contentLength
	 */
	public Integer getContentLength() {
		return contentLength;
	}
	/**
	 * @param contentLength the contentLength to set
	 */
	public void setContentLength(Integer contentLength) {
		this.contentLength = contentLength;
	}
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * @return the lastModifiedBy
	 */
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	/**
	 * @param lastModifiedBy the lastModifiedBy to set
	 */
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	/**
	 * @return the lastModifiedDate
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	 
}
