from flask import Flask, redirect


def create_http_app(secret_key) -> Flask:
    app = Flask(
        __name__,
        static_folder='static',
        static_url_path=''
    )

    app.config['SECRET_KEY'] = secret_key

    @app.route('/')
    def main_page():
        return redirect("index.html")

    return app
