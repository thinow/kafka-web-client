import logging

from flask_socketio import SocketIO, emit

from ...kafka.consumer import KafkaConsumer


def create_socket_app(http_app):
    socket_app = SocketIO(http_app)

    @socket_app.on('start')
    def start(request):
        logging.info(f"socket : received event start. request={request}")
        consumer = KafkaConsumer(
            cluster=request['cluster'],  # TODO rename to server
            topic=request['topic'],
            max_messages=request['max_messages']
        )
        consumer.consume(
            on_consumed_message=lambda message: emit('consumed-message', message),
            on_end=lambda: emit('end')
        )

    return socket_app
