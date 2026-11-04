package zm.co.poultryfarm.exception;

// Custom exception for invalid farm records (reported as HTTP 400)
public class InvalidRecordException extends RuntimeException {

    public InvalidRecordException(String message) {
        super(message);
    }
}
