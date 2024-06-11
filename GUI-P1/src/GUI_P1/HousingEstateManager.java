package GUI_P1;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDate;
import java.util.Date;

public class HousingEstateManager {
    private static TenantLetter letter;
    private static int parkingPlaceId;

    private static void clearFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false))) {
            // Boş bir dosya yaratılır
        } catch (IOException e) {
            System.out.println("Error clearing the file: " + e.getMessage());
        }
    }

    private static List<Apartment> apartments;
    private static List<ParkingPlace> parkingPlaces;
    private static List<Person> people;
    private List<Vehicle> vehicles = new ArrayList<>();
    private static List<TenantLetter>tenantLetters=new ArrayList<>();

    //Şu anki tarihi saklamak için bir alan oluşturalım.
    private LocalDate currentDate;
    private ScheduledExecutorService executorService;
    public List<Apartment> getApartments() {
        return apartments;
    }
    public List<TenantLetter>getTenantLetters(){return tenantLetters;}
    public List<ParkingPlace> getParkingPlaces(){
        return parkingPlaces;
    }
    public List<Person> getPeople() {
        return people;
    }
    public List<Vehicle>getVehicles(){return vehicles;}
    public HousingEstateManager() {
        // Initialize your lists and perform other setup here.
        apartments = new ArrayList<>();
        parkingPlaces = new ArrayList<>();
        people = new ArrayList<>();
        // Başlangıçta günümüzün tarihini alalım.
        currentDate = LocalDate.now();
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService = Executors.newSingleThreadScheduledExecutor();
       // executorService.scheduleAtFixedRate(this::passTime, 0, 5, TimeUnit.SECONDS);
        boolean sentTenantLetter = false;
    }

    // Implement methods for renting, checking in/out, data saving, and other functionality

    public void rentApartment(Apartment apartment, Person person, int durationInDays) {
        if (!apartment.isRented() && person.rentApartment(apartment, durationInDays)) {
            apartment.checkIn(person);
            Date currentDate = new Date();
            Date finishDate = new Date(currentDate.getTime() + durationInDays * 24 * 60 * 60 * 1000);
            System.out.println("Apartment rented to " + person.getName() + " " + person.getSurname() + " for " + durationInDays + " days in this date "+currentDate);

            new Thread(() -> {
                for (int i = 1; i <= durationInDays + 2; i++) {
                    Thread dateUpdateThread = new DateUpdateThread(currentDate);
                    dateUpdateThread.start();

                    try {
                        // Work every thread once in 5 second
                        Thread.sleep(5000); //now it is 5 seconds to try quickly(in case I forget to change)
                        if (i % 2 == 0) {
                            if (currentDate.after(finishDate)) {
                                System.out.println(currentDate);
                                System.out.println("Tenant letter has been sent to " + person.getName() +" "+ person.getSurname() + " for the apartment " + apartment.getId());
                                sendTenantLetter(apartment, person);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            System.out.println("Apartment is already rented or person has reached the maximum limit.");
        }
    }





    static class DateUpdateThread extends Thread {
        private Date currentDate;

        public DateUpdateThread(Date currentDate) {
            this.currentDate = currentDate;
        }

        @Override
        public void run() {
            // Tarihi bir gün ileri al
            currentDate.setDate(currentDate.getDate() + 1);

            // Güncellenmiş tarihi yazdır
            //   System.out.println("Güncellenmiş Tarih: " + currentDate);
        }
    }



    private Date getFinishDate(Date startDate, int durationInDays) {
        return new Date(startDate.getTime() + durationInDays * 24 * 60 * 60 * 1000);
    }

    private void sendTenantLetter(Apartment apartment, Person person) {
        // System.out.println(person.getName() + " " + person.getSurname() + " have a tenant letter for Apartment " + apartment.getId());
        TenantLetter tenantLetter = new TenantLetter(person.getIDNumber(), apartment.getId());
        person.addTenantLetter(tenantLetter);
        tenantLetters.add(tenantLetter);
        Date currentDate = new Date();
        Date finishDate = new Date(currentDate.getTime() + 30 * 24 * 60 * 60 * 1000);

        ScheduledExecutorService scheduler = null;
        ScheduledExecutorService finalScheduler = scheduler;


        // new TenantLetterExpiryChecker(tenantLetter).start();
    }
    private void checkFreeApartment(Apartment apartment, Person person) {
        if (!apartment.isRented()) {
            TenantLetter tenantLetter = new TenantLetter(person.getIDNumber(), apartment.getId());
            person.addTenantLetter(tenantLetter);
            // System.out.println("TenantLetter created for " + person.getName() + " " + person.getSurname());
            executorService.shutdown();
        }
    }








    private static void removeTenantLetter(int TlId) {
        TenantLetter letter=findTenantLetterById(TlId);
        // Remove TenantLetter from the person's object
        findPersonById(letter.getTenantId()).getTenantLetters().remove(letter);
        tenantLetters.remove(letter);
        System.out.println("TenantLetter removed for " + findPersonById(letter.getTenantId()).getName() + " " + findPersonById(letter.getTenantId()).getSurname());
    }


    public boolean isVehicleStoredInGarage(String vehicleId, int parkingPlaceId) {
        ParkingPlace foundParking = null;
        // HousingEstateManager örneğini sınıf seviyesinde tanımlayın ve kullanın
        for (ParkingPlace p : this.getParkingPlaces()) {
            if (p.getId() == parkingPlaceId) {
                foundParking = p;
                break;
            }
        }

        if (foundParking != null) {
            for (Ivehicle v : foundParking.getStoredVehicles()) {
                if (v.getId().equals(vehicleId)) {
                    return true;  // Aracın garajda olduğunu belirt
                }
                break;
            }
        }

        return false; // Aracın garajda olmadığını belirt
    }


    public boolean isVehicleStoredInAnyGarage(String vehicleId) {
        HousingEstateManager estateManager = new HousingEstateManager();

        for (ParkingPlace parkingPlace : estateManager.getParkingPlaces()) {
            for (Ivehicle vehicle : parkingPlace.getStoredVehicles()) {
                if (vehicle.getId().equals(vehicleId)) {
                    return true; // Aracın en az bir garajda olduğunu belirt
                }
            }
        }

        return false; // Aracın hiçbir garajda olmadığını belirt
    }
    public int checkVehicleInAnyGarage(String vehicleId) {
        HousingEstateManager estateManager = new HousingEstateManager();

        for (ParkingPlace parkingPlace : estateManager.getParkingPlaces()) {
            for (Ivehicle vehicle : parkingPlace.getStoredVehicles()) {
                if (vehicle.getId().equals(vehicleId)) {
                    return parkingPlace.getId(); // Aracın bulunduğu garajın ID'sini döndür
                }
            }
        }

        return 0; // Aracın hiçbir garajda olmadığını belirtmek için 0 döndür
    }



    public boolean isParkingPlaceRentedByTenant(String tenantId, int parkingPlaceId) {
        // Iterate through all ParkingPlaces to find the one with the provided PpId
        for (ParkingPlace parkingPlace : parkingPlaces) {
            if (parkingPlace.getId() == parkingPlaceId) {
                // Check if the ParkingPlace is rented and has a tenant
                if (parkingPlace.isRented() && parkingPlace.getTenant() != null) {
                    // If the ID of the tenant matches the provided TenantId, return true
                    if (parkingPlace.getTenant().getIDNumber().equals(tenantId)) {
                        return true;
                    }
                }
                // If the ParkingPlace is not rented or has no tenant, return false
                return false;
            }
        }
        // If no ParkingPlace with the provided PpId is found, return false
        return false;
    }



    class TooManyThingsException extends RuntimeException {
        public TooManyThingsException(String message) {
            super(message);
        }
    }

    public void rentParkingPlace(ParkingPlace parkingPlace, Person person) {
        if (!parkingPlace.isRented() && person.rentParkingPlace(parkingPlace)) {
            parkingPlace.rentTo(person);
            System.out.println("ParkingPlace rented to " + person.getName() + " " + person.getSurname());
        } else {
            System.out.println("ParkingPlace is already rented or person has reached the maximum limit.");
        }
    }

    public void checkInPerson(Person person, Apartment apartment) {
        if (!apartment.isRented() && apartment.getTenant() == null) {
            apartment.checkIn(person);
            System.out.println(person.getName() + " checked into the apartment.");
        } else {
            System.out.println("Invalid check-in. Apartment is not rented to " + person.getName() + " " + person.getSurname());
        }
    }
    public void checkInPersonForParkingPlace(Person person, ParkingPlace parkingPlace) {
        if (!parkingPlace.isRented() && parkingPlace.getTenant() == null) {
            parkingPlace.checkInForParkingPlace(person);
            System.out.println(person.getName() + " checked into the parking place.");
        } else {
            System.out.println("Invalid check-in. Parking place is not rented to " + person.getName() + " " + person.getSurname());
        }
    }
    public void checkOutVehicleFromGarage(String vehicleId, int garageId) {
        ParkingPlace foundParking = null;

        // GarageId'ye sahip garajı bul
        for (ParkingPlace parkingPlace : this.getParkingPlaces()) {
            if (parkingPlace.getId() == garageId) {
                foundParking = parkingPlace;
                break;
            }
        }

        // Eğer garaj bulunduysa devam et
        if (foundParking != null) {
            Ivehicle foundVehicle = null;

            // VehicleId'ye sahip aracı ve bulunduğu garajdaki freeVolume'u güncelle
            for (Ivehicle vehicle : foundParking.getStoredVehicles()) {
                if (vehicle.getId().equals(vehicleId)) {
                    foundVehicle = vehicle;
                    foundParking.setFreeVolume(foundParking.getFreeVolume() + foundVehicle.getVolume());
                    break;
                }
            }

            // Eğer araç bulunduysa, aracı garajdan çıkar
            if (foundVehicle != null) {
                foundParking.checkOutVehicle(foundVehicle);
                foundParking.getStoredVehicles().remove(foundVehicle);
                System.out.println("Vehicle " + vehicleId + " checked out from garage " + garageId);
            } else {
                System.out.println("Vehicle " + vehicleId + " not found in garage " + garageId);
            }
        } else {
            System.out.println("Garage with ID " + garageId + " not found");
        }
    }

    public void checkInVehicleToGarage(String vehicleId, int parkingPlaceId) {
      //6
        // List<ParkingPlace> parkingPlaces = getParkingPlaces(); // Parking yerlerini al
        ParkingPlace foundParking = findParkingPlaceById(parkingPlaceId);

        if (foundParking != null) {
            Vehicle foundVehicle = findVehicleById(vehicles, vehicleId);

            if (foundVehicle != null) {
                double remainingVolume = foundParking.getFreeVolume() - foundVehicle.getVolume();

                if (remainingVolume >= 0) {
                    // Eğer yeterli free volume varsa, aracı garaja check-in yap
                    foundParking.checkInVehicle(foundVehicle);
                    foundParking.setFreeVolume(remainingVolume);
                    foundParking.getStoredVehicles().add(foundVehicle);
                    System.out.println("Vehicle " + foundVehicle.getId() + " checked in to the parking place " + foundParking.getId());
                } else {
                    System.out.println("Not enough free volume in the parking place.");
                }
            } else {
                System.out.println("Vehicle not found.");
            }
        } else {
            System.out.println("Parking place not found.");
        }
    }






    public void checkOutPerson(Person person, Apartment apartment) {
        if (apartment.isRented() && apartment.getTenant() == person) {
            apartment.checkOut();
            System.out.println(person.getName() + " checked out from the apartment.");
        } else {
            System.out.println("Invalid check-out. Apartment is not rented to " + person.getName() + " " + person.getSurname());
        }
    }
    public void checkOutPersonForParkingPlace(Person person, ParkingPlace parkingPlace) {
        if (parkingPlace.isRented() && parkingPlace.getTenant() == person) {
            parkingPlace.checkOut();
            System.out.println(person.getName() + " checked out from the Parking Place.");
        } else {
            System.out.println("Invalid check-out. Apartment is not rented to " + person.getName() + " " + person.getSurname());
        }
    }

    public void saveDataToFile(String filename) {
        try (FileWriter fileWriter = new FileWriter(filename);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {

            // Save apartment data
            Collections.sort(apartments, Comparator.comparing(Apartment::getVolume).reversed());
            for (Apartment apartment : apartments) {
                writer.write("Apartment ID: " + apartment.getId() + "\n");
                writer.write("Volume: " + apartment.getVolume() + "\n");
                if (apartment.isRented()) {
                    writer.write("Tenant: " + apartment.getTenant().getName() + " " + apartment.getTenant().getSurname() + "\n");
                } else {
                    writer.write("Not rented\n");
                }
                writer.write("\n");
            }

            // Save parking place data
            for (ParkingPlace parkingPlace : parkingPlaces) {
                writer.write("Parking Place ID: " + parkingPlace.getId() + "\n");
                writer.write("Volume: "+parkingPlace.getVolume()+"\n");
                writer.write("Free volume: "+parkingPlace.getFreeVolume()+"\n");
                if (parkingPlace.isRented()) {
                    writer.write("Tenant: " + parkingPlace.getTenant().getName() + " " + parkingPlace.getTenant().getSurname() + "\n");
                } else {
                    writer.write("Not rented\n");
                }

                // Save stored vehicles in the parking place
                writer.write("Stored Vehicles:\n");
                for (Ivehicle storedVehicle : parkingPlace.getStoredVehicles()) {
                    writer.write("   Vehicle ID: " + storedVehicle.getId() + "\n");
                    writer.write("   Volume: " + storedVehicle.getVolume() + "\n");
                    writer.write("   Name: " + storedVehicle.getName() + "\n");
                    writer.write("   Vehicle Type: " + storedVehicle.getVehicleType() + "\n");
                    // Add other relevant information about the vehicle

                    writer.write("\n");
                }

                writer.write("\n");
            }

            // Save person data
            for (Person person : people) {
                writer.write("Person ID: " + person.getId() + "\n");
                writer.write("Name: " + person.getName() + " " + person.getSurname() + "\n");
                writer.write("ID Number: " + person.getIDNumber() + "\n");
                writer.write("Address: " + person.getAddress() + "\n");

                writer.write("Rented Apartments:\n");
                for (Apartment rentedApartment : person.getRentedApartments()) {
                    writer.write(rentedApartment.getId() + " ");
                }
                writer.write("\n");

                writer.write("Rented Parking Places:\n");
                for (ParkingPlace rentedParkingPlace : person.getRentedParkingPlaces()) {
                    writer.write(rentedParkingPlace.getId() + " ");
                }
                writer.write("\n");

                writer.write("Tenant Letters:\n");
                for (TenantLetter letter : person.getTenantLetters()) {
                    writer.write(letter.getContent() + "\n");
                }

                writer.write("\n");
            }

            System.out.println("Data saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving data to the file: " + e.getMessage());
        }
    }


    public void startTimers() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // Zamanı bir gün ilerletme iş parçacığı
        scheduler.scheduleAtFixedRate(this::advanceTimeByOneDay, 0, 5, TimeUnit.SECONDS);

        // Kira durumunu kontrol iş parçacığı
        scheduler.scheduleAtFixedRate(this::checkRentalStatus, 0, 10, TimeUnit.SECONDS);
    }

    private void checkRentalStatus() {
        LocalDate today = LocalDate.now(); // Bugünün tarihini al

        for (Apartment apartment : apartments) {
            if (apartment.isRented()) {
                LocalDate rentDate = apartment.getRentDate(); // Kira tarihini al
                int durationInDays = apartment.getDurationInDays(); // Kira süresini al

                LocalDate endDate = rentDate.plusDays(durationInDays); // Kira süresinin bitiş tarihini hesapla

                if (today.isAfter(endDate) || today.isEqual(endDate)) {
                    checkOutPerson(apartment.getTenant(), apartment); // Süre doldu, otomatik çıkış yap
                }
            }
        }
    }
    public void ApartmentInfo(int apartmentId) {
        // Verilen apartmentId'ye sahip dairenin bilgilerini bul
        Apartment apartment = findApartmentById(apartmentId);

        // Daire bulunduysa bilgileri ekrana yazdır
        if (apartment != null) {
            System.out.println("Apartment ID: " + apartment.getId());
            System.out.println("Volume: " + apartment.getVolume());

            if (apartment.isRented()) {
                System.out.println("Tenant: " + apartment.getTenant().getName() + " " + apartment.getTenant().getSurname());
                System.out.println("Tenant ID: " + apartment.getTenant().getIDNumber());
            } else {
                System.out.println("Status: Available");
            }
        } else {
            System.out.println("Apartment not found with ID: " + apartmentId);
        }
    }

    private static Apartment findApartmentById(int apartmentId) {
        // Verilen ID'ye sahip daireyi bulan yardımcı bir metod
        for (Apartment apartment : apartments) {
            if (apartment.getId() == apartmentId) {
                return apartment;
            }
        }
        return null;
    }
    public static Person findPersonById(String personId) {
        for (Person person : people) {
            if (person.getIDNumber().equals(personId)) {
                return person;
            }
        }
        return null; // ID ile eşleşen kişi bulunamadı
    }

    public static TenantLetter findTenantLetterById(int id) {
        for (TenantLetter letter : tenantLetters) {
            if (letter.getID() == id) {
                return letter;
            }
        }
        return null; // ID ile eşleşen TenantLetter bulunamadı
    }

    private static ParkingPlace findParkingPlaceById (int parkingPlaceId) {
        HousingEstateManager.parkingPlaceId = parkingPlaceId;
       /* for (ParkingPlace p : parkingPlaces) {
            if (p.getId()==parkingPlaceId) {
                return p;
            }
        }
        return null;
        */
        for (ParkingPlace parkingPlace : parkingPlaces) {
            if (parkingPlace.getId() == parkingPlaceId) {
                return parkingPlace;
            }
        }
        return null;

       /* for (ParkingPlace p : parkingPlaces) {
            if (p.getId()==parkingPlaceId) {
                return p;

            }
        }
        return null;*/
    }

    public static Vehicle findVehicleById(List<Vehicle> vehicles, String VehicleId) {
        for (Vehicle p : vehicles ) {
            if (p.getId().equalsIgnoreCase(VehicleId)) {
                return p;
            }
        }
        return null;
    }

    public void ParkingPlaceInfo(int parkingPlaceId) {
        // Verilen parkingPlaceId'ye sahip park yerinin bilgilerini bul
        ParkingPlace parkingPlace = findParkingPlaceById(parkingPlaceId);

        // Park yeri bulunduysa bilgileri ekrana yazdır
        if (parkingPlace != null) {
            System.out.println("Parking Place ID: " + parkingPlace.getId());
            System.out.println("Volume: " + parkingPlace.getVolume());
            System.out.println("Free volume: "+parkingPlace.getFreeVolume());

            if (parkingPlace.isRented()) {
                System.out.println("Tenant: " + parkingPlace.getTenant().getName() + " " + parkingPlace.getTenant().getSurname());
                System.out.println("Tenant ID: " + parkingPlace.getTenant().getIDNumber());
            } else {
                System.out.println("Status: Available");
            }
        } else {
            System.out.println("Parking Place not found with ID: " + parkingPlaceId);
        }
    }

    /*private static ParkingPlace findParkingPlaceById(int parkingPlaceId) {
        // Verilen ID'ye sahip park yerini bulan yardımcı bir metod
        for (ParkingPlace parkingPlace : parkingPlaces) {
            if (parkingPlace.getId() == parkingPlaceId) {
                return parkingPlace;
            }
        }
        return null;
    }*/


    private void advanceTimeByOneDay() {
        // Burada, zamanı bir gün ilerletme mantığını yazabilirsiniz
        // Zamanın ilerletilmesi, bir takvim veya tarih nesnesi kullanılarak gerçekleştirilebilir.

        currentDate = currentDate.plusDays(1); // Günü bir gün ileri alalım.

        // currentDate = currentDate.plusDays(7); // Örneğin, bir hafta ilerletmek için 7 gün ekleyebiliriz.
        // currentDate = currentDate.plusMonths(1); // Bir ay ilerletmek için 1 ay ekleyebiliriz.
        // currentDate = currentDate.plusYears(1); // Bir yıl ilerletmek için 1 yıl ekleyebiliriz.
    }

    public static void main(String[] args) {



        clearFile("data.txt");
        HousingEstateManager estateManager = new HousingEstateManager();

        // Create and start the time passage thread
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            // Implement time passage logic here (move time forward by one day)
        }, 0, 5, TimeUnit.SECONDS);

        // Implement a command-line menu for user interaction
        // Allow users to perform operations and demonstrate your application's functionality

        // Ensure proper exception handling

        // Here, you can have a loop to handle user input and call methods accordingly
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Choose an option:");
            System.out.println("1. Rent an apartment");
            System.out.println("2. Check-in/out people");
            System.out.println("3. Save data");
            System.out.println("4. Exit");
            System.out.println("5. Info about properties");
            System.out.println("6. Inserting and removing items");
            System.out.println("7. Tenant Letter functions");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    double volume = 0;
                    /*   System.out.println("Enter the volume for the apartment:");*/
                    System.out.println("1 to enter volume 2 for sides");
                    int Vchoise = scanner.nextInt();
                    switch (Vchoise) {
                        case 1:
                            System.out.println("Enter the volume for the apartment");
                            volume = scanner.nextDouble();
                            break;
                        case 2:
                            System.out.println("Enter the sides");
                            double a = scanner.nextDouble();
                            double b = scanner.nextDouble();
                            double c = scanner.nextDouble();
                            volume = a * b * c;
                            break;
                        default:
                            System.out.println("Wrong input");

                            break;

                    }
                    scanner.nextLine(); // Consume newline
                    System.out.println("Enter tenant's ID number:");
                    String idNumber = scanner.nextLine();
                    // Check if the person already exists based on ID number
                    Person existingPerson = null;
                    for (Person p : estateManager.getPeople()) {
                        if (p.isSamePerson(idNumber)) {
                            existingPerson = p;
                            break;
                        }
                    }
                    if (existingPerson != null) {
                        // Person exists, use the existing person
                        System.out.println("Person already exists: " + existingPerson.getName());
                        // Assign the apartment to the existing person
                        Apartment apartment = new Apartment(volume);
                        System.out.println("Enter the duration with days");
                        int duration2 = scanner.nextInt();
                        scanner.nextLine();
                        estateManager.rentApartment(apartment, existingPerson, duration2);
                        estateManager.getApartments().add(apartment);

                        // Check if they want a Parking Place
                        System.out.println("Do they want Parking Place? 'Y' for Yes and 'N' for No");
                        String PpAnswer = scanner.nextLine();
                        scanner.nextLine(); // Consume newline

                        if (PpAnswer.equalsIgnoreCase("y")) {
                            double Ppvolume = 0;

                            System.out.println("1 for volume, 2 for sides");
                            int Ppchoice = scanner.nextInt();

                            switch (Ppchoice) {
                                case 1:
                                    System.out.println("Enter the volume");
                                    Ppvolume = scanner.nextDouble();
                                    break;

                                case 2:
                                    System.out.println("Enter the sides");
                                    double a1 = scanner.nextDouble();
                                    double b1 = scanner.nextDouble();
                                    double c1 = scanner.nextDouble();
                                    Ppvolume = a1 * b1 * c1;
                                    break;
                            }
                            scanner.nextLine();


                            ParkingPlace parkingPlace = new ParkingPlace(Ppvolume);
                            estateManager.rentParkingPlace(parkingPlace, existingPerson);
                            estateManager.getParkingPlaces().add(parkingPlace);
                        }
                    } else {
                        // Person doesn't exist, create a new person
                        System.out.println("Enter tenant's name:");
                        String name = scanner.nextLine();

                        System.out.println("Enter tenant's surname:");
                        String surname = scanner.nextLine();

                        System.out.println("Enter tenant's address:");
                        String address = scanner.nextLine();

                        // Create a new person object
                        Person person = new Person(name, surname, idNumber, address);
                        estateManager.getPeople().add(person);

                        // Rent the apartment with given volume to the person
                        Apartment apartment = new Apartment(volume);
                        System.out.println("Enter the duration for renting the apartment (in days):");
                        int duration = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        estateManager.rentApartment(apartment, person, duration);

                        estateManager.getApartments().add(apartment);

                        // Check if they want a Parking Place
                        System.out.println("Do they want Parking Place? 'Y' for Yes and 'N' for No");
                        String PPAnswer = scanner.nextLine();
                        if (PPAnswer.equalsIgnoreCase("y")) {
                            double PPvolume = 0;
                            /*   System.out.println("Enter the volume for the Parking Place:");*/
                            System.out.println("1 to enter volume 2 for sides");
                            int Vchoise2 = scanner.nextInt();
                            switch (Vchoise2) {
                                case 1:
                                    System.out.println("Enter the volume for the Parking Place");
                                    PPvolume = scanner.nextDouble();
                                    break;
                                case 2:
                                    System.out.println("Enter the sides");
                                    double a = scanner.nextDouble();
                                    double b = scanner.nextDouble();
                                    double c = scanner.nextDouble();
                                    PPvolume = a * b * c;
                                    break;
                            }

                            scanner.nextLine(); // Consume newline

                            ParkingPlace parkingPlace = new ParkingPlace(PPvolume);
                            estateManager.rentParkingPlace(parkingPlace, person);
                            estateManager.getParkingPlaces().add(parkingPlace);
                            break;
                        } else if (PPAnswer.equalsIgnoreCase("n")) break;
                        else {
                            System.out.println("wrong input");
                        }
                        break;
                    }
                    /*System.out.println("Do you want to add any vehicle to the Parking Place");
                            double d= scanner.nextInt();
                            ParkingPlace parkingPlace=new ParkingPlace(d);
                    Ivehicle CityCar = new CityCar("Mercedes",4.5,5,"abc");
                    parkingPlace.parkVehicle(CityCar);*/
                    break;


                case 2:
                    System.out.println("Enter the person's ID number:");
                    String personId = scanner.nextLine();

                    Person foundPerson = null;
                    for (Person p : estateManager.getPeople()) {
                        if (p.isSamePerson(personId)) {
                            foundPerson = p;
                            break;
                        }
                    }

                    if (foundPerson == null) {
                        System.out.println("Person not found. Do you want to add a new person? (Y/N)");
                        String addNewPersonChoice = scanner.nextLine();

                        if (addNewPersonChoice.equalsIgnoreCase("Y")) {
                            System.out.println("Enter the person's name:");
                            String personName = scanner.nextLine();
                            System.out.println("Enter the person's surname:");
                            String surname = scanner.nextLine();
                            // System.out.println("Enter the person's ID number:");
                            // idNumber = scanner.nextLine();
                            System.out.println("Enter the person's address:");
                            String address = scanner.nextLine();

                            foundPerson = new Person(personName, surname, personId, address);
                            estateManager.getPeople().add(foundPerson);
                            System.out.println("New person added: " + foundPerson.getName());
                        } else {
                            System.out.println("Operation canceled. Person not added.");
                            break;
                        }
                    }

                    if (foundPerson != null) {
                        System.out.println("Select the type of property for Check-in or Check-out? (Enter 1 for Apartment, 2 for Parking Place)");
                        int propertyTypeChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        switch (propertyTypeChoice) {
                            case 1:
                                // Check-in or Check-out for Apartment
                                System.out.println("Check-in or Check-out? (Enter 1 for Check-in, 2 for Check-out)");
                                int checkChoice = scanner.nextInt();
                                scanner.nextLine(); // Consume newline

                                switch (checkChoice) {
                                    case 1:
                                        // Check-in for Apartment
                                        System.out.println("Enter the apartment ID:");
                                        int apartmentId = scanner.nextInt();
                                        System.out.println("Enter the duration with months");
                                        int duration = scanner.nextInt();
                                        scanner.nextLine(); // Consume newline

                                        Apartment foundApartment = findApartmentById(apartmentId);

                                        if (foundApartment != null) {
                                            estateManager.checkInPerson(foundPerson, foundApartment);
                                            estateManager.rentApartment(foundApartment, foundPerson, duration);
                                        } else {
                                            System.out.println("Apartment not found.");
                                        }
                                        break;

                                    case 2:
                                        // Check-out for Apartment
                                        System.out.println("Enter the apartment ID:");
                                        int checkOutApartmentId = scanner.nextInt();
                                        scanner.nextLine(); // Consume newline

                                        Apartment checkOutApartment = findApartmentById(checkOutApartmentId);

                                        if (checkOutApartment != null) {
                                            estateManager.checkOutPerson(foundPerson, checkOutApartment);
                                        } else {
                                            System.out.println("Apartment not found.");
                                        }
                                        break;

                                    default:
                                        System.out.println("Invalid check choice.");
                                        break;
                                }
                                break;

                            case 2:
                                // Check-in or Check-out for Parking Place
                                System.out.println("Check-in or Check-out? (Enter 1 for Check-in, 2 for Check-out)");
                                int parkingCheckChoice = scanner.nextInt();
                                scanner.nextLine(); // Consume newline

                                switch (parkingCheckChoice) {
                                    case 1:
                                        // Check-in for Parking Place
                                        System.out.println("Enter the parking place ID:");
                                        int parkingPlaceId = scanner.nextInt();
                                        scanner.nextLine(); // Consume newline

                                        ParkingPlace foundParkingPlace = findParkingPlaceById(parkingPlaceId);

                                        if (foundParkingPlace != null) {
                                            estateManager.checkInPersonForParkingPlace(foundPerson, foundParkingPlace);
                                            estateManager.rentParkingPlace(foundParkingPlace, foundPerson);
                                        } else {
                                            System.out.println("Parking Place not found.");
                                        }
                                        break;

                                    case 2:
                                        // Check-out for Parking Place
                                        System.out.println("Enter the parking place ID:");
                                        int checkOutParkingPlaceId = scanner.nextInt();
                                        scanner.nextLine(); // Consume newline

                                        ParkingPlace checkOutParkingPlace = findParkingPlaceById(checkOutParkingPlaceId);

                                        if (checkOutParkingPlace != null) {
                                            estateManager.checkOutPersonForParkingPlace(foundPerson, checkOutParkingPlace);
                                        } else {
                                            System.out.println("Parking Place not found.");
                                        }
                                        break;

                                    default:
                                        System.out.println("Invalid check choice.");
                                        break;
                                }
                                break;

                            default:
                                System.out.println("Invalid property type choice.");
                                break;
                        }
                    }


                    break;

                case 3:
                    //     System.out.println("Enter the filename to save the data:");
                    //String filename = scanner.nextLine();
                    estateManager.saveDataToFile("data.txt");
                    break;
                case 4:
                    exit = true;
                    break;

                case 5:
                    System.out.println("Choose the type of information you want to get:");
                    System.out.println("1. Apartment");
                    System.out.println("2. Parking Place");

                    int infoTypeChoice = scanner.nextInt();

                    switch (infoTypeChoice) {
                        case 1:
                            System.out.println("Enter apartment ID:");
                            int apID = scanner.nextInt();
                            estateManager.ApartmentInfo(apID);
                            break;

                        case 2:
                            System.out.println("Enter parking place ID:");
                            int ppID = scanner.nextInt();
                            estateManager.ParkingPlaceInfo(ppID);
                            break;

                        default:
                            System.out.println("Invalid choice. Please enter 1 or 2.");
                            break;
                    }

                case 6:

//Asking ID of tenant
                    System.out.println("Enter the ID of the Tenant");
                    String HId = scanner.nextLine();
//Does such a person exist?
                    Person foundPerson2 = null;
                    for (Person p : estateManager.getPeople()) {
                        if (p.isSamePerson(HId)) {
                            foundPerson2 = p;
                            break;
                        }
                    }
                    //if tenant does not exist stop
                    if (foundPerson2 == null) {
                        System.out.println("The tenant is not found");
                        break;
                    } else {
//ask id of PP(Parking Place)
                        System.out.println("Enter the ID of the Parking Place");
                        int PpId = scanner.nextInt();
                        scanner.nextLine();
                        //does this PP exist?
                        ParkingPlace foundParking = null;
                        for (ParkingPlace p : estateManager.getParkingPlaces()) {
                            if (p.isSameParkingPlace(PpId)) {
                                foundParking = p;
                                break;
                            }
                        }
                        //if PP does not exist end
                        if (foundParking == null) {
                            System.out.println("Not such a parking place exists");
                        }
                        //if PP exists
                        else {
                            //Is this PP rented by this Tenant?
                            boolean isRentedByH = estateManager.isParkingPlaceRentedByTenant(HId, PpId);
//if not stop
                            if (!isRentedByH) {
                                System.out.println("The Parking place is not rented by this tenant!");
                                break;
                            }
                            //if so
                            else {
                                //asking for vehicle
                                System.out.println("Enter the ID of the Vehicle");
                                String VId = scanner.nextLine();
                                //asking for inserting or removing items
                                System.out.println("Enter 1 to insert, 2 to remove the item.");
                                int IR = scanner.nextInt();
                                scanner.nextLine();
                                switch (IR) {
                                    case 1:
                                        //checking if the vehicle exists
                                        Vehicle foundVehicle = null;
                                        for (Vehicle p : estateManager.getVehicles()) {
                                            if (p.isSameVehicle(VId)) {
                                                foundVehicle = p;
                                                break;
                                            }
                                        }
                                        if (foundVehicle == null) {
                                            //requiring new information to create a new vehicle
                                            System.out.println("The vehicle does not exist, enter the name of Vehicle");
                                            String Vname = scanner.nextLine();
                                            System.out.println("Enter the volume of Vehicle:");
                                            Double Vvolume = scanner.nextDouble();
                                            scanner.nextLine();
                                            System.out.println("Enter the engine capacity of Vehicle:");
                                            Double Vcapacity = scanner.nextDouble();
                                            scanner.nextLine();
                                            System.out.println("Enter the engine type of Vehicle:");
                                            String Vengine = scanner.nextLine();
                                            //scanner.nextLine();
                                            System.out.println("Enter the vehicle type:");
                                            String vehicleTypeString = scanner.nextLine();
                                            VehicleType vehicleType = VehicleType.valueOf(vehicleTypeString.toUpperCase());
                                            foundVehicle = new Vehicle(VId, Vname, Vvolume, Vcapacity, Vengine, vehicleType);
                                            estateManager.getVehicles().add(foundVehicle);
                                            System.out.println("New vehicle added: " + foundVehicle.getName());
                                        } else {
//now we have to add our vehicle to the garage
//firstly, we have to find out if our vehicle is already stored in a different garage
                                            boolean isVehicleStored = estateManager.isVehicleStoredInAnyGarage(VId);
                                            if (isVehicleStored) {
                                                System.out.println("Do you want to remove it from that Parking Place? 1 for yes 2 for no");
                                                int remove = scanner.nextInt();
                                                scanner.nextLine();
                                                switch (remove) {
                                                    case 1:
                                                        int previousGarageId = estateManager.checkVehicleInAnyGarage(VId);
                                                        // Çıkarma işlemi
                                                        estateManager.checkOutVehicleFromGarage(VId, previousGarageId); // Bu metodu kendin oluşturman gerekecek
                                                        break;
                                                    case 2:
                                                        System.out.println("Process ended");
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }
//now let's add vehicle to garage
                                        estateManager.checkInVehicleToGarage(VId, PpId);
                                        break;

                                    case 2:
                                        //checking if the vehicle exists
                                        Vehicle foundVehicle2 = null;
                                        for (Vehicle p : estateManager.getVehicles()) {
                                            if (p.isSameVehicle(VId)) {
                                                foundVehicle2 = p;
                                                break;
                                            }
                                        }
                                        if (foundVehicle2 == null) {
                                            System.out.println("This vehicle does not exist");
                                            break;
                                        } else {
                                            //finding if this vehicle is stored in this garage
                                            //error here
                                            boolean isVehicleInThisGarage = estateManager.isVehicleStoredInGarage(VId, PpId);
                                            if (!isVehicleInThisGarage) {
                                                System.out.println("This vehicle is not stored in this Parking Place");
                                                break;
                                            } else {
                                                //if yes check out
                                                estateManager.checkOutVehicleFromGarage(VId, PpId);
                                            }
                                        }
                                }
                            }
                        }
                    }
                    break;

                case 7:
                    System.out.println("Enter Tenant Letter ID:");
                    int tenantLetterId = scanner.nextInt();
                    TenantLetter tenantLetter = findTenantLetterById(tenantLetterId); // Bu metodu TenantLetter'ı bulmak için implement etmelisiniz
                    if (tenantLetter == null) {
                        System.out.println("No Tenant Letter with ID " + tenantLetterId);
                    } else if (!tenantLetter.isActive()) {
                        System.out.println("Tenant Letter with ID " + tenantLetterId + " is not active.");
                    } else {

                        System.out.println("Do you want to extend or cancel the rent? (1 for extend, 2 for cancel)");
                        int choice3 = scanner.nextInt();

                        if (choice3 == 1) {
                            System.out.println("How many days do you want to extend the rent?");
                            int extensionDays = scanner.nextInt();


                            // Check-out from the apartment
                            estateManager.checkOutPerson(findPersonById(tenantLetter.getTenantId()), findApartmentById(tenantLetter.getApartmentId()));

                            // Check-in to the apartment for the extended period
                            estateManager.checkInPerson(findPersonById(tenantLetter.getTenantId()), findApartmentById(tenantLetter.getApartmentId()));
                           //  estateManager.rentApartment(findApartmentById(tenantLetter.getApartmentId()), findPersonById(tenantLetter.getTenantId()), extensionDays);
                            // Remove the TenantLetter
                            removeTenantLetter(tenantLetterId);
                        } else if (choice3 == 2) {
                            // Check-out from the apartment
                            estateManager.checkOutPerson(findPersonById(tenantLetter.getTenantId()), findApartmentById(tenantLetter.getApartmentId()));


                            // Remove the TenantLetter
                            removeTenantLetter(tenantLetterId);
                        }
                        // TenantLetter hala aktif, burada diğer işlemleri gerçekleştirebilirsiniz...
                    }
                    break;
            }
        }

        // Cleanup and exit
        scanner.close();
        scheduler.shutdown();

    }


}