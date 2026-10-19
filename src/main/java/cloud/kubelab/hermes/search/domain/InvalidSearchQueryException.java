package cloud.kubelab.hermes.search.domain;

public class InvalidSearchQueryException extends RuntimeException {
    public InvalidSearchQueryException(String message) {
        super(message);
    }

    public InvalidSearchQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
