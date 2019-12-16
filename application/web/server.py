import logging

from flask import Flask, redirect
from flask_socketio import SocketIO, emit

from ..kafka.consumer import KafkaConsumer

app = Flask(
    __name__,
    static_folder='static',
    static_url_path=''
)

app.config['SECRET_KEY'] = 'aSilLySECr3t!?!'  # TODO get from an environment variable
socket_io = SocketIO(app)


@app.route('/')
def main_page():
    return redirect("index.html")


@socket_io.on('start')
def start(request):
    logging.info(f"socket : received event start. request={request}")
    consumer = KafkaConsumer(
        cluster=request['cluster'],
        topic=request['topic'],
        max_messages=request['max_messages']
    )
    consumer.consume(
        on_consumed_message=lambda message: emit('consumed-message', message),
        on_end=lambda: emit('end')
    )


class WebServer:
    def start(self, host: str, port: int) -> None:
        socket_io.run(app, host=host, port=port)
