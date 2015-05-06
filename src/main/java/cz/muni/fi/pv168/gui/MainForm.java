package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;

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

    BasicDataSource basicDataSource = new BasicDataSource();
    final static Logger log = LoggerFactory.getLogger(MainForm.class);
    private String action;
    CustomerManager customerManager;
    CarManager carManager;
    LeaseManager leaseManager;
    TableModel carDataModel;
    TableModel customerDataModel;
    TableModel leaseDataModel;


    private void setUp() throws Exception {
        Properties configFile = new Properties();
        InputStream in;
        in = MainForm.class.getResourceAsStream("/myconf.properties");
        configFile.load(in);
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl(configFile.getProperty("jdbc.url"));
        bds.setPassword(configFile.getProperty("jdbc.password"));
        bds.setUsername(configFile.getProperty("jdbc.user"));
        basicDataSource = bds;
    }

    public MainForm() {
        super("Car-Rental-Service");
        try {
            setUp();
        } catch (Exception ex) {
            log.error("Application setup failed." + ex);
        }
        customerManager = new CustomerManagerImpl(basicDataSource);
        carManager = new CarManagerImpl(basicDataSource);
        leaseManager = new LeaseManagerImpl(basicDataSource);

        carDataModel = new CarsTableModel();
        carTable.setModel(carDataModel);

        customerDataModel = new CustomersTableModel();
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
                try {
                    carManager.createCar(new Car(carLicencePlateTextField.getText(), carModelTextField.getText(), new BigDecimal(carRentalPaymentTextField.getText()), availableCheckBox.isSelected()));
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
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
