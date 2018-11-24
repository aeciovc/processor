package es.com.kuehne.processor.service;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

public class Extractor {

    private Document document;

    public Extractor(String content) throws SAXException{

        try {

            DocumentBuilderFactory documentumentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentumentBuilderFactory.setNamespaceAware(true);

            DocumentBuilder documentumentBuilder = documentumentBuilderFactory.newDocumentBuilder();

            document = documentumentBuilder.parse(new InputSource(new StringReader(content)));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getValueWithExp(String exp) throws javax.xml.xpath.XPathExpressionException{

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        XPathExpression expr = xpath.compile(exp);

        Node node = (Node) expr.evaluate(this.document, XPathConstants.NODE);

        return node == null ? "" : node.getNodeValue();
    }
}
