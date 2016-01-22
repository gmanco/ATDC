/*
 * Created on 12-dic-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package core.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Vector;


import core.Item;
import core.ItemSet;

/**
 * @author Giuseppe Manco
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ArffDatabase extends Database {
	String fileName;

	Vector asymmetry;

	private boolean checkForAsymmetry;

	Vector m_Attributes;

	/**
	 * @author Giuseppe Manco
	 * 
	 * TODO To change the template for this generated type comment go to Window -
	 * Preferences - Java - Code Generation - Code and Comments
	 */
	public class ArffEnumeration extends DataEnumeration {
		Reader reader;

		StreamTokenizer tokenizer;

		protected ArffEnumeration() {
			try {
				reader = new BufferedReader(new FileReader(fileName));
			} catch (Exception ex) {
				ex.printStackTrace();
				System.err.println(ex.getMessage());
			}

			tokenizer = new StreamTokenizer(reader);
			initTokenizer();
			readHeader();
			current = nextItemSet();
		}

		/**
		 * Reads and skips all tokens before next end of line token.
		 * 
		 * @param tokenizer
		 *            the stream tokenizer
		 */
		protected void readTillEOL() throws IOException {

			while (tokenizer.nextToken() != StreamTokenizer.TT_EOL) {
			}
			;
			tokenizer.pushBack();
		}

		protected void getFirstToken() throws IOException {

			while (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
			}
			;
			if ((tokenizer.ttype == '\'') || (tokenizer.ttype == '"')) {
				tokenizer.ttype = StreamTokenizer.TT_WORD;
			} else if ((tokenizer.ttype == StreamTokenizer.TT_WORD)
					&& (tokenizer.sval.equals("?"))) {
				tokenizer.ttype = '?';
			}
		}

		/**
		 * Gets token and checks if its end of line.
		 * 
		 * @param tokenizer
		 *            the stream tokenizer
		 * @exception IOException
		 *                if it doesn't find an end of line
		 */
		protected void getLastToken(boolean endOfFileOk) throws IOException {

			if ((tokenizer.nextToken() != StreamTokenizer.TT_EOL)
					&& ((tokenizer.ttype != StreamTokenizer.TT_EOF) || !endOfFileOk)) {
				errms("end of line expected");
			}
		}

		/**
		 * Gets next token, checking for a premature and of line.
		 * 
		 * @param tokenizer
		 *            the stream tokenizer
		 * @exception IOException
		 *                if it finds a premature end of line
		 */
		protected void getNextToken() throws IOException {

			if (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
				System.err.println("premature end of line");
				System.exit(-1);
			}
			if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
				System.err.println("premature end of line");
				System.exit(-1);
			} else if ((tokenizer.ttype == '\'') || (tokenizer.ttype == '"')) {
				tokenizer.ttype = StreamTokenizer.TT_WORD;
			} else if ((tokenizer.ttype == StreamTokenizer.TT_WORD)
					&& (tokenizer.sval.equals("?"))) {
				tokenizer.ttype = '?';
			}
		}

		/**
		 * Gets index, checking for a premature and of line.
		 * 
		 * @param tokenizer
		 *            the stream tokenizer
		 * @exception IOException
		 *                if it finds a premature end of line
		 */
		protected void getIndex() throws IOException {

			if (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
				System.err.println("premature end of line");
				System.exit(-1);
			}
			if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
				System.err.println("premature end of file");
				System.exit(-1);
			}
		}

		protected void initTokenizer() {

			tokenizer.resetSyntax();
			tokenizer.whitespaceChars(0, ' ');
			tokenizer.wordChars(' ' + 1, '\u00FF');
			tokenizer.whitespaceChars(',', ',');
			tokenizer.commentChar('%');
			tokenizer.quoteChar('"');
			tokenizer.quoteChar('\'');
			tokenizer.ordinaryChar('{');
			tokenizer.ordinaryChar('}');
			tokenizer.eolIsSignificant(true);
		}

		protected void readHeader() {

			String attributeName;
			Vector attributeValues;
			int i;
			try {

				// Get name of relation.
				getFirstToken();
				if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
					System.err.println("premature end of file");
					System.exit(-1);
				}
				if (tokenizer.sval.equalsIgnoreCase("@relation")) {
					getNextToken();
					// m_RelationName = tokenizer.sval;
					getLastToken(false);
				} else {
					System.err.println("keyword @relation expected");
				}

				// Create vectors to hold information temporarily.
				m_Attributes = new Vector();

				// Get attribute declarations.
				getFirstToken();
				if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
					errms("premature end of file");
				}

				while (tokenizer.sval.equalsIgnoreCase("@attribute")) {

					// Get attribute name.
					getNextToken();
					attributeName = tokenizer.sval;

					m_Attributes.addElement(attributeName);
					readTillEOL();
					getFirstToken();
				}

				// Check if data part follows. We can't easily check for EOL.
				if (!tokenizer.sval.equalsIgnoreCase("@data")) {
					errms("keyword @data expected");
				}

				// Check if any attributes have been declared.
				if (m_Attributes.size() == 0) {
					errms("no attributes declared");
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

		/**
		 * @param tokenizer2
		 * @param string
		 */
		private void errms(String string) {
			System.err.println(string);
			System.exit(-1);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see core.data.Database.DataEnumeration#nextItemSet()
		 */
		protected ItemSet nextItemSet() {
			try {
				// Check if any attributes have been declared.
				if (m_Attributes.size() == 0) {
					errms("no header information available");
				}

				// Check if end of file reached.
				getFirstToken();
				if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
					return null;
				}
				// Parse instance
				if (tokenizer.ttype == '{') {
					return getInstanceSparse(tokenizer);
				} else {
					return getInstanceFull(tokenizer);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * @param tokenizer2
		 * @return
		 */
private ItemSet getInstanceSparse(StreamTokenizer tokenizer2) throws IOException {
			int sz = m_Attributes.size();

			String m_ValueBuffer;
			int m_IndexBuffer = 0;

			ItemSet itemset = new ItemSet();
			int valIndex, numValues = 0, maxIndex = -1;

				// Get values
				do {

					// Get index
					getIndex();
					if (tokenizer.ttype == '}') {
						break;
					}

					// Is index valid?
					try {
						m_IndexBuffer = Integer.valueOf(tokenizer.sval).intValue();
					} catch (NumberFormatException e) {
						System.err.println( "index number expected");
						System.exit(-1);
					}
					if (m_IndexBuffer <= maxIndex) {
						System.err.println("indices have to be ordered");
						System.exit(-1);
					}
					if ((m_IndexBuffer < 0) || (m_IndexBuffer >= sz)) {
						System.err.println("index out of bounds");
						System.exit(-1);
					}
					maxIndex = m_IndexBuffer;
					m_ValueBuffer = (String)m_Attributes.get(m_IndexBuffer);

					// Get value;
					getNextToken();

					// Check if value is missing.
					if (tokenizer.ttype != '?') {

						// Check if token is valid.
						if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
							System.err.println( "not a valid value");
							System.exit(-1);
						}
						if (m_ValueBuffer.equals("class") && m_IndexBuffer == 0)
							itemset.setM_class(tokenizer.sval);
						else {
							m_ValueBuffer += "="+tokenizer.sval;
							Item item = new Item(m_ValueBuffer);
							itemset.addElement(item);
						}
					}
				} while (true);
					getLastToken(true);
				// Add instance to dataset
				return itemset;

		}
		/**
		 * @param tokenizer2
		 * @return
		 */
		private ItemSet getInstanceFull(StreamTokenizer tokenizer2)
				throws IOException {
			ItemSet itemset = new ItemSet();

			int sz = m_Attributes.size();
			for (int i = 0; i < sz; i++) {
				if (i > 0)
					getNextToken();

				if (tokenizer.ttype != '?') {
					if (i != sz - 1) {
						if (!checkForAsymmetry
								|| (checkForAsymmetry && !asym(tokenizer.sval))) {
							String value = ((String) m_Attributes.get(i)) + "="
									+ tokenizer.sval;
							Item item = new Item(value);
							itemset.addElement(item);
						}
					} else {
						itemset.setM_class(tokenizer.sval);
						getLastToken(true);
					}
				}
			}
			return itemset;
		}

		/**
		 * @param sval
		 * @return
		 */
		private boolean asym(String sval) {
			boolean res = false;
			for (int i = 0; i < asymmetry.size() && !res; i++)
				res = sval.equals(asymmetry.get(i));
			return res;
		}
	}

	/**
	 * @param string
	 */
	public ArffDatabase(String fname) {
		fileName = fname;
		checkForAsymmetry = false;
		bulk_load();
	}

	/**
	 * @param string
	 */
	public ArffDatabase(String fname, String[] as) {
		fileName = fname;
		checkForAsymmetry = true;
		asymmetry = new Vector();
		for (int i = 0; i < as.length; i++)
			asymmetry.add(as[i]);
		bulk_load();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.data.Database#getEnumeration()
	 */
	protected Enumeration getEnumeration() {
		return new ArffEnumeration();
	}

}
