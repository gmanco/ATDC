/*
 * Created on 16-mar-2005,19.15.48
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package core.data;

import java.util.Iterator;
import java.util.Vector;

import core.*;

/**
 * @author Eugenio Cesario
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
/*
public class DataContainer {
	class DCIterator extends DataContainer implements Iterator {
		int pos;
		Iterator it;

		DCIterator() {
			pos = 0;
			if (container[pos] != null)                //non dovrei cercare la prima posizione con oggetti?
				it = container[pos].iterator();
			else
				it = null;
		}

		public void remove() {
			if (it != null)
				it.remove();
		}

		public boolean hasNext() {
			// 	TODO Auto-generated method stub
			return ((it != null) && (it.hasNext() || (pos < container.length)));
			//non dovrei verificare che se it.hasNext() ==  
		}

		public Object next() {
			while ((it == null)
				|| (!it.hasNext() && (pos < container.length))) {
				pos++;
				if (container[pos] != null)
					it = container[pos].iterator();
			}
			if (hasNext())
				return it.next();
			else
				return null;
		}
	}
*/	

public class DataContainer {
	class DCIterator implements Iterator {
		int pos;
		Iterator it;

		DCIterator() {
			pos = 0;
			boolean found = false;
			//search the first element in container
			while (pos < container.length && !found){
				if (container[pos] != null)
					found = true;
				else
					pos++;	    
			}//while
			if (found)
				it = container[pos].iterator();	
			else
				it = null;
		}//Costruttore

		public void remove() {
			if (it != null)
				it.remove();
		}

		public boolean hasNext() {
			boolean hasnext = false;                                 
			if (it == null)                       				//if "it" is null
				hasnext = false;                       				//return false
			else if (it.hasNext())               				//if "it" has next
				hasnext = true;                        				//return true
			else{                                 				//else find the next available 
				int posTemp = pos + 1;            				//element in the structure
				boolean found = false;            				//...
				while (posTemp < container.length && !found){   //...
					if (container[posTemp] != null 
					 && container[posTemp].size() > 0)          //...
						found = true;                           //...
					posTemp++;                                  //...
				}//while                                        //... 
				if (found)                                      //if a next element exists
					hasnext = true;                                  //return true
			}//else
			return hasnext;
		}//hasNext

		public Object next() {
			if (it == null)
				return null;
			else if (it.hasNext())
				return it.next();
			else{
				boolean found = false;
				while ( (pos + 1 < container.length) && !found){
					pos++;
					if (container[pos] != null 
                      && container[pos].size() > 0){
						found = true;
						it = container[pos].iterator();
					}//if
				}//while
				if (found)
					return it.next();
				else
					return null;
			}//else
		}//next
	}//DCIterator



	private Vector[] container;
	private final static int DEFAULT_SIZE = 1000; //1000
	private final static int DEFAULT_INCR = 100;  //100
	private final static int MAX_ALLOWED_LOAD = 50; //50
	protected int size = 0;
	public DataContainer() {
		container = new Vector[DEFAULT_SIZE];
	}

	public void add(ItemSet x) {
		int pos = hash(x);

		if (container[pos] == null)
			container[pos] = new Vector();
		boolean found = false;
		for (int i = 0; i < container[pos].size() && !found; i++)
			if (container[pos].get(i).equals(x))
				found = true;
		if (!found) {
			container[pos].addElement(x);
			size++;
		}
		int load = size / container.length;
		if (load > MAX_ALLOWED_LOAD)
			restructure();
	}
	/**
	 * 
	 */
	private void restructure() {
		Vector[] containerOld = container;
		container = new Vector[size];
		for (int i = 0; i < containerOld.length; i++)
			for (Iterator it = containerOld[i].iterator(); it.hasNext();) {
				ItemSet x = (ItemSet) it.next();
				int pos = hash(x);

				if (container[pos] == null)
					container[pos] = new Vector();
				boolean found = false;
				for (int k = 0; k < container[pos].size() && !found; k++)
					if (container[pos].get(k).equals(x))
						found = true;
				if (!found)
					container[pos].addElement(x);
			}
	}

	public void remove(ItemSet x) {
		int pos = hash(x);

		if (container[pos] != null) {
			boolean found = false;
			for (Iterator it = container[pos].iterator();
				it.hasNext() && !found;
				)
				if (it.next().equals(x)) {
					it.remove();
					found = true;
					size--;
				}
			if (found && container[pos].size() == 0)
				container[pos] = null; 
		}
	}
	public boolean contains(ItemSet x) {
		int pos = hash(x);

		if (container[pos] == null)
			return false;
		boolean found = false;
		for (int i = 0; i < container[pos].size() && !found; i++)
			if (container[pos].get(i).equals(x))
				found = true;
		return found;
	}
	private int hash(ItemSet x) {
		int key = x.getId();
		key += (key << 12);
		key ^= (key >> 22);
		key += (key << 4);
		key ^= (key >> 9);
		key += (key << 10);
		key ^= (key >> 2);
		key += (key << 7);
		key ^= (key >> 12);
		return key % container.length;
	}

	public Iterator iterator() {
		return new DCIterator();
	}


	public static void main(String[] args){
		DataContainer dc = new DataContainer();
		ItemSet x1 = new ItemSet();
		ItemSet x2 = new ItemSet();
		ItemSet x3 = new ItemSet();
		ItemSet x4 = new ItemSet();

		x1.addElement(new Item("a1"));

		x2.addElement(new Item("a2"));

		x3.addElement(new Item("a3"));

		x4.addElement(new Item("a4"));

		dc.add(x1);
		dc.add(x2);
		dc.remove(x2);
		
		Iterator it = dc.iterator();
		while (it.hasNext()){
			ItemSet is = (ItemSet)it.next();
			System.out.println(is);
		}
		
	} 
}
