package com.lifeForce.service;

import java.util.List;

import com.lifeForce.domain.BlobStorage;
import com.lifeForce.wrappers.BlobStorageProfile;



public interface BlobStorageService {
	public BlobStorageProfile createBlobStorage(BlobStorage blob) throws Exception;
	public void deleteBlobStorage(Long blobStorageId) throws Exception;
	
	public BlobStorageProfile findByUuid(String uuid)  throws Exception;
	public List<BlobStorageProfile> findByCreatedBy(String user)  throws Exception;
	
}
