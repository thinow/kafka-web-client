import logging
import os

from .web.server import WebServer

HOST: str = os.getenv('HOST', '127.0.0.1')
PORT: int = int(os.getenv('PORT', 8080))

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s %(message)s'
)

if __name__ == '__main__':
    WebServer().start(HOST, PORT)
