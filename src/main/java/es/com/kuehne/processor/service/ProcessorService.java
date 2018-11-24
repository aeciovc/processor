package es.com.kuehne.processor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorService.class);

    // PENDING It could be configurable
    private static String XPATH_EXP_TRNVER = "/UC_STOCK_LEVEL_IFD/CTRL_SEG/TRNVER/text()";
    private static String XPATH_EXP_REQUEST_ID = "/UC_STOCK_LEVEL_IFD/CTRL_SEG/REQUEST_ID/text()";

    // PENDING The method return could be an object
    public Map<String,String> processMessage(String content) throws SAXException {

        if (content == null)
            throw new SAXException();

        Extractor extractor = new Extractor(content);

        Map<String,String> result = new HashMap<String,String>();

        try {

            String date = extractor.getValueWithExp(XPATH_EXP_TRNVER);
            String requestID = extractor.getValueWithExp(XPATH_EXP_REQUEST_ID);

            if (!date.isEmpty()) {
                result.put("DATE", date);
            }

            if (!requestID.isEmpty()) {
                result.put("REQUEST_ID", requestID);
            }

        }catch (XPathExpressionException e){
            LOGGER.error("Error on XPathExpression < "+e.getMessage()+" >");
        }

        return result;
    }

}
