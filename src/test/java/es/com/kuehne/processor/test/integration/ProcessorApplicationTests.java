package es.com.kuehne.processor.test.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.com.kuehne.processor.config.JmsDestinationConfig;
import es.com.kuehne.processor.config.JmsSourceConfig;
import es.com.kuehne.processor.jms.Receiver;
import es.com.kuehne.processor.jms.Sender;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@ActiveProfiles("test-integration")
@SpringBootTest
public class ProcessorApplicationTests {

	@Autowired
	Sender sender;

	@Autowired
	JmsSourceConfig jmsSource;

    @Autowired
    JmsDestinationConfig jmsDestination;

    @Autowired
    JmsTemplate jmsTemplateQueue;

	@Autowired
    JmsTemplate jmsTemplateTopic;

	@Autowired
    Receiver receiver;

    ArrayList<String> expectedResults = new ArrayList<>();
	ArrayList<String> results = new ArrayList<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Logger log = LoggerFactory.getLogger(ProcessorApplicationTests.class);

	@Test
	public void testProcessMessagesSuccess() throws JMSException{

        List<String> testCaseFiles = new ArrayList<>();
        testCaseFiles = listTestCaseFiles();
        log.info("Loading files {}", testCaseFiles);

        if (!testCaseFiles.isEmpty()) {

            testCaseFiles.forEach(filename -> {

                final List<String> jsonStrings = readTestCases(filename);

                log.info("-----Executing integration testcases from file {} ",filename ,"-----");

                jsonStrings.forEach(jsonString -> {
                    JsonNode jsonObject = parseJson(jsonString);
                    try {
                        executeTestCase(jsonObject, filename);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                });
            });
        }

        await().atMost(10, SECONDS).until(() -> results.size() == expectedResults.size());

        assertThat(results.size()).isEqualTo(expectedResults.size());
        assertThat(results.toString()).isEqualTo(expectedResults.toString());
	}

    private List<String> listTestCaseFiles() {
        try {
            return Files.list(Paths.get("src/test/resources/testcases"))
                    .filter(Files::isRegularFile)
                    .map(f -> f.getFileName().toString())
                    .filter(f -> f.endsWith(".json"))
                    .collect(toList());
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    private List<String> readTestCases(String filename) {
        ClassPathResource jsonResource = new ClassPathResource("testcases/" + filename);
        try (InputStream inputStream = jsonResource.getInputStream()) {
            return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(toList());
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    private JsonNode parseJson(String jsonString) {
        try {
            return OBJECT_MAPPER.readTree(jsonString);
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    private void executeTestCase(JsonNode jsonObject, String filename) throws JMSException {

        String message = jsonObject.get("message").asText();
        String expected = jsonObject.get("expected").asText();

        // Send message
        sender.sendMessage(jmsSource.getQueue(), message);

        // Add expected result if the expected value is not empty
        if (!expected.isEmpty()) {
            expectedResults.add(expected);
        }
    }

    @JmsListener(destination = "${spring.destination.topic}", containerFactory = "topicListenerFactory")
    public void receiveTopicMessage(Message message) throws JMSException {
	    ActiveMQTextMessage result = (ActiveMQTextMessage) message;
	    results.add(result.getText());
    }
}
