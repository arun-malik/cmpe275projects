/**
 * 
 */
package com.lifeForce.storage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id; 
import javax.persistence.Table;

/**
 * @author arun_malik
 *
 */

@Entity
@Table(name="mapper")
public class MapperStorage {
 	@Id
    @Column(name = "idmapper", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mapperId;

	@Column(name = "uuid", unique = true, nullable = false, length = 100)
     private String uuid;
 	 
 	 @Column(name = "nodeid", unique = false, nullable = false)
 	 private long nodeId;
 	 
 	 
 	 /**
 	 * @return the mapperId
 	 */
 	public Long getMapperId() {
 		return mapperId;
 	}

 	/**
 	 * @param mapperId the mapperId to set
 	 */
 	public void setMapperId(Long mapperId) {
 		this.mapperId = mapperId;
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
 	 * @return the nodeId
 	 */
 	public long getNodeId() {
 		return nodeId;
 	}

 	/**
 	 * @param l the nodeId to set
 	 */
 	public void setNodeId(long l) {
 		this.nodeId = l;
 	}
}