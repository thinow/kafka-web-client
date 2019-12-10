import os

from .web.server import web_server

port = int(os.getenv('PORT', 5000))

if __name__ == '__main__':
    web_server.run(port=port)
