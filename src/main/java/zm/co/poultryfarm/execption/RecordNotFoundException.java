package zm.co.poultryfarm.exception;

// Thrown when a flock or record ID does not exist (reported as HTTP 404)
public class RecordNotFoundException extends InvalidRecordException {

    public RecordNotFoundException(String message) {
        super(message);
    }
}