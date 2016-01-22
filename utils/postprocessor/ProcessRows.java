/*
 * Created on 5-apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package utils.postprocessor;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

class ProcessRows implements ContentHandler {
	private Cluster current;
	private HashSet itemset;
	private Vector clusters;
	private boolean parsed;
	private int counter = -1;
	private Vector classes;


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		clusters = new Vector();
		classes = new Vector();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		parsed = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
	 *      java.lang.String)
	 */
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	public void endPrefixMapping(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes attrs) throws SAXException {
		// TODO Auto-generated method stub
		if (rawName.equalsIgnoreCase("cluster")) {
			current = new Cluster();
		}
		if (rawName.equalsIgnoreCase("items")) {

		}
		if (rawName.equalsIgnoreCase("item")) {
			if ((itemset != null) && (attrs.getValue("name") != null)) {
				itemset.add(attrs.getValue("name"));
			} else {
				String name = attrs.getValue("name");
				double val = Double.parseDouble(attrs.getValue("frequency"));
				if (current != null && name != null)
					current.insertItem(name, val);
			}
		}
		if (rawName.equalsIgnoreCase("itemsets")) {
			current.size = Integer.parseInt(attrs.getValue("size"));
		}
		if (rawName.equalsIgnoreCase("itemset")) {
			if (attrs.getValue("size") != null) {
				int size = Integer.parseInt(attrs.getValue("size"));
				String clid = attrs.getValue("class");
				if (!classes.contains(clid))
					classes.add(clid);
				System.out.println(classes.indexOf(clid));
				itemset = new HashSet();
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(String nmespaceURI, String localName, String rawName)
			throws SAXException {
		if (rawName.equalsIgnoreCase("cluster")) {
			clusters.add(current);
			current = null;
		}
		if (rawName.equalsIgnoreCase("items")) {

		}
		if (rawName.equalsIgnoreCase("item")) {

		}
		if (rawName.equalsIgnoreCase("itemsets")) {

		}
		if (rawName.equalsIgnoreCase("itemset")) {
			if (current != null) {
				current.elems.add(itemset);
			}
			itemset = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
	 *      java.lang.String)
	 */
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String arg0) throws SAXException {
		

	}

	/**
	 * @return
	 */
	public Vector getClusters(String filename) {
		if (!parsed){
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = null;
			try {
				saxParser = saxParserFactory.newSAXParser();
				XMLReader parser = saxParser.getXMLReader();
				parser.setContentHandler(this);
				parser.parse(filename);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return clusters;
	}
}

