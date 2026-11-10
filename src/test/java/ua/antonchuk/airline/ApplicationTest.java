package ua.antonchuk.airline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.antonchuk.airline.config.AppConfig;
import ua.antonchuk.airline.model.CargoAirplane;
import ua.antonchuk.airline.model.CombiAirplane;
import ua.antonchuk.airline.model.PassengerAirplane;
import ua.antonchuk.airline.repository.AirplaneRepository;
import ua.antonchuk.airline.repository.DataLoadException;
import ua.antonchuk.airline.service.AirlineService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {

    private static final String FUEL_SECTION =
            "Airplanes with fuel consumption from 2.0 to 5.0 t/h:";

    @Mock
    private AirplaneRepository repository;

    private ByteArrayOutputStream buffer;
    private PrintStream out;
    private AirlineService service;
    private AppConfig config;
    private PassengerAirplane passenger;
    private CargoAirplane cargo;
    private CombiAirplane combi;

    @BeforeEach
    void setUp() {
        buffer = new ByteArrayOutputStream();
        out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        service = new AirlineService(repository);
        config = new AppConfig("Test Air", Path.of("unused.txt"), 2.0, 5.0);
        passenger = new PassengerAirplane("Boeing 737", 5400, 2.5, 189);
        cargo = new CargoAirplane("Boeing 747-8F", 8130, 10.5, 137.7);
        combi = new CombiAirplane("Test Combi", 6000, 4.0, 150, 12.5);
    }

    private List<String> outputLines() {
        return buffer.toString(StandardCharsets.UTF_8).lines().toList();
    }

    @Test
    void constructorRejectsNullService() {
        assertThrows(IllegalArgumentException.class,
                () -> new Application(null, config, out));
    }

    @Test
    void constructorRejectsNullConfig() {
        assertThrows(IllegalArgumentException.class,
                () -> new Application(service, null, out));
    }

    @Test
    void constructorRejectsNullOutput() {
        assertThrows(IllegalArgumentException.class,
                () -> new Application(service, config, null));
    }

    @Test
    void runPrintsFullReport() {
        when(repository.findAll()).thenReturn(List.of(cargo, passenger, combi));

        new Application(service, config, out).run();

        assertEquals(List.of(
                "Airline: Test Air",
                "",
                "Fleet:",
                "  " + cargo,
                "  " + passenger,
                "  " + combi,
                "",
                "Total passenger capacity: 339",
                "Total cargo capacity: 150.2 t",
                "",
                "Airplanes sorted by flight range:",
                "  " + passenger,
                "  " + combi,
                "  " + cargo,
                "",
                FUEL_SECTION,
                "  " + passenger,
                "  " + combi), outputLines());
    }

    @Test
    void runPrintsNoneForEmptyFleet() {
        when(repository.findAll()).thenReturn(List.of());

        new Application(service, config, out).run();

        assertEquals(List.of(
                "Airline: Test Air",
                "",
                "Fleet:",
                "  none",
                "",
                "Total passenger capacity: 0",
                "Total cargo capacity: 0.0 t",
                "",
                "Airplanes sorted by flight range:",
                "  none",
                "",
                FUEL_SECTION,
                "  none"), outputLines());
    }

    @Test
    void runPrintsNoneWhenNoAirplaneMatchesSearch() {
        when(repository.findAll()).thenReturn(List.of(cargo));

        new Application(service, config, out).run();

        List<String> lines = outputLines();
        assertEquals(FUEL_SECTION, lines.get(lines.size() - 2));
        assertEquals("  none", lines.get(lines.size() - 1));
    }

    @Test
    void runPropagatesLoadFailure() {
        when(repository.findAll())
                .thenThrow(new DataLoadException("Cannot read data", new RuntimeException()));
        Application application = new Application(service, config, out);

        assertThrows(DataLoadException.class, application::run);
    }
}