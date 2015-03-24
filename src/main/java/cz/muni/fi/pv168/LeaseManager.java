package cz.muni.fi.pv168;

import java.util.List;

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
}
