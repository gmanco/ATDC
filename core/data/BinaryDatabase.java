/*
 * Created on 9-ott-2003
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package core.data;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import core.Item;
import core.ItemSet;
/**
 * @author manco
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class BinaryDatabase extends Database {
	private String fileName;
	class BinaryFileEnumeration extends DataEnumeration {
		private DataInputStream in;
		/**
		 * This method was created by a SmartGuide.
		 * 
		 * @return ItemSet
		 */
		protected BinaryFileEnumeration() {
			current = nextItemSet();
		}
		protected ItemSet nextItemSet() {
			if (in == null)
				try {
					in = new DataInputStream(new FileInputStream(fileName));
				} catch (FileNotFoundException e) {
					System.out.println("Could not open " + fileName);
				}
			try {
				long cid, tid, size;
				String elem = "";
				ItemSet itemset = new ItemSet();
				cid = in.readLong();
				tid = in.readLong();
				size = in.readLong();
				for (int i = 0; i < size; i++) {
					elem += in.readLong();
					Item item = new Item(elem);
					itemset.addElement(item);
				}
				return itemset;
			} catch (EOFException e) {
				return null;
			} catch (IOException e) {
				System.out.println("I/O Error");
				return null;
			}
		}
	}
	public BinaryDatabase(String FileName) {
		fileName = FileName;
		bulk_load();
	}
	/**
	 * Insert the method's description here. Creation date: (16/02/2003 1.05.48)
	 * 
	 * @return java.util.Enumeration
	 */
	public Enumeration getEnumeration() {
		return new BinaryFileEnumeration();
	}
}