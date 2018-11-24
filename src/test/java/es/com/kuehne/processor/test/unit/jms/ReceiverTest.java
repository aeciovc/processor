package es.com.kuehne.processor.test.unit.jms;

import es.com.kuehne.processor.config.JmsDestinationConfig;
import es.com.kuehne.processor.config.JmsSourceConfig;
import es.com.kuehne.processor.jms.Receiver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import org.apache.activemq.command.ActiveMQTextMessage;
import javax.jms.JMSException;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class ReceiverTest {

    @Autowired
    private JmsTemplate jmsTemplateQueue;

    @Autowired
    private JmsTemplate jmsTemplateTopic;

    @Autowired
    private Receiver receiver;

    @Autowired
    private JmsDestinationConfig jmsDestination;

    @Autowired
    private JmsSourceConfig jmsSource;

    static String messageToSend = "<UC_STOCK_LEVEL_IFD><CTRL_SEG><TRNNAM>UC_STOCK_LEVEL</TRNNAM><TRNVER>20180100</TRNVER><UUID>0de01919-81eb-4cc7-a51d-15f6085fc1a4</UUID><WH_ID>WHS01</WH_ID><CLIENT_ID>CLIE01</CLIENT_ID><ISO_2_CTRY_NAME>GB</ISO_2_CTRY_NAME><REQUEST_ID>bc2a55e8-5a07-4af6-85fd-8290d3ccfb51</REQUEST_ID><ROUTE_ID>186</ROUTE_ID></CTRL_SEG></UC_STOCK_LEVEL_IFD>";

    @Test
    public void testReceiverMessageSuccess() throws  JMSException, IOException {

        // Send a message to Queue source
        this.jmsTemplateQueue.convertAndSend(jmsSource.getQueue(), messageToSend);

        // Get message on topic
        ActiveMQTextMessage result = (ActiveMQTextMessage) jmsTemplateTopic.receive(jmsDestination.getTopic());

        // Assert the message is in topic
        assertThat(result.getText()).isEqualTo("{DATE=20180100, REQUEST_ID=bc2a55e8-5a07-4af6-85fd-8290d3ccfb51}");
        assertThat(result.getProperty("date")).isEqualTo("20180100");
        assertThat(result.getProperty("request_id")).isEqualTo("bc2a55e8-5a07-4af6-85fd-8290d3ccfb51");
    }

    //TODO It should be more tests
}
