package cz.muni.fi.pv168;

import java.math.BigDecimal;
import java.sql.Date;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jaro on 10.3.2015.
 */

public class LeaseManagerImpl implements LeaseManager {
    private DataSource dataSource;
    private CustomerManager customerManager = new CustomerManagerImpl();
    private CarManager carManager = new CarManagerImpl();

    public LeaseManagerImpl(DataSource ds) {
        this.dataSource = ds;
    }

    public LeaseManagerImpl() {

    }

    @Override
    public void createLease(Lease lease) throws DatabaseException{
        if (lease.getID() != null) {
            throw new IllegalArgumentException("Cant create lease. ID is already set.");
        }
        if (lease.getCar() == null) {
            throw new IllegalArgumentException("Cant create lease. Car is null.");
        }
        if (lease.getCustomer() == null) {
            throw new IllegalArgumentException("Cant create lease. Customer is null.");
        }
        if (lease.getCar().getID() == null) {
            throw new IllegalArgumentException("Cant create lease. Cur`s ID is null.");
        }
        if (lease.getCustomer().getID() == null) {
            throw new IllegalArgumentException("Cant create lease. Customer`s ID is null.");
        }
        if (!lease.getCar().getStatus()) {
            throw new IllegalArgumentException("Car is already rented.");
        }
        if (lease.getPrice() == null || lease.getEndDate() == null || lease.getStartDate() == null) {
            throw new IllegalArgumentException("Cant create lease with wrong attribute(s).");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("INSERT INTO LEASES (CUSTOMER, CAR, PRICE,START_DATE,END_DATE VALUES (?,?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, lease.getCustomer().getID());
                statement.setLong(2, lease.getCar().getID());
                statement.setBigDecimal(3, lease.getPrice());
                statement.setDate(4, lease.getStartDate());
                statement.setDate(5, lease.getEndDate());

                int addedRows = statement.executeUpdate();
                if (addedRows != 1) {
                    throw new DatabaseException("Database error while inserting new lease.");
                }
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        lease.setID(id);
                    }
                }
                lease.getCar().setStatus(false);
                carManager.updateCar(lease.getCar());
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Error while inserting lease to database.", ex);
        }

    }

    @Override
    public void updateLease(Lease lease) throws DatabaseException{
        if (lease == null) {
            throw new IllegalArgumentException("Cant update lease. Lease is null.");
        }
        if (lease.getCar() == null) {
            throw new IllegalArgumentException("Cant update lease. Car is null.");
        }
        if (lease.getCustomer() == null) {
            throw new IllegalArgumentException("Cant update lease. Customer is null.");
        }
        if (lease.getCar().getID() == null) {
            throw new IllegalArgumentException("Cant update lease. Cur`s ID is null.");
        }
        if (lease.getCustomer().getID() == null) {
            throw new IllegalArgumentException("Cant update lease. Customer`s ID is null.");
        }
        if (lease.getCar().getStatus()) {
            throw new IllegalArgumentException("Car in this lease is not rented. Cant update.");
        }
        if (lease.getPrice() == null || lease.getEndDate() == null || lease.getStartDate() == null) {
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
                    throw new DatabaseException("Lease with ID: " + lease.getID() + " was not updated.");
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Error while updating lease in database.", ex);
        }
    }

    @Override
    public void deleteLease(Long ID) throws DatabaseException{
        if (ID == null) {
            throw new IllegalArgumentException("Cant delete lease with ID null.");
        }
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM LEASES WHERE id=?")) {
                statement.setLong(1, ID);
                int s = statement.executeUpdate();
                if (s != 1) {
                    throw new DatabaseException("Lease with ID: " + ID + " was not deleted.");
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Error while deleting lease from database.", ex);
        }
    }

    @Override
    public Lease getLeaseByID(Long ID)throws DatabaseException {
        if (ID == null) {
            throw new IllegalArgumentException("Lease with null ID.");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM LEASES WHERE id = ?")) {
                st.setLong(1, ID);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        Lease resultLease = getLeaseFromResultSet(rs);
                        if (rs.next()) {
                            throw new DatabaseException("Error, find more leases with ID: " + ID);
                        }
                        return resultLease;
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Selecting specify lease from database failed.", e);
        }
    }

    @Override
    public List<Lease> getAllLeases()throws DatabaseException {
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
        } catch (SQLException e) {
            throw new DatabaseException("Get all leases failed on database.", e);
        }
    }

    @Override
    public List<Lease> getLeasesForCustomer(Customer customer)throws DatabaseException {
        if (customer == null) {
            throw new IllegalArgumentException("Cant get all leases. Customer is null.");
        }
        if (customer.getID() == null) {
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
        } catch (SQLException e) {
            throw new DatabaseException("Get all leases for customer with ID: " + customer.getID().toString() + " failed on database.", e);
        }

    }

    @Override
    public List<Lease> getLeasesForCar(Car car)throws DatabaseException {
        if (car == null) {
            throw new IllegalArgumentException("Cant get all leases. Car is null.");
        }
        if (car.getID() == null) {
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
        } catch (SQLException e) {
            throw new DatabaseException("Get all leases for car with ID: " + car.getID().toString() + " failed on database.", e);
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
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillis = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    public BigDecimal calculatePriceByDays(Lease lease) {
        if (lease == null) {
            throw new IllegalArgumentException("Cant calculate price, lease is null.");
        }
        if (lease.getStartDate() == null || lease.getEndDate() == null || lease.getCar() == null) {
            throw new IllegalArgumentException("Cant calculate price, lease with wrong parameter(s).");
        }
        if (lease.getCar().getRentalPayment().compareTo(BigDecimal.ZERO) < 0 || lease.getCar().getRentalPayment() == null) {
            throw new IllegalArgumentException("Cant calculate price, wrong car rental payment.");
        }

        BigDecimal dayPrice = lease.getCar().getRentalPayment();
        return dayPrice.multiply(new BigDecimal(getDateDiff(lease.getStartDate(), lease.getEndDate(), TimeUnit.DAYS)));
    }
}
