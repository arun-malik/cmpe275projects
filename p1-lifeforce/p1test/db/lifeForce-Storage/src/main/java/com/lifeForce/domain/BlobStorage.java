/**
 * 
 */
package com.lifeForce.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author arun_malik
 *
 */

@Entity
@Table(name="blobStorage")
@NamedQueries({
	@NamedQuery(name="BlogStorage.findByUuid", query="SELECT b FROM BlobStorage b where b.uuid = :uuid"),
	@NamedQuery(name="BlogStorage.findByCreatedBy", query="SELECT b FROM BlobStorage b where b.createdBy = :createdBy")
})

public class BlobStorage {
 	@Id
    @Column(name = "blobStorageId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blobStorageId;
 	
 	 @Column(name = "uuid", unique = true, nullable = false, length = 100)
     private String uuid;
 	 
 	 @Column(name = "caption", unique = false, nullable = false, length = 45)
 	 private String caption;
 	 
 	@Column(name = "img", unique = false, nullable = false, length = 100000)
    private byte[] imageData;
 	
 	@Column(name = "contentLength", unique = false, nullable = false )
 	private Integer contentLength;
 	
 	 @Column(name = "createdBy", unique = false, nullable = false, length = 45)
 	 private String createdBy;
 	
 	@Column(name = "createdDate", columnDefinition="DATETIME")
 	@Temporal(TemporalType.TIMESTAMP)
 	private Date createdDate;

 	@Column(name = "lastModifiedBy", unique = false, nullable = false, length = 45)
 	private String lastModifiedBy;
 	
 	@Column(name = "lastModifiedDate", columnDefinition="DATETIME")
 	@Temporal(TemporalType.TIMESTAMP)
 	private Date lastModifiedDate;

	public Long getBlobStorageId() {
		return blobStorageId;
	}

	public void setBlobStorageId(Long blobStorageId) {
		this.blobStorageId = blobStorageId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public Integer getContentLength() {
		return contentLength;
	}

	public void setContentLength(Integer contentLength) {
		this.contentLength = contentLength;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
