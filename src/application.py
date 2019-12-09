import os

from src.web.server import server as web_server

port = int(os.getenv('PORT', 5000))

if __name__ == '__main__':
    web_server.run(port=port)
