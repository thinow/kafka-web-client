FROM python:3.7-alpine3.10

LABEL description="TBD" \
      maintainer="Thierry Nowak" \
      email="thinow@gmail.com"

RUN apk --no-cache add \
    gcc=~8.3 \
    musl-dev=~1.1 \
    librdkafka-dev=~1.0

RUN mkdir -p /opt/not-named-yet

COPY context/application /opt/not-named-yet/application
COPY context/requirements.txt /opt/not-named-yet/requirements.txt

WORKDIR /opt/not-named-yet

RUN pip install -r requirements.txt

ENTRYPOINT ["python", "-m", "application.runner"]
