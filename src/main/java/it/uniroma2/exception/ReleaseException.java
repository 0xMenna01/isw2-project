package it.uniroma2.exception;

public class ReleaseException extends Exception {
    public ReleaseException() {
        super();
    }

    public ReleaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReleaseException(String message) {
        super(message);
    }
}
