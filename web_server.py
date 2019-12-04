import os

from flask import Flask

port = os.getenv('PORT', 5000)

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello, World!'


if __name__ == '__main__':
    app.run(port=port)
