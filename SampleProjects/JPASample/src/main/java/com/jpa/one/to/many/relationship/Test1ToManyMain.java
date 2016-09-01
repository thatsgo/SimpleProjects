/**
 * 
 */
package com.jpa.one.to.many.relationship;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.jpa.User;
import com.mysql.cj.fabric.xmlrpc.base.Array;

/**
 * @author 559207
 *
 */
public class Test1ToManyMain {

	private static final String PERSISTENCE_UNIT_NAME = "User";
	private static EntityManagerFactory factory;

	public static void main(String[] args) {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		
		Employee emp = new Employee();
		emp.setName("KRISHNA");
		
		Address address1 = new Address();
		address1.setStreetName("sss");
		address1.setEmployee1(emp); 
		
		Address address2 = new Address();
		address2.setStreetName("sss");
		address2.setEmployee1(emp);
		
		List<Address> addressDetails = new ArrayList<>();
		addressDetails.add(address2);
		addressDetails.add(address1);
		
		emp.setAddress(addressDetails);
		em.persist(emp);
		
		
		em.getTransaction().commit();
		System.out.println("Sucess");
	}

}
