package ua.antonchuk.airline.model;

public class CombiAirplane extends PassengerAirplane {

    private final double cargoCapacity;

    public CombiAirplane(String model, int flightRange, double fuelConsumption,
                         int passengerCapacity, double cargoCapacity) {
        super(model, flightRange, fuelConsumption, passengerCapacity);
        if (!(cargoCapacity > 0) || Double.isInfinite(cargoCapacity)) {
            throw new IllegalArgumentException(
                    "Cargo capacity must be a positive finite number: " + cargoCapacity);
        }
        this.cargoCapacity = cargoCapacity;
    }

    @Override
    public double getCargoCapacity() {
        return cargoCapacity;
    }
}