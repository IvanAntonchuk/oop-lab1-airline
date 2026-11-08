package ua.antonchuk.airline.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.antonchuk.airline.model.Airline;
import ua.antonchuk.airline.model.Airplane;
import ua.antonchuk.airline.model.CargoAirplane;
import ua.antonchuk.airline.model.PassengerAirplane;
import ua.antonchuk.airline.repository.AirplaneRepository;
import ua.antonchuk.airline.repository.DataLoadException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirlineServiceTest {

    @Mock
    private AirplaneRepository repository;

    private AirlineService service;
    private PassengerAirplane passenger;
    private CargoAirplane cargo;

    @BeforeEach
    void setUp() {
        service = new AirlineService(repository);
        passenger = new PassengerAirplane("Boeing 737", 5400, 2.5, 189);
        cargo = new CargoAirplane("Boeing 747-8F", 8130, 10.5, 137.7);
    }

    @Test
    void constructorRejectsNullRepository() {
        assertThrows(IllegalArgumentException.class, () -> new AirlineService(null));
    }

    @Test
    void createAirlineLoadsAirplanesFromRepository() {
        when(repository.findAll()).thenReturn(List.of(passenger, cargo));

        Airline airline = service.createAirline("Test Air");

        assertEquals("Test Air", airline.getName());
        assertEquals(List.of(passenger, cargo), airline.getAirplanes());
        verify(repository).findAll();
    }

    @Test
    void createAirlineWithEmptyRepositoryHasNoAirplanes() {
        when(repository.findAll()).thenReturn(List.of());

        Airline airline = service.createAirline("Test Air");

        assertTrue(airline.getAirplanes().isEmpty());
    }

    @Test
    void createAirlineQueriesRepositoryOnEachCall() {
        when(repository.findAll()).thenReturn(List.of(passenger));

        Airline first = service.createAirline("First");
        Airline second = service.createAirline("Second");

        assertNotSame(first, second);
        verify(repository, times(2)).findAll();
    }

    @Test
    void createAirlinePropagatesLoadFailure() {
        when(repository.findAll())
                .thenThrow(new DataLoadException("Cannot read data", new RuntimeException()));

        assertThrows(DataLoadException.class, () -> service.createAirline("Test Air"));
    }

    @Test
    void createAirlineRejectsBlankNameWithoutLoadingData() {
        assertThrows(IllegalArgumentException.class, () -> service.createAirline(" "));

        verifyNoInteractions(repository);
    }

    @Test
    void createAirlineRejectsNullAirplaneFromRepository() {
        List<Airplane> withNull = Arrays.asList(passenger, null);
        when(repository.findAll()).thenReturn(withNull);

        assertThrows(IllegalArgumentException.class, () -> service.createAirline("Test Air"));
    }
}