package ua.antonchuk.airline.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AirplaneTest {

    private static final double DELTA = 1e-9;

    private static class StubAirplane extends Airplane {

        StubAirplane(String model, int flightRange, double fuelConsumption) {
            super(model, flightRange, fuelConsumption);
        }

        @Override
        public int getPassengerCapacity() {
            return 100;
        }

        @Override
        public double getCargoCapacity() {
            return 10.0;
        }
    }

    @Test
    void constructorStoresValidValues() {
        Airplane airplane = new StubAirplane("Test 100", 5000, 2.5);

        assertEquals("Test 100", airplane.getModel());
        assertEquals(5000, airplane.getFlightRange());
        assertEquals(2.5, airplane.getFuelConsumption(), DELTA);
    }

    @Test
    void constructorTrimsModel() {
        Airplane airplane = new StubAirplane("  Test 100  ", 5000, 2.5);

        assertEquals("Test 100", airplane.getModel());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void constructorRejectsBlankModel(String model) {
        assertThrows(IllegalArgumentException.class,
                () -> new StubAirplane(model, 5000, 2.5));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5000})
    void constructorRejectsNonPositiveFlightRange(int flightRange) {
        assertThrows(IllegalArgumentException.class,
                () -> new StubAirplane("Test 100", flightRange, 2.5));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -2.5, Double.NaN, Double.POSITIVE_INFINITY})
    void constructorRejectsInvalidFuelConsumption(double fuelConsumption) {
        assertThrows(IllegalArgumentException.class,
                () -> new StubAirplane("Test 100", 5000, fuelConsumption));
    }

    @Test
    void toStringContainsAllCharacteristics() {
        Airplane airplane = new StubAirplane("Test 100", 5000, 2.5);

        assertEquals("StubAirplane [model=Test 100, range=5000 km, fuel=2.5 t/h, "
                + "passengers=100, cargo=10.0 t]", airplane.toString());
    }
}