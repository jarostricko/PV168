package cz.muni.fi.pv168.gui;

import com.sun.jnlp.ApiDialog;
import cz.muni.fi.pv168.Car;
import cz.muni.fi.pv168.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Jaro on 13.5.2015.
 */
public class EditDialog extends JDialog implements ActionListener {
    JTextField licencePlate = null;
    JTextField model = null;
    JTextField price = null;
    JButton updateButton = null;

    private Car car;
    private Customer customer;
    private ApiDialog.DialogResult dialogResult;

    public EditDialog(Frame parent, boolean modal, CustomersTableModel customersTableModel) {

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }
}
