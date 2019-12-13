from flask import Flask, redirect

web_server = Flask(
    __name__,
    static_folder='static',
    static_url_path=''
)


@web_server.route('/')
def main_page():
    return redirect("index.html")


@web_server.route('/api/hello')
def hello_world():
    return 'Hello World!'
