import uuid

import confluent_kafka

CONFIG = {
    'bootstrap.servers': 'localhost:9092',
    'group.id': str(uuid.uuid4()),
    'auto.offset.reset': 'earliest'
}

print('Initializing...')
consumer = confluent_kafka.Consumer(CONFIG)

print('Subscribing...')
consumer.subscribe(['mytopic'])

print('Starting to poll...')

while True:
    msg = consumer.poll(1.0)

    if msg is None:
        continue
    if msg.error():
        print(f'Consumer error: {msg.error()}')
        continue

    print(f'Received message: {msg.value().decode("utf-8")}')

consumer.close()
