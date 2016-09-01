/**
 * 
 */
package com.jpa.one.to.many.relationship;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author 559207
 *
 */
/**
 * @author 559207
 *
 */
@Entity
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String streetName;

	@ManyToOne
	@JoinColumn(name = "employee_fk")
	private Employee employee1;

	public Employee getEmployee1() {
		return employee1;
	}

	public void setEmployee1(Employee employee1) {
		this.employee1 = employee1;
	}

	public Address() {
	}
	
	public Address(int id, String streetName,Employee e) {
		this.id = id;
		this.streetName = streetName;
		this.employee1=e;
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
	 * @return the streetName
	 */
	public String getStreetName() {
		return streetName;
	}

	/**
	 * @param streetName
	 *            the streetName to set
	 */
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}}
