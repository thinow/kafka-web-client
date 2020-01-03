import mock
from confluent_kafka import Consumer

from application.kafka.consumer import KafkaConsumer


@mock.patch.object(Consumer, 'close')
@mock.patch.object(Consumer, 'poll')
@mock.patch.object(Consumer, 'subscribe')
def test_consume(mock_subscribe, mock_poll, mock_close):
    consumer = KafkaConsumer('SERVER', 'TOPIC', 1)
    consumer.consume(
        on_consumed_message=lambda: print('on_consumed_message'),
        on_end=lambda: print('on_end')
    )
