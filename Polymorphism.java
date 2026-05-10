//package Polymorphism;

class Vehicle{
    double speed(){
        return 0;
    } 
}

class Car extends Vehicle{
    double engineSize;
    double fuelCapacity;

    Car(){
        this.engineSize=0;
        this.fuelCapacity=0;
    }

    Car(double engineSize, double fuelCapacity){
        this.engineSize=engineSize;
        this.fuelCapacity=fuelCapacity;
    }

    @Override
    double speed(){
        return engineSize*fuelCapacity*0.5;
    }
}

class Bike extends Vehicle {
    double frameWeight;
    double wheelSize;

    Bike(double frameWeight, double wheelSize) {
        this.frameWeight = frameWeight;
        this.wheelSize = wheelSize;
    }

    @Override
    double speed() {
        return (frameWeight / wheelSize) * 10;
    }
}

class Boat extends Vehicle {
    double displacement;
    double hullLength;

    Boat(double displacement, double hullLength) {
        this.displacement = displacement;
        this.hullLength = hullLength;
    }

    @Override
    double speed() {
        return (displacement / hullLength) * 3;
    }
}

public class Polymorphism{
    public static void Main(String[] args);{
        Vehicle[] vehicles= new Vehicle[3];

        vehicles[0]=new Car(2, 35);
        vehicles[1]=new Bike(12.5, 10);
        vehicles[2]=new Boat(14, 20);

        for(int i=0; i < vehicle.length; i++){
            System.out.printl(vehicles[i].getClass().getSimpleName()+"Speed"+vehicles[i].speed());
        }
    }
}