package it.uniroma2.exception;

public class ProjectNameException extends Exception {
    public ProjectNameException() {
        super();
    }

    public ProjectNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectNameException(String message) {
        super(message);
    }
}
