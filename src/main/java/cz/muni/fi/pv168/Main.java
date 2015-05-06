package cz.muni.fi.pv168;

/*
TODO LIST:




 */

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class Main {

    public static void main(String[] args) throws IOException, DatabaseException {
        Properties p = new Properties();
        InputStream in;
        in = Main.class.getResourceAsStream("/myconf.properties");
        p.load(in);

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby://localhost:1527/MojeDB");
        System.err.println("jdbc:derby://localhost:1527/MojeDB");
        ds.setUsername(p.getProperty("jdbc.user"));
        System.err.println(p.getProperty("jdbc.user"));

        ds.setPassword(p.getProperty("jdbc.password"));
        System.err.println(p.getProperty("jdbc.password"));

        CarManager carManager = new CarManagerImpl(ds);
        carManager.createCar(new Car("ke-200bu", "oktavja", new BigDecimal(125.0), true));
        List<Car> allCars = new ArrayList<>();
        allCars = carManager.getAllCars();
        allCars.forEach(System.out::println);

        LeaseManager leaseManager = new LeaseManagerImpl(ds);
        Customer customer = new Customer(3L, "Milan Bandurka", "Koksov Baksa, 04058, Slovakia", "+421 965 214 658");
        Car car = new Car("ke-200bu", "oktavja", new BigDecimal(100.0), true);
        car.setID(2L);
        //Date date = new Date(2014,2,12);
        Lease lease = new Lease();
        lease.setID(1l);
        lease.setCar(car);
        lease.setCustomer(customer);
        lease.setStartDate(Date.valueOf("2012-03-20"));
        lease.setEndDate(new Date(112, 2, 25));
        lease.setPrice(leaseManager.calculatePriceByDays(lease));
        System.out.println(lease);
        System.out.println(car);
        System.out.println(customer);
        System.out.println();

        Date date1 = Date.valueOf("2012-03-20");
        Date date2 = Date.valueOf("2012-03-25");
        long diff = leaseManager.getDateDiff(date1, date2, TimeUnit.DAYS);
        System.out.println("Num of dayz between " + date1 + " and " + date2 + " : " + diff);
        //List<Car> allCars = carManager.getAllCars();
        //allCars.forEach(System.out::println);
        if (diff < 0) {
            System.out.println("je mensi ako 0");
        }

        /*
        String mysetting1 = p.getProperty("mysetting1");
        printProperties(p);
        System.out.println("mysetting =" + p);

        Properties sysprops = System.getProperties();
        //printProperties(sysprops);

        Locale loc = Locale.getDefault();
        System.out.println("local " + loc);
        DateFormat full = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL);
        */


    }

    private static void printProperties(Properties p) {
        for(String key : p.stringPropertyNames()) {
            String property = p.getProperty(key);
            System.out.println(key + "=" + property);
        }
    }


}
