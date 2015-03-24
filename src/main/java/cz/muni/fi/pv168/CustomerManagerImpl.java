package cz.muni.fi.pv168;

import java.util.List;

/**
 * Created by Jaro on 10.3.2015.
 */
public class CustomerManagerImpl implements CustomerManager {
    @Override
    public void createCustomer(Customer customer) throws DatabaseException{
    }

    @Override
    public void deleteCustomer(Long ID) throws DatabaseException{
    }

    @Override
    public void upgradeCustomer(Customer customer) throws DatabaseException{
    }

    @Override
    public Customer getCustomerByID(Long ID) throws DatabaseException{
        return null;
    }

    @Override
    public List<Customer> getAllCustomers()throws DatabaseException {
        return null;
    }
}
