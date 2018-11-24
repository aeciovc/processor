package es.com.kuehne.processor.test.unit.service;

import es.com.kuehne.processor.service.Extractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class ExtractorTest {

    static String content = "<UC_STOCK_LEVEL_IFD><CTRL_SEG><TRNNAM>UC_STOCK_LEVEL</TRNNAM><TRNVER>20180100</TRNVER><UUID>0de01919-81eb-4cc7-a51d-15f6085fc1a4</UUID><WH_ID>WHS01</WH_ID><CLIENT_ID>CLIE01</CLIENT_ID><ISO_2_CTRY_NAME>GB</ISO_2_CTRY_NAME><REQUEST_ID>bc2a55e8-5a07-4af6-85fd-8290d3ccfb51</REQUEST_ID><ROUTE_ID>186</ROUTE_ID></CTRL_SEG></UC_STOCK_LEVEL_IFD>";


    @Test(expected = SAXException.class)
    public void testExtractValueWithInvalidXMLContent() throws XPathExpressionException, SAXException{

        //Input
        String XPATH_EXP = "/UC_STOCK_LEVEL_IFD/CTRL_SEG/TRNVER/text()";

        Extractor extractor = new Extractor("invalid xml content");

        //Execute
        String  result = extractor.getValueWithExp(XPATH_EXP);

        //Assert
        assertThat(result).isEqualTo("20180100");
    }


    @Test
    public void testExtractValue() throws XPathExpressionException, SAXException{

        //Input
        String XPATH_EXP = "/UC_STOCK_LEVEL_IFD/CTRL_SEG/TRNVER/text()";

        Extractor extractor = new Extractor(content);

        //Execute
        String  result = extractor.getValueWithExp(XPATH_EXP);

        //Assert
        assertThat(result).isEqualTo("20180100");
    }

    @Test
    public void testExtractValueWithNotFoundValue() throws XPathExpressionException, SAXException{

        //Input
        String XPATH_EXP = "/UC_STOCK_LEVEL_IFD/CTRL_SEG/NOT_KEY/text()";

        Extractor extractor = new Extractor(content);

        //Execute
        String result = extractor.getValueWithExp(XPATH_EXP);

        //Assert
        assertThat(result).isEqualTo("");
    }


    @Test(expected = XPathExpressionException.class)
    public void testExtractValueWithWrongSyntaxXpath() throws XPathExpressionException, SAXException{

        //Input
        String XPATH_EXP = "??dddd/NOTEY";

        Extractor extractor = new Extractor(content);

        extractor.getValueWithExp(XPATH_EXP);
    }

    //TODO It should be more tests
}

