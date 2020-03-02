package kafkawebclient.kafka;

import kafkawebclient.kafka.error.KafkaPollingTimedOut;
import kafkawebclient.model.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Instant;
import java.util.Iterator;
import java.util.stream.LongStream;

@Slf4j
public class KafkaPoller implements AutoCloseable {

    private final Consumer<?, String> kafkaConsumer;
    private final KafkaPollerConfiguration configuration;

    private long maxMessages = 1L;

    public KafkaPoller(Consumer<?, String> kafkaConsumer, KafkaPollerConfiguration configuration) {
        this.kafkaConsumer = kafkaConsumer;
        this.configuration = configuration;
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
            timeOutIfDelayHasBeenReached(startTime);

            final ConsumerRecords<?, String> records = kafkaConsumer.poll(configuration.getPollTimeout());
            log.trace("fetched {} messages", records.count());

            final Iterator<? extends ConsumerRecord<?, String>> iterator = records.iterator();

            final long count = Math.min(records.count(), remaining);
            LongStream.range(0L, count)
                    .mapToObj(index -> iterator.next())
                    .map(this::transformRecordToKafkaMessage)
                    .forEach(callback);

            remaining -= count;
        }
    }

    private void timeOutIfDelayHasBeenReached(long startTime) {
        final long now = System.currentTimeMillis();
        final long delay = configuration.getSessionTimeout().toMillis();
        if (now > startTime + delay) {
            throw new KafkaPollingTimedOut();
        }
    }

    private KafkaMessage transformRecordToKafkaMessage(ConsumerRecord<?, String> record) {
        return new KafkaMessage(
                record.partition(),
                record.offset(),
                computeUserReadableTimestamp(record),
                record.value()
        );
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
