package es.com.kuehne.processor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JmsSourceConfig {

    @Value("${spring.source.activemq.broker-url}")
    String brokerUrl;

    @Value("${spring.source.activemq.username}")
    String userName;

    @Value("${spring.source.activemq.password}")
    String password;

    @Value("${spring.source.queue}")
    String queue;

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getQueue() {
        return queue;
    }
}
