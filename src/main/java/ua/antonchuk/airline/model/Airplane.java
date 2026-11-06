package ua.antonchuk.airline.model;

import java.util.Locale;

public abstract class Airplane {

    private final String model;
    private final int flightRange;
    private final double fuelConsumption;

    protected Airplane(String model, int flightRange, double fuelConsumption) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model must not be null or blank");
        }
        if (flightRange <= 0) {
            throw new IllegalArgumentException(
                    "Flight range must be positive: " + flightRange);
        }
        if (!(fuelConsumption > 0) || Double.isInfinite(fuelConsumption)) {
            throw new IllegalArgumentException(
                    "Fuel consumption must be a positive finite number: " + fuelConsumption);
        }
        this.model = model.trim();
        this.flightRange = flightRange;
        this.fuelConsumption = fuelConsumption;
    }

    public String getModel() {
        return model;
    }

    public int getFlightRange() {
        return flightRange;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public abstract int getPassengerCapacity();

    public abstract double getCargoCapacity();

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "%s [model=%s, range=%d km, fuel=%.1f t/h, passengers=%d, cargo=%.1f t]",
                getClass().getSimpleName(), model, flightRange, fuelConsumption,
                getPassengerCapacity(), getCargoCapacity());
    }
}