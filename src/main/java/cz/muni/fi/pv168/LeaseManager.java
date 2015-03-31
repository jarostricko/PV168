package cz.muni.fi.pv168;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jaro on 10.3.2015.
 */
public interface LeaseManager {

    public void createLease(Lease lease) throws DatabaseException;
    public void updateLease(Lease lease) throws DatabaseException;
    public void deleteLease(Long ID) throws DatabaseException;
    public Lease getLeaseByID(Long ID) throws DatabaseException;
    public List<Lease> getAllLeases() throws DatabaseException;
    public List<Lease> getLeasesForCustomer(Customer customer) throws DatabaseException;
    public List<Lease> getLeasesForCar(Car car) throws DatabaseException;

    public long getDateDiff(Date date1, Date date2, TimeUnit timeUnit);
    public BigDecimal calculatePriceByDays(Lease lease) throws DatabaseException;
}
