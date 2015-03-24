package cz.muni.fi.pv168;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CustomerManagerImplTest {
    private CustomerManagerImpl customerManager;

    @Before
    public void setUp() throws Exception {
        customerManager = new CustomerManagerImpl();
    }

    public static Customer newCustomer(String fullName, String address, String phoneNumber) {
        Customer customer = new Customer();
        customer.setID(null);
        customer.setFullName(fullName);
        customer.setAddress(address);
        customer.setPhoneNumber(phoneNumber);
        return customer;
    }

    @Test
    public void testCreateCustomer() throws DatabaseException {
        Customer customer = newCustomer("Milan Bandurka","Koksov Baksa, 04058, Slovakia","+421 965 214 658");

        customerManager.createCustomer(customer);
        Long customerID = customer.getID();
        assertNotNull(customerID);

        Customer result = customerManager.getCustomerByID(customerID);
        assertEquals(customer,result);
        assertNotSame(customer, result);
        assertCustomerDeepEquals(customer, result);

        try {
            customerManager.createCustomer(null);
            fail();
        } catch (IllegalArgumentException ex) {
        }
        customer = newCustomer("Stevo Kocur","Filakovo, 06587, Slovakia","+421458986254");
        customer.setID(new Long(50L));
        try {
            customerManager.createCustomer(customer);
            fail();
        }catch (IllegalArgumentException ex) {
        }
        customer.setFullName(null);
        customer.setID(null);
        try{
            customerManager.createCustomer(customer);
            fail();
        }catch (IllegalArgumentException ex){}
    }

    @Test
    public void removeCustomerWithNullID() throws DatabaseException {
        Customer customer = newCustomer("Milan Bandurka", "Koksov Baksa, 04058, Slovakia", "+421 965 214 658");

        try {
            customer.setID(null);
            customerManager.deleteCustomer(customer.getID());
            fail("nevyhodil vynimku pre customera s ID null");
        }catch (NullPointerException ex){

        }
    }

    @Test
    public void testRemoveCustomer() throws DatabaseException {
        assertTrue(customerManager.getAllCustomers().isEmpty());
        Customer customer1 = newCustomer("Stevo Kocur","Filakovo, 06587, Slovakia","+421458986254");
        Customer customer2 = newCustomer("Milan Bandurka","Koksov Baksa, 04058, Slovakia","+421 965 214 658");

        customerManager.createCustomer(customer1);
        customerManager.createCustomer(customer2);

        Long ID1 = customer1.getID();
        customerManager.deleteCustomer(ID1);

        assertNull(customerManager.getCustomerByID(customer1.getID()));
        assertNotNull(customerManager.getCustomerByID(customer2.getID()));
    }

    @Test
    public void testUpdateCustomer() throws DatabaseException {
        Customer customer = newCustomer("Stevo Kocur","Filakovo, 06587, Slovakia","+421458986254");
        customerManager.createCustomer(customer);
        Long ID = customer.getID();
        customer.setAddress("new address");
        customerManager.upgradeCustomer(customer);
        Customer result = customerManager.getCustomerByID(ID);
        assertCustomerDeepEquals(customer,result);

        customer = newCustomer("Milan Bandurka","Koksov Baksa, 04058, Slovakia","055636578");
        //customer.setID(new Long(ID + 1));

        try {
            customerManager.upgradeCustomer(customer);
            fail();
        }catch (Exception ex) {
        }

    }


    public static void assertCustomerDeepEquals(Customer expected, Customer actual) {
        assertEquals(expected.getID(), actual.getID());
        assertEquals(expected.getFullName(), actual.getFullName());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
    }


}