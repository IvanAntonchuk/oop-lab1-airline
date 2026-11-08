package ua.antonchuk.airline.repository;

public class DataLoadException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}