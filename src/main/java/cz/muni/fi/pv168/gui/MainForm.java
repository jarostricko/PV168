package cz.muni.fi.pv168.gui;

import com.toedter.calendar.JSpinnerDateEditor;
import cz.muni.fi.pv168.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jaro on 27.4.2015.
 */
public class MainForm extends JFrame {
    private static MainForm MAIN_FORM;

    private JPanel rootPanel;
    private JTextField carLicencePlateTextField;
    private JTextField carModelTextField;
    private JTextField carRentalPaymentTextField;
    private JButton createCarButton;
    private JTable customerTable;
    private JTable carTable;
    private JCheckBox availableCheckBox;
    private JTable leaseTable;
    private JButton createCustomerButton;
    private JButton updateCarButton;
    private JButton deleteCarButton;
    private JTabbedPane carTabbedPane;
    private JTabbedPane customerTabbedPane;
    private JTabbedPane leaseTabbedPane;
    private JTabbedPane autorentalTabbedPane;
    private JButton createLeaseButton;
    private JTextField customerFullnameTextField;
    private JTextField customerAddressTextField;
    private JTextField customerPhoneNumberTextField;
    private JComboBox<Customer> customerComboBox;
    private JComboBox<Car> carComboBox;
    private JSpinnerDateEditor leaseStartDateSetter;
    private JSpinnerDateEditor leaseEndDateSetter;
    private JButton updateCustomerButton;
    private JButton deleteCustomerButton;
    private JButton updateLeaseButton;
    private JButton deleteLeaseButton;
    private JComboBox<Car> carComboBox1;
    private JTextField licencePlateCarTextField;
    private JTextField modelCarTextField;
    private JTextField rentalPaymentCarTextField;
    private JTextField customerUpdateFullnameTextField;
    private JTextField customerUpdateAddressTextField;
    private JTextField customerUpdatePhonenumberTextField;
    private JComboBox<Customer> customerComboBox1;
    private JComboBox<Lease> leaseComboBox;
    private JSpinnerDateEditor leaseUpdateStartDateEditor;
    private JSpinnerDateEditor leaseUpdateEndDateEditor;
    private JComboBox<Car> leaseUpdateCarComboBox;
    private JComboBox<Customer> leaseUpdateCustomerComboBox;

    BasicDataSource basicDataSource = new BasicDataSource();
    final static Logger log = LoggerFactory.getLogger(MainForm.class);
    CustomerManager customerManager;
    CarManager carManager;
    LeaseManager leaseManager;
    CarsTableModel carDataModel;
    CustomersTableModel customerDataModel;
    LeasesTableModel leaseDataModel;
    java.util.ResourceBundle bundle;

    private void setTitles(java.util.ResourceBundle bundle) {
        setTitle(bundle.getString("main.title"));
        autorentalTabbedPane.setTitleAt(0, bundle.getString("Autorental.dialog_leases_carLabel.text"));
        autorentalTabbedPane.setTitleAt(1, bundle.getString("Autorental.dialog_leases_customerLabel.text"));
        autorentalTabbedPane.setTitleAt(2, bundle.getString("Autorental.dialog_leases_leaseLabel.text"));
        carTabbedPane.setTitleAt(0, bundle.getString("carCreateTab"));
        carTabbedPane.setTitleAt(1, bundle.getString("update"));
        carTabbedPane.setTitleAt(2, bundle.getString("carShowListTab"));
        customerTabbedPane.setTitleAt(0, bundle.getString("customerCreateTab"));
        customerTabbedPane.setTitleAt(1, bundle.getString("update"));
        customerTabbedPane.setTitleAt(2, bundle.getString("customerShowListTab"));
        leaseTabbedPane.setTitleAt(0, bundle.getString("leaseCreateTab"));
        leaseTabbedPane.setTitleAt(1, bundle.getString("update"));
        leaseTabbedPane.setTitleAt(2, bundle.getString("leaseShowListTab"));
        createCarButton.setText(bundle.getString("Autorental.leases_add.text"));
        createCustomerButton.setText(bundle.getString("Autorental.leases_add.text"));
        createLeaseButton.setText(bundle.getString("Autorental.leases_add.text"));
    }

