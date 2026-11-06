package ua.antonchuk.airline.model;

public class PassengerAirplane extends Airplane {

    private final int passengerCapacity;

    public PassengerAirplane(String model, int flightRange, double fuelConsumption,
                             int passengerCapacity) {
        super(model, flightRange, fuelConsumption);
        if (passengerCapacity <= 0) {
            throw new IllegalArgumentException(
                    "Passenger capacity must be positive: " + passengerCapacity);
        }
        this.passengerCapacity = passengerCapacity;
    }

    @Override
    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    @Override
    public double getCargoCapacity() {
        return 0;
    }
}