package ua.antonchuk.airline.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CombiAirplaneTest {

    private static final double DELTA = 1e-9;

    @Test
    void constructorStoresValidValues() {
        CombiAirplane airplane = new CombiAirplane("Test Combi", 6000, 4.0, 150, 12.5);

        assertEquals("Test Combi", airplane.getModel());
        assertEquals(6000, airplane.getFlightRange());
        assertEquals(4.0, airplane.getFuelConsumption(), DELTA);
        assertEquals(150, airplane.getPassengerCapacity());
        assertEquals(12.5, airplane.getCargoCapacity(), DELTA);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY})
    void constructorRejectsInvalidCargoCapacity(double cargoCapacity) {
        assertThrows(IllegalArgumentException.class,
                () -> new CombiAirplane("Test Combi", 6000, 4.0, 150, cargoCapacity));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void constructorRejectsNonPositivePassengerCapacity(int passengerCapacity) {
        assertThrows(IllegalArgumentException.class,
                () -> new CombiAirplane("Test Combi", 6000, 4.0, passengerCapacity, 12.5));
    }

    @Test
    void constructorRejectsInvalidBaseArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> new CombiAirplane("", 6000, 4.0, 150, 12.5));
    }

    @Test
    void capacitiesAreReturnedThroughBaseType() {
        Airplane airplane = new CombiAirplane("Test Combi", 6000, 4.0, 150, 12.5);

        assertEquals(150, airplane.getPassengerCapacity());
        assertEquals(12.5, airplane.getCargoCapacity(), DELTA);
    }

    @Test
    void toStringContainsAllCharacteristics() {
        CombiAirplane airplane = new CombiAirplane("Test Combi", 6000, 4.0, 150, 12.5);

        assertEquals("CombiAirplane [model=Test Combi, range=6000 km, fuel=4.0 t/h, "
                + "passengers=150, cargo=12.5 t]", airplane.toString());
    }
}