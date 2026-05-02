package InterfaceTask;
interface Drivable {
    void start();
    void stop();

    default void describe() {
        System.out.println("This is a drivable vehicle.");
    }
}

abstract class Vehicle implements Drivable {

    private String brand;

    public Vehicle(String brand) {
        System.out.println("Vehicle constructor called");
        this.brand = brand;
    }

    public abstract double calculateFuelEfficiency();

    @Override
    public abstract String toString();

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}

class Car extends Vehicle {

    private double distanceTravelled;
    private double fuelConsumed;

    public Car(String brand, double distanceTravelled, double fuelConsumed) {
        super(brand);
        System.out.println("Car constructor called");

        this.distanceTravelled = distanceTravelled;
        this.fuelConsumed = fuelConsumed;
    }
    public double getDistanceTravelled() { return distanceTravelled; }
    public void setDistanceTravelled(double distanceTravelled) { this.distanceTravelled = distanceTravelled; }

    public double getFuelConsumed() { return fuelConsumed; }
    public void setFuelConsumed(double fuelConsumed) { this.fuelConsumed = fuelConsumed; }


    @Override
    public double calculateFuelEfficiency() {
        return distanceTravelled / fuelConsumed;
    }

    @Override
    public String toString() {
        return "Car brand is: " + getBrand() + " and fuel efficiency is: " + calculateFuelEfficiency();
    }

    @Override
    public void start() {
       System.out.println("Car " + getBrand() + " engine started.");
    }

    @Override
    public void stop() {
        System.out.println("Car " + getBrand() + " engine stopped.");
    }
}

class Motorcycle extends Vehicle {

    private int engineCapacity;
    private double mileage;

    public Motorcycle(String brand, int engineCapacity, double mileage) {
        super(brand);
        System.out.println("Motorcycle constructor called");

        this.engineCapacity = engineCapacity;
        this.mileage = mileage;
    }

    public int getEngineCapacity() { return engineCapacity; }
    public void setEngineCapacity(int engineCapacity) { this.engineCapacity = engineCapacity; }

    public double getMileage() { return mileage; }
    public void setMileage(double mileage) { this.mileage = mileage; }

    @Override
    public double calculateFuelEfficiency() {
        return mileage;
    }

    @Override
    public String toString() {
        return "Motorcycle brand is: " + getBrand() + " and fuel efficiency is: " + calculateFuelEfficiency();
    }

    @Override
    public void start() {
        System.out.println("Motorcycle " + getBrand() + " engine started.");
    }

    @Override
    public void stop() {
        System.out.println("Motorcycle " + getBrand() + " engine stopped.");
    }
}

public class InterfaceOnlineTask {
    public static void main(String[] args) {

        Vehicle v1 = new Car("Toyota", 500, 40);
        Vehicle v2 = new Motorcycle("Yamaha", 150, 45.5);

        System.out.println(v1);
        System.out.println(v2);

        v1.start();
        v1.stop();
        v2.start();
        v2.stop();

        v1.describe();
        v2.describe();

        v1.setBrand("Tesla");
        System.out.println("Updated: " + v1);
    }
}