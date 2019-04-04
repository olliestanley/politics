package pw.ollie.politics.data;

public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException(String reason) {
        super(reason);
    }

    public InvalidConfigurationException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigurationException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
