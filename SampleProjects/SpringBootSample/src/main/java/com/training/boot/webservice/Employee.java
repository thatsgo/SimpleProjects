package com.training.boot.webservice;

/**
 * 
 * @author 559207
 *
 */
public class Employee {

	private String name;

	private int id;

	private String designation;

	public Employee() {
	}
	
	public Employee(String name, int id, String designation) {
		this.name = name;
		this.id = id;
		this.designation = designation;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation
	 *            the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Employee [name=" + name + ", id=" + id + ", designation=" + designation + "]";
	}

}
