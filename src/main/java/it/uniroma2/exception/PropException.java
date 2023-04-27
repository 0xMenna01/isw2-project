package it.uniroma2.exception;

public class PropException extends Exception {

    public PropException() {
        super();
    }

    public PropException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropException(String message) {
        super(message);
    }
}