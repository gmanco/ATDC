/*
 * Created on 18-mar-2005
 *
 * 
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package utils.postprocessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Giuseppe Manco
 * 
 * 
 * Preferences - Java - Code Generation - Code and Comments
 */

public class PostProcessor {
	String filename;

	private static Vector items;

	private static Vector rowClusters;

	private static Vector columnClusters;

	private static PrintStream out;

	private static boolean clusteredCols = true;

	public static void main(String[] args) {
		String filein = args[0];
		ProcessRows rowsHandler = new ProcessRows();
		rowClusters = rowsHandler.getClusters(filein);
		String filecols = filein + ".cols";
		try {
			ProcessColumns colsHandler = new ProcessColumns();
			columnClusters = colsHandler.getClusters(filecols+".xml");
		} catch (Exception e) {
			clusteredCols = false;
		}
		prepareForPrinting();
		print();
		if (!clusteredCols)
			try {
				generateCoOccurrence(filecols);
			} catch (Exception e1) {
			}
	}

	/**
	 * @param file
	 * 
	 */
	private static void generateCoOccurrence(String file) throws Exception {
		PrintStream dout = new PrintStream(file);
		int sz = items.size();
		for (int i = 0; i < sz; i++) {
			String curr = (String) items.get(i);
			for (int j = 0; j < rowClusters.size(); j++) {
				Cluster currc = (Cluster) rowClusters.get(j);
				if (currc.contains(curr))
					dout.println(i + "\t0\t" + j);

			}

		}

	}

	/**
	 * 
	 */
	public static void print() {
		try {
			out = new PrintStream("C:/WUTemp/matrix.dat");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Cluster current;
		HashSet itemset;
		printHeader();
		for (int i = 0; i < rowClusters.size(); i++) {
			current = (Cluster) rowClusters.get(i);
			for (int j = 0; j < current.elems.size(); j++) {
				itemset = (HashSet) current.elems.get(j);
				printItemSet(itemset);
			}
		}
	}

	/**
	 * 
	 */
	private static void printHeader() {
		/*
		 * for (int i = 0; i < items.size(); i++) out.print(items.get(i)+"\t");
		 * out.println();
		 */
	}

	/**
	 * 
	 */
	private static void printItemSet(HashSet itemset) {
		for (int i = 0; i < items.size(); i++) {
			String item = (String) items.get(i);
			if (itemset.contains(item))
				out.print(1 + " ");
			else
				out.print(0 + " ");
		}
		out.println();
	}

	/**
	 * 
	 */
	public static void prepareForPrinting() {
		Vector citems = new Vector();
		int sz = 0;
		Cluster current;
		for (int i = 0; i < rowClusters.size(); i++) {
			current = (Cluster) rowClusters.get(i);
			current.sort();
			for (Iterator it = current.frequencies.iterator(); it.hasNext();) {
				Item itm = (Item) it.next();
				double relfreq = itm.frequency;
				if (isRepresentative(itm.name, relfreq, i))
					citems.add(itm.name);
			}
			sz += current.size;
			if (!clusteredCols)
				System.out.println(citems.size() + " " + sz);
			else
				System.out.println(sz);
		}
		System.out.println();
		if (!clusteredCols)
			items = citems;
		else {
			sz = 0;
			items = new Vector(citems.size());
			for (int i = 0; i < columnClusters.size(); i++){
				Vector cluster = (Vector)columnClusters.get(i);
				for (int j = 0; j < cluster.size(); j++){
					int itmval = ((Integer)cluster.get(j)).intValue();
					items.add(citems.get(itmval));
				}
				sz += cluster.size();
				System.out.println(sz);

			}
				
		}
			
	}

	/**
	 * @param item
	 * @param frequency
	 * @param i
	 * @return
	 */
	private static boolean isRepresentative(String item, double relfreq, int i) {
		for (int j = 0; j < rowClusters.size(); j++) {
			if (j != i) {
				Cluster curr = (Cluster) rowClusters.get(j);
				if (curr.myitems.containsKey(item)) {
					double f = ((Double) curr.myitems.get(item)).doubleValue();
					if (f >= relfreq)
						return false;
				}
			}
		}
		return true;
	}
}
