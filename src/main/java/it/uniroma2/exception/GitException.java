package it.uniroma2.exception;

public class GitException extends Exception {

    public GitException() {
        super();
    }

    public GitException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitException(String message) {
        super(message);
    }
}
