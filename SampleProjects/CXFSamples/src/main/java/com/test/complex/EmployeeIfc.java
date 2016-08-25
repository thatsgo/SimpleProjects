package com.test.complex;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.test.pojo.Employee;

@WebService
public interface EmployeeIfc {

	/**
	 * 
	 * @param e
	 * @return
	 */
	@WebMethod
	public String display(Employee e);
}
