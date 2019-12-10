import json

from confluent_kafka import Producer

BROKERS = ('localhost:9092',)

some_data_source = (
    {'id': 1, 'text': 'foo'},
    {'id': 2, 'text': 'bar'}
)

producer = Producer({'bootstrap.servers': ','.join(BROKERS)})


def delivery_report(error, msg):
    """ Called once for each message produced to indicate delivery result.
        Triggered by poll() or flush(). """
    if error is not None:
        print(f'Message delivery failed: {error}')
    else:
        print(f'Message delivered to {msg.topic()} [{msg.partition()}]')


for data in some_data_source:
    # Trigger any available delivery report callbacks from previous produce() calls
    producer.poll(0)

    # Asynchronously produce a message, the delivery report callback
    # will be triggered from poll() above, or flush() below, when the message has
    # been successfully delivered or failed permanently.
    producer.produce('mytopic', json.dumps(data).encode('utf-8'), callback=delivery_report)

# Wait for any outstanding messages to be delivered and delivery report
# callbacks to be triggered.
producer.flush()
