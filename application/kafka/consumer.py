from kafka import KafkaConsumer as Consumer


def find_timestamp(message):
    # (timestamp_type, value) = message.timestamp()
    # if timestamp_type == TIMESTAMP_NOT_AVAILABLE:
    #     return None
    # else:
    #     date = datetime.utcfromtimestamp(value / 1000)
    #     # TODO return a standard format
    #     return date.strftime('%c')
    return None


class KafkaConsumer:
    def __init__(self, cluster: str, topic: str, max_messages: int):
        self.cluster = cluster
        self.topic = topic
        self.max_messages = max_messages

    def consume(self, on_consumed_message, on_end) -> None:
        internal_consumer = Consumer(
            self.topic,
            bootstrap_servers=self.cluster, group_id='my_favorite_group'
        )
        try:
            for index in range(self.max_messages):
                response = internal_consumer.poll(timeout_ms=3000, max_records=1)
                for messages in response.values():
                    for message in messages:
                        on_consumed_message({
                            'index': index,
                            'offset': message.offset,
                            'timestamp': message.timestamp,
                            'value': str(message.value, 'utf-8')
                        })
            on_end()
        finally:
            internal_consumer.close()
