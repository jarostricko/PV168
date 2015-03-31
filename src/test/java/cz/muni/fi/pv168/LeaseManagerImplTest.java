package cz.muni.fi.pv168;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static cz.muni.fi.pv168.CarManagerImplTest.*;
import static cz.muni.fi.pv168.CustomerManagerImplTest.newCustomer;
import static org.junit.Assert.*;

public class LeaseManagerImplTest {
    private DataSource dataSource;
    private LeaseManagerImpl leaseManager;
    private CustomerManagerImpl customerManager;
    private CarManagerImpl carManager;
    private Car car1;
    private Car car2;
    private Car carWithoutID;
    private Car carNotInDB;
    private Customer customer1;
    private Customer customer2;
    private Customer customerWithoutID;
    private Customer customerNotInDB;

    private void prepareDate() throws DatabaseException {
        car1 = newCar("KE-238BU", true, "Audi", new BigDecimal(55.50));
        car2 = newCar("BA-547KU", true, "Skoda", new BigDecimal(55.50));
        customer1 = newCustomer("Stevo Kocur","Filakovo, 06587, Slovakia","+421458986254");
        customer2 = newCustomer("Milan Bandurka","Koksov Baksa, 04058, Slovakia","+421 965 214 658");

        carManager.createCar(car1);
        carManager.createCar(car2);

        customerManager.createCustomer(customer1);
        customerManager.createCustomer(customer2);

        carWithoutID = newCar("8B3 9763", true, "Audi", BigDecimal.valueOf(400.00));
        carNotInDB = newCar("3B6 8463", true, "Peugeot", BigDecimal.valueOf(0.00));
        carNotInDB.setID(car2.getID() + 100);

        customerWithoutID = newCustomer("Martin Pulec", "Brno, 5-11-24", "+420897589");
        customerNotInDB = newCustomer("Lukas Rucka", "Brno, 5-21-06", "+420256354");
        customerNotInDB.setID(customer2.getID() + 100);


    }

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:leaseManager-test;create=true");
        return ds;

    }

    @Before
    public void setUp() throws Exception {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, LeaseManager.class.getResourceAsStream("/createTables.sql"));
        leaseManager = new LeaseManagerImpl(dataSource);
        carManager = new CarManagerImpl(dataSource);
        customerManager = new CustomerManagerImpl(dataSource);
        prepareDate();
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, LeaseManager.class.getResourceAsStream("/dropTables.sql"));
    }

    @Test
    public void testCreateLease() throws DatabaseException {
        Lease lease = newLease(customer1, car1, Date.valueOf("2012-03-21"), Date.valueOf("2012-03-31"));
        lease.setPrice(leaseManager.calculatePriceByDays(lease));

        leaseManager.createLease(lease);
        Long leaseID = lease.getID();
        assertNotNull(leaseID);
        Lease result = leaseManager.getLeaseByID(leaseID);
        assertEquals(lease, result);
        assertNotSame(lease, result);
        assertLeaseDeepEquals(lease, result);
    }

    @Test
    public void testCreateLeaseWithNull() throws Exception {
        try {
            leaseManager.createLease(null);
            fail("nevyhodil NullPointerException pro prazdny vstup");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void createLeaseWithWrongAttributes() throws DatabaseException {
        Lease lease = newLease(customer1, car1, Date.valueOf("2012-03-21"), Date.valueOf("2012-03-31"));

        lease.setID(1l);
        try {
            leaseManager.createLease(lease);
            fail("Already setted ID, but no exception.");
        } catch (IllegalArgumentException e) {
        }

        lease = newLease(customer1, null, Date.valueOf("2012-03-21"), Date.valueOf("2012-03-31"));
        try {
            leaseManager.createLease(lease);
            fail("No exception for car = null.");
        } catch (IllegalArgumentException e) {
        }

        lease = newLease(null, car1, Date.valueOf("2012-03-21"), Date.valueOf("2012-03-31"));
        try {
            leaseManager.createLease(lease);
            fail("No exception for customer = null.");
        } catch (IllegalArgumentException e) {
        }

        lease = newLease(customer1, car1, null, Date.valueOf("2012-03-31"));
        try {
            leaseManager.createLease(lease);
            fail("No exception for startTime = null");
        } catch (IllegalArgumentException e) {
        }

        lease = newLease(customer1, car1, Date.valueOf("2012-03-21"), null);
        try {
            leaseManager.createLease(lease);
            fail("No exception for endTime = null.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testUpdateLease() throws DatabaseException {
        Lease lease = newLease(customer1, car1, Date.valueOf("2012-03-21"), Date.valueOf("2012-03-31"));
        lease.setPrice(leaseManager.calculatePriceByDays(lease));

        leaseManager.createLease(lease);
        Long leaseID = lease.getID();

        lease.setCustomer(customer2);
        leaseManager.updateLease(lease);
        Lease result = leaseManager.getLeaseByID(leaseID);
        assertLeaseDeepEquals(lease, result);

        lease.setCar(car2);
        leaseManager.updateLease(lease);
        result = leaseManager.getLeaseByID(leaseID);
        assertLeaseDeepEquals(lease, result);

        lease.setStartDate(Date.valueOf("2012-03-10"));
        leaseManager.updateLease(lease);
        result = leaseManager.getLeaseByID(leaseID);
        assertLeaseDeepEquals(lease, result);

        lease.setEndDate(Date.valueOf("2012-04-10"));
        leaseManager.updateLease(lease);
        result = leaseManager.getLeaseByID(leaseID);
        assertLeaseDeepEquals(lease, result);

    }

    @Test
    public void removeLease() throws DatabaseException {
        Lease lease1 = newLease(customer1, car1, Date.valueOf("2012-03-21"), Date.valueOf("2012-03-30"));
        lease1.setPrice(leaseManager.calculatePriceByDays(lease1));
        Lease lease2 = newLease(customer2, car2, Date.valueOf("2012-04-30"), Date.valueOf("2012-05-05"));
        lease2.setPrice(leaseManager.calculatePriceByDays(lease2));

        leaseManager.createLease(lease1);
        leaseManager.createLease(lease2);

        assertNotNull(leaseManager.getLeaseByID(lease1.getID()));
        assertNotNull(leaseManager.getLeaseByID(lease2.getID()));

        leaseManager.deleteLease(lease1.getID());

        assertNull(leaseManager.getLeaseByID(lease1.getID()));
        assertNotNull(leaseManager.getLeaseByID(lease2.getID()));
    }

    @Test
    public void removeLeaseWithNullID() throws DatabaseException {
        Lease lease1 = newLease(customer1, car1, Date.valueOf("2012-03-21"), Date.valueOf("2012-03-30"));
        lease1.setPrice(leaseManager.calculatePriceByDays(lease1));

        try {
            lease1.setID(null);
            leaseManager.deleteLease(lease1.getID());
            fail("nevyhodil vynimku pre lease s ID null");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testGetLeaseByID() throws DatabaseException {

        assertNull(leaseManager.getLeaseByID(1l));

        Lease lease1 = newLease(customer1, car1, Date.valueOf("2012-03-21"), Date.valueOf("2012-03-30"));
        lease1.setPrice(leaseManager.calculatePriceByDays(lease1));

        leaseManager.createLease(lease1);
        Long leaseID = lease1.getID();

        Lease result = leaseManager.getLeaseByID(leaseID);
        assertEquals(lease1, result);
        assertLeaseDeepEquals(lease1, result);
    }

    @Test
    public void testGetLeasesForCustomer() throws DatabaseException {

        Lease lease1 = newLease(customer1, car1, Date.valueOf("2012-03-21"), Date.valueOf("2012-03-30"));
        lease1.setPrice(leaseManager.calculatePriceByDays(lease1));
        Lease lease2 = newLease(customer1, car2, Date.valueOf("2012-04-30"), Date.valueOf("2012-05-05"));
        lease2.setPrice(leaseManager.calculatePriceByDays(lease2));

        List<Lease> leasesOfCustomer1 = Arrays.asList(lease1, lease2);
        leaseManager.createLease(lease1);
        leaseManager.createLease(lease2);

        List<Lease> result = leaseManager.getLeasesForCustomer(customer1);

        assertLeaseDeepEquals(leasesOfCustomer1, result);


        try {
            leaseManager.getLeasesForCustomer(null);
            fail("No exception for null customer");
        } catch (IllegalArgumentException ex) {
        }

        try {
            customer1.setID(null);
            leaseManager.getLeasesForCustomer(customer1);
            fail("No exception for customer with ID null");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testGetLeasesForCar() throws DatabaseException {

        Lease lease1 = newLease(customer1, car1, Date.valueOf("2012-03-21"), Date.valueOf("2012-03-30"));
        lease1.setPrice(leaseManager.calculatePriceByDays(lease1));
        Lease lease2 = newLease(customer2, car1, Date.valueOf("2012-04-30"), Date.valueOf("2012-05-05"));
        lease2.setPrice(leaseManager.calculatePriceByDays(lease2));

        List<Lease> leasesOfCar1 = Arrays.asList(lease1, lease2);
        leaseManager.createLease(lease1);
        leaseManager.createLease(lease2);

        List<Lease> result = leaseManager.getLeasesForCar(car1);

        assertLeaseDeepEquals(leasesOfCar1, result);


        try {
            leaseManager.getLeasesForCar(null);
            fail("No exception for null car");
        } catch (IllegalArgumentException ex) {
        }

        try {
            car1.setID(null);
            leaseManager.getLeasesForCar(car1);
            fail("No exception for car with ID null");
        } catch (IllegalArgumentException ex) {
        }
    }

    private static Lease newLease(Customer customer, Car car, Date startDate, Date endDate) {
        Lease lease = new Lease();
        lease.setCustomer(customer);
        lease.setCar(car);
        lease.setEndDate(endDate);
        lease.setStartDate(startDate);
        return lease;
    }

    private void assertLeaseDeepEquals(Lease expected, Lease actual) {
        assertEquals(expected.getID(), actual.getID());
        assertEquals(expected.getCar(), actual.getCar());
        assertEquals(expected.getCustomer(), actual.getCustomer());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getStartDate(), actual.getStartDate());
    }

    private void assertLeaseDeepEquals(List<Lease> expectedList, List<Lease> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Lease expected = expectedList.get(i);
            Lease actual = actualList.get(i);
            assertLeaseDeepEquals(expected, actual);
        }
    }
}