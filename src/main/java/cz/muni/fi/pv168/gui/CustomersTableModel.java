package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.Customer;
import cz.muni.fi.pv168.CustomerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaro on 29.4.2015.
 */
public class CustomersTableModel extends AbstractTableModel {
    final static Logger log = LoggerFactory.getLogger(CustomersTableModel.class.getName());
    private CustomerManager customerManager;
    private List<Customer> customers = new ArrayList<>();

    private static enum COLUMNS {
        ID, FULLNAME, ADDRESS, PHONENUMBER, STATUS
    }

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }
    @Override
    public int getRowCount() {
        return customers.size();
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
            case FULLNAME:
            case ADDRESS:
            case PHONENUMBER:
                return String.class;
            case STATUS:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Customer customer = customers.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return customer.getID();
            case 1:
                return customer.getFullName();
            case 2:
                return customer.getAddress();
            case 3:
                return customer.getPhoneNumber();
            case 4:
                return customer.getStatus();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle").getString("customers_table_id");
            case 1:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle").getString("customers_table_fullname");
            case 2:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle").getString("customers_table_address");
            case 3:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle").getString("customers_table_phoneNumber");
            case 4:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle").getString("customers_table_status");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Customer customer = customers.get(rowIndex);
        switch (columnIndex) {
            case 0:
                customer.setID((Long) aValue);
                break;
            case 1:
                customer.setFullName((String) aValue);
                break;
            case 2:
                customer.setAddress((String) aValue);
                break;
            case 3:
                customer.setPhoneNumber((String) aValue);
                break;
            case 4:
                customer.setStatus((Boolean) aValue);
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        try {
            customerManager.updateCustomer(customer);
            fireTableDataChanged();
        } catch (Exception ex) {
            log.info("User request failed, exception: " + ex);
        }
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        int lastRow = customers.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void removeCustomer(Customer customer) {
        customers.remove(customer);
        fireTableDataChanged();
    }

    public void removeRow(int row) {
        customers.remove(row);
        fireTableDataChanged();
    }

    public void clear() {
        customers.clear();
        fireTableDataChanged();
    }

    public void update(Customer customer) {
        customers.remove(customer);
        customers.add(customer);
        fireTableDataChanged();

    }

    public List<Customer> getAllCustomers() {
        return customers;
    }
}
