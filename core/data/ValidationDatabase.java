/*
 * Created on 11-dic-2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package core.data;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import core.Item;
import core.ItemSet;
/**
 * @author Giuseppe Manco
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ValidationDatabase extends Database {
	private String fileName;
	class ValidationEnumeration extends DataEnumeration {
		String cid;
		Item citem;
		String cclass;
		int setcnt = 0;
		private BufferedReader in;
		protected ValidationEnumeration() {
			current = nextItemSet();
		}
		protected ItemSet nextItemSet() {
			if (in == null)
				try {
					in = new BufferedReader(new FileReader(fileName));
				} catch (FileNotFoundException e) {
					System.out.println("Could not open " + fileName);
				}
			try {
				String s;
				ItemSet itemset = null;
				if (cid != null) {
					itemset = new ItemSet();
					itemset.addElement(citem);
					itemset.setM_class(cclass);
				}
				while (true) {
					s = in.readLine();
					if (s == null){
						cid = null;
						citem = null;
						cclass = null;
						break;
					}
					StringTokenizer st = new StringTokenizer(s, " \t");
					if (cid == null) {
						itemset = new ItemSet();
						cid = st.nextToken();
						cclass = st.nextToken();
						citem = new Item(st.nextToken());
						itemset.addElement(citem);
						itemset.setM_class(cclass);
					} else {
						String nid = st.nextToken();
						if (nid.equals(cid)) {
							st.nextToken();
							Item item = new Item(st.nextToken());
							itemset.addElement(item);
						} else {
							cid = nid;
							cclass = st.nextToken();
							citem = new Item(st.nextToken());
							break;
						}
					}
				}
				if (itemset!= null){
					itemset.setid(setcnt);
					setcnt++;
				}
				return itemset;
			} catch (IOException e) {
				System.out.println("st.nextToken() unsuccessful");
				return null;
			}
		}
	}
	/**
	 * Defines an object of type AsciiDatabase which maps a file to a database
	 * 
	 * @param FileName
	 *            java.lang.String
	 */
	public ValidationDatabase(String FileName) {
		fileName = FileName;
		bulk_load();
	}
	/**
	 * This method was created by a SmartGuide. Inits a text file for reading of
	 * 
	 * @param FileName
	 *            java.lang.String
	 */
	public ValidationDatabase(String FileName, boolean toload) {
		fileName = FileName;
		load = toload;
		if (load)
			bulk_load();
	}
	/**
	 * Insert the method's description here. Creation date: (16/02/2003 1.05.48)
	 * 
	 * @return java.util.Enumeration
	 */
	public Enumeration getEnumeration() {
		return new ValidationEnumeration();
	}
	public static void main(String[] args){
		Database data = new ValidationDatabase("resources/trans1k.txt");
		for (Enumeration e = data.elements(); e.hasMoreElements();)
			System.out.println(e.nextElement());
	}

}