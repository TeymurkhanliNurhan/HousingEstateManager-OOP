package GUI_P1;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Person {

    private Map<Integer, Integer> rentedApartmentDurations;
    private static final int MAX_PARKING_PLACES = 5;
    private static final int MAX_APARTMENTS =5 ;
    private static int nextPersonId = 1;

    private int id;
    private String name;
    private String surname;
    private String IDNumber;
    private String address;
    private List<Apartment> rentedApartments;
    private List<ParkingPlace> rentedParkingPlaces;
    private List<TenantLetter> tenantLetters;
    private List<Integer> rentedApartmentIds;
    private List<Integer> rentedParkingPlaceIds;


    public Person(String name, String surname, String IDNumber, String address) {
        this.id = nextPersonId++;
        this.name = name;
        this.surname = surname;
        this.IDNumber = IDNumber;
        this.address = address;
        this.rentedApartments = new ArrayList<>();
        this.rentedParkingPlaces = new ArrayList<>();
        this.tenantLetters = new ArrayList<>();
        rentedApartments = new ArrayList<>();
        rentedParkingPlaces = new ArrayList<>();
        rentedApartmentIds = new ArrayList<>();
        rentedParkingPlaceIds = new ArrayList<>();
        rentedApartmentDurations = new HashMap<>();
    }

    public boolean isSamePerson(String id) {
        return this.IDNumber.equals(id);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getIDNumber() {
        return IDNumber;
    }

    public String getAddress() {
        return address;
    }

    public List<Apartment> getRentedApartments() {
        return rentedApartments;
    }

    public void addApartment(Apartment apartment) {
        rentedApartments.add(apartment);
    }

    public void removeApartment(Apartment apartment) {
        rentedApartments.remove(apartment);
    }

    public void addParkingPlace(ParkingPlace parkingPlace) { rentedParkingPlaces.add(parkingPlace);
    }

    public void removeParkingPlace(ParkingPlace parkingPlace) { rentedParkingPlaces.remove(parkingPlace);
    }


    public List<ParkingPlace> getRentedParkingPlaces() {
        return rentedParkingPlaces;
    }

    public List<TenantLetter> getTenantLetters() {
        return tenantLetters;
    }

    public boolean rentApartment(Apartment apartment, int durationInDays) {
        // Kiralanan dairelerin ID'lerini depolama
        // Şu anki kiralanma süresini kontrol etmek için mevcut kiralama süresini depolama
        if (rentedApartments.size() < MAX_APARTMENTS && !isAlreadyRenting(apartment)) {
            rentedApartments.add(apartment);
            rentedApartmentIds.add(apartment.getId());
            rentedApartmentDurations.put(apartment.getId(), durationInDays); // Kiralama süresini ilişkilendirme
            return true;
        } else {
            if (isAlreadyRenting(apartment)) {
                System.out.println("You've already rented this apartment.");
            } else {
                System.out.println("You've reached the maximum limit for renting apartments.");
            }
            return false;
        }
    }

    // Belirli bir dairenin zaten kiralanıp kiralanmadığını kontrol etmek için yardımcı bir metod
    private boolean isAlreadyRenting(Apartment apartment) {
        return rentedApartmentIds.contains(apartment.getId());
    }


    public boolean rentParkingPlace(ParkingPlace parkingPlace) {
        // Kiralanan otopark yerlerinin ID'lerini depolama
        if (rentedParkingPlaces.size() < MAX_PARKING_PLACES) {
            rentedParkingPlaces.add(parkingPlace);
            rentedParkingPlaceIds.add(parkingPlace.getId());
            return true;
        } else {
            System.out.println("You've reached the maximum limit for renting parking places.");
            return false;
        }
    }

    public void addTenantLetter(TenantLetter letter) {
        tenantLetters.add(letter);
    }

    // Other methods and logic related to persons
}
