package com.lifeForce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lifeForce.domain.BlobStorage;

@Repository
public interface BlogStorageRepository extends JpaRepository<BlobStorage, Long>{

	public BlobStorage findByBlobStorageId(Long blobStorageId);
	
	 public BlobStorage findByUuid(String uuid);
	 
	 public List<BlobStorage> findByCreatedBy(String user);
	 
	 public List<BlobStorage> findByCaption(String caption);

}
