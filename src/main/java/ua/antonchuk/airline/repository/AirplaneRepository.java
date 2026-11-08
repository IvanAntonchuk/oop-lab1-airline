package ua.antonchuk.airline.repository;

import ua.antonchuk.airline.model.Airplane;

import java.util.List;

public interface AirplaneRepository {

    List<Airplane> findAll();
}