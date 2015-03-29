package cz.muni.fi.pv168;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


/**
 * Created by Jaro on 4.3.2015.
 */
public class CarManagerImpl implements CarManager {

    private DataSource dataSource;

    public CarManagerImpl(DataSource ds) {
        this.dataSource = ds;
    }

    public CarManagerImpl() {
    }

    @Override
    public void createCar(Car car) throws DatabaseException {
        if (car == null) {
            throw new IllegalArgumentException("Car is null.");
        }
        if (car.getID() != null) {
            throw new IllegalArgumentException("Cars ID is already set.");
        }
        if (car.getLicencePlate() == null || car.getModel() == null ||
                car.getRentalPayment() == null) {
            throw new IllegalArgumentException("Car with wrong parameter(s).");
        }
        if (car.getRentalPayment().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cars rental payment is lower then 0.");
        }


        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("INSERT INTO CARS (licence_plate,model,rental_payment,status) VALUES (?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, car.getLicencePlate());
                statement.setString(2, car.getModel());
                statement.setBigDecimal(3, car.getRentalPayment());
                statement.setBoolean(4, car.getStatus());
                int addedRows = statement.executeUpdate();
                if (addedRows != 1) {
                    throw new DatabaseException("Database error while updating after inserting new car.");
                }
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        car.setID(id);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Error while inserting car to database.", ex);
        }

    }

    @Override
    public void updateCar(Car car) throws DatabaseException {
        if (car == null) {
            throw new IllegalArgumentException("Car is null.");
        }
        if (car.getID() == null) {
            throw new IllegalArgumentException("Cars ID is null.");
        }
        if (car.getLicencePlate() == null || car.getModel() == null ||
                car.getRentalPayment() == null) {
            throw new IllegalArgumentException("Car with wrong parameter(s).");
        }

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE CARS SET licence_plate = ?,model = ?," +
                    " rental_payment = ?,status = ? WHERE id=?")) {
                statement.setString(1, car.getLicencePlate());
                statement.setString(2, car.getModel());
                statement.setBigDecimal(3, car.getRentalPayment());
                statement.setBoolean(4, car.getStatus());
                statement.setLong(5, car.getID());
                int s = statement.executeUpdate();
                if (s != 1) {
                    throw new DatabaseException("Car with ID: " + car.getID() + " was not updated.");
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Error while updating car in database.", ex);
        }
    }

    @Override
    public void deleteCar(Long ID) throws DatabaseException {
        if (ID == null) {
            throw new IllegalArgumentException("Cars ID is null.");
        }
        if (!getCarByID(ID).getStatus()) {
            throw new IllegalArgumentException("Cant delete rented car.");
        }
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM CARS WHERE id=?")) {
                statement.setLong(1, ID);
                int s = statement.executeUpdate();
                if (s != 1) {
                    throw new DatabaseException("Car with ID: " + ID + " was not deleted.");
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Error while deleting car from database.", ex);
        }
    }

    @Override
    public Car getCarByID(Long ID) throws DatabaseException {
        if (ID == null) {
            throw new IllegalArgumentException("Car with null ID.");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM CARS WHERE id = ?")) {
                st.setLong(1, ID);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        Car resultCar = getCarFromResultSet(rs);
                        if (rs.next()) {
                            throw new DatabaseException("Error, find more car with ID: " + ID);
                        }
                        return resultCar;
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Selecting specify car from database failed.", e);
        }
    }

    @Override
    public List<Car> getAllCars() throws DatabaseException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM CARS")) {
                try (ResultSet rs = st.executeQuery()) {
                    List<Car> allCars = new ArrayList<>();

                    while (rs.next()) {
                        allCars.add(getCarFromResultSet(rs));
                    }
                    return allCars;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Get all cars failed on database.", e);
        }
    }


    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setID(resultSet.getLong("id"));
        car.setLicencePlate(resultSet.getString("licence_plate"));
        car.setModel(resultSet.getString("model"));
        car.setRentalPayment(resultSet.getBigDecimal("rental_payment"));
        car.setStatus(resultSet.getBoolean("status"));
        return car;
    }
}

