package kafkawebclient.model;

import org.apache.kafka.clients.consumer.OffsetResetStrategy;

public enum FetchMethod {
    LATEST(OffsetResetStrategy.LATEST),
    OLDEST(OffsetResetStrategy.EARLIEST);

    private OffsetResetStrategy strategy;

    FetchMethod(OffsetResetStrategy strategy) {
        this.strategy = strategy;
    }

    public OffsetResetStrategy asOffsetResetStrategy() {
        return strategy;
    }
}
