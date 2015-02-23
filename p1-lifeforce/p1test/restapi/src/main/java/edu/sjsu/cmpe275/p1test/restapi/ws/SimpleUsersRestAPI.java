package edu.sjsu.cmpe275.p1test.restapi.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.p1test.restapi.service.SimpleDataStore;


@RestController
@RequestMapping(value = "/api/users")
public class SimpleUsersRestAPI {
	
	@Autowired
	private SimpleDataStore dataServices;
	
	
	/**
	 * Maps to "{serverHostName}/api/users/"
	 * @return
	 */
	@RequestMapping(value = {"", "/"}, method=RequestMethod.GET, produces = "application/json")
	public String[] getListOfUsers() {
		return dataServices.getListOfUsers();
	}
	
	
	/**
	 * Maps to "{serverHostName}/api/users/{user}"
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/{user}", method=RequestMethod.GET, produces = "application/json")
	public String[] getListOfFilesForUser(@PathVariable("user") String user) {
		return dataServices.getFilesForUser(user);
	}
	
}
