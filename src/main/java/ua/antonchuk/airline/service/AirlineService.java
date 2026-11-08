package ua.antonchuk.airline.service;

import ua.antonchuk.airline.model.Airline;
import ua.antonchuk.airline.model.Airplane;
import ua.antonchuk.airline.repository.AirplaneRepository;

public class AirlineService {

    private final AirplaneRepository repository;

    public AirlineService(AirplaneRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository must not be null");
        }
        this.repository = repository;
    }

    public Airline createAirline(String name) {
        Airline airline = new Airline(name);
        for (Airplane airplane : repository.findAll()) {
            airline.addAirplane(airplane);
        }
        return airline;
    }
}