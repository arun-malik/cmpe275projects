package edu.sjsu.cmpe275.p1test.restapi.ws;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value="/api/images")
public class SimpleFilesRestApi {
	
	@RequestMapping(value = {"", "/"}, method=RequestMethod.POST, produces = "application/json")
	public Map<String, Object> uploadFileToUser(@PathVariable("user") String user
			, @RequestParam("file") MultipartFile file) {
		// TODO 
		return null;
	}

	@RequestMapping(value = "/{uuid}", method=RequestMethod.GET, produces = "image/jpeg")
	public String[] getFileByUuid(@PathVariable("uuid") String uuid) {
		// TODO
		return null;
	}


	@RequestMapping(value = "/{uuid}", method=RequestMethod.PUT, produces = "application/json")
	public Map<String, Object> updateFileByUuid(@PathVariable("uuid") String uuid
			, @RequestParam("file") MultipartFile file) {
		// TODO
		return null;
	}
	
	
	@RequestMapping(value = "/{uuid}", method=RequestMethod.DELETE, produces = "application/json")
	public Map<String, Object> deleteFileByUuid(@PathVariable("uuid") String uuid) {
		// TODO
		return null;
	}
	
	
	



	
	

}
