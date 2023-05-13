package it.uniroma2.exception;

public class SmoteNumPositiveException extends Exception {
    public SmoteNumPositiveException() {
        super();
    }

    public SmoteNumPositiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmoteNumPositiveException(String message) {
        super(message);
    }
}
