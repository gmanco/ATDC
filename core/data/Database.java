package core.data;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

import core.Item;
import core.ItemSet;

/*import java.io.*;
import java.util.*;
import java.sql.*;
import sun.jdbc.odbc.*;
import WebMining.Patterns.*;*/
/**
 * This interface was generated by a SmartGuide.
 * Provides access to the common data items.
 * @author Giuseppe Manco
 */
public abstract class Database {
	protected boolean load = true;
	protected long size = -1;
	protected Vector container;
	public abstract class DataEnumeration implements Enumeration {
		protected ItemSet current;
		/**
		 * This method was created by a SmartGuide.
		 * 
		 * @exception java.sql.SQLException
		 *                The exception description.
		 */
		protected abstract ItemSet nextItemSet();
		public Object nextElement() {
			if (current != null){
				ItemSet res = current;
				current = nextItemSet();
				return res;
			}
			throw new NoSuchElementException("Database Enumeration");
		}
		/* (non-Javadoc)
		 * @see java.util.Enumeration#hasMoreElements()
		 */
		public boolean hasMoreElements() {
			// TODO Auto-generated method stub
			return (current != null);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16/02/2003 1.05.48)
	 * @return java.util.Enumeration
	 */
	public Enumeration elements() {
		if (load)
			return container.elements();
		else
			return getEnumeration();
	}

	/**
	 * @return
	 */
	protected abstract Enumeration getEnumeration();

	/* (non-Javadoc)
	 * @see WebMining.DatabaseInterface.Database#getSize()
	 */
	public long size() {
		if (size == -1) {
			long sz = 0;
			Enumeration el = getEnumeration();
			while (el.hasMoreElements()){
				sz++;
				el.nextElement();
			}
			size = sz;
		}
		return size;
	}
	/**
	 * Insert the method's description here. Creation date: (16/02/2003 1.05.48)
	 * 
	 * @return java.util.Enumeration
	 */
	public void bulk_load() {
		container = new Vector();
		size = 0;
		try {
			Enumeration le = getEnumeration();
			while (le.hasMoreElements()) {
				ItemSet is = (ItemSet) le.nextElement();
				is.setid((int)size);
				container.addElement(is);
				size++;
			}
		} catch (Exception e) {
			System.out.println("Read error");
		}
	}
	/**
	 * @return
	 */
	public Hashtable getClassFrequencies() {
		Hashtable cfreqs = new Hashtable();
		for (Enumeration e = elements(); e.hasMoreElements();){
			ItemSet is = (ItemSet)e.nextElement();
			String aclass = is.getM_class();
			if (aclass != null)
					if (cfreqs.containsKey(aclass)){
						double val = ((Double)cfreqs.get(aclass)).doubleValue()+1;
						cfreqs.put(aclass,new Double(val));
					}
					else
						cfreqs.put(aclass,new Double(1));
		}
		return cfreqs;
	}
	/**
	 * @return
	 */
	public Object[] getClasses() {
		HashSet classes = new HashSet();
		for (Enumeration e = elements(); e.hasMoreElements();){
			ItemSet is = (ItemSet)e.nextElement();
			String aclass = is.getM_class();
			if ((aclass != null) && (!classes.contains(aclass))){
				classes.add(aclass);
			}
		}
		if (classes.size() > 0)
			return classes.toArray();
		else
			return null;
		
	}
	public static long numOfItems(){
		return Item.getNOfItems();
	}


}
