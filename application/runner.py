import logging
import os

from .web.server import WebServer

PORT: int = int(os.getenv('PORT', 5000))

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s %(message)s'
)

if __name__ == '__main__':
    WebServer().start(PORT)
