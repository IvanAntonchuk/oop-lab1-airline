package ua.antonchuk.airline.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CargoAirplaneTest {

    private static final double DELTA = 1e-9;

    @Test
    void constructorStoresValidValues() {
        CargoAirplane airplane = new CargoAirplane("Boeing 747-8F", 8130, 10.5, 137.7);

        assertEquals("Boeing 747-8F", airplane.getModel());
        assertEquals(8130, airplane.getFlightRange());
        assertEquals(10.5, airplane.getFuelConsumption(), DELTA);
        assertEquals(137.7, airplane.getCargoCapacity(), DELTA);
    }

    @Test
    void passengerCapacityIsZero() {
        CargoAirplane airplane = new CargoAirplane("Boeing 747-8F", 8130, 10.5, 137.7);

        assertEquals(0, airplane.getPassengerCapacity());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY})
    void constructorRejectsInvalidCargoCapacity(double cargoCapacity) {
        assertThrows(IllegalArgumentException.class,
                () -> new CargoAirplane("Boeing 747-8F", 8130, 10.5, cargoCapacity));
    }

    @Test
    void constructorRejectsInvalidBaseArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> new CargoAirplane("Boeing 747-8F", 0, 10.5, 137.7));
    }

    @Test
    void toStringContainsAllCharacteristics() {
        CargoAirplane airplane = new CargoAirplane("Boeing 747-8F", 8130, 10.5, 137.7);

        assertEquals("CargoAirplane [model=Boeing 747-8F, range=8130 km, fuel=10.5 t/h, "
                + "passengers=0, cargo=137.7 t]", airplane.toString());
    }
}