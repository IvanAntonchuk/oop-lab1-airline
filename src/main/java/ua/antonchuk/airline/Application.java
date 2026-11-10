package ua.antonchuk.airline;

import ua.antonchuk.airline.config.AppConfig;
import ua.antonchuk.airline.model.Airline;
import ua.antonchuk.airline.model.Airplane;
import ua.antonchuk.airline.service.AirlineService;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

public class Application {

    private final AirlineService service;
    private final AppConfig config;
    private final PrintStream out;

    public Application(AirlineService service, AppConfig config, PrintStream out) {
        if (service == null) {
            throw new IllegalArgumentException("Service must not be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("Config must not be null");
        }
        if (out == null) {
            throw new IllegalArgumentException("Output stream must not be null");
        }
        this.service = service;
        this.config = config;
        this.out = out;
    }

    public void run() {
        Airline airline = service.createAirline(config.getAirlineName());
        double min = config.getMinFuelConsumption();
        double max = config.getMaxFuelConsumption();

        out.println("Airline: " + airline.getName());
        printSection("Fleet", airline.getAirplanes());

        out.println();
        out.println("Total passenger capacity: " + airline.getTotalPassengerCapacity());
        out.println(String.format(Locale.ROOT, "Total cargo capacity: %.1f t",
                airline.getTotalCargoCapacity()));

        printSection("Airplanes sorted by flight range",
                airline.getAirplanesSortedByFlightRange());
        printSection(String.format(Locale.ROOT,
                        "Airplanes with fuel consumption from %.1f to %.1f t/h", min, max),
                airline.findAirplanesByFuelConsumption(min, max));
    }

    private void printSection(String title, List<Airplane> airplanes) {
        out.println();
        out.println(title + ":");
        if (airplanes.isEmpty()) {
            out.println("  none");
            return;
        }
        for (Airplane airplane : airplanes) {
            out.println("  " + airplane);
        }
    }
}