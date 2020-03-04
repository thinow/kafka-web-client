package kafkawebclient.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.util.Assert;

import java.util.Properties;

import static java.lang.String.format;

@Slf4j
public class KafkaProducer {

    public static void main(String... args) throws Exception {
        Assert.isTrue(args.length == 3, "command should contain 3 arguments : servers, topic, count");

        final String servers = args[0];
        final String topic = args[1];
        final long count = Long.parseLong(args[2]);

        produce(servers, topic, count);
    }

    public static void produce(String servers, String topic, long count) throws InterruptedException, java.util.concurrent.ExecutionException {
        try (Producer<Long, String> producer = createProducer(servers)) {
            final long time = System.currentTimeMillis();
            for (long index = 0; index < count; index++) {
                final ProducerRecord<Long, String> record = new ProducerRecord<>(topic, time + index,
                        format("{\"key\":\"value\",\"index\":%d}", index));

                final RecordMetadata metadata = producer.send(record).get();

                log.info("sent topic(name={}) record(key={} value={}) meta(partition={}, offset={}) time={}", topic,
                        record.key(), record.value(), metadata.partition(), metadata.offset(),
                        System.currentTimeMillis() - time);
            }
            producer.flush();
        }
    }

    private static Producer<Long, String> createProducer(final String servers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaWebClientProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new org.apache.kafka.clients.producer.KafkaProducer<>(props);
    }
}
