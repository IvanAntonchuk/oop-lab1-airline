package ua.antonchuk.airline.config;

public class ConfigException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}