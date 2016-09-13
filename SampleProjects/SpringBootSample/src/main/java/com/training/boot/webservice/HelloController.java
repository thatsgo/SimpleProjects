package com.training.boot.webservice;

import javax.websocket.server.PathParam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class HelloController {

	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}

	@RequestMapping(value="/getemployee/{employeeId}",method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> getEmployeeDetails(@PathParam("employeeId") String id )
	{
		return new ResponseEntity<Employee>(new Employee("krishna",1,"SW"),HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/createemployee",method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public void createEmployeeDetails(@RequestBody Employee e )
	{
		System.out.println(" EEE :: " + e.toString());
	}
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(HelloController.class, args);
	}
}
