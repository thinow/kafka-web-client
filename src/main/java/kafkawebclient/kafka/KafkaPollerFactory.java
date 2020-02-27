package kafkawebclient.kafka;

import kafkawebclient.model.FetchMethod;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Component
public class KafkaPollerFactory {

    public KafkaPoller createPoller(String bootstrapServers, Collection<String> topics, FetchMethod method) {
        final Properties properties = createProperties(bootstrapServers, method);
        final Consumer<?, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(properties);
        consumer.subscribe(topics);

        return new KafkaPoller(consumer);
    }

    private static Properties createProperties(final String bootstrapServers, FetchMethod method) {
        final Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(GROUP_ID_CONFIG, generateGroupID());
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // TODO extract serialization from Kafka Consumer
        properties.put(AUTO_OFFSET_RESET_CONFIG, method.asOffsetResetStrategy().name().toLowerCase());
        properties.put(ENABLE_AUTO_COMMIT_CONFIG, false);
        return properties;
    }

    private static String generateGroupID() {
        return "KafkaWebClient-" + UUID.randomUUID();
    }
}
