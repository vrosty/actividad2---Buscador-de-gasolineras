package modelo;

public class CSVLecturaException extends RuntimeException {
    public CSVLecturaException(String message) {
        super(message);
    }

    public CSVLecturaException(String message, Throwable cause) {
        super(message, cause);
    }
}
