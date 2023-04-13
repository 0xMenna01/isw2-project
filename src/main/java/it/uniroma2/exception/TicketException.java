package it.uniroma2.exception;

public class TicketException extends Exception {
    public TicketException() {
        super();
    }

    public TicketException(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketException(String message) {
        super(message);
    }
}
