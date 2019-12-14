import uuid

from confluent_kafka import Consumer as ConfluentConsumer


class KafkaConsumer:
    def __init__(self, cluster: str, topic: str, max_messages: int):
        self.cluster = cluster
        self.topic = topic
        self.max_messages = max_messages

    def consume(self, on_consumed_message, on_end) -> None:
        internal_consumer = ConfluentConsumer({
            'bootstrap.servers': self.cluster,
            'group.id': f'kafka-web-client/{uuid.uuid4()}',
            'auto.offset.reset': 'earliest'
        })
        try:
            internal_consumer.subscribe([self.topic])
            for index in range(self.max_messages):
                message = internal_consumer.poll()
                on_consumed_message({
                    'index': index,
                    'datetime': 'unknown',  # TODO get value from kafka
                    'value': message.value().decode('utf-8')
                })
            on_end()
        finally:
            internal_consumer.close()
