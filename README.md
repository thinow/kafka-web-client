# kafka-web-client

Dockerized web console to fetch some messages produced in Kafka in order to watch the content of topics.

## Usage

```bash
docker run -p 8080:8080 thinow/kafka-web-client
```

Open [http://localhost:8080/](http://localhost:8080/).

## Development

The following commands require [JDK 13](https://www.oracle.com/java/technologies/javase-jdk13-downloads.html) to be installed.

### Run

```bash
# Define the IP of your machine, for instance with the IP 192.168.0.1
export DOCKER_HOST_IP=192.168.0.1

# Start Kafka and Zookeeper
./gradlew devStart

# Run the tests
./gradlew test

# Run the server
./gradlew bootRun
```

Open [http://localhost:8080/](http://localhost:8080/) and use the IP of your machine as the host and the port `9092`, e.g. `192.168.0.1:9092`.

### Build

```bash
# Build docker image
./gradlew buildDocker
```