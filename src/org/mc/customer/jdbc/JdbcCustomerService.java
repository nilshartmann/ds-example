package org.mc.customer.jdbc;

import java.util.Map;

import org.mc.customerservice.Customer;
import org.mc.customerservice.CustomerService;
import org.mc.database.Database;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(immediate=true)
public class JdbcCustomerService implements CustomerService {
	
	private Database _database;

	@Override
	public String getCustomerServiceDescription() {
		return "Database-based Customer Service";
	}

	@Override
	public Customer findCustomerById(long id) {
		Map<String, String> singleResult = _database.getSingeResult(String.format("SELECT * FROM CUSTOMER WHERE id=%d",
				id));

		
		Customer customer = new Customer(id, singleResult.get("firstname"), singleResult.get("familyname"));
		return customer;
	}
	
	@Reference
	public void setDatabase(Database database) {
		System.out.println("Setting database: " + database);
		_database = database;
		
		System.out.println("customer: " + findCustomerById(1));
		
	}
	

}
