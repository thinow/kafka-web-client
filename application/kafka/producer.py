import json
import random
import uuid

import names
from confluent_kafka import Producer

BROKERS = ('localhost:9092',)
TOPIC = 'people'

producer = Producer({'bootstrap.servers': ','.join(BROKERS)})


def delivery_report(error, msg):
    """ Called once for each message produced to indicate delivery result.
        Triggered by poll() or flush(). """
    if error is not None:
        print(f'Message delivery failed: {error}')
    else:
        print(f'Message delivered to {msg.topic()} [{msg.partition()}]')


print("Starting to produce...")

for index in range(1000):
    # Trigger any available delivery report callbacks from previous produce() calls
    producer.poll(0)

    # Asynchronously produce a message, the delivery report callback
    # will be triggered from poll() above, or flush() below, when the message has
    # been successfully delivered or failed permanently.
    data = {
        'uuid': str(uuid.uuid4()),
        'name': names.get_full_name(),
        'age': random.randint(12, 99)
    }
    producer.produce(TOPIC, json.dumps(data).encode('utf-8'), callback=delivery_report)

# Wait for any outstanding messages to be delivered and delivery report
# callbacks to be triggered.
producer.flush()
