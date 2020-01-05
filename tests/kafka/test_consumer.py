# import mock
# from kafka import KafkaConsumer as Consumer
#
# from application.kafka.consumer import KafkaConsumer
#
#
# @mock.patch.object(Consumer, 'close')
# @mock.patch.object(Consumer, 'poll')
# @mock.patch.object(Consumer, 'subscribe')
# def test_consume(mock_subscribe, mock_poll, mock_close):
#     mock_subscribe.return_value = 42
#
#     consumer = KafkaConsumer('SERVER', 'TOPIC', 1)
#     result = consumer.consume(
#         on_consumed_message=lambda: print('on_consumed_message'),
#         on_end=lambda: print('on_end')
#     )
#
#     assert result == 42
