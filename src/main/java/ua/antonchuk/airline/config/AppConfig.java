package ua.antonchuk.airline.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class AppConfig {

    private static final String AIRLINE_NAME_KEY = "airline.name";
    private static final String DATA_FILE_KEY = "data.file";
    private static final String MIN_FUEL_KEY = "search.fuel.min";
    private static final String MAX_FUEL_KEY = "search.fuel.max";

    private final String airlineName;
    private final Path dataFile;
    private final double minFuelConsumption;
    private final double maxFuelConsumption;

    public AppConfig(String airlineName, Path dataFile,
                     double minFuelConsumption, double maxFuelConsumption) {
        if (airlineName == null || airlineName.isBlank()) {
            throw new IllegalArgumentException("Airline name must not be null or blank");
        }
        if (dataFile == null) {
            throw new IllegalArgumentException("Data file must not be null");
        }
        if (!isValidBound(minFuelConsumption) || !isValidBound(maxFuelConsumption)) {
            throw new IllegalArgumentException(
                    "Fuel consumption bounds must be non-negative finite numbers: "
                            + minFuelConsumption + ", " + maxFuelConsumption);
        }
        if (minFuelConsumption > maxFuelConsumption) {
            throw new IllegalArgumentException(
                    "Minimum must not be greater than maximum: "
                            + minFuelConsumption + " > " + maxFuelConsumption);
        }
        this.airlineName = airlineName.trim();
        this.dataFile = dataFile;
        this.minFuelConsumption = minFuelConsumption;
        this.maxFuelConsumption = maxFuelConsumption;
    }

    public static AppConfig load(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null");
        }
        Properties properties = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException | IllegalArgumentException e) {
            throw new ConfigException("Cannot read configuration from " + path, e);
        }
        return fromProperties(properties);
    }

    public static AppConfig fromProperties(Properties properties) {
        try {
            return new AppConfig(
                    required(properties, AIRLINE_NAME_KEY),
                    Path.of(required(properties, DATA_FILE_KEY)),
                    Double.parseDouble(required(properties, MIN_FUEL_KEY)),
                    Double.parseDouble(required(properties, MAX_FUEL_KEY)));
        } catch (IllegalArgumentException e) {
            throw new ConfigException("Invalid configuration: " + e.getMessage(), e);
        }
    }

    public String getAirlineName() {
        return airlineName;
    }

    public Path getDataFile() {
        return dataFile;
    }

    public double getMinFuelConsumption() {
        return minFuelConsumption;
    }

    public double getMaxFuelConsumption() {
        return maxFuelConsumption;
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing property: " + key);
        }
        return value.trim();
    }

    private static boolean isValidBound(double value) {
        return value >= 0 && !Double.isInfinite(value);
    }
}