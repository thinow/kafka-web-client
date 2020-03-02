package kafkawebclient.kafka;

import kafkawebclient.kafka.error.KafkaPollingTimedOut;
import kafkawebclient.model.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.stream.LongStream;

@Slf4j
public class KafkaPoller implements AutoCloseable {

    public static final Duration OVERALL_TIMEOUT = Duration.ofSeconds(5);
    public static final Duration POLL_TIMEOUT = Duration.ofSeconds(1);

    private final Consumer<?, String> kafkaConsumer;
    private long maxMessages = 1L;

    public KafkaPoller(Consumer<?, String> kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }

    public KafkaPoller poll(long maxMessages) {
        this.maxMessages = maxMessages;
        return this;
    }

    public void forEach(java.util.function.Consumer<KafkaMessage> callback) {
        log.debug("start polling...");
        final long startTime = System.currentTimeMillis();

        long remaining = maxMessages;
        while (remaining > 0) {
            timeOutIfDelayHasBeenReached(startTime + OVERALL_TIMEOUT.toMillis());

            final ConsumerRecords<?, String> records = kafkaConsumer.poll(POLL_TIMEOUT);
            log.trace("fetched {} messages", records.count());

            final Iterator<? extends ConsumerRecord<?, String>> iterator = records.iterator();

            final long count = Math.min(records.count(), remaining);
            LongStream.range(0L, count)
                    .mapToObj(index -> iterator.next())
                    .map(record -> new KafkaMessage(
                            record.partition(),
                            record.offset(),
                            computeUserReadableTimestamp(record),
                            record.value()
                    ))
                    .forEach(callback);

            remaining -= count;
        }
    }

    private void timeOutIfDelayHasBeenReached(final long maxTime) {
        final long now = System.currentTimeMillis();
        if (now > maxTime) {
            throw new KafkaPollingTimedOut();
        }
    }

    private String computeUserReadableTimestamp(ConsumerRecord<?, ?> record) {
        // TODO evaluate the timestamp type to compute a value
        final long timestamp = record.timestamp();
        return Instant.ofEpochMilli(timestamp).toString();
    }

    @Override
    public void close() {
        log.debug("closing poller");
        kafkaConsumer.close();
    }
}
