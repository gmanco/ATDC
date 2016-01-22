/*
 * Created on 21-lug-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package utils;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import core.Item;
import core.ItemSet;
import core.data.ArffDatabase;

/**
 * @author Giuseppe Manco
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Arff2Trans {

	public static void main(String[] args) throws Exception {
		String[] ex = {"0"};
		ArffDatabase data = new ArffDatabase("C:\\PAPERS\\WORKING\\MachineLearning\\Experiments\\UCI\\ad.arff",ex);
		PrintStream out = new PrintStream("C:/temp/mush.txt");
		int count = 0;
		
		for (Enumeration e = data.elements(); e.hasMoreElements(); ){
			ItemSet s = (ItemSet)e.nextElement();
			String cname = s.getM_class();
			
			for (int i = 0; i < s.size(); i++){
				Item a = (Item)s.elementAt(i);
					out.print(a+" ");
				}
			out.println();
			}			
	}
}
