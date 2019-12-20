from uuid import uuid4

from pytest import fixture

from application.web.server import WebServer
from tests.util.docker import DockerComposeRunner
from tests.util.kafka import KafkaProducer

PRODUCED_MESSAGES = (
    'first-message',
    'second-message',
    'third-message',
    'fourth-message',
    'fifth-message',
)


@fixture(scope='module')
def kafka_cluster():
    with DockerComposeRunner():
        yield {
            'server': 'localhost:9092'
        }


@fixture
def http_client():
    http = WebServer.http

    http.config['TESTING'] = True

    with http.test_client() as client:
        yield client


@fixture
def socket_client():
    http = WebServer.http
    socket = WebServer.socket
    socket_client = socket.test_client(http)
    socket_client.get_received()  # clear all the previous messages
    return socket_client


def test_redirection_on_main_route(http_client):
    """
    Should redirect to the main static file when accessing root path.
    """
    # when
    response = http_client.get('/')

    # then
    assert response.status_code == 302
    assert response.location == 'http://localhost/index.html'


def test_fetch_messages_from_socket(kafka_cluster, socket_client):
    """
    Should receive messages produced in Kafka by using Web Socket events.
    """
    # given
    topic = generate_topic_name()
    KafkaProducer().produce(kafka_cluster['server'], topic, PRODUCED_MESSAGES)

    # when
    socket_client.emit('start', {
        'cluster': kafka_cluster['server'],
        'topic': topic,
        'max_messages': len(PRODUCED_MESSAGES)
    })

    # then
    all_events = socket_client.get_received()

    events_with_messages = filter(lambda event: event['name'] == 'consumed-message', all_events)
    messages = tuple((event['args'][0]['value'] for event in events_with_messages))
    assert messages == PRODUCED_MESSAGES

    last_event = all_events[-1]
    assert last_event['name'] == 'end'

    assert len(all_events) == len(PRODUCED_MESSAGES) + 1


def test_fetch_specific_number_of_messages(kafka_cluster, socket_client):
    """
    Should receive a specific number of messages from Kafka.
    """
    # given
    topic = generate_topic_name()
    KafkaProducer().produce(kafka_cluster['server'], topic, PRODUCED_MESSAGES)

    # when
    max_messages = 3
    socket_client.emit('start', {
        'cluster': kafka_cluster['server'],
        'topic': topic,
        'max_messages': max_messages
    })

    # then
    all_events = socket_client.get_received()

    events_with_messages = filter(lambda event: event['name'] == 'consumed-message', all_events)
    assert len(list(events_with_messages)) == max_messages


def generate_topic_name():
    return f'topic-test-{uuid4()}'
