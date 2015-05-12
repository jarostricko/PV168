package cz.muni.fi.pv168;

/**
 * Created by Jaro on 10.3.2015.
 */
public class Customer {
    private Long ID;
    private String fullName;
    private String address;
    private String phoneNumber;
    private boolean status;


    public Customer(String fullName, String address, String phoneNumber) {
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.status = true;
    }

    public Customer(){

    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;

        Customer customer = (Customer) o;

        if (ID != null ? !ID.equals(customer.ID) : customer.ID != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ID != null ? ID.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ID: " + ID +
                ", " + fullName + ", " + address +
                ", " + phoneNumber;
    }
}
