# Service to route messages

This is a servie to offer routing messages from a queue to a topic using a message filter.

## Running

##### Run app

```bash
mvn spring-boot:run
```

##### Run unit and integration tests

```bash
mvn clean && mvn test
```

##### Dependencies

To run integration tests, there is a file called docker-compose.yml with activemq dependencie. To run the activemq on docker compose, runs: 

```bash
docker-compose up
```

## to do and improvements

##### Something should be better

* More unit tests on Service, Receiver and Send
* Support configuration for another brokers
* Integration tests using another brokers

There are comments along all code about things should be better. (TODO, PENDING etc)