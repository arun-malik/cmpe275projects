package edu.sjsu.cmpe275.p1test.restapi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import edu.sjsu.cmpe275.p1test.restapi.pojo.ImageFile;

@Service
public class SimpleDataStore {
	
	// Not that this is no the most effect way for storing data but it will do for now
	private Map<String, ImageFile> imagesByUuid;
	private Map<String, ArrayList<String>> listOfUuidsForEachUser;
	
	@PostConstruct
	private void postConstruct() {
		imagesByUuid 			= new HashMap<String, ImageFile>();
		listOfUuidsForEachUser 	= new HashMap<String, ArrayList<String>>();
	}
	
	
	/**
	 * Returns a list of users
	 * @return String[] containing the list of users
	 */
	public String[] getListOfUsers() {
		return listOfUuidsForEachUser.keySet().toArray(new String[listOfUuidsForEachUser.size()]);
	}
	
	
	
	/**
	 * Returns a list of UUIDs that this user already has
	 * 
	 * @param user
	 * @return String[] of files
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public String[] getFilesForUser(String user) throws NullPointerException, IllegalArgumentException {
		// Argument validation
		if(user == null) {
			throw new NullPointerException();
		}
		if(user.trim().length() <= 0) {
			throw new IllegalArgumentException("username cannot be empty");
		}
		
		// returns data in the map, null if user is not found or does not exist
		ArrayList<String> uuidsForUser = listOfUuidsForEachUser.get(user);
		return uuidsForUser == null ? null : uuidsForUser.toArray(new String[uuidsForUser.size()]);
	}
	
	
	
	/**
	 * Save file with username and data
	 * @param user
	 * @param imageFile
	 * @return
	 */
	public String saveImage(String user, ImageFile imageFile) {
		// Argument validation
		if(user == null || imageFile == null) {
			throw new NullPointerException();
		}
		if(user.trim().length() <= 0) {
			throw new IllegalArgumentException("username cannot be empty");
		}
		
		// Generate UUID
		String uuid = UUID.randomUUID().toString();
		imageFile.setUuid(uuid);
		
		// update data store
		imagesByUuid.put(uuid, imageFile);
		ArrayList<String> uuidsForUser = listOfUuidsForEachUser.get(user);
		if(uuidsForUser == null) {
			// Add user if not existant
			uuidsForUser = new ArrayList<String>();
			listOfUuidsForEachUser.put(user, uuidsForUser);
		}
		uuidsForUser.add(uuid);
		
		// return uuid
		return uuid;
	}
	
	
	/**
	 * Get Image by UUID
	 * @param uuid
	 * @return
	 */
	public ImageFile getImageByUuid(String uuid) {
		// Argument validation
		if(uuid == null) {
			throw new NullPointerException();
		}
		if(uuid.trim().length() <= 0) {
			throw new IllegalArgumentException("uuid cannot be empty");
		}
		
		return imagesByUuid.get(uuid);
	}
	
	/**
	 * Update a file
	 * TODO throw error if file does not exist
	 * 
	 * @param uuid
	 * @param imageFile
	 * @return
	 */
	public ImageFile updateImageByUuid(String uuid, ImageFile imageFile) {
		// Argument validation
		if(uuid == null) {
			throw new NullPointerException();
		}
		if(uuid.trim().length() <= 0) {
			throw new IllegalArgumentException("uuid cannot be empty");
		}
		
		return imagesByUuid.put(uuid, imageFile);
	}
	
	/**
	 * Delete a file
	 * TODO update listOfUuidsForEachUser
	 * @param uuid
	 * @return
	 */
	public ImageFile deleteImageByUuid(String uuid) {
		// Argument validation
		if(uuid == null) {
			throw new NullPointerException();
		}
		if(uuid.trim().length() <= 0) {
			throw new IllegalArgumentException("uuid cannot be empty");
		}
		
		return imagesByUuid.remove(uuid);
	}
}
