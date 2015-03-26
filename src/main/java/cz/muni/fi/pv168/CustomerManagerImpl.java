package cz.muni.fi.pv168;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rado on 10.3.2015.
 */
public class CustomerManagerImpl implements CustomerManager {

    private DataSource dataSource;

    public CustomerManagerImpl(DataSource ds) {
        this.dataSource = ds;
    }

    public CustomerManagerImpl() {

    }

    @Override
    public void createCustomer(Customer customer) throws DatabaseException {
        if (customer == null) {
            throw new IllegalArgumentException("Customer is null.");
        }
        if (customer.getID() != null) {
            throw new IllegalArgumentException("Customer`s ID is already set.");
        }
        if (customer.getFullName() == null || customer.getAddress() == null ||
                customer.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Customer with wrong parameter(s).");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("INSERT INTO CUSTOMERS (full_name,address,phone_number) VALUES (?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, customer.getFullName());
                statement.setString(2, customer.getAddress());
                statement.setString(3, customer.getPhoneNumber());
                int addedRows = statement.executeUpdate();
                if (addedRows != 1) {
                    throw new DatabaseException("Databse error while updating after inseting new customer.");
                }
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        customer.setID(id);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Error while inserting customer to database.", ex);
        }
    }

    @Override
    public void deleteCustomer(Long ID) throws DatabaseException {
        if (ID == null) {
            throw new IllegalArgumentException("Customer`s ID is null.");
        }
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM CUSTOMERS WHERE id=?")) {
                statement.setLong(1, ID);
                int s = statement.executeUpdate();
                if (s != 1) {
                    throw new DatabaseException("Customer with ID: " + ID + " was not deleted.");
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Error while deleting customer from database.", ex);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws DatabaseException {
        if (customer == null) {
            throw new IllegalArgumentException("Customer is null.");
        }
        if (customer.getID() == null) {
            throw new IllegalArgumentException("Customer`s ID is null.");
        }
        if (customer.getFullName() == null || customer.getAddress() == null ||
                customer.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Customer with wrong parameter(s).");
        }

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE CUSTOMERS SET full_name = ?,address = ?," +
                    " phone_number = ? WHERE id=?")) {
                statement.setString(1, customer.getFullName());
                statement.setString(2, customer.getAddress());
                statement.setString(3, customer.getPhoneNumber());
                statement.setLong(4, customer.getID());
                int s = statement.executeUpdate();
                if (s != 1) {
                    throw new DatabaseException("Customer with ID: " + customer.getID() + " was not updated.");
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Error while updating customer in database.", ex);
        }
    }

    @Override
    public Customer getCustomerByID(Long ID) throws DatabaseException {
        if (ID == null) {
            throw new IllegalArgumentException("Customer with null ID.");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM CUSTOMERS WHERE id = ?")) {
                st.setLong(1, ID);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        Customer resultCustomer = getCustomerFromResultSet(rs);
                        if (rs.next()) {
                            throw new DatabaseException("Error, find more customer with ID: " + ID);
                        }
                        return resultCustomer;
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Selecting specify customer from database failed.", e);
        }
    }


    @Override
    public List<Customer> getAllCustomers() throws DatabaseException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM CUSTOMERS")) {
                try (ResultSet rs = st.executeQuery()) {
                    List<Customer> allCustomers = new ArrayList<>();

                    while (rs.next()) {
                        allCustomers.add(getCustomerFromResultSet(rs));
                    }
                    return allCustomers;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Get all customers failed on database.", e);
        }
    }

    private Customer getCustomerFromResultSet(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();
        customer.setID(resultSet.getLong("id"));
        customer.setFullName(resultSet.getString("full_name"));
        customer.setAddress(resultSet.getString("address"));
        customer.setPhoneNumber(resultSet.getString("phone_number"));
        return customer;
    }

}
