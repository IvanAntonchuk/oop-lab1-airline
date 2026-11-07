package ua.antonchuk.airline.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirlineTest {

    private static final double DELTA = 1e-9;

    private Airline airline;
    private PassengerAirplane passenger;
    private CargoAirplane cargo;

    @BeforeEach
    void setUp() {
        airline = new Airline("Test Air");
        passenger = new PassengerAirplane("Boeing 737", 5400, 2.5, 189);
        cargo = new CargoAirplane("Boeing 747-8F", 8130, 10.5, 137.7);
    }

    @Test
    void constructorStoresTrimmedName() {
        Airline trimmed = new Airline("  Sky Lines  ");

        assertEquals("Sky Lines", trimmed.getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void constructorRejectsBlankName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Airline(name));
    }

    @Test
    void newAirlineHasNoAirplanes() {
        assertTrue(airline.getAirplanes().isEmpty());
    }

    @Test
    void addAirplaneStoresAirplanesInOrder() {
        airline.addAirplane(passenger);
        airline.addAirplane(cargo);

        assertEquals(List.of(passenger, cargo), airline.getAirplanes());
    }

    @Test
    void addAirplaneRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> airline.addAirplane(null));
    }

    @Test
    void getAirplanesReturnsUnmodifiableList() {
        airline.addAirplane(passenger);

        assertThrows(UnsupportedOperationException.class,
                () -> airline.getAirplanes().add(cargo));
        assertEquals(1, airline.getAirplanes().size());
    }

    @Test
    void getAirplanesReturnsSnapshot() {
        airline.addAirplane(passenger);
        List<Airplane> snapshot = airline.getAirplanes();

        airline.addAirplane(cargo);

        assertEquals(1, snapshot.size());
        assertEquals(2, airline.getAirplanes().size());
    }

    @Test
    void emptyAirlineHasZeroTotalPassengerCapacity() {
        assertEquals(0, airline.getTotalPassengerCapacity());
    }

    @Test
    void emptyAirlineHasZeroTotalCargoCapacity() {
        assertEquals(0.0, airline.getTotalCargoCapacity(), DELTA);
    }

    @Test
    void totalPassengerCapacitySumsAllAirplaneTypes() {
        airline.addAirplane(passenger);
        airline.addAirplane(cargo);
        airline.addAirplane(new CombiAirplane("Test Combi", 6000, 4.0, 150, 12.5));

        assertEquals(339, airline.getTotalPassengerCapacity());
    }

    @Test
    void totalCargoCapacitySumsAllAirplaneTypes() {
        airline.addAirplane(passenger);
        airline.addAirplane(cargo);
        airline.addAirplane(new CombiAirplane("Test Combi", 6000, 4.0, 150, 12.5));

        assertEquals(150.2, airline.getTotalCargoCapacity(), DELTA);
    }
}