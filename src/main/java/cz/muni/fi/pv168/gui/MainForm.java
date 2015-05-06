package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.Car;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

/**
 * Created by Jaro on 27.4.2015.
 */
public class MainForm extends JFrame {
    private JButton pushMEButton;
    private JPanel rootPanel;
    private JTextField carLicencePlateTextField;
    private JTextField carModelTextField;
    private JTextField carRentalPaymentTextField;
    private JButton createCarButton;
    private JTable customerTable;
    private JTable carTable;
    private JScrollPane carScrollPanel;
    private JCheckBox availableCheckBox;
    private JScrollPane customerScrollPanel;
    private JTable leaseTable;
    private JScrollPane leaseScrollPalen;
    private JButton createCustomerButton;
    private boolean status;


    public MainForm() {
        super("Car-Rental-Service");

        TableModel carDataModel = new CarsTableModel();
        carTable.setModel(carDataModel);

        TableModel customerDataModel = new CustomersTableModel();
        customerTable.setModel(customerDataModel);

        availableCheckBox.setSelected(true);

        CarsTableModel model = (CarsTableModel) carTable.getModel();
        model.addCar(new Car("2B6 7895", "Renault Clio", new BigDecimal(444.5), true));
        model.addCar(new Car("2A1 9999", "Škoda 120", new BigDecimal(444.5), true));


        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        createCarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CarsTableModel model = (CarsTableModel) carTable.getModel();
                model.addCar(new Car(carLicencePlateTextField.getText(), carModelTextField.getText(), new BigDecimal(carRentalPaymentTextField.getText()), availableCheckBox.isSelected()));
            }
        });
        createCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });


        pushMEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showConfirmDialog(MainForm.this, "Clicked");
            }
        });


    }

    public static void main(String[] args) {
        MainForm mainForm = new MainForm();

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
