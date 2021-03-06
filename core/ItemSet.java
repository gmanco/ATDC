package core;
import java.util.*;
/**
 * This class was generated by a SmartGuide.
 *  
 */
public class ItemSet implements Comparable {
	private SortVector container;
	private HashSet elems;
	private boolean sorted = false;
	private double support;
	private Object m_cluster;
	private boolean xml = false;
	private String m_class;
	private double weight;
	private int id;
	/**
	 * Insert the method's description here. Creation date: (08/02/2003
	 * 21.08.00)
	 */
	public ItemSet() {
		container = new SortVector();
	}
	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @param obj
	 *            java.lang.Object
	 */
	public void addElement(Object obj) {
		container.addElement(obj);
		container.sort();
	}
	/**
	 * Insert the method's description here. Creation date: (08/02/2003
	 * 16.48.40)
	 * 
	 * @return Patterns.ItemSet
	 */
	public boolean contains(ItemSet s) {
		/*
		 * int i =0, j=0; Compare cmp = container.getCompare(); boolean test =
		 * true;
		 * 
		 * while (test && (i < size()) && (j < s.size())){ if
		 * (cmp.lessThan(elementAt(i),s.elementAt(j))) i++; else if
		 * (cmp.equal(elementAt(i),s.elementAt(j))) j++; else if
		 * (cmp.lessThan(s.elementAt(j),elementAt(i))) test = false; } return
		 * test;
		 */
		return container.containsAll(s.container);
	}
	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return java.lang.Object
	 * @param index
	 *            int
	 */
	public Object elementAt(int index) {
		if (!sorted) {
			container.sort();
			sorted = true;
		}
		return container.elementAt(index);
	}
	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return java.util.Enumeration
	 */
	public Enumeration elements() {
		if (!sorted) {
			container.sort();
			sorted = true;
		}
		return container.elements();
	}
	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return java.util.Enumeration
	 */
	public Iterator iterator() {
		if (!sorted) {
			container.sort();
			sorted = true;
		}
		return container.iterator();
	}

	/**
	 * Insert the method's description here. Creation date: (13/02/2003
	 * 12.55.05)
	 * 
	 * @return double
	 */
	public double getSupport() {
		return support;
	}
	public Object getM_cluster() {
		return m_cluster;
	}
	public void setM_cluster(Object c) {
		m_cluster = c;
	}
	/**
	 * Insert the method's description here. Creation date: (12/02/2003
	 * 19.23.25)
	 * 
	 * @param args
	 *            java.lang.String[]
	 */
	public static void main(String[] args) {
		ItemSet s = new ItemSet();
		ItemSet p = new ItemSet();
		s.addElement(new Item("luigi"));
		s.addElement(new Item("paolo"));
		p.addElement(new Item("luigi"));
		p.addElement(new Item("paolo"));
		p.addElement(new Item("eloiso"));
		System.out.println(s);
		System.out.println(p);
		if (s.contains(p))
			System.out.println("ok");
		else
			System.out.println("no");
		if (p.contains(s))
			System.out.println("ok");
		else
			System.out.println("no");
	}
	/**
	 * Insert the method's description here. Creation date: (13/02/2003
	 * 12.55.05)
	 * 
	 * @param newSupport
	 *            double
	 */
	public void setSupport(double newSupport) {
		support = newSupport;
	}
	/**
	 * Insert the method's description here. Creation date: (13/02/2003
	 * 12.55.05)
	 * 
	 * @param newSupport
	 *            double
	 */
	public void setWeight(double newWeight) {
		weight = newWeight;
	}
	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return int
	 */
	public int size() {
		return container.size();
	}
	/**
	 * Insert the method's description here. Creation date: (08/02/2003
	 * 12.29.52)
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		if (xml) {
			String heading = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			String result = heading + "<ITEMSET size=\"" + size()
					+ "\" support=\"" + getSupport() + "\">\n";
			for (Enumeration e = elements(); e.hasMoreElements();) {
				result += "\t" + e.nextElement() + "\n";
			}
			result += "</ITEMSET>";
			return result;
		} else {
			String res = "{";
			res += "ID=" + id + ",";
			int sz = size();
			for (int i = 0; i < sz; i++) {
				res += elementAt(i);
				if (i != sz - 1)
					res += ", ";
			}
			res += "}";
			return res;
		}
	}
	/**
	 * @param cclass
	 */
	public void setM_class(String cclass) {
		m_class = new String();
		m_class += cclass;
	}
	/**
	 * @return
	 */
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}
	/**
	 * @return
	 */
	public String getM_class() {
		// TODO Auto-generated method stub
		return m_class;
	}
	/**
	 * @param a
	 * @return
	 */
	public boolean contains(Item a) {
		return container.contains(a);
	}
	/**
	 * @param setcnt
	 */
	public void setid(int setcnt) {
		id = setcnt;
		
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		if ( (this.weight - ((ItemSet)arg0).weight < 0) 
		|| ((this.weight == ((ItemSet)arg0).weight) && (this.getId() < ((ItemSet)arg0).getId()))
		   )
			return -1;
		if ( (this.weight - ((ItemSet)arg0).weight > 0) 
		|| ((this.weight == ((ItemSet)arg0).weight) && (this.getId() > ((ItemSet)arg0).getId()))
		   )
			return 1;
			
		return 0;
	}
	/**
	 * @return
	 */
	public double getWeight() {
		// TODO Auto-generated method stub
		return weight;
	}
}