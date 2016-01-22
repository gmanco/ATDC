/*
 * Created on 5-apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package utils.postprocessor;

import java.util.HashMap;
import java.util.Vector;

public class Cluster {
	Vector frequencies;
	HashMap myitems;

	int size;

	Vector elems;

	Cluster() {
		frequencies = new Vector();
		elems = new Vector();
		myitems = new HashMap();
	}

	protected void insertItem(String item, double val) {
		frequencies.add(new Item(item,val));
		myitems.put(item,new Double(val));
	}
	void sort(){
		Item a,b;
		for (int i = frequencies.size()-1; i >=1;i--){
			for (int j = 1; j < i; j++){
				a = (Item)frequencies.get(j-1);
				b = (Item)frequencies.get(j);
				if (a.frequency < b.frequency){
					frequencies.set(j-1,b);
					frequencies.set(j,a);
				}
				
			}
		}

	}

	/**
	 * @param curr
	 * @return
	 */
	public boolean contains(String curr) {
		return myitems.containsKey(curr);
	}
}
