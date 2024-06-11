package GUI_P1;

public class CityCar implements Ivehicle {
    private String name;
    private double volume;
    private double engineCapacity;
    private String engineType;
    private String Id;
    private VehicleType vehicleType = VehicleType.CITY_CAR;

    public CityCar(int id,String name, double volume, double engineCapacity, String engineType) {
        this.name = name;
        this.volume = volume;
        this.engineCapacity = engineCapacity;
        this.engineType = engineType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getVolume() {
        return volume;
    }

    @Override
    public double getEngineCapacity() {
        return engineCapacity;
    }

    @Override
    public String getEngineType() {
        return engineType;
    }

    @Override
    public String getVehicleType() {
        return vehicleType.name();
    }
    public String getId() {return Id;}
    @Override
    public void displayFeatures() {
        System.out.println("Vehicle Type: " + getVehicleType());
        System.out.println("Name: " + getName());
        System.out.println("Volume: " + getVolume());
        System.out.println("Engine Capacity: " + getEngineCapacity());
        System.out.println("Engine Type: " + getEngineType());
        // Add more specific features related to CityCar
    }
}

// Similar implementations for Boat, Motorcycle, and Amphibious classes...
