package ua.antonchuk.airline.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppConfigTest {

    private static final double DELTA = 1e-9;
    private static final Path DATA_FILE = Path.of("data/airplanes.txt");

    @TempDir
    Path tempDir;

    private Properties validProperties() {
        Properties properties = new Properties();
        properties.setProperty("airline.name", "Test Air");
        properties.setProperty("data.file", "data/airplanes.txt");
        properties.setProperty("search.fuel.min", "2.0");
        properties.setProperty("search.fuel.max", "5.0");
        return properties;
    }

    private Path writeFile(String content) throws IOException {
        Path file = tempDir.resolve("app.properties");
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }

    @Test
    void constructorStoresValues() {
        AppConfig config = new AppConfig("Test Air", DATA_FILE, 2.0, 5.0);

        assertEquals("Test Air", config.getAirlineName());
        assertEquals(DATA_FILE, config.getDataFile());
        assertEquals(2.0, config.getMinFuelConsumption(), DELTA);
        assertEquals(5.0, config.getMaxFuelConsumption(), DELTA);
    }

    @Test
    void constructorTrimsAirlineName() {
        AppConfig config = new AppConfig("  Test Air  ", DATA_FILE, 2.0, 5.0);

        assertEquals("Test Air", config.getAirlineName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void constructorRejectsBlankAirlineName(String name) {
        assertThrows(IllegalArgumentException.class,
                () -> new AppConfig(name, DATA_FILE, 2.0, 5.0));
    }

    @Test
    void constructorRejectsNullDataFile() {
        assertThrows(IllegalArgumentException.class,
                () -> new AppConfig("Test Air", null, 2.0, 5.0));
    }

    @ParameterizedTest
    @CsvSource({
            "-1.0, 5.0",
            "2.0, -1.0",
            "5.0, 2.0",
            "NaN, 5.0",
            "2.0, NaN",
            "2.0, Infinity"
    })
    void constructorRejectsInvalidFuelBounds(double min, double max) {
        assertThrows(IllegalArgumentException.class,
                () -> new AppConfig("Test Air", DATA_FILE, min, max));
    }

    @Test
    void constructorAcceptsEqualBounds() {
        AppConfig config = new AppConfig("Test Air", DATA_FILE, 3.0, 3.0);

        assertEquals(3.0, config.getMinFuelConsumption(), DELTA);
        assertEquals(3.0, config.getMaxFuelConsumption(), DELTA);
    }

    @Test
    void fromPropertiesReadsAllValues() {
        AppConfig config = AppConfig.fromProperties(validProperties());

        assertEquals("Test Air", config.getAirlineName());
        assertEquals(DATA_FILE, config.getDataFile());
        assertEquals(2.0, config.getMinFuelConsumption(), DELTA);
        assertEquals(5.0, config.getMaxFuelConsumption(), DELTA);
    }

    @Test
    void fromPropertiesTrimsValues() {
        Properties properties = validProperties();
        properties.setProperty("data.file", "  data/airplanes.txt  ");
        properties.setProperty("search.fuel.min", "  2.0  ");
        properties.setProperty("search.fuel.max", "  5.0  ");

        AppConfig config = AppConfig.fromProperties(properties);

        assertEquals(DATA_FILE, config.getDataFile());
        assertEquals(2.0, config.getMinFuelConsumption(), DELTA);
        assertEquals(5.0, config.getMaxFuelConsumption(), DELTA);
    }

    @ParameterizedTest
    @ValueSource(strings = {"airline.name", "data.file", "search.fuel.min", "search.fuel.max"})
    void fromPropertiesRejectsMissingProperty(String key) {
        Properties properties = validProperties();
        properties.remove(key);

        assertThrows(ConfigException.class, () -> AppConfig.fromProperties(properties));
    }

    @Test
    void fromPropertiesRejectsBlankValue() {
        Properties properties = validProperties();
        properties.setProperty("airline.name", "   ");

        assertThrows(ConfigException.class, () -> AppConfig.fromProperties(properties));
    }

    @Test
    void fromPropertiesRejectsNonNumericBound() {
        Properties properties = validProperties();
        properties.setProperty("search.fuel.min", "abc");

        assertThrows(ConfigException.class, () -> AppConfig.fromProperties(properties));
    }

    @Test
    void fromPropertiesRejectsMinGreaterThanMax() {
        Properties properties = validProperties();
        properties.setProperty("search.fuel.min", "5.0");
        properties.setProperty("search.fuel.max", "2.0");

        assertThrows(ConfigException.class, () -> AppConfig.fromProperties(properties));
    }

    @Test
    void loadReadsConfigurationFile() throws IOException {
        Path file = writeFile(String.join("\n",
                "airline.name=Test Air",
                "data.file=data/airplanes.txt",
                "search.fuel.min=2.0",
                "search.fuel.max=5.0"));

        AppConfig config = AppConfig.load(file);

        assertEquals("Test Air", config.getAirlineName());
        assertEquals(DATA_FILE, config.getDataFile());
        assertEquals(2.0, config.getMinFuelConsumption(), DELTA);
        assertEquals(5.0, config.getMaxFuelConsumption(), DELTA);
    }

    @Test
    void loadRejectsNullPath() {
        assertThrows(IllegalArgumentException.class, () -> AppConfig.load(null));
    }

    @Test
    void loadThrowsWhenFileDoesNotExist() {
        Path missing = tempDir.resolve("missing.properties");

        assertThrows(ConfigException.class, () -> AppConfig.load(missing));
    }

    @Test
    void loadThrowsOnMalformedEscapeSequence() throws IOException {
        Path file = writeFile("airline.name=\\uZZZZ");

        assertThrows(ConfigException.class, () -> AppConfig.load(file));
    }

    @Test
    void loadThrowsWhenPropertyIsMissing() throws IOException {
        Path file = writeFile("airline.name=Test Air");

        assertThrows(ConfigException.class, () -> AppConfig.load(file));
    }
}