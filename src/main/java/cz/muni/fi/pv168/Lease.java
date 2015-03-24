package cz.muni.fi.pv168;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Jaro on 10.3.2015.
 */
public class Lease {
    private Long ID;
    private Customer customer;
    private Car car;
    private BigDecimal price;
    private Date startDate, endDate;

    public Lease(Customer customer, Car car, BigDecimal price, Date startDate, Date endDate) {
        this.customer = customer;
        this.car = car;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public Lease() {

    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Lease{" +
                "ID=" + ID +
                ", customer=" + customer +
                ", car=" + car +
                ", price=" + price +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lease)) return false;

        Lease lease = (Lease) o;

        if (ID != null ? !ID.equals(lease.ID) : lease.ID != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ID != null ? ID.hashCode() : 0;
    }
}
