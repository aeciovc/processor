package es.com.kuehne.processor.jms;

import es.com.kuehne.processor.config.JmsDestinationConfig;
import es.com.kuehne.processor.config.JmsSourceConfig;
import es.com.kuehne.processor.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.listener.adapter.JmsResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.util.Map;

@Component
public class Receiver {

    private static Logger log = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    private ProcessorService processorService;

    @Autowired
    private JmsDestinationConfig jmsDestination;

    @Autowired
    private JmsSourceConfig jmsSource;

    @JmsListener(destination = "${spring.source.queue}", containerFactory = "queueListenerFactory")
    public JmsResponse<Message> receiveAndPublish(Message message) {

        log.debug("Message received is < " + message.getPayload() +" >");
        log.debug("Message headers is < " + message.getHeaders() +" >");
        log.info("Message received on < " + jmsSource.getQueue() +" >");

        // TODO Here it should check the _type message to support other kind of objects
        // PENDING the JMS message could address which topic it should be going

        // Process message extracting values
        Map<String, String> result = null;
        try {
            result = processorService.processMessage(message.getPayload().toString());
        } catch (SAXException e) {
            log.error("Invalid XML message, ignoring message. Details < "+e.getMessage()+" >");
            return null;
        }

        if (result.isEmpty()){
            return  null;
        }

        // Build message to response
        Message<String> response = MessageBuilder.
                withPayload(result.toString()).
                setHeader("date", result.get("DATE")).
                setHeader("request_id", result.get("REQUEST_ID")).
                build();

        // REVIEW The destination could be to JMSReplyTo
        log.info("Publishing on topic " + jmsDestination.getTopic());
        return JmsResponse.forTopic(response, jmsDestination.getTopic());
    }
}

