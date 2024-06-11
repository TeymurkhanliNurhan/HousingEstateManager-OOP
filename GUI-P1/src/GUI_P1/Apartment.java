package GUI_P1;

import java.time.LocalDate;

public class Apartment {
    private static int nextApartmentId = 1;

    private LocalDate rentDate;

    public LocalDate getRentDate() {
        return rentDate;
    }

    private int id;
    private double volume;
    private boolean rented;
    private Person tenant;

    private int durationInDays;

    public void setDurationInDays(int duration) {
        this.durationInDays = duration;
    }
    public int getDurationInDays() {
        return durationInDays;
    }
    public Apartment(double volume) {
        this.id = nextApartmentId++;
        this.volume = volume;
        this.rented = false;
        this.tenant = null;
    }
    public Apartment(int ID) {
        this.id = nextApartmentId++;
        this.volume = volume;
        this.rented = false;
        this.tenant = null;
    }
    public Apartment(double length, double width, double height) {
        this.id = nextApartmentId++;
        this.volume = length * width * height;
        this.rented = false;
        this.tenant = null;
    }

    public int getId() {
        return id;
    }

    public double getVolume() {
        return volume;
    }

    public boolean isRented() {
        return rented;
    }

    public Person getTenant() {
        return tenant;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public void setTenant(Person tenant) {
        this.tenant = tenant;
    }


    public void displayApartmentInfo() {
        System.out.println("Apartment ID: " + id);
        System.out.println("Volume: " + volume);
        if (rented) {
            System.out.println("Tenant: " + tenant.getName());
        } else {
            System.out.println("Status: Available");
        }
    }
    public void checkOut() {
        if (rented) {
            rented = false;
            tenant.removeApartment(this);
            tenant = null;
        } else {
            System.out.println("Apartment is not currently rented.");
        }
        // Diğer kontrol veya işlemler
    }

    public void checkIn(Person person) {
        if (!rented && tenant == null) {
            tenant = person;
            rented = true;
            tenant.addApartment(this);
           // System.out.println(person.getName() + " checked into the apartment.");
        } else {
            System.out.println("Invalid check-in. Apartment is already rented.");
        }
    }

    // Additional methods for rent calculation, expiration checks, etc.
}
