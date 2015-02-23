package com.lifeForce.service.implementation;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import com.lifeForce.domain.BlobStorage;
import com.lifeForce.service.BlobStorageService;
import com.lifeForce.wrappers.BlobStorageProfile;

@Service
public class BlobStorageServiceImplementation implements BlobStorageService {
 
	EntityManagerFactory entityManagerFactory;
	EntityManager entityManager;
	Query query;
	

	public BlobStorageServiceImplementation(){
		 entityManagerFactory=Persistence.createEntityManagerFactory("lifeForce");
		 entityManager=entityManagerFactory.createEntityManager();
	}
	
	public BlobStorageProfile createBlobStorage(BlobStorage blob) throws Exception {
		
		entityManager.getTransaction().begin();
		entityManager.persist(blob);
		entityManager.getTransaction().commit();
 
		return new BlobStorageProfile(blob);
	}

	public void deleteBlobStorage(Long blobStorageId) throws Exception {
		 
		 entityManager.getTransaction().begin();
		 entityManager.remove(blobStorageId);
		 entityManager.getTransaction().commit();
	}

	@SuppressWarnings("unchecked")
	public BlobStorageProfile findByUuid(String uuid) throws Exception {

		query = entityManager.createNamedQuery("BlogStorage.findByUuid").setParameter("uuid", uuid);
		List<BlobStorage>  lstQueryRes = query.getResultList();

		BlobStorage blob = (BlobStorage) lstQueryRes.get(0);
		return new BlobStorageProfile(blob);
	}

	@SuppressWarnings("unchecked")
	public List<BlobStorageProfile> findByCreatedBy(String createdBy)
			throws Exception {
		
		query = entityManager.createNamedQuery("BlogStorage.findByCreatedBy").setParameter("createdBy", createdBy);
		List<BlobStorage>  lstQueryRes = query.getResultList();
 
		 List<BlobStorageProfile> blobStorageProfiles = new ArrayList<BlobStorageProfile>();

			for (BlobStorage blob : lstQueryRes)
			{
				blobStorageProfiles.add(new BlobStorageProfile(blob));
			}
			
		  return blobStorageProfiles;
		
	}

}