    private void setUp() throws Exception {
        Properties configFile = new Properties();
        InputStream in;
        in = MainForm.class.getResourceAsStream("/myconf.properties");
        configFile.load(in);
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName(configFile.getProperty("jdbc.driver"));
        bds.setUrl(configFile.getProperty("jdbc.url"));
        bds.setPassword(configFile.getProperty("jdbc.password"));
        bds.setUsername(configFile.getProperty("jdbc.user"));
        basicDataSource = bds;

        customerManager = new CustomerManagerImpl(basicDataSource);
        carManager = new CarManagerImpl(basicDataSource);
        leaseManager = new LeaseManagerImpl(basicDataSource);
        carDataModel = new CarsTableModel();
        carTable.setModel(carDataModel);
        customerDataModel = new CustomersTableModel();
        customerTable.setModel(customerDataModel);
        leaseDataModel = new LeasesTableModel();
        leaseTable.setModel(leaseDataModel);
        availableCheckBox.setSelected(true);
        bundle = java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.gui.Bundle");
        carTable.getColumnModel().getColumn(4).setMinWidth(0);
        carTable.getColumnModel().getColumn(4).setMaxWidth(0);
        customerTable.getColumnModel().getColumn(4).setMinWidth(0);
        customerTable.getColumnModel().getColumn(4).setMaxWidth(0);

    }

