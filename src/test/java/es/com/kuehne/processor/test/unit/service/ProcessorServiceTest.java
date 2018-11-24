package es.com.kuehne.processor.test.unit.service;

import es.com.kuehne.processor.service.ProcessorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.xml.sax.SAXException;

import javax.security.sasl.SaslException;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class ProcessorServiceTest {

    @Autowired
    ProcessorService processorService;

    static String XMLContent = "<UC_STOCK_LEVEL_IFD><CTRL_SEG><TRNNAM>UC_STOCK_LEVEL</TRNNAM><TRNVER>20180100</TRNVER><UUID>0de01919-81eb-4cc7-a51d-15f6085fc1a4</UUID><WH_ID>WHS01</WH_ID><CLIENT_ID>CLIE01</CLIENT_ID><ISO_2_CTRY_NAME>GB</ISO_2_CTRY_NAME><REQUEST_ID>bc2a55e8-5a07-4af6-85fd-8290d3ccfb51</REQUEST_ID><ROUTE_ID>186</ROUTE_ID></CTRL_SEG></UC_STOCK_LEVEL_IFD>";

    @Test(expected = SAXException.class)
    public void testProcessMessageWithEmptyContent() throws SAXException {

        //Action
        Map<String,String> result = processorService.processMessage("");

        //Assert
        assertThat(result.size()).isEqualTo(0);
    }

    @Test(expected = SAXException.class)
    public void testProcessMessageWithNoXMLContext() throws SAXException{

        //Action
        Map<String,String> result = processorService.processMessage("any message format");

        //Assert
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testProcessMessageWithNoMatchXMLContext() throws SAXException {

        //Action
        Map<String,String> result = processorService.processMessage("<test><id>12345</id><req>New request</req></test>");

        //Assert
        assertThat(result.size()).isEqualTo(0);
    }

    @Test(expected = SAXException.class)
    public void testProcessMessageWithNullContext() throws SAXException{

        //Action
        Map<String,String> result = processorService.processMessage(null);

        //Assert
        assertThat(result.size()).isEqualTo(0);
    }


    @Test
    public void testProcessMessageWithContextSuccess() throws SAXException{

        //Action
        Map<String,String> result = processorService.processMessage(XMLContent);

        //Assert
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get("DATE")).isEqualTo("20180100");
        assertThat(result.get("REQUEST_ID")).isEqualTo("bc2a55e8-5a07-4af6-85fd-8290d3ccfb51");
    }

    //TODO It should be more tests
}
