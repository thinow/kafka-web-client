import os
import subprocess

FILE_PATH = './tests/fixtures/docker-compose.yml'
SHOULD_STOP = os.getenv('KEEP_DOCKER_COMPOSE', 'false') != 'true'


class DockerComposeRunner:
    def __enter__(self):
        subprocess.run(['docker-compose', '-f', FILE_PATH, 'up', '-d'])

    def __exit__(self, exc_type, exc_val, exc_tb):
        if SHOULD_STOP:
            subprocess.run(['docker-compose', '-f', FILE_PATH, 'down'])
