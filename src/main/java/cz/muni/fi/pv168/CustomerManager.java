package cz.muni.fi.pv168;

import java.util.List;

/**
 * Created by Jaro on 10.3.2015.
 */
public interface CustomerManager {
    public void createCustomer(Customer customer) throws DatabaseException;
    public void deleteCustomer(Long ID) throws DatabaseException;
    public void updateCustomer(Customer customer) throws DatabaseException;
    public Customer getCustomerByID(Long ID) throws DatabaseException;
    public List<Customer> getAllCustomers() throws DatabaseException;
}
