//package Abstraction;
abstract class Vehicle {
    private String brand;

    public Vehicle(String brand) {
        this.brand = brand;
        System.out.println("Vehicle constructor called");
    }

    public abstract double calculateFuelEfficiency();
    
    @Override
    public abstract String toString();

    public String getBrand() {
        return brand;
    }
}

class Car extends Vehicle {
    private double distanceTravelled;
    private double fuelConsumed;

    public Car(String brand, double distanceTravelled, double fuelConsumed) {
        super(brand); 
        this.distanceTravelled = distanceTravelled;
        this.fuelConsumed = fuelConsumed;
        System.out.println("Car constructor called");
    }

    @Override
    public double calculateFuelEfficiency() {
        if (fuelConsumed == 0) return 0; 
        return distanceTravelled / fuelConsumed;
    }

    @Override
    public String toString() {
        return "Car brand is " + getBrand() + " and fuel efficiency is: " + calculateFuelEfficiency() + " km/l";
    }
}

class Motorcycle extends Vehicle {
    private int engineCapacity;
    private double mileage;

    public Motorcycle(String brand, int engineCapacity, double mileage) {
        super(brand);
        this.engineCapacity = engineCapacity;
        this.mileage = mileage;
        System.out.println("Motorcycle constructor called");
    }

    @Override
    public double calculateFuelEfficiency() {
        return mileage; 
    }

    @Override
    public String toString() {
        return "Motorcycle brand is " + getBrand() + " and fuel efficiency is: " + calculateFuelEfficiency() + " km/l";
    }
}

public class Abstraction {
    public static void MainAb(String[] args) {
        System.out.println("--- Instantiating Car ---");
        Vehicle myCar = new Car("Toyota", 500.0, 40.0);
        
        System.out.println("\n--- Instantiating Motorcycle ---");

        Vehicle myMotorcycle = new Motorcycle("Yamaha", 150, 45.5);

        System.out.println("\n--- Object Output ---");

        System.out.println(myCar.toString());
        System.out.println(myMotorcycle.toString());
    }
}