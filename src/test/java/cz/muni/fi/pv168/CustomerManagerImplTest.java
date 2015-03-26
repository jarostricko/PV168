package cz.muni.fi.pv168;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;

public class CustomerManagerImplTest {
    private CustomerManagerImpl customerManager;
    private DataSource dataSource;

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:carManager-test;create=true");
        return ds;

    }

    @Before
    public void setUp() throws Exception {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, CustomerManager.class.getResourceAsStream("/createTables.sql"));
        customerManager = new CustomerManagerImpl(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, CarManager.class.getResourceAsStream("/dropTables.sql"));
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
    }

    @Test
    public void testGetCustomerByID() throws DatabaseException {

        assertNull(customerManager.getCustomerByID(1l));

        Customer customer = newCustomer("Milan Bandurka", "Koksov Baksa, 04058, Slovakia", "+421 965 214 658");
        customerManager.createCustomer(customer);
        Long customerID = customer.getID();

        Customer result = customerManager.getCustomerByID(customerID);
        assertEquals(customer, result);
        assertCustomerDeepEquals(customer, result);
    }

    @Test
    public void testGetAllCustomers() throws DatabaseException {

        assertTrue(customerManager.getAllCustomers().isEmpty());

        Customer c1 = newCustomer("Milan Bandurka", "Koksov Baksa, 04058, Slovakia", "+421 965 214 658");
        Customer c2 = newCustomer("Stevo Kocur", "Filakovo, 06587, Slovakia", "+421458986254");

        customerManager.createCustomer(c1);
        customerManager.createCustomer(c2);

        List<Customer> expected = Arrays.asList(c1, c2);
        List<Customer> actual = customerManager.getAllCustomers();

        assertCustomerCollectionDeepEquals(expected, actual);
    }

    @Test
    public void removeCustomerWithNullID() throws DatabaseException {
        Customer customer = newCustomer("Milan Bandurka", "Koksov Baksa, 04058, Slovakia", "+421 965 214 658");

        try {
            customer.setID(null);
            customerManager.deleteCustomer(customer.getID());
            fail("nevyhodil vynimku pre customera s ID null");
        } catch (IllegalArgumentException ex) {

        }
    }

    @Test
    public void testUpdateCustomer() throws DatabaseException {
        Customer customer = newCustomer("Stevo Kocur", "Filakovo, 06587, Slovakia", "+421458986254");
        customerManager.createCustomer(customer);

        Customer c2 = newCustomer("Milan Bandurka", "Koksov Baksa, 04058, Slovakia", "+421 965 214 658");
        customerManager.createCustomer(c2);

        Long customerID = customer.getID();
        Customer result;

        customer = customerManager.getCustomerByID(customerID);
        customer.setFullName("");
        customerManager.updateCustomer(customer);
        result = customerManager.getCustomerByID(customerID);
        assertCustomerDeepEquals(customer, result);

        customer = customerManager.getCustomerByID(customerID);
        customer.setAddress("");
        customerManager.updateCustomer(customer);
        result = customerManager.getCustomerByID(customerID);
        assertCustomerDeepEquals(customer, result);

        customer = customerManager.getCustomerByID(customerID);
        customer.setPhoneNumber("");
        customerManager.updateCustomer(customer);
        result = customerManager.getCustomerByID(customerID);
        assertCustomerDeepEquals(customer, result);

        // Check if updates didn't affected other records
        assertCustomerDeepEquals(c2, customerManager.getCustomerByID(c2.getID()));
    }

    @Test
    public void updateCustomerWithWrongAttributes() throws DatabaseException {

        Customer customer = newCustomer("Stevo Kocur", "Filakovo, 06587, Slovakia", "+421458986254");
        customerManager.createCustomer(customer);
        Long customerId = customer.getID();

        try {
            customerManager.updateCustomer(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = customerManager.getCustomerByID(customerId);
            customer.setID(null);
            customerManager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = customerManager.getCustomerByID(customerId);
            customer.setID(customerId - 1);
            customerManager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        } catch (DatabaseException ex) {
            //OK
        }

        try {
            customer = customerManager.getCustomerByID(customerId);
            customer.setFullName(null);
            customerManager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = customerManager.getCustomerByID(customerId);
            customer.setAddress(null);
            customerManager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = customerManager.getCustomerByID(customerId);
            customer.setPhoneNumber(null);
            customerManager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    @Test
    public void testDeleteCustomer() throws DatabaseException {
        assertTrue(customerManager.getAllCustomers().isEmpty());
        Customer customer1 = newCustomer("Stevo Kocur","Filakovo, 06587, Slovakia","+421458986254");
        Customer customer2 = newCustomer("Milan Bandurka","Koksov Baksa, 04058, Slovakia","+421 965 214 658");

        customerManager.createCustomer(customer1);
        customerManager.createCustomer(customer2);

        assertNotNull(customerManager.getCustomerByID(customer1.getID()));
        assertNotNull(customerManager.getCustomerByID(customer2.getID()));

        Long ID1 = customer1.getID();
        customerManager.deleteCustomer(ID1);

        assertNull(customerManager.getCustomerByID(customer1.getID()));
        assertNotNull(customerManager.getCustomerByID(customer2.getID()));
    }

    @Test
    public void testDeleteCustomerWithWrongAtributes() throws DatabaseException {
        Customer customer = newCustomer("Stevo Kocur","Filakovo, 06587, Slovakia","+421458986254");

        try {
            customer.setID(null);
            Long custID = customer.getID();
            customerManager.deleteCustomer(custID);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    public static Customer newCustomer(String fullName, String address, String phoneNumber) {
        Customer customer = new Customer();
        customer.setID(null);
        customer.setFullName(fullName);
        customer.setAddress(address);
        customer.setPhoneNumber(phoneNumber);
        return customer;
    }

    public static void assertCustomerDeepEquals(Customer expected, Customer actual) {
        assertEquals(expected.getID(), actual.getID());
        assertEquals(expected.getFullName(), actual.getFullName());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
    }

    static void assertCustomerCollectionDeepEquals(List<Customer> expected, List<Customer> actual) {

        assertEquals(expected.size(), actual.size());
        List<Customer> expectedSortedList = new ArrayList<>(expected);
        List<Customer> actualSortedList = new ArrayList<>(actual);
        Collections.sort(expectedSortedList, customerKeyComparator);
        Collections.sort(actualSortedList, customerKeyComparator);
        for (int i = 0; i < expectedSortedList.size(); i++) {
            assertCustomerDeepEquals(expectedSortedList.get(i), actualSortedList.get(i));
        }
    }

    private static final Comparator<Customer> customerKeyComparator = (o1, o2) -> {
        Long k1 = o1.getID();
        Long k2 = o2.getID();
        return k1 == null ? (k2 == null ? 0 : -1) : k2 == null ? 1 : k1.compareTo(k2);

    };


}