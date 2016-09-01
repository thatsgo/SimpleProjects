package com.test.calc;

import javax.jws.WebMethod;
import javax.jws.WebParam;
/**
 * 
 * @author 559207
 *
 */
import javax.jws.WebService;

@WebService
public interface CalculatorIfc {
	/**
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 */
	@WebMethod
	public int addition(@WebParam(name = "number1") int num1, @WebParam(name = "number2") int num2);

	/**
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 */
	@WebMethod(operationName="")
	public int subtraction(int num1, int num2);

	/**
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 */
	@WebMethod
	public int multiplication(int num1, int num2);

	/**
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	public float division(int num1, int num2) throws Exception;
}
