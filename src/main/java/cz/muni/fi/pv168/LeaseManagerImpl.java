package cz.muni.fi.pv168;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jaro on 10.3.2015.
 */

public class LeaseManagerImpl implements LeaseManager {
    final static Logger log = LoggerFactory.getLogger(LeaseManagerImpl.class);

    private DataSource dataSource;
    private CustomerManager customerManager;
    private CarManager carManager;

    public LeaseManagerImpl(DataSource ds) {
        this.dataSource = ds;
        customerManager = new CustomerManagerImpl(dataSource);
        carManager = new CarManagerImpl(dataSource);
    }

    public LeaseManagerImpl() {

    }

    @Override
    public void createLease(Lease lease) throws DatabaseException {
        if (lease == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant create lease. Lease is null.");
        }
        if (!checkIfCarInThisLeaseIsAvailable(lease)) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Car is not available.");
        }
        if (lease.getID() != null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant create lease. ID is already set.");
        }
        if (lease.getCar() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant create lease. Car is null.");
        }
        if (lease.getCustomer() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant create lease. Customer is null.");
        }
        if (lease.getCar().getID() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant create lease. Cur`s ID is null.");
        }
        if (lease.getCustomer().getID() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant create lease. Customer`s ID is null.");
        }
        if (!lease.getCar().getStatus()) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Car is already rented.");
        }
        if (lease.getEndDate() == null || lease.getStartDate() == null || lease.getPrice() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant create lease with wrong attribute(s).");
        }
        if (getDateDiff(lease.getStartDate(), lease.getEndDate(), TimeUnit.DAYS) < 0) {
            log.error("wrong dates");
            throw new IllegalArgumentException("Cant create lease with wrong dates.");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("INSERT INTO LEASES (CUSTOMER, CAR, PRICE,START_DATE,END_DATE) VALUES (?,?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, lease.getCustomer().getID());
                statement.setLong(2, lease.getCar().getID());
                statement.setBigDecimal(3, lease.getPrice());
                statement.setDate(4, lease.getStartDate());
                statement.setDate(5, lease.getEndDate());

                int addedRows = statement.executeUpdate();
                if (addedRows != 1) {
                    log.error("Database error while crating a lease.");
                    throw new DatabaseException("Database error while inserting new lease.");
                }
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        lease.setID(id);
                        log.debug("New Lease with ID: " + lease.getID() + " created.");
                    }
                }
            }
            lease.getCustomer().setStatus(false);
            customerManager.updateCustomer(lease.getCustomer());
            lease.getCar().setStatus(false);
            carManager.updateCar(lease.getCar());
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new DatabaseException("Error while inserting lease to database.", ex);
        }
    }

    @Override
    public void updateLease(Lease lease) throws DatabaseException {
        if (lease == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant update lease. Lease is null.");
        }
        if (lease.getCar() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant update lease. Car is null.");
        }
        if (lease.getCustomer() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant update lease. Customer is null.");
        }
        if (lease.getCar().getID() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant update lease. Cur`s ID is null.");
        }
        if (lease.getCustomer().getID() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant update lease. Customer`s ID is null.");
        }

        if (lease.getPrice() == null || lease.getEndDate() == null || lease.getStartDate() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant update lease with wrong attribute(s).");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("UPDATE LEASES SET CUSTOMER = ?,CAR = ?," +
                    "PRICE = ?,start_date = ?, end_date = ? WHERE id=?")) {
                statement.setLong(1, lease.getCustomer().getID());
                statement.setLong(2, lease.getCar().getID());
                statement.setBigDecimal(3, lease.getPrice());
                statement.setDate(4, lease.getStartDate());
                statement.setDate(5, lease.getEndDate());
                statement.setLong(6, lease.getID());

                int addedRows = statement.executeUpdate();
                if (addedRows != 1) {
                    log.error("Database error while updating a lease.");
                    throw new DatabaseException("Lease with ID: " + lease.getID() + " was not updated.");
                }
                log.debug("Lease with ID: " + lease.getID() + " updated.");
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new DatabaseException("Error while updating lease in database.", ex);
        }
    }

    @Override
    public void deleteLease(Long ID) throws DatabaseException {
        if (ID == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant delete lease with ID null.");
        }
        try (Connection connection = dataSource.getConnection()) {
            Car car = getLeaseByID(ID).getCar();
            Customer customer = getLeaseByID(ID).getCustomer();
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM LEASES WHERE id=?")) {
                statement.setLong(1, ID);
                int s = statement.executeUpdate();
                if (s != 1) {
                    log.error("Database error while deleting a lease.");
                    throw new DatabaseException("Lease with ID: " + ID + " was not deleted.");
                }
                log.debug("Lease with ID: " + ID + " deleted.");
                car.setStatus(true);
                carManager.updateCar(car);
                log.debug("Car for deleted lease is now available");
                if (checkIfCustomerIsWithoutLeases(customer)) {
                    customer.setStatus(true);
                    customerManager.updateCustomer(customer);
                    log.debug("Customer for deleted lease now has no leases.");
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new DatabaseException("Error while deleting lease from database.", ex);
        }
    }

    @Override
    public Lease getLeaseByID(Long ID) throws DatabaseException {
        log.debug("getting lease by ID: " + ID);
        if (ID == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Lease with null ID.");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM LEASES WHERE id = ?")) {
                st.setLong(1, ID);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        Lease resultLease = getLeaseFromResultSet(rs);
                        if (rs.next()) {
                            log.error("Database error while getting a lease by ID: " + ID);
                            throw new DatabaseException("Error, find more leases with ID: " + ID);
                        }
                        return resultLease;
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new DatabaseException("Selecting specify lease from database failed.", ex);
        }
    }

    @Override
    public List<Lease> getAllLeases() throws DatabaseException {
        log.debug("getting all leases");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM LEASES")) {
                try (ResultSet rs = st.executeQuery()) {
                    List<Lease> allLeases = new ArrayList<>();
                    while (rs.next()) {
                        allLeases.add(getLeaseFromResultSet(rs));
                    }
                    return allLeases;
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new DatabaseException("Get all leases failed on database.", ex);
        }
    }

    @Override
    public List<Lease> getLeasesForCustomer(Customer customer) throws DatabaseException {
        log.debug("getting all leases for customer with ID:" + customer.getID());
        if (customer == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant get all leases. Customer is null.");
        }
        if (customer.getID() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant get all leases. Customers ID is null.");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM LEASES WHERE CUSTOMER = ?")) {
                st.setLong(1, customer.getID());
                try (ResultSet rs = st.executeQuery()) {
                    List<Lease> allLeases = new ArrayList<>();
                    while (rs.next()) {
                        allLeases.add(getLeaseFromResultSet(rs));
                    }
                    return allLeases;
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new DatabaseException("Get all leases for customer with ID: " + customer.getID().toString() + " failed on database.", ex);
        }

    }

    @Override
    public List<Lease> getLeasesForCar(Car car) throws DatabaseException {
        log.debug("getting all leases for car with ID: " + car.getID());
        if (car == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant get all leases. Car is null.");
        }
        if (car.getID() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant get all leases. Cars ID is null.");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM LEASES WHERE CAR = ?")) {
                st.setLong(1, car.getID());
                try (ResultSet rs = st.executeQuery()) {
                    List<Lease> allLeases = new ArrayList<>();
                    while (rs.next()) {
                        allLeases.add(getLeaseFromResultSet(rs));
                    }
                    return allLeases;
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new DatabaseException("Get all leases for car with ID: " + car.getID().toString() + " failed on database.", ex);
        }
    }

    private Lease getLeaseFromResultSet(ResultSet resultSet) throws SQLException, DatabaseException {
        Lease lease = new Lease();
        lease.setID(resultSet.getLong("id"));
        lease.setCustomer(customerManager.getCustomerByID(resultSet.getLong("customer")));
        lease.setCar(carManager.getCarByID(resultSet.getLong("car")));
        lease.setPrice(resultSet.getBigDecimal("price"));
        lease.setStartDate(resultSet.getDate("start_date"));
        lease.setEndDate(resultSet.getDate("end_date"));
        return lease;
    }

    /**
     * Get a diff between two dates
     *
     * @param date1    the oldest date
     * @param date2    the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillis = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    public BigDecimal calculatePriceByDays(Lease lease) {
        if (lease == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant calculate price, lease is null.");
        }
        if (lease.getStartDate() == null || lease.getEndDate() == null || lease.getCar() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant calculate price, lease with wrong parameter(s).");
        }
        if (lease.getCar().getRentalPayment().compareTo(BigDecimal.ZERO) < 0 || lease.getCar().getRentalPayment() == null) {
            log.error("Wrong parameter");
            throw new IllegalArgumentException("Cant calculate price, wrong car rental payment.");
        }

        BigDecimal dayPrice = lease.getCar().getRentalPayment();
        return dayPrice.multiply(new BigDecimal(getDateDiff(lease.getStartDate(), lease.getEndDate(), TimeUnit.DAYS)));
    }

    public boolean checkIfCarInThisLeaseIsAvailable(Lease lease) throws DatabaseException {
        List<Lease> allLeasesForCar = getLeasesForCar(lease.getCar());
        for (Lease lease1 : allLeasesForCar) {
            long diff = getDateDiff(lease1.getEndDate(), lease.getStartDate(), TimeUnit.DAYS);
            if (diff <= 0) {
                return false;
            }
        }
        lease.getCar().setStatus(true);
        carManager.updateCar(lease.getCar());
        return true;
    }

    public boolean checkIfCustomerIsWithoutLeases(Customer customer) throws DatabaseException {
        List<Lease> leases = getLeasesForCustomer(customer);

        if (leases.isEmpty()) {
            customer.setStatus(true);
            return true;
        }
        return false;
    }

}
