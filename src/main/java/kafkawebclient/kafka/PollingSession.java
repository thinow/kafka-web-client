package kafkawebclient.kafka;

import kafkawebclient.model.ConsumedMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.stream.LongStream;

@Slf4j
public class PollingSession implements AutoCloseable {

    public static final Duration POLL_TIMEOUT = Duration.ofSeconds(1L);

    private final Consumer<Long, String> consumer;
    private long maxMessages = 1L;

    public PollingSession(Consumer<Long, String> consumer) {
        this.consumer = consumer;
    }

    public PollingSession poll(long maxMessages) {
        this.maxMessages = maxMessages;
        return this;
    }

    public void forEach(java.util.function.Consumer<ConsumedMessage> callback) {
        log.debug("start consuming...");
        long remaining = maxMessages;
        while (remaining > 0) {
            final ConsumerRecords<Long, String> records = consumer.poll(POLL_TIMEOUT);
            log.debug("consumed {} messages", records.count());

            final Iterator<ConsumerRecord<Long, String>> iterator = records.iterator();

            final long count = Math.min(records.count(), remaining);
            LongStream.range(0L, count)
                    .mapToObj(index -> iterator.next())
                    .map(record -> new ConsumedMessage(
                            record.offset(),
                            record.offset(),
                            Instant.ofEpochMilli(record.timestamp()).toString(),
                            record.value()
                    ))
                    .forEach(callback);

            remaining -= count;
        }
    }

    @Override
    public void close() {
        log.debug("closing consumer");
        consumer.close();
    }
}
