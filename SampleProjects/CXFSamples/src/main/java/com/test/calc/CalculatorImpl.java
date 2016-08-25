package com.test.calc;

import javax.jws.WebService;
/**
 * 
 * @author 559207
 *
 */
@WebService(endpointInterface = "com.test.calc.CalculatorIfc", serviceName = "calculatorService")
public class CalculatorImpl implements CalculatorIfc {

	public CalculatorImpl() {
		System.out.println("<===== Inside constructor ====>");
	}
	public int addition(int num1, int num2) {
		System.out.println("Inside addition...");
		return num1 + num2;
	}

	public int subtraction(int num1, int num2) {
		System.out.println("Inside subtraction...");
		return num1 - num2;
	}

	public int multiplication(int num1, int num2) {
		System.out.println("Inside multiplication...");
		return num1 * num2;
	}

	@Override
	public float division(int num1, int num2) throws Exception {
		System.out.println("Inside division...");
		return num1 / num2;
	}

}
