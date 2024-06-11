package GUI_P1;

import java.util.ArrayList;
import java.util.List;

public class ParkingPlace {
    private static int nextParkingPlaceId = 1;

    private int id;
    private double volume;
    private double freeVolume;
    private boolean rented;
    private Person tenant;
    private List<Item> storedItems;
    private List<Ivehicle> storedVehicles;


    public ParkingPlace(double volume) {
        this.id = nextParkingPlaceId++;
        this.volume = volume;
        this.rented = false;
        this.tenant = null;
        this.storedItems = new ArrayList<>();
        this.storedVehicles = new ArrayList<>();
        this.freeVolume=volume;
    }

    public boolean isSameParkingPlace(int id) { return (this.id==id);}
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
    public double getFreeVolume(){return freeVolume;}
    public void setFreeVolume(double v) {this.freeVolume=v;}

    public List<Item> getStoredItems() {
        return storedItems;
    }

    public List<Ivehicle> getStoredVehicles() {
        return storedVehicles;
    }

    public void rentTo(Person person) {
        if (!rented) {
            rented = true;
            tenant = person;
            System.out.println("ParkingPlace rented to " + person.getName() + " " + person.getSurname());
        } else {
            System.out.println("ParkingPlace is already rented.");
        }
    }

    public void checkOut() {
        if (rented) {
            rented = false;
            tenant = null;
            storedItems.clear();
            storedVehicles.clear();
            System.out.println("Parking place has been checked out.");
        } else {
            System.out.println("Parking place is not currently rented.");
        }
    }

    public void parkItem(Item item) {
        if (!rented) {
            System.out.println("Cannot park item, parking place is not rented.");
            return;
        }

        if (item.getVolume() <= volume) {
            storedItems.add(item);
            System.out.println(item.getName() + " has been parked in the parking place.");
        } else {
            System.out.println("The item is too large for the parking place.");
        }
    }

    public void parkVehicle(Ivehicle vehicle) {
        if (!rented) {
            System.out.println("Cannot park vehicle, parking place is not rented.");
            return;
        }

        if (vehicle.getVolume() <= volume) {
            storedVehicles.add(vehicle);
            System.out.println(vehicle.getName() + " has been parked in the parking place.");
            volume-=vehicle.getVolume();
        } else {
            System.out.println("The vehicle is too large for the parking place.");
        }
    }

    /*public void displayParkingPlaceInfo() {
        System.out.println("Parking Place ID: " + id);
        System.out.println("Area: " + volume);
        if (rented) {
            System.out.println("Occupied by: " + tenant.getName() + " " + tenant.getSurname());
        } else {
            System.out.println("Status: Available");
        }
    }
*/
    public void checkInForParkingPlace(Person person) {
        if (!rented && tenant == null) {
            tenant = person;
            rented = true;
            tenant.addParkingPlace(this);
            System.out.println(person.getName() + " checked into the parking place.");
        } else {
            System.out.println("Invalid check-in. Parking place is already rented.");
        }
    }

    public void checkOutForParkingPlace() {
        if (rented) {
            rented = false;
            tenant.removeParkingPlace(this);
            tenant = null;
        } else {
            System.out.println("Parking place is not currently rented.");
        }
        // Diğer kontrol veya işlemler
    }


    public void checkOutVehicle(Ivehicle vehicle) {
        if (storedVehicles.contains(vehicle)) {
            //  Remove vehicle from the garage
            storedVehicles.remove(vehicle);
            // Update the free volume in the garage
            freeVolume += vehicle.getVolume();
            System.out.println("Vehicle " + vehicle.getId() + " checked out from the parking place.");
        } else {
            System.out.println("Vehicle " + vehicle.getId() + " not found in the parking place.");
        }
    }

    public void checkInVehicle(Vehicle foundVehicle) {
        // Check if the vehicle is not already stored in the garage
        if (!storedVehicles.contains(foundVehicle)) {
            // Check if there is enough free volume in the garage
            if (freeVolume >= foundVehicle.getVolume()) {
                // Add the vehicle to the garage
                storedVehicles.add((Ivehicle) foundVehicle);
                // Update the free volume in the garage
                freeVolume -= foundVehicle.getVolume();
                System.out.println("Vehicle " + foundVehicle.getId() + " checked in to the parking place.");
            } else {
                System.out.println("Not enough free volume in the parking place.");
            }
        } else {
            System.out.println("Vehicle " + foundVehicle.getId() + " is already stored in the parking place.");
        }
    }

}
