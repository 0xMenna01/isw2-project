package it.uniroma2.exception;

public class EnumException extends Exception {

    public EnumException() {
        super();
    }

    public EnumException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnumException(String message) {
        super(message);
    }

}
