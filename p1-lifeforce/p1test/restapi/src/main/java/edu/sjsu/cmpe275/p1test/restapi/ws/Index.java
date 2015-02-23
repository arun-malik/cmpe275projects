package edu.sjsu.cmpe275.p1test.restapi.ws;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Index {
	
	@RequestMapping(value={"/**"})
	@ResponseBody
	public String index() {
		return "SJSU CMPE 275 Project 1 REST Home Page";
	}
}
