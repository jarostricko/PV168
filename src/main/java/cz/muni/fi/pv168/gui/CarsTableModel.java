package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.Car;
import cz.muni.fi.pv168.CarManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaro on 29.4.2015.
 */
public class CarsTableModel extends AbstractTableModel {
    final static Logger log = LoggerFactory.getLogger(CarsTableModel.class.getName());
    private CarManager carManager;
    private List<Car> cars = new ArrayList<>();

    private static enum COLUMNS {
        ID, PLATE, MODEL, RENTALPAYMENT, STATUS
    }

    public void setCarManager(CarManager carManager) {
        this.carManager = carManager;
    }

    @Override
    public int getRowCount() {
        return cars.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.values().length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
            case 2:
                return String.class;
            case 3:
                return BigDecimal.class;
            case 4:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Car car = cars.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return car.getID();
            case 1:
                return car.getLicencePlate();
            case 2:
                return car.getModel();
            case 3:
                return car.getRentalPayment();
            case 4:
                return car.getStatus();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Car car = cars.get(rowIndex);
        switch (COLUMNS.values()[columnIndex]) {
            case MODEL:
                car.setModel((String) aValue);
                break;
            case PLATE:
                car.setLicencePlate((String) aValue);
                break;
            case RENTALPAYMENT:
                car.setRentalPayment((BigDecimal) aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        try {
            carManager.updateCar(car);
            fireTableDataChanged();
        } catch (Exception ex) {
            log.info("User request failed, exception: " + ex);
        }
    }


    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui/Bundle").getString("cars_table_id");
            case 1:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui/Bundle").getString("cars_table_licencePlate");
            case 2:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui/Bundle").getString("cars_table_model");
            case 3:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui/Bundle").getString("cars_table_rentalPayment");
            case 4:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui/Bundle").getString("cars_table_status");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addCar(Car car) {
        cars.add(car);
        int lastRow = cars.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void removeCar(Car car) {
        cars.remove(car);
        fireTableDataChanged();
    }

    public void removeRow(int row) {
        cars.remove(row);
        fireTableDataChanged();
    }

    public void updateCar(Car car) {
        cars.remove(car);
        cars.add(car);
        fireTableDataChanged();
    }

    public void clear() {
        cars.clear();
        fireTableDataChanged();
    }

}
