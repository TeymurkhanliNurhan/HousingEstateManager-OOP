package GUI_P1;

public class Vehicle implements Ivehicle {
    private String name;
    private double volume;
    private VehicleType vehicleType;
    private String ID;
    private double capacity;
    private String engine;
    private boolean inGarage;
    // Add specific attributes related to vehicle type

    public Vehicle(String vehicleID, String vname, Double vvolume, Double vcapacity, String vengine,VehicleType vehicleType) {
        this.name = vname;
        this.volume = vvolume;
         this.vehicleType = vehicleType;
        this.ID=vehicleID;
        this.capacity=vcapacity;
        this.engine=vengine;
        this.inGarage = false;
    }
    public boolean isInGarage() {
        return inGarage;
    }

    public void setInGarage(boolean inGarage) {
        this.inGarage = inGarage;
    }
    public boolean isSameVehicle(String id) {
        return this.ID.equals(id);
    }

    public Vehicle(String vehicleID, String vname, Double vvolume, Double vcapacity, String vengine) {
        this.name = vname;
        this.volume = vvolume;
       // this.vehicleType = vehicleType;
        this.ID=vehicleID;
        this.capacity=vcapacity;
        this.engine=vengine;

    }

    public String getName() {
        return name;
    }

    public double getVolume() {
        return volume;
    }

    public double getEngineCapacity() {
        return capacity;
    }

    @Override
    public String getEngineType() {
        return engine;
    }

    public String getVehicleType() {
        return vehicleType.name();
    }

    public String  getId() {return ID;}

    @Override
    public void displayFeatures() {

    }

    // Implement methods and logic related to vehicles
}
