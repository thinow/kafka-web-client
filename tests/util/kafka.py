from confluent_kafka import Producer


class KafkaProducer:
    def produce(self, server: str, topic: str, messages: tuple):
        producer = Producer({'bootstrap.servers': server})
        for message in messages:
            producer.produce(topic, message)
        producer.flush()  # wait all messages to be delivered
