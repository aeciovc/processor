package es.com.kuehne.processor.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class Sender {

    private static Logger log = LoggerFactory.getLogger(Sender.class);

    @Autowired
    JmsTemplate jmsTemplateQueue;

    public void sendMessage(String destination, String message){

        log.info("Sending message for "+ destination);
        jmsTemplateQueue.convertAndSend(destination, message);
    }
}
