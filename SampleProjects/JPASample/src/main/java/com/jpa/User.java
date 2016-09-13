package com.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name = "Users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "USER_NAME")
	private String name;
	private String login;
	private String password;

	private LocalDateTime loginDateTime;
	
	private LocalDate arrival;

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
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login
	 *            the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the loginDateTime
	 */
	public LocalDateTime getLoginDateTime() {
		return loginDateTime;
	}

	/**
	 * @param loginDateTime the loginDateTime to set
	 */
	public void setLoginDateTime(LocalDateTime loginDateTime) {
		this.loginDateTime = loginDateTime;
	}

	/**
	 * @return the arrival
	 */
	public LocalDate getArrival() {
		return arrival;
	}

	/**
	 * @param arrival the arrival to set
	 */
	public void setArrival(LocalDate arrival) {
		this.arrival = arrival;
	}

	
}