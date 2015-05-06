package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

/**
 * Created by Jaro on 29.4.2015.
 */
public class LeasesTableModel extends AbstractTableModel {
    final static Logger log = LoggerFactory.getLogger(LeasesTableModel.class.getName());
    private LeaseManager leaseManager;
    private CarManager carManager;
    private CustomerManager customerManager;
    private List<Lease> leases = new ArrayList<Lease>();

    private static enum COLUMNS {
        ID, CAR, CUSTOMER, PRICE, STARTDATE, ENDDATE
    }

    public void setLeaseManager(LeaseManager leaseManager) {
        this.leaseManager = leaseManager;
    }

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    public void setCarManager(CarManager carManager) {
        this.carManager = carManager;
    }

    @Override
    public int getRowCount() {
        return leases.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.values().length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (COLUMNS.values()[columnIndex]) {
            case ID:
                return Long.class;
            case CAR://? id ??
                return Car.class;
            case CUSTOMER://?
                return Customer.class;
            case PRICE:
                return BigDecimal.class;
            case STARTDATE:
            case ENDDATE:
                return Date.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Lease lease = leases.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return lease.getID();
            case 1:
                return lease.getCar();
            case 2:
                return lease.getCustomer();
            case 3:
                return lease.getPrice();
            case 4:
                return lease.getStartDate();
            case 5:
                return lease.getEndDate();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle").getString("leases_table_id");
            case 1:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle").getString("leases_table_car");
            case 2:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle").getString("leases_table_customer");
            case 3:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle").getString("leases_table_price");
            case 4:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle").getString("leases_table_start");
            case 5:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle").getString("leases_table_end");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Lease lease = leases.get(rowIndex);
        switch (columnIndex) {
            case 0:
                lease.setID((Long) aValue);
                break;
            case 1:
                lease.setCar((Car) aValue);
                break;
            case 2:
                lease.setCustomer((Customer) aValue);
                break;
            case 3:
                lease.setPrice(new BigDecimal((String) aValue));
                break;
            case 4:
                lease.setStartDate(Date.valueOf((String) aValue));
            case 5:
                lease.setEndDate(Date.valueOf((String) aValue));
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        try {
            leaseManager.updateLease(lease);
            fireTableDataChanged();
        } catch (Exception ex) {
            log.info("User request failed, exception: " + ex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (COLUMNS.values()[columnIndex]) {
            case ID:
                return false;
            case CAR:
            case CUSTOMER:
            case PRICE:
            case ENDDATE:
            case STARTDATE:
                return true;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addLease(Lease lease) {
        leases.add(lease);
        int lastRow = leases.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void removeLease(Lease lease) {
        leases.remove(lease);
        fireTableDataChanged();
    }

    public void clear() {
        leases.clear();
        fireTableDataChanged();
    }

    public List<Lease> getAllCustomers() {
        return leases;
    }
}
