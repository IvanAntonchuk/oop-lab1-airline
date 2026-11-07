package ua.antonchuk.airline.model;

public class CargoAirplane extends Airplane {

    private final double cargoCapacity;

    public CargoAirplane(String model, int flightRange, double fuelConsumption,
                         double cargoCapacity) {
        super(model, flightRange, fuelConsumption);
        if (!(cargoCapacity > 0) || Double.isInfinite(cargoCapacity)) {
            throw new IllegalArgumentException(
                    "Cargo capacity must be a positive finite number: " + cargoCapacity);
        }
        this.cargoCapacity = cargoCapacity;
    }

    @Override
    public int getPassengerCapacity() {
        return 0;
    }

    @Override
    public double getCargoCapacity() {
        return cargoCapacity;
    }
}