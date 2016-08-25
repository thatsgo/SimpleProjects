package com.test.complex;

import javax.jws.WebService;

import com.test.pojo.Employee;

@WebService(endpointInterface="com.test.complex.EmployeeIfc", serviceName = "displayService")
public class EmployeImpl implements EmployeeIfc {

	@Override
	public String display(Employee e) {
		System.out.println("Received input : "+ e.toString());
		return "Printed in log...";
	}

}
