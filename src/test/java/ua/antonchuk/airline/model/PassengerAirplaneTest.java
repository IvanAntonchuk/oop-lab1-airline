package ua.antonchuk.airline.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PassengerAirplaneTest {

    private static final double DELTA = 1e-9;

    @Test
    void constructorStoresValidValues() {
        PassengerAirplane airplane = new PassengerAirplane("Boeing 737", 5400, 2.5, 189);

        assertEquals("Boeing 737", airplane.getModel());
        assertEquals(5400, airplane.getFlightRange());
        assertEquals(2.5, airplane.getFuelConsumption(), DELTA);
        assertEquals(189, airplane.getPassengerCapacity());
    }

    @Test
    void cargoCapacityIsZero() {
        PassengerAirplane airplane = new PassengerAirplane("Boeing 737", 5400, 2.5, 189);

        assertEquals(0.0, airplane.getCargoCapacity(), DELTA);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -189})
    void constructorRejectsNonPositivePassengerCapacity(int passengerCapacity) {
        assertThrows(IllegalArgumentException.class,
                () -> new PassengerAirplane("Boeing 737", 5400, 2.5, passengerCapacity));
    }

    @Test
    void constructorRejectsInvalidBaseArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> new PassengerAirplane(" ", 5400, 2.5, 189));
    }

    @Test
    void toStringContainsAllCharacteristics() {
        PassengerAirplane airplane = new PassengerAirplane("Boeing 737", 5400, 2.5, 189);

        assertEquals("PassengerAirplane [model=Boeing 737, range=5400 km, fuel=2.5 t/h, "
                + "passengers=189, cargo=0.0 t]", airplane.toString());
    }
}