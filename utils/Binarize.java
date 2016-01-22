/*
 * Created on 23-giu-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package utils;

import core.*;
import core.data.*;
import java.util.*;
import java.io.*;

/**
 * @author Giuseppe Manco
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Binarize {

	public static void main(String[] args) throws Exception {
		ArffDatabase data = new ArffDatabase("C:\\PAPERS\\WORKING\\MachineLearning\\Experiments\\UCI\\mushroom.arff");
		PrintStream out = new PrintStream("C:/WUTemp/mush.txt");
		HashSet items = new HashSet();
		int count = 0;
		
		for (Enumeration e = data.elements(); e.hasMoreElements(); ){
			ItemSet s = (ItemSet)e.nextElement();
			
			for (int i = 0; i < s.size(); i++){
				Item a = (Item)s.elementAt(i);
				if (!items.contains(a)){
					items.add(a);
				}
			}			
		}
		for (Enumeration e = data.elements(); e.hasMoreElements(); ){
			ItemSet s = (ItemSet)e.nextElement();
			
			for (Iterator it = items.iterator(); it.hasNext();){
				Item a = (Item)it.next();
				if (!s.contains(a))
					out.print("0 ");
				else
					out.print("1 ");
			}
			out.println();
		}
	}
}
