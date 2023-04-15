package it.uniroma2.exception;

public class ParallelColdStartException extends Exception {

    public ParallelColdStartException() {
        super();
    }

    public ParallelColdStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParallelColdStartException(String message) {
        super(message);
    }

}
