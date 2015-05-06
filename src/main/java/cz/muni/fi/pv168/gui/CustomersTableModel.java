package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.Customer;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaro on 29.4.2015.
 */
public class CustomersTableModel extends AbstractTableModel {
    private List<Customer> customers = new ArrayList<Customer>();

    @Override
    public int getRowCount() {
        return customers.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
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
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "ID";
            case 1:
                return "Full name";
            case 2:
                return "Address";
            case 3:
                return "Phone number";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addCar(Customer customer) {
        customers.add(customer);
        int lastRow = customers.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }
}
