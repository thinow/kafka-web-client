from kafka import KafkaProducer as Producer


class KafkaProducer:
    def produce(self, server: str, topic: str, messages: tuple):
        producer = Producer(bootstrap_servers=server)
        try:
            for message in messages:
                future = producer.send(topic, bytes(message, 'utf-8'))
                future.get()  # wait until the message is sent
        finally:
            producer.close()
