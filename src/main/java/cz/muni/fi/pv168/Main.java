package cz.muni.fi.pv168;


import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


public class Main {

    public static void main(String[] args) throws IOException, DatabaseException {
        Properties p = new Properties();
        InputStream in;
        in = Main.class.getResourceAsStream("/myconf.properties");
        p.load(in);

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(p.getProperty("jdbc.url"));
        ds.setUsername(p.getProperty("jdbc.user"));
        ds.setPassword(p.getProperty("jdbc.password"));

        CarManager carManager = new CarManagerImpl(ds);
        carManager.createCar(new Car("ke-200bu","oktavja",new BigDecimal(125.0),true));



        //List<Car> allCars = carManager.getAllCars();
        //allCars.forEach(System.out::println);


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
