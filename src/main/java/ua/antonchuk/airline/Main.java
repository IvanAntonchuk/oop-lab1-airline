package ua.antonchuk.airline;

import ua.antonchuk.airline.config.AppConfig;
import ua.antonchuk.airline.config.ConfigException;
import ua.antonchuk.airline.repository.AirplaneRepository;
import ua.antonchuk.airline.repository.DataLoadException;
import ua.antonchuk.airline.repository.FileAirplaneRepository;
import ua.antonchuk.airline.service.AirlineService;

import java.io.PrintStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class Main {

    private static final Path DEFAULT_CONFIG_PATH = Path.of("config", "app.properties");

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
        try {
            Path configPath = args.length > 0 ? Path.of(args[0]) : DEFAULT_CONFIG_PATH;
            AppConfig config = AppConfig.load(configPath);
            AirplaneRepository repository = new FileAirplaneRepository(config.getDataFile());
            new Application(new AirlineService(repository), config, out).run();
            return 0;
        } catch (InvalidPathException | ConfigException | DataLoadException e) {
            err.println("Error: " + e.getMessage());
            return 1;
        }
    }
}