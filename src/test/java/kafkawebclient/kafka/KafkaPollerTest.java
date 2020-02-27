package kafkawebclient.kafka;

import kafkawebclient.model.KafkaMessage;
import lombok.Builder;
import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.record.TimestampType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.apache.kafka.clients.consumer.OffsetResetStrategy.EARLIEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KafkaPollerTest {

    public static final Topic TOPIC = Topic.builder()
            .name("topic")
            .beginningOffset(0L)
            .build();

    public static final long TIMESTAMP = 1582666339753L;

    private final ArgumentCaptor<KafkaMessage> captor = ArgumentCaptor.forClass(KafkaMessage.class);
    private Consumer<KafkaMessage> callback = mock(Consumer.class);

    @Test
    void shouldFetchOneSingleMessage() {
        // given
        final MockConsumer<String, String> kafkaConsumer = createMockKafkaConsumer(EARLIEST, List.of(TOPIC));
        kafkaConsumer.addRecord(createRecord(TOPIC, 0, "any-value"));

        // when
        new KafkaPoller(kafkaConsumer).poll(1).forEach(callback);

        // then
        verify(callback, times(1)).accept(captor.capture());

        final KafkaMessage message = captor.getValue();
        assertThat(message.getValue()).isEqualTo("any-value");
    }

    @Test
    void shouldFetchMultipleMessages() {
        // given
        final MockConsumer<String, String> kafkaConsumer = createMockKafkaConsumer(EARLIEST, List.of(TOPIC));

        int offset = 0;
        kafkaConsumer.addRecord(createRecord(TOPIC, offset++, "foo"));
        kafkaConsumer.addRecord(createRecord(TOPIC, offset++, "bar"));
        kafkaConsumer.addRecord(createRecord(TOPIC, offset++, "baz"));

        // when
        new KafkaPoller(kafkaConsumer).poll(3).forEach(callback);

        // then
        verify(callback, times(3)).accept(captor.capture());

        final List<KafkaMessage> messages = captor.getAllValues();
        assertThat(messages).extracting("value").containsOnly("foo", "bar", "baz");
    }

    @Test
    void shouldFetchExpectedNumberOfMessages() {
        // given
        final MockConsumer<String, String> kafkaConsumer = createMockKafkaConsumer(EARLIEST, List.of(TOPIC));

        IntStream.range(0, 10)
                .forEach(offset -> kafkaConsumer.addRecord(createRecord(TOPIC, offset, "anything")));

        // when
        new KafkaPoller(kafkaConsumer).poll(5).forEach(callback);

        // then
        verify(callback, times(5)).accept(any(KafkaMessage.class));
    }

    @Test
    void shouldConvertRecordToConsumedMessageObject() {
        // given
        final int expectedPartition = 123;
        final long expectedOffset = 456L;

        final Topic topic = Topic.builder()
                .name("topic")
                .partition(expectedPartition)
                .beginningOffset(expectedOffset)
                .build();
        final MockConsumer<String, String> kafkaConsumer = createMockKafkaConsumer(EARLIEST, List.of(topic));

        kafkaConsumer.addRecord(createRecord(topic, expectedOffset, "string-value"));

        // when
        new KafkaPoller(kafkaConsumer).poll(1).forEach(callback);

        // then
        verify(callback).accept(captor.capture());

        final KafkaMessage message = captor.getValue();
        assertThat(message.getIndex()).isEqualTo(0L);
        assertThat(message.getPartition()).isEqualTo(expectedPartition);
        assertThat(message.getOffset()).isEqualTo(expectedOffset);
        assertThat(message.getTimestamp()).isEqualTo("2020-02-25T21:32:19.753Z");
        assertThat(message.getValue()).isEqualTo("string-value");
    }

    @Data
    @Builder
    public static class Topic {
        private String name;
        private int partition;
        private Long beginningOffset;
    }

    private MockConsumer<String, String> createMockKafkaConsumer(OffsetResetStrategy strategy, Collection<Topic> topics) {
        final MockConsumer<String, String> kafkaConsumer = new MockConsumer<>(strategy);

        for (Topic topic : topics) {
            final TopicPartition topicPartition = new TopicPartition(topic.getName(), topic.getPartition());
            kafkaConsumer.assign(List.of(topicPartition));
            kafkaConsumer.updateBeginningOffsets(Map.of(topicPartition, topic.getBeginningOffset()));
        }

        return kafkaConsumer;
    }

    private ConsumerRecord<String, String> createRecord(Topic topic, long offset, String value) {
        final long checksum = 0;
        final int serializedKeySize = 0;
        final int serializedValueSize = 0;
        final String key = "any-key";

        return new ConsumerRecord<>(
                topic.getName(),
                topic.getPartition(),
                offset,
                TIMESTAMP,
                TimestampType.CREATE_TIME,
                checksum,
                serializedKeySize,
                serializedValueSize,
                key,
                value
        );
    }
}