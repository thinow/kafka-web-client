package kafkawebclient.kafka.error;

public class KafkaPollingTimedOut extends RuntimeException {
    public KafkaPollingTimedOut() {
        super("Request timed out");
    }
}
