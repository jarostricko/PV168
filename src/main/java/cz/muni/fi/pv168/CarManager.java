package cz.muni.fi.pv168;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by Jaro on 4.3.2015.
 */
public interface CarManager {

    public void createCar(Car car) throws DatabaseException;

    public void updateCar(Car car) throws DatabaseException;

    public void deleteCar(Long ID) throws DatabaseException;

    public Car getCarByID(Long ID) throws DatabaseException;

    public List<Car> getAllCars() throws DatabaseException;

    public void setDataSource(DataSource ds);
}
