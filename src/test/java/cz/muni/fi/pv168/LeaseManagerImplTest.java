package cz.muni.fi.pv168;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;


import static cz.muni.fi.pv168.CarManagerImplTest.newCar;
import static cz.muni.fi.pv168.CustomerManagerImplTest.newCustomer;

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
        car1 = newCar("KE-238BU", true, "Audi", new BigDecimal(55.5));
        car2 = newCar("BA-547KU", true, "Skoda", new BigDecimal(55.5));
        customer1 = newCustomer("Stevo Kocur","Filakovo, 06587, Slovakia","+421458986254");
        customer2 = newCustomer("Milan Bandurka","Koksov Baksa, 04058, Slovakia","+421 965 214 658");

        carManager.createCar(car1);
        carManager.createCar(car2);

        customerManager.createCustomer(customer1);
        customerManager.createCustomer(customer2);

        carWithoutID = newCar("8B3 9763", true, "Audi", BigDecimal.valueOf(400.0));
        carNotInDB = newCar("3B6 8463", true, "Peugeot", BigDecimal.valueOf(0.0));
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
    public void testCreateLease() throws Exception {
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
}