FROM openjdk:13-alpine3.10

LABEL description="Kafka Web Client to watch messages from Kafka." \
      maintainer="Thierry Nowak" \
      email="thinow@gmail.com"

EXPOSE 8080

CMD ["java", "-jar", "/opt/kafka-web-client.jar"]

WORKDIR /opt

COPY context/kafka-web-client.jar /opt/kafka-web-client.jar