    private void setUpComboBoxes() {
        List<Car> cars = new ArrayList<>();
        try {
            cars = carManager.getAllCars();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        for (Car car : cars) {
            carComboBox.addItem(car);
            carComboBox1.addItem(car);
            leaseUpdateCarComboBox.addItem(car);
        }

        List<Customer> customers = new ArrayList<>();
        try {
            customers = customerManager.getAllCustomers();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        for (Customer customer : customers) {
            customerComboBox.addItem(customer);
            customerComboBox1.addItem(customer);
            leaseUpdateCustomerComboBox.addItem(customer);

        }

        List<Lease> leases = new ArrayList<>();
        try {
            leases = leaseManager.getAllLeases();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        for (Lease lease : leases) {
            leaseComboBox.addItem(lease);
        }

    }

    private LeasesSwingWorker leasesSwingWorker;

    private class LeasesSwingWorker extends SwingWorker<Void, Lease> {

        @Override
        protected Void doInBackground() throws Exception {
            leaseDataModel = (LeasesTableModel) leaseTable.getModel();
            leaseDataModel.setLeaseManager(leaseManager);
            int counter = 0;
            for (Lease lease : leaseManager.getAllLeases()) {
                counter++;
                Thread.sleep(150);
                publish(lease);
                setProgress(counter);
            }

            return null;
        }

        @Override
        protected void process(List<Lease> items) {
            for (Lease i : items) {
                leaseDataModel.addLease(i);
            }
        }

        @Override
        protected void done() {
            //leases_load.setEnabled(true);
            //rents_progress.setValue(100);
            leasesSwingWorker = null;
        }
    }

    private CustomersSwingWorker customersSwingWorker;

    private class CustomersSwingWorker extends SwingWorker<Void, Customer> {

        @Override
        protected Void doInBackground() throws Exception {
            customerDataModel = (CustomersTableModel) customerTable.getModel();
            customerDataModel.setCustomerManager(customerManager);
            int counter = 0;
            for (Customer customer : customerManager.getAllCustomers()) {
                counter++;
                Thread.sleep(50);
                publish(customer);
                setProgress(counter);
            }
            return null;
        }

        @Override
        protected void process(List<Customer> items) {
            for (Customer i : items) {
                customerDataModel.addCustomer(i);
            }
        }

        @Override
        protected void done() {
            //customers_load.setEnabled(true);
            //customers_progress.setValue(100);
            customersSwingWorker = null;
        }
    }

    private CarsSwingWorker carsSwingWorker;

    private class CarsSwingWorker extends SwingWorker<Void, Car> {

        @Override
        protected Void doInBackground() throws Exception {
            carDataModel = (CarsTableModel) carTable.getModel();
            carDataModel.setCarManager(carManager);
            int counter = 0;
            for (Car car : carManager.getAllCars()) {
                counter++;
                Thread.sleep(100);
                publish(car);
                setProgress(counter);
            }
            return null;
        }

        @Override
        protected void process(List<Car> items) {
            for (Car i : items) {
                carDataModel.addCar(i);
            }
        }

        @Override
        protected void done() {
            //cars_load.setEnabled(true);
            //cars_progress.setValue(100);
            carsSwingWorker = null;
        }
    }

    public MainForm() {
        try {
            setUp();
            setUpComboBoxes();
        } catch (Exception ex) {
            log.error("Application setup failed." + ex);
        }
        setTitles(bundle);
        leasesSwingWorker = new LeasesSwingWorker();
        leasesSwingWorker.execute();

        customersSwingWorker = new CustomersSwingWorker();
        customersSwingWorker.execute();

        carsSwingWorker = new CarsSwingWorker();
        carsSwingWorker.execute();

        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create buttons
        createCarButton.addActionListener(actionEvent -> createCarButtonAction());
        createCustomerButton.addActionListener(actionEvent -> createCustomerButtonAction());
        createLeaseButton.addActionListener(actionEvent -> {
            try {
                createLeaseButtonAction();
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        });

        //Update buttons
        updateCarButton.addActionListener(actionEvent -> updateCarButtonAction());
        updateCustomerButton.addActionListener(actionEvent -> updateCustomerButtonAction());
        updateLeaseButton.addActionListener(actionEvent -> updateLeaseButtonAction());

        //Delete buttons
        deleteCarButton.addActionListener(actionEvent -> {
            try {
                deleteCarButtonAction();
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        });
        deleteCustomerButton.addActionListener(actionEvent -> deleteCustomerButtonAction());
        deleteLeaseButton.addActionListener(actionEvent -> deleteLeaseButtonAction());

        //ComboBoxes
        carComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Car car = (Car) carComboBox1.getSelectedItem();
                licencePlateCarTextField.setText(car.getLicencePlate());
                modelCarTextField.setText(car.getModel());
                rentalPaymentCarTextField.setText(car.getRentalPayment().toString());
            }
        });
        customerComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Customer customer = (Customer) customerComboBox1.getSelectedItem();
                customerUpdateFullnameTextField.setText(customer.getFullName());
                customerUpdateAddressTextField.setText(customer.getAddress());
                customerUpdatePhonenumberTextField.setText(customer.getPhoneNumber());
            }
        });
        leaseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Lease lease = (Lease) leaseComboBox.getSelectedItem();
                leaseUpdateCustomerComboBox.setSelectedItem(lease.getCustomer());
                leaseUpdateCarComboBox.setSelectedItem(lease.getCar());
                leaseUpdateStartDateEditor.setDate(lease.getStartDate());
                leaseUpdateEndDateEditor.setDate(lease.getEndDate());
            }
        });
    }

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {

        System.out.println("Start");
        java.awt.EventQueue.invokeAndWait(() -> {
            MAIN_FORM = new MainForm();
            MAIN_FORM.setVisible(true);
        });
        System.out.println("End");

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private void createCarButtonAction() {
        CarsTableModel model = (CarsTableModel) carTable.getModel();
        Car car = new Car();
        if (carLicencePlateTextField.getText().equals("")) {
            log.debug("Licenceplate is empty");
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("carLicencePlateDialog"));
            throw new IllegalArgumentException("LicencePlate field is empty.");
        }
        if (carModelTextField.getText().equals("")) {
            log.debug("Model is empty");
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("carModelDialog"));
            throw new IllegalArgumentException("Model field is empty.");
        }
        if (!carRentalPaymentTextField.getText().matches("^[0-9.]+$")) {
            log.debug("rental Payment wrong");
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("carRentalPaymentDialog"));
            throw new IllegalArgumentException("Rental payment must be number.");
        }
        car.setStatus(availableCheckBox.isSelected());
        car.setRentalPayment(new BigDecimal(carRentalPaymentTextField.getText()));
        car.setModel(carModelTextField.getText());
        car.setLicencePlate(carLicencePlateTextField.getText());
        try {
            carManager.createCar(car);
            model.addCar(car);
            carComboBox.addItem(car);
            carComboBox1.addItem(car);
            leaseUpdateCarComboBox.addItem(car);
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("carCreatedDialog"));
        } catch (DatabaseException e) {
            log.error("Database exception");
            e.printStackTrace();
        }

        carLicencePlateTextField.setText("");
        carRentalPaymentTextField.setText("");
        carModelTextField.setText("");
    }

    private void createCustomerButtonAction() {
        CustomersTableModel model = (CustomersTableModel) customerTable.getModel();
        Customer customer = new Customer();
        if (customerFullnameTextField.getText().equals("")) {
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("customerFullnameDialog"));
            throw new IllegalArgumentException("Fullname field is empty.");
        }
        if (customerAddressTextField.getText().equals("")) {
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("customerAddressDialog"));
            throw new IllegalArgumentException("Address field is empty.");
        }
        if (customerPhoneNumberTextField.getText().equals("")) {
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("customerPhoneNumberDialog"));
            throw new IllegalArgumentException("Phone number field is empty.");
        }
        customer.setFullName(customerFullnameTextField.getText());
        customer.setAddress(customerAddressTextField.getText());
        customer.setPhoneNumber(customerPhoneNumberTextField.getText());
        customer.setStatus(true);
        try {
            customerManager.createCustomer(customer);
            model.addCustomer(customer);
            customerComboBox.addItem(customer);
            customerComboBox1.addItem(customer);
            leaseUpdateCustomerComboBox.addItem(customer);
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("customerCreatedDialog"));
        } catch (DatabaseException e) {
            log.error("Database exception");
            e.printStackTrace();
        }

        customerFullnameTextField.setText("");
        customerAddressTextField.setText("");
        customerPhoneNumberTextField.setText("");
    }

    private void createLeaseButtonAction() throws DatabaseException {
        LeasesTableModel model = (LeasesTableModel) leaseTable.getModel();
        Lease lease = new Lease();
        lease.setCustomer((Customer) customerComboBox.getSelectedItem());
        lease.setCar((Car) carComboBox.getSelectedItem());
        lease.setStartDate(convertUtilToSql(leaseStartDateSetter.getDate()));
        lease.setEndDate(convertUtilToSql(leaseEndDateSetter.getDate()));
        if (leaseManager.getDateDiff(lease.getStartDate(), lease.getEndDate(), TimeUnit.DAYS) < 0) {
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("leaseWrongDateDialog"));
        } else if (!leaseManager.checkIfCarInThisLeaseIsAvailable(lease)) {
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("leaseWrongStatusDialog"));
        } else {
            lease.setPrice(leaseManager.calculatePriceByDays(lease));
            leaseManager.createLease(lease);
            model.addLease(lease);
            carDataModel.updateCar(lease.getCar());
            leaseComboBox.addItem(lease);
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("leaseCreatedDialog"));
        }

    }

    private void updateCarButtonAction() {
        Car car = (Car) carComboBox1.getSelectedItem();
        car.setLicencePlate(licencePlateCarTextField.getText());
        car.setModel(modelCarTextField.getText());
        car.setRentalPayment(new BigDecimal(rentalPaymentCarTextField.getText()));
        try {
            carManager.updateCar(car);
            carDataModel.updateCar(car);
            leaseDataModel.fireTableDataChanged();
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("updatedCarDialog"));

        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private void updateCustomerButtonAction() {
        Customer customer = (Customer) customerComboBox1.getSelectedItem();
        customer.setFullName(customerUpdateFullnameTextField.getText());
        customer.setAddress(customerUpdateAddressTextField.getText());
        customer.setPhoneNumber(customerUpdatePhonenumberTextField.getText());
        try {
            customerManager.updateCustomer(customer);
            customerDataModel.update(customer);
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("updatedCustomerDialog"));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private void updateLeaseButtonAction() {
        Lease lease = (Lease) leaseComboBox.getSelectedItem();
        lease.setCustomer((Customer) leaseUpdateCustomerComboBox.getSelectedItem());
        lease.setCar((Car) leaseUpdateCarComboBox.getSelectedItem());
        lease.setStartDate(convertUtilToSql(leaseUpdateStartDateEditor.getDate()));
        lease.setEndDate(convertUtilToSql(leaseUpdateEndDateEditor.getDate()));
        try {
            leaseManager.updateLease(lease);
            leaseDataModel.updateLease(lease);
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("updatedLeaseDialog"));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }


    private void deleteCarButtonAction() throws DatabaseException {
        CarsTableModel model = (CarsTableModel) carTable.getModel();
        int row = carTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("selectRowDialog"));
        } else if (!carManager.getCarByID((Long) carTable.getValueAt(row, 0)).getStatus()) {
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("wrongCarStatusDialog"));
        } else {
            if ((Boolean) carTable.getValueAt(row, 4)) {
                Car car = carManager.getCarByID((Long) carTable.getValueAt(row, 0));
                carManager.deleteCar((Long) carTable.getValueAt(row, 0));
                carComboBox.removeItem(car);
                carComboBox1.removeItem(car);
                leaseUpdateCarComboBox.removeItem(car);
                model.removeRow(row);
                JOptionPane.showMessageDialog(MainForm.this, bundle.getString("carDeletedDialog"));
            } else {
                JOptionPane.showMessageDialog(MainForm.this, bundle.getString("carStatusDialog"));
            }
        }


    }

    private void deleteCustomerButtonAction() {
        CustomersTableModel model = (CustomersTableModel) customerTable.getModel();
        int row = customerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("selectRowDialog"));
        } else {
            try {
                Customer customer = customerManager.getCustomerByID((Long) customerTable.getValueAt(row, 0));
                if (leaseManager.checkIfCustomerIsWithoutLeases(customer) && customer.getStatus()) {
                    customerManager.deleteCustomer((Long) customerTable.getValueAt(row, 0));
                    customerComboBox.removeItem(customer);
                    customerComboBox1.removeItem(customer);
                    leaseUpdateCustomerComboBox.removeItem(customer);
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(MainForm.this, bundle.getString("customerDeletedDialog"));

                } else {
                    JOptionPane.showMessageDialog(MainForm.this, bundle.getString("customerStatusDialog"));
                }
            } catch (DatabaseException e) {
                log.error("Database exception");
                e.printStackTrace();
            }
        }


    }

    private void deleteLeaseButtonAction() {
        LeasesTableModel model = (LeasesTableModel) leaseTable.getModel();
        int row = leaseTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(MainForm.this, bundle.getString("selectRowDialog"));
        } else {
            try {
                Customer customer = leaseManager.getLeaseByID((Long) leaseTable.getValueAt(row, 0)).getCustomer();
                leaseManager.deleteLease((Long) leaseTable.getValueAt(row, 0));
                model.removeRow(row);
                customerDataModel.update(customerManager.getCustomerByID(customer.getID()));
                JOptionPane.showMessageDialog(MainForm.this, bundle.getString("leaseDeletedDialog"));
            } catch (DatabaseException e) {
                log.error("Database exception");
                e.printStackTrace();
            }
        }

    }
    private static java.sql.Date convertUtilToSql(java.util.Date uDate) {
        return new java.sql.Date(uDate.getTime());
    }

}
