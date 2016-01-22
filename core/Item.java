package core;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

/*
 * Created on 11-dic-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
/**
 * @author Giuseppe Manco
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Item {
	int val;
	static Hashtable ston = new Hashtable();
	static Vector ntos = new Vector();
	static Vector freqs = new Vector();
	static int numOfItems = 0;
	public static String lookup(int i){
		String res = (String)ntos.get(i);
		return res;
	}
	public static int insert(String val){
		if (ston.containsKey(val)){
			int res = ((Integer)ston.get(val)).intValue();
			int frequency = ((Integer)freqs.get(res)).intValue();
			freqs.set(res,new Integer(frequency+1));
			return res;
		}
		else {
			ston.put(val, new Integer(numOfItems));
			ntos.add(val);
			freqs.add(new Integer(1));
			numOfItems++;
			return numOfItems-1;
		}
	}
	public Item(String val){
		this.val = insert(val);
	}
	public String toString(){
		return lookup(val);
	}
	public boolean lessThan(Object i){
//		return (this.val < ((Item)i).val);
		return (lookup(val)).compareTo(lookup(((Item)i).val)) < 0; 
	}
	public boolean lessThanOrEqual(Object i){
//		return (this.val <= ((Item)i).val);
		return (lookup(val)).compareTo(lookup(((Item)i).val)) <= 0; 
	}
	public boolean equals(Object i){
		return (this.val == ((Item)i).val);
	}
	public int hashCode(){
		return val;
	}
	public static void main(String[] args){
		Item a = new Item("a");
		Item b = new Item("b");
		Item c = new Item("a");
		
		HashSet s = new HashSet();
		s.add(a);
		s.add(b);
		if (s.contains(c))
			System.out.println("ok");
		else
			System.out.println("no");
	}
	/**
	 * @return
	 */
	public static long getNOfItems() {
		// TODO Auto-generated method stub
		return numOfItems;
	}
	public static long getFrequency(Item a){
		return ((Integer)freqs.get(a.val)).intValue();
	}
}
