package ua.antonchuk.airline.repository;

import ua.antonchuk.airline.model.Airplane;
import ua.antonchuk.airline.model.CargoAirplane;
import ua.antonchuk.airline.model.CombiAirplane;
import ua.antonchuk.airline.model.PassengerAirplane;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileAirplaneRepository implements AirplaneRepository {

    private static final String SEPARATOR = ",";
    private static final String COMMENT_PREFIX = "#";

    private final Path path;

    public FileAirplaneRepository(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null");
        }
        this.path = path;
    }

    @Override
    public List<Airplane> findAll() {
        List<String> lines = readLines();
        List<Airplane> airplanes = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith(COMMENT_PREFIX)) {
                continue;
            }
            try {
                airplanes.add(parseLine(line));
            } catch (IllegalArgumentException e) {
                throw new DataLoadException(
                        "Invalid data at line " + (i + 1) + ": " + e.getMessage(), e);
            }
        }
        return List.copyOf(airplanes);
    }

    private List<String> readLines() {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataLoadException("Cannot read airplane data from " + path, e);
        }
    }

    private Airplane parseLine(String line) {
        String[] parts = line.split(SEPARATOR, -1);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        AirplaneType type = parseType(parts[0]);
        return switch (type) {
            case PASSENGER -> {
                requireFieldCount(parts, 5);
                yield new PassengerAirplane(parts[1], Integer.parseInt(parts[2]),
                        Double.parseDouble(parts[3]), Integer.parseInt(parts[4]));
            }
            case CARGO -> {
                requireFieldCount(parts, 5);
                yield new CargoAirplane(parts[1], Integer.parseInt(parts[2]),
                        Double.parseDouble(parts[3]), Double.parseDouble(parts[4]));
            }
            case COMBI -> {
                requireFieldCount(parts, 6);
                yield new CombiAirplane(parts[1], Integer.parseInt(parts[2]),
                        Double.parseDouble(parts[3]), Integer.parseInt(parts[4]),
                        Double.parseDouble(parts[5]));
            }
        };
    }

    private AirplaneType parseType(String token) {
        try {
            return AirplaneType.valueOf(token.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown airplane type: '" + token + "'");
        }
    }

    private void requireFieldCount(String[] parts, int expected) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("Expected " + expected + " fields for "
                    + parts[0] + " but found " + parts.length);
        }
    }
}