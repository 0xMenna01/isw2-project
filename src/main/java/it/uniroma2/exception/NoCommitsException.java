package it.uniroma2.exception;

public class NoCommitsException extends Exception{

    public NoCommitsException() {
        super();
    }

    public NoCommitsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoCommitsException(String message) {
        super(message);
    }
}
