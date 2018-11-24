package es.com.kuehne.processor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JmsDestinationConfig {

    @Value("${spring.destination.activemq.broker-url}")
    String brokerUrl;

    @Value("${spring.destination.activemq.username}")
    String userName;

    @Value("${spring.destination.activemq.password}")
    String password;

    @Value("${spring.destination.topic}")
    String topic;

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getTopic() {
        return topic;
    }
}
