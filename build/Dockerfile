FROM python:3.7.5-alpine

LABEL description="TBD" \
      maintainer="Thierry Nowak" \
      email="thinow@gmail.com"

RUN mkdir -p /opt/not-named-yet

COPY context/application /opt/not-named-yet/application
COPY context/requirements.txt /opt/not-named-yet/requirements.txt

WORKDIR /opt/not-named-yet

RUN apk --no-cache add gcc musl-dev librdkafka-dev
RUN pip install -r requirements.txt

ENTRYPOINT ["python", "-m", "application.runner"]
