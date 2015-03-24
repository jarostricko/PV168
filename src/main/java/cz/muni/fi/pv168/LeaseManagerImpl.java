package cz.muni.fi.pv168;

import java.util.List;

/**
 * Created by Jaro on 10.3.2015.
 */
public class LeaseManagerImpl implements LeaseManager {
    @Override
    public void createLease(Lease lease) throws DatabaseException{

    }

    @Override
    public void updateLease(Lease lease) throws DatabaseException{

    }

    @Override
    public void deleteLease(Long ID) throws DatabaseException{

    }

    @Override
    public Lease getLeaseByID(Long ID)throws DatabaseException {
        return null;
    }

    @Override
    public List<Lease> getAllLeases()throws DatabaseException {
        return null;
    }

    @Override
    public List<Lease> getLeasesForCustomer(Customer customer)throws DatabaseException {
        return null;
    }

    @Override
    public List<Lease> getLeasesForCar(Car car)throws DatabaseException {
        return null;
    }
}
