package ua.antonchuk.airline.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ua.antonchuk.airline.model.Airplane;
import ua.antonchuk.airline.model.CargoAirplane;
import ua.antonchuk.airline.model.CombiAirplane;
import ua.antonchuk.airline.model.PassengerAirplane;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileAirplaneRepositoryTest {

    private static final double DELTA = 1e-9;

    @TempDir
    Path tempDir;

    private Path writeFile(String content) throws IOException {
        Path file = tempDir.resolve("airplanes.txt");
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }

    @Test
    void constructorRejectsNullPath() {
        assertThrows(IllegalArgumentException.class,
                () -> new FileAirplaneRepository(null));
    }

    @Test
    void findAllLoadsAllAirplaneTypes() throws IOException {
        Path file = writeFile(String.join("\n",
                "PASSENGER,Boeing 737,5400,2.5,189",
                "CARGO,Boeing 747-8F,8130,10.5,137.7",
                "COMBI,Test Combi,6000,4.0,150,12.5"));

        List<Airplane> airplanes = new FileAirplaneRepository(file).findAll();

        assertEquals(3, airplanes.size());

        Airplane passenger = airplanes.get(0);
        assertEquals(PassengerAirplane.class, passenger.getClass());
        assertEquals("Boeing 737", passenger.getModel());
        assertEquals(5400, passenger.getFlightRange());
        assertEquals(2.5, passenger.getFuelConsumption(), DELTA);
        assertEquals(189, passenger.getPassengerCapacity());

        Airplane cargo = airplanes.get(1);
        assertEquals(CargoAirplane.class, cargo.getClass());
        assertEquals("Boeing 747-8F", cargo.getModel());
        assertEquals(8130, cargo.getFlightRange());
        assertEquals(10.5, cargo.getFuelConsumption(), DELTA);
        assertEquals(137.7, cargo.getCargoCapacity(), DELTA);

        Airplane combi = airplanes.get(2);
        assertEquals(CombiAirplane.class, combi.getClass());
        assertEquals("Test Combi", combi.getModel());
        assertEquals(6000, combi.getFlightRange());
        assertEquals(4.0, combi.getFuelConsumption(), DELTA);
        assertEquals(150, combi.getPassengerCapacity());
        assertEquals(12.5, combi.getCargoCapacity(), DELTA);
    }

    @Test
    void findAllSkipsBlankLinesAndComments() throws IOException {
        Path file = writeFile(String.join("\n",
                "# Header comment",
                "",
                "PASSENGER,A,1000,1.0,10",
                "   ",
                "# another comment",
                "CARGO,B,2000,2.0,20.0"));

        List<Airplane> airplanes = new FileAirplaneRepository(file).findAll();

        assertEquals(2, airplanes.size());
        assertEquals("A", airplanes.get(0).getModel());
        assertEquals("B", airplanes.get(1).getModel());
    }

    @Test
    void findAllTrimsFieldsAndIgnoresTypeCase() throws IOException {
        Path file = writeFile("  passenger , Boeing 737 , 5400 , 2.5 , 189  ");

        List<Airplane> airplanes = new FileAirplaneRepository(file).findAll();

        assertEquals(1, airplanes.size());
        assertEquals(PassengerAirplane.class, airplanes.get(0).getClass());
        assertEquals("Boeing 737", airplanes.get(0).getModel());
        assertEquals(189, airplanes.get(0).getPassengerCapacity());
    }

    @Test
    void findAllReturnsEmptyListForEmptyFile() throws IOException {
        Path file = writeFile("");

        assertTrue(new FileAirplaneRepository(file).findAll().isEmpty());
    }

    @Test
    void findAllReturnsUnmodifiableList() throws IOException {
        Path file = writeFile("PASSENGER,A,1000,1.0,10");
        List<Airplane> airplanes = new FileAirplaneRepository(file).findAll();

        assertThrows(UnsupportedOperationException.class, airplanes::clear);
    }

    @Test
    void findAllReadsNonLatinModelNames() throws IOException {
        Path file = writeFile("PASSENGER,Ан-148,3500,1.6,75");

        List<Airplane> airplanes = new FileAirplaneRepository(file).findAll();

        assertEquals("Ан-148", airplanes.get(0).getModel());
    }

    @Test
    void findAllThrowsWhenFileDoesNotExist() {
        Path missing = tempDir.resolve("missing.txt");

        assertThrows(DataLoadException.class,
                () -> new FileAirplaneRepository(missing).findAll());
    }

    @Test
    void findAllThrowsWhenPathIsDirectory() {
        assertThrows(DataLoadException.class,
                () -> new FileAirplaneRepository(tempDir).findAll());
    }

    @Test
    void findAllThrowsOnInvalidUtf8() throws IOException {
        Path file = tempDir.resolve("broken.txt");
        Files.write(file, new byte[] {(byte) 0xC3, (byte) 0x28});

        assertThrows(DataLoadException.class,
                () -> new FileAirplaneRepository(file).findAll());
    }

    @Test
    void findAllThrowsOnUnknownType() throws IOException {
        Path file = writeFile("HELICOPTER,A,1000,1.0,10");

        DataLoadException exception = assertThrows(DataLoadException.class,
                () -> new FileAirplaneRepository(file).findAll());

        assertTrue(exception.getMessage().contains("HELICOPTER"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "PASSENGER,Boeing 737,5400,2.5",
            "PASSENGER,Boeing 737,5400,2.5,189,1",
            "CARGO,Boeing 747-8F,8130,10.5",
            "COMBI,Test Combi,6000,4.0,150"
    })
    void findAllThrowsOnWrongFieldCount(String line) throws IOException {
        Path file = writeFile(line);

        assertThrows(DataLoadException.class,
                () -> new FileAirplaneRepository(file).findAll());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "PASSENGER,Boeing 737,abc,2.5,189",
            "PASSENGER,Boeing 737,5400,abc,189",
            "PASSENGER,Boeing 737,5400,2.5,many",
            "PASSENGER,Boeing 737,5400.5,2.5,189",
            "CARGO,Boeing 747-8F,8130,10.5,heavy",
            "COMBI,Test Combi,6000,4.0,many,12.5",
            "COMBI,Test Combi,6000,4.0,150,heavy"
    })
    void findAllThrowsOnNonNumericValues(String line) throws IOException {
        Path file = writeFile(line);

        assertThrows(DataLoadException.class,
                () -> new FileAirplaneRepository(file).findAll());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "PASSENGER,,5400,2.5,189",
            "CARGO,Boeing 747-8F,-1,10.5,137.7",
            "PASSENGER,Boeing 737,5400,2.5,0",
            "COMBI,Test Combi,6000,4.0,150,0"
    })
    void findAllThrowsOnInvalidDomainValues(String line) throws IOException {
        Path file = writeFile(line);

        assertThrows(DataLoadException.class,
                () -> new FileAirplaneRepository(file).findAll());
    }

    @Test
    void findAllReportsLineNumberOfInvalidLine() throws IOException {
        Path file = writeFile(String.join("\n",
                "# header",
                "PASSENGER,A,1000,1.0,10",
                "",
                "CARGO,B,oops,2.0,20.0"));

        DataLoadException exception = assertThrows(DataLoadException.class,
                () -> new FileAirplaneRepository(file).findAll());

        assertTrue(exception.getMessage().contains("line 4"));
    }
}