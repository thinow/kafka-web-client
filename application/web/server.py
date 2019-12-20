from .http.factory import create_http_app
from .socket.factory import create_socket_app


class WebServer:
    SECRET_KEY = 'aSilLySECr3t!?!'  # TODO get from an environment variable

    http = create_http_app(SECRET_KEY)
    socket = create_socket_app(http)

    def start(self, host: str, port: int) -> None:
        WebServer.socket.run(WebServer.http, host=host, port=port)
