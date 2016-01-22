package utils.postprocessor;

import java.io.FileNotFoundException;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ProcessColumns implements ContentHandler {

	private Vector clusters;

	private Vector current;

	private boolean parsed;

	public void setDocumentLocator(Locator arg0) {
	}

	public void startDocument() throws SAXException {
		clusters = new Vector();
	}

	public void endDocument() throws SAXException {
		parsed = true;
	}

	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
	}

	public void endPrefixMapping(String arg0) throws SAXException {
	}

	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes attrs) throws SAXException {
		if (rawName.equalsIgnoreCase("cluster")) {
			current = new Vector();
		}
		if (rawName.equalsIgnoreCase("itemset")) {
			if (attrs.getValue("id") != null) {
				current.add(new Integer(attrs.getValue("id")));
			}

		}
	}

	public void endElement(String nmespaceURI, String localName, String rawName)
			throws SAXException {
		if (rawName.equalsIgnoreCase("cluster")) {
			clusters.add(current);
			current = null;
		}
	}

	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
	}

	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
	}

	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
	}

	public void skippedEntity(String arg0) throws SAXException {
	}

	public Vector getClusters(String filename) throws FileNotFoundException {
		if (!parsed) {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = null;
			try {
				saxParser = saxParserFactory.newSAXParser();
				XMLReader parser = saxParser.getXMLReader();
				parser.setContentHandler(this);
				parser.parse(filename);
			} catch (FileNotFoundException e){
				throw new FileNotFoundException();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return clusters;
	}
}