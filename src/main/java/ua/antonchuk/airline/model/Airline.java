package ua.antonchuk.airline.model;

import java.util.ArrayList;
import java.util.List;

public class Airline {

    private final String name;
    private final List<Airplane> airplanes = new ArrayList<>();

    public Airline(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    public void addAirplane(Airplane airplane) {
        if (airplane == null) {
            throw new IllegalArgumentException("Airplane must not be null");
        }
        airplanes.add(airplane);
    }

    public List<Airplane> getAirplanes() {
        return List.copyOf(airplanes);
    }

    public int getTotalPassengerCapacity() {
        return airplanes.stream()
                .mapToInt(Airplane::getPassengerCapacity)
                .sum();
    }

    public double getTotalCargoCapacity() {
        return airplanes.stream()
                .mapToDouble(Airplane::getCargoCapacity)
                .sum();
    }
}