package cz.muni.fi.pv168;

import java.sql.Date;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


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
            throw new IllegalArgumentException("Car is already rented");
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

    }

    @Override
    public void deleteLease(Long ID) throws DatabaseException{

    }

    @Override
    public Lease getLeaseByID(Long ID)throws DatabaseException {
        return null;
    }

    @Override
    public List<Lease> getAllLeases()throws DatabaseException {
        return null;
    }

    @Override
    public List<Lease> getLeasesForCustomer(Customer customer)throws DatabaseException {
        return null;
    }

    @Override
    public List<Lease> getLeasesForCar(Car car)throws DatabaseException {
        return null;
    }
}
