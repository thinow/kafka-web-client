package kafkawebclient.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Component
public class KafkaConsumerFactory {

    public PollingSession consume(String bootstrapServers, Collection<String> topics) {
        final Properties properties = createProperties(bootstrapServers);
        final Consumer<Long, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(properties);
        consumer.subscribe(topics);

        return new PollingSession(consumer);
    }

    private static Properties createProperties(final String bootstrapServers) {
        final Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(GROUP_ID_CONFIG, "KafkaWebClient");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        props.put(AUTO_OFFSET_RESET_CONFIG, "latest");
//        props.put(PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "???");
        return props;
    }
}
