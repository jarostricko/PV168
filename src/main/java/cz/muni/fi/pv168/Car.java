package cz.muni.fi.pv168;

import java.math.BigDecimal;

/**
 * Created by Jaro on 4.3.2015.
 */
public class Car {
    private Long ID;
    private String licencePlate;
    private String model;
    private BigDecimal rentalPayment;
    private boolean status; //true means available, false rented

    public Car(String licencePlate, String model, BigDecimal rentalPayment, boolean status) {
        this.licencePlate = licencePlate;
        this.model = model;
        this.rentalPayment = rentalPayment;
        this.status = status;
    }

    public Car(){

    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getRentalPayment() {
        return rentalPayment;
    }

    public void setRentalPayment(BigDecimal rentalPayment) {
        this.rentalPayment = rentalPayment;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Car{" +
                "ID=" + ID +
                ", licencePlate='" + licencePlate + '\'' +
                ", model='" + model + '\'' +
                ", rental payment= '" + rentalPayment + '\''+
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;

        Car car = (Car) o;

        if (ID != car.ID) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (ID ^ (ID >>> 32));
    }
}
