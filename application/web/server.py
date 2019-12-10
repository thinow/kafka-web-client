from flask import Flask

web_server = Flask(__name__)


@web_server.route('/')
def hello_world():
    return 'Hello, World!'
