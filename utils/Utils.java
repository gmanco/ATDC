/*
 * Created on 25-mar-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package utils;

import java.util.Enumeration;
import java.util.Vector;

import core.*;
import core.data.*;

/**
 * @author Giuseppe Manco
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Utils {
	/**
	 * @param curdata
	 * @param avg
	 * @param i
	 * @param length
	 */
	public static void sort(Vector data) {
		quicksort(data);
	}

	/**
	 * @param data
	 * @param wgt
	 */
	private static void heapsort(Vector data) {
		int length = data.size();
		for (int i = length/2 -1; i >= 0; --i)
			moveDown(data,i,length-1);
		for (int i = length-1;i>= 1; --i){
			swap(data,0,i);
			moveDown(data,0,i-1);
		}
	}

	/**
	 * @param data
	 * @param i
	 * @param i2
	 */
	private static void swap(Vector data, int i, int j) {
		Object o = data.get(i);
		data.set(i,data.get(j));
		data.set(j,o);
	}

	/**
	 * @param data
	 * @param i
	 * @param j
	 */
	private static void moveDown(Vector data, int first, int last) {
		int largest = 2*first + 1;
		while (largest <= last) {
				if (largest < last &&
						((Comparable)data.get(largest)).compareTo(data.get(largest+1)) < 0)
					largest++;
				if (((Comparable)data.get(first)).compareTo(data.get(largest)) < 0) {
					swap(data,first,largest);
					first = largest;
					largest = 2*first + 1;
				}
				else largest = last + 1;
			}
	}

	/**
	 * @param data
	 * @param wgt
	 */
	private static void quicksort(Vector data) {
		if (data.size() < 2)
			return;
		int max = 0;
		for (int i = 1; i < data.size(); i++)
			if (((Comparable)data.get(max)).compareTo(data.get(i)) < 0)
				max = i;
			swap(data,data.size()-1,max);
			quicksort(data,0,data.size()-2);
	}

	/**
	 * @param data
	 * @param i
	 * @param j
	 */
	private static void quicksort(Vector data, int first, int last) {
		int lower = first + 1, upper = last;
		swap(data,first,(first+last)/2);
		Comparable bound = (Comparable)data.get(first);
		while (lower <= upper) {
			while (((Comparable)data.get(lower)).compareTo(bound) < 0)
				lower++;
			while (bound.compareTo(data.get(upper)) < 0)
				upper--;
			if (lower < upper)
				swap(data,lower++,upper--);
			else
				lower++;
		}
		swap(data,upper,first);
		if (first < upper -1)
			quicksort(data,first,upper -1);
		if (upper+1 < last)
			quicksort(data,upper+1,last);
	}
	public static void main(String[] args){
		Database db = new ValidationDatabase("C:/WUTemp/cazztrans.txt");
		Vector data = new Vector();
		for (Enumeration e = db.elements(); e.hasMoreElements();){
			ItemSet curr = (ItemSet)e.nextElement();
			curr.setWeight(Math.random());
			data.addElement(curr);
		}
		sort(data);
	}

	/**
	 * @param vector
	 * @param vector2
	 * @return
	 */
	public static Vector merge(Vector data1, Vector data2) {
		Vector result = new Vector(data1.size()+data2.size());
		int i = 0, j = 0;
		while (i < data1.size() && j < data2.size()) {
			if (((Comparable)data1.get(i)).compareTo(data2.get(j)) < 0)
				result.add(data1.get(i++));
			else
				result.add(data2.get(j++));
		}
		for (; i < data1.size(); i++)
			result.add(data1.get(i));
		for (; j < data2.size(); j++)
			result.add(data2.get(j));
		return result;
	}
}
