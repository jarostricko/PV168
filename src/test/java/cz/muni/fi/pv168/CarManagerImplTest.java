package cz.muni.fi.pv168;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

//import javax.activation.DataSource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;


public class CarManagerImplTest {

    private CarManagerImpl carManager;
    private DataSource dataSource;

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        carManager = new CarManagerImpl();
        carManager.setDataSource(dataSource);

    }
    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:MojeDB;create=true");
        return ds;
    }

    @Test
    public void testCreateCar() throws DatabaseException {
        Car car = newCar("KE-238BU", true, "Audi", 5555.5);

        carManager.createCar(car);

        Long carID = car.getID();
        assertNotNull(carID);

        Car result = carManager.getCarByID(carID);
        assertEquals(car, result);
        assertNotSame(car, result);
        assertDeepEquals(car, result);
    }

    @Test
    public void testCreateCarWithNull() throws Exception {
        try {
            carManager.createCar(null);
            fail("nevyhodil NullPointerException pro prazdny vstup");
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void createCarWithWrongAttributes() throws DatabaseException {
        Car car = newCar("KE-238BU", true, "Audi", 5555.5);
        car.setID(1l);
        try {
            carManager.createCar(car);
            fail();
        } catch (IllegalArgumentException e) {
        }

        car = newCar(null, true, "Audi", 5555.5);
        try {
            carManager.createCar(car);
            fail("nevyhodil Exception ked licencPlate je null");
        } catch (IllegalArgumentException e) {
        }

        car = newCar("KE-238BU", false, "Audi",  5555.5);
        try {
            carManager.createCar(car);
            fail("nevyhodil Exception ked status je false");
        } catch (IllegalArgumentException e) {
        }

        car = newCar("KE-238BU", true, null,  5555.5);
        try {
            carManager.createCar(car);
            fail("nevyhodil Exception ked model je null");
        } catch (IllegalArgumentException e) {
        }


        car = newCar("KE-238BU", true, "Audi",(-1.0));
        try {
            carManager.createCar(car);
            fail("nevyhodil Exception ked payment je zaporny");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void removeCar() throws DatabaseException {
        Car car1 = newCar("KE-238BU", true, "Audi",  5555.5);
        Car car2 = newCar("BA-547KU", true, "Skoda",  444.5);

        carManager.createCar(car1);
        carManager.createCar(car2);

        assertNotNull(carManager.getCarByID(car1.getID()));
        assertNotNull(carManager.getCarByID(car2.getID()));

        carManager.deleteCar(car1.getID());

        assertNull(carManager.getCarByID(car1.getID()));
        assertNotNull(carManager.getCarByID(car2.getID()));
    }

    @Test
    public void removeCarWithNullID() throws DatabaseException {
        Car car1 = newCar("KE-238BU", true, "Audi", 5555.5);

        try {
            car1.setID(null);
            carManager.deleteCar(car1.getID());
            fail("nevyhodil vynimku pre car s ID null");
        }catch (NullPointerException ex){

        }

    }

    private void assertDeepEquals(List<Car> expectedList, List<Car> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Car expected = expectedList.get(i);
            Car actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Car expected, Car actual) {
        assertEquals(expected.getID(), actual.getID());
        assertEquals(expected.getLicencePlate(), actual.getLicencePlate());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getModel(), actual.getModel());

    }

    public static Car newCar(String licencePlate, boolean status, String model, double payment) {
        Car car = new Car();

        car.setLicencePlate(licencePlate);
        car.setStatus(status);
        car.setModel(model);
        car.setRentalPayment(payment);
        return car;
    }
}