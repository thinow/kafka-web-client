FROM python:3.7-alpine3.10

LABEL description="Kafka Web Client to watch, filter, and browse messages consumed from Kafka." \
      maintainer="Thierry Nowak" \
      email="thinow@gmail.com"

ENV HOST 0.0.0.0

RUN apk --no-cache add \
    gcc=~8.3 \
    musl-dev=~1.1 \
    librdkafka-dev=~1.0

RUN mkdir -p /opt/kafka-web-client

COPY context/application /opt/kafka-web-client/application
COPY context/requirements.txt /opt/kafka-web-client/requirements.txt

WORKDIR /opt/kafka-web-client

RUN pip install -r requirements.txt

ENTRYPOINT ["python", "-m", "application.runner"]
