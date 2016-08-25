package com.test.calc.client;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.test.calc.CalculatorIfc;
/**
 * 
 * @author 559207
 *
 */
public class DemoClient {

	public static void main(String[] args) {
		String serviceUrl = "http://10.242.133.153:8080/salary/calculatorservice?wsdl";
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(CalculatorIfc.class);
		factory.setAddress(serviceUrl);
		CalculatorIfc bookService = (CalculatorIfc) factory.create();
		System.out.println(bookService.addition(1, 3));
	}
}
