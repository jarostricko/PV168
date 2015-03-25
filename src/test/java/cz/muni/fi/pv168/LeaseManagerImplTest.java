package cz.muni.fi.pv168;

import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import java.math.BigDecimal;

import static cz.muni.fi.pv168.CarManagerImplTest.newCar;
import static cz.muni.fi.pv168.CustomerManagerImplTest.newCustomer;

public class LeaseManagerImplTest {
    private DataSource dataSource;
    private LeaseManagerImpl leaseManager;
    private CustomerManagerImpl customerManager;
    private CarManagerImpl carManager;
    private Car car1;
    private Car car2;
    private Customer customer1;
    private Customer customer2;

    private void prepareDate() throws DatabaseException {
        car1 = newCar("KE-238BU", true, "Audi", new BigDecimal(55.5));
        car1.setID(1L);
        car2 = newCar("BA-547KU", true, "Skoda", new BigDecimal(55.5));
        car2.setID(2L);
        customer1 = newCustomer("Stevo Kocur","Filakovo, 06587, Slovakia","+421458986254");
        customer1.setID(1L);
        customer2 = newCustomer("Milan Bandurka","Koksov Baksa, 04058, Slovakia","+421 965 214 658");
        customer2.setID(2L);

        carManager.createCar(car1);
        carManager.createCar(car2);

        customerManager.createCustomer(customer1);
        customerManager.createCustomer(customer2);

    }

    @Before
    public void setUp() throws Exception {
        leaseManager = new LeaseManagerImpl();
        carManager = new CarManagerImpl(dataSource);
        customerManager = new CustomerManagerImpl();

    }

    @Test
    public void testCreateLease() throws Exception {

    }
}