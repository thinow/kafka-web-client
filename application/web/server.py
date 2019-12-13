import logging
import time

from flask import Flask, redirect
from flask_socketio import SocketIO, emit

app = Flask(
    __name__,
    static_folder='static',
    static_url_path=''
)

app.config['SECRET_KEY'] = 'aSilLySECr3t!?!'  # TODO get from a variable
socket_io = SocketIO(app)


@app.route('/')
def main_page():
    return redirect("index.html")


@socket_io.on('connect')
def connect():
    logging.info("Client connected")


@socket_io.on('disconnect')
def disconnect():
    logging.info("Client disconnected")


@socket_io.on('start')
def test_message(message):
    time.sleep(.800)
    emit('consumed-message', {'position': 1, 'datetime': 'now', 'content': {'key': 'value'}})
    time.sleep(.800)
    emit('consumed-message', {'position': 2, 'datetime': 'now', 'content': {'key': 'value'}})
    time.sleep(.800)
    emit('consumed-message', {'position': 3, 'datetime': 'now', 'content': {'key': 'value'}})
    emit('end')


class WebServer:
    def run(self, port):
        socket_io.run(app, port=port)
