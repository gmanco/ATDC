/*
 * Created on 4-dic-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package transaction;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import utils.Utils;
import utils.Console;

import core.ExtMath;
import core.Item;
import core.ItemSet;
import core.data.*;


/**
 * @author Giuseppe Manco
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class Cluster extends Database implements Comparable {
	static short idcounter = 0;
	boolean useWeightedFrequencies = false;
	Database data;
	HashSet elements;
	Hashtable frequencies;
	int nOfItemSets;
	int nOfItems;
	double sumOfSizes;
	double CategoryUtility;
	double globalSquaredFrequencies;
	double localSquaredFrequencies;
	short id;
	private Partition partition;

	class ClusterEnumeration extends DataEnumeration {
		Iterator it;

		private boolean checkConditionalMembership;

		protected ClusterEnumeration() {
			checkConditionalMembership = false;
			it = elements.iterator();
			current = nextItemSet();
		}

		protected ItemSet nextItemSet() {
			if (it.hasNext())
				return (ItemSet) it.next();
			else
				return null;
		}
	}

	/**
	 * Cluster constructor comment.
	 */
	public Cluster() {
		load = false;
		sumOfSizes = 0;
		nOfItemSets = nOfItems = 0;
		elements = new HashSet();
		frequencies = new Hashtable();
		id = idcounter++;
		globalSquaredFrequencies = localSquaredFrequencies = 0;

	}

	public Cluster(Database d,Partition p) {
		this();
		data = d;
		partition = p;
	}

	/**
	 * Insert the method's description here. Creation date: (24/09/2003
	 * 20.27.19)
	 * 
	 * @param is
	 *            WebMining.Patterns.ItemSet
	 */

	public void assign_instance(ItemSet is) {
		if (!elements.contains(is)) {
			if (is.getM_cluster()!= null)
				((Cluster)is.getM_cluster()).remove_instance(is);
			elements.add(is);
			is.setM_cluster(this);
			push(is);
		}
	}

	/**
	 * @param is
	 */
	private void push(ItemSet is) {
		removeCUContribution(is);
		nOfItemSets++;
		sumOfSizes += is.size();
		for (Enumeration e = is.elements(); e.hasMoreElements();) {
			Item i = (Item) e.nextElement();
			double val = 0;
			if (frequencies.containsKey(i))
				val = ((Double) frequencies.get(i)).doubleValue();
			else
				nOfItems++;
			frequencies.put(i, new Double(val + 1));
		}
		addCUContribution(is);
		updateQuality();
	}

	/**
	 * 
	 */
	private void updateQuality() {
		double sz = data.size();
		if (sz == 0)
			CategoryUtility = 0;
		else
			CategoryUtility = 1.0/nOfItemSets*localSquaredFrequencies/nOfItemSets 
				- 1.0/sz*globalSquaredFrequencies/sz;		
	}

	/**
	 * @param is
	 * @return
	 */
	private void removeCUContribution(ItemSet is) {
		double pca, pa, ja;
		double result = 0;

		for (Enumeration e = is.elements(); e.hasMoreElements();) {
			Item a = (Item) e.nextElement();
			if (frequencies.containsKey(a))
				ja = ((Double) frequencies.get(a)).doubleValue();
			else ja = 0;
			double weight;
			if (ja != 0){
				if (useWeightedFrequencies)
					weight = this.partition.getWeight(a);
				else 
					weight = 1;
				pa = Item.getFrequency(a);
				pca = ja;
				localSquaredFrequencies -= pca*pca*weight;
				globalSquaredFrequencies -= pa*pa*weight;
			}
		}
	}
	/**
	 * @param is
	 * @return
	 */
	private void addCUContribution(ItemSet is) {
		double pca, pa, ja;

		for (Enumeration e = is.elements(); e.hasMoreElements();) {
			Item a = (Item) e.nextElement();
			if (frequencies.containsKey(a))
				ja = ((Double) frequencies.get(a)).doubleValue();
			else ja = 0;
			if (ja != 0){
				double weight;
				if (useWeightedFrequencies)
					weight = this.partition.getWeight(a);
				else 
					weight = 1;
				pca = ja;
				localSquaredFrequencies += pca*pca*weight;
				pa = Item.getFrequency(a);
				globalSquaredFrequencies += pa*pa*weight;
			}
		}
	}

	/**
	 * Insert the method's description here. Creation date: (24/09/2003
	 * 20.27.19)
	 * 
	 * @param is
	 *            WebMining.Patterns.ItemSet
	 */
	public void remove_instance(ItemSet is) {
		if (elements.contains(is))
			elements.remove(is);
		is.setM_cluster(null);
		pop(is);
	}

	/**
	 * @param is
	 */
	private void pop(ItemSet is) {
		removeCUContribution(is);
		nOfItemSets--;
		sumOfSizes -= is.size();
		for (Enumeration e = is.elements(); e.hasMoreElements();) {
			Item i = (Item) e.nextElement();
			double weight;
			if (useWeightedFrequencies)
				weight = 1.0 / is.size();
			else
				weight = 1.0;
			if (frequencies.containsKey(i)) {
				double val = ((Double) frequencies.get(i)).doubleValue();
				if (val -weight == 0) {
					frequencies.remove(i);
					nOfItems--;
				} else
					frequencies.put(i, new Double(val-weight));
//				decrementQuality(is,i,val,weight);
			}
		}
		addCUContribution(is);
		updateQuality();
	}

	/**
	 * Insert the method's description here. Creation date: (24/09/2003
	 * 19.43.31)
	 * 
	 * @return java.util.Enumeration
	 */
	public Enumeration getEnumeration() {
		return new ClusterEnumeration();
	}

	public double getQuality() {
		double sf = getSizeFactor();
//		return  sf * getClusterUtility();
		return  sf * CategoryUtility;
	}

	protected double getClusterUtility() {
//		if (nOfItemSets == 0)
//			return Double.NEGATIVE_INFINITY;
		double pca, pa, ja;
		double result = 0;

		for (Enumeration e = frequencies.keys(); e.hasMoreElements();) {
			Item a = (Item) e.nextElement();
			ja = ((Double) frequencies.get(a)).doubleValue();
			double weight;
				if (useWeightedFrequencies)
					weight = this.partition.getWeight(a);
				else 
					weight = 1;
			pca = ja / nOfItemSets;
			pa = Item.getFrequency(a) * 1.0 / data.size();
			result += pca * pca*weight - pa * pa*weight; 
		}
		return result;
	}

	/**
	 * Insert the method's description here. Creation date: (24/09/2003
	 * 19.43.31)
	 * 
	 * @return long
	 */
	public long size() {
		return nOfItemSets;
	}

	/**
	 * Returns a String that represents the value of this object.
	 * 
	 * @return a string representation of the receiver
	 */
	public String toString() {
		String res = "id = "+this.id+" [Quality = " + Math.floor(this.getQuality()*10000)/10000;
//		res += " (JFactor = " + this.getJFactor() / nOfItems;
//		res += ", Entropy = " + getEntropy();
		res += " (Utility = " + Math.floor(getClusterUtility()*10000)/10000;
		res += ", size = " + size() + ")";
/*		res += "\nfrequencies:\n";
		for (Enumeration e = frequencies.keys(); e.hasMoreElements();) {
			Item a = (Item) e.nextElement();
			res += "\t" + a + "  -> " + Math.floor(((Double)frequencies.get(a)).doubleValue()*100.0/nOfItemSets*100)/100 
				+ " (weigth: "+partition.getWeight(a)+")\n";
		}
		*/
		res += "]";
		return res;
	}
	
	public String print() {
		String res =
			"id = "
				+ this.id
				+ " [Quality = "
				+ Math.floor(this.getQuality() * 10000) / 10000;
		//		res += " (JFactor = " + this.getJFactor() / nOfItems;
		//		res += ", Entropy = " + getEntropy();
		res += " (Utility = " + Math.floor(getClusterUtility() * 10000) / 10000;
		res += ", size = " + size() + ")";
		res += "\nfrequencies:\n";
		for (Enumeration e = frequencies.keys(); e.hasMoreElements();) {
			Item a = (Item) e.nextElement();
			res += "\t" + a + "  -> " + ((Double)frequencies.get(a)).doubleValue()*100.0/nOfItemSets + "\n";
		}
		res += "\nmembership:\n";
		for (Enumeration e = elements(); e.hasMoreElements();) {
			res += "\t" + e.nextElement();
			if (e.hasMoreElements())
				res += ",\n";
			else 
				break;
		}
		res += "\n";
		res += "]";
		return res;
	}

	/**
	 * @return
	 */
	protected double getSizeFactor() {
//		if (nOfItemSets == 0)
//			return 1;
		double cfreq = ((double) nOfItemSets) / data.size();
		return cfreq; // ExtMath.log(cfreq + 1);
		// - ExtMath.log(nOfItems);
	}

	public static void main(String[] args) {
		Database db = new ValidationDatabase("D:\\Eugenio\\A_PNew\\Data\\cazztrans.txt");
		Partition p = new Partition(db);
		Cluster c1 = new Cluster(db,p);
		Cluster c2 = new Cluster(db,p);
		Cluster c3 = new Cluster(db,p);

		ItemSet x1 = new ItemSet();
		ItemSet x2 = new ItemSet();
		ItemSet x3 = new ItemSet();
		ItemSet x4 = new ItemSet();

		x1.addElement(new Item("a1"));
		x1.addElement(new Item("a2"));
		x1.addElement(new Item("a3"));
		x1.addElement(new Item("a4"));
		x1.addElement(new Item("a5"));

		x2.addElement(new Item("a1"));
		x2.addElement(new Item("a6"));
		x2.addElement(new Item("a10"));

		x3.addElement(new Item("a1"));
		x3.addElement(new Item("a2"));
		x3.addElement(new Item("a3"));
		x3.addElement(new Item("a7"));
		x3.addElement(new Item("a8"));
		x3.addElement(new Item("a9"));

		x4.addElement(new Item("a1"));
		x4.addElement(new Item("a10"));
		x4.addElement(new Item("a11"));

		// c1.assign_instance(x1);
		c1.assign_instance(x1);
		c1.assign_instance(x2);
		c1.assign_instance(x3);
		c2.assign_instance(x4);

		System.out.println("Quality(C1)= " + c1.getQuality());
		System.out.println("Quality(C2)= " + c2.getQuality());
		System.out.println("Quality(C3)= " + c3.getQuality());
		System.out.println("Quality(C2)+Quality(C3)= "
				+ (c2.getQuality() + c3.getQuality()));
		System.out
				.println("Quality(P)= "
						+ (c1.getQuality() + c2.getQuality() + c3.getQuality() - 4 * ExtMath
								.log(4)));

	}
	/**
	 * @return
	 */
	private ItemSet extractMaximalItemSet() {
		ItemSet x = null;
		boolean compare;
		double qual = Double.NEGATIVE_INFINITY;
		double cq;
		for (Enumeration e = this.elements(); e.hasMoreElements();) {
			ItemSet curr = (ItemSet) e.nextElement();
			pop(curr);
			cq = getQuality();
			push(curr);
			if (cq > qual) {
				qual = cq;
				x = curr;
			}
		}
		return x;
	}
	/**
	 * @param x
	 * @param b
	 * @return
	 */
	public double getCondQuality(ItemSet x, boolean union) {
		double res;
		if (!union) {
			if (!elements.contains(x))
				return getQuality();
			else {
				pop(x);
				res = getQuality();
				push(x);
			}
		} else {
			if (elements.contains(x))
				return getQuality();
			else {
				push(x);
				res = getQuality();
				pop(x);
			}
		}
		return res;
	}

	/**
	 * 
	 */
	public void restore(Cluster c) {
		for (Iterator it = c.elements.iterator(); it.hasNext();) {
			ItemSet x = (ItemSet) it.next();
			push(x);
			this.elements.add(x);
			x.setM_cluster(this);
			it.remove();
		}
	}

	/**
	 * 
	 */
	public void validate() {
	}

	/**
	 * @return
	 */
	public double getNOfItems() {
		return nOfItems;
	}

	private boolean checkSwapConvenience(Cluster c1, Cluster c2,ItemSet x){
		double q1 = c1.getQuality(), q2 = c2.getQuality();
		double cq1 = c1.getCondQuality(x, false);
		double cq2 = c2.getCondQuality(x, true);
		if ((q1 + q2) < (cq1 + cq2)) 
			return true;
		return false;
	}
	
	/**
	 * @param c1
	 * @param c2
	 */
	public void Split(Cluster c) {
		double cq1, cq2;
		ItemSet x;
		Vector curdata = initDataPoints(); 
//		Vector curdata = new Vector(this.elements); 


//		x = initCluster(c);
/*		x = this.extractMaximalItemSet();
		if ((x != null) && checkSwapConvenience(this,c,x)){
			c.assign_instance(x);
System.out.println("x="+x.getId()+",Q="+this.getQuality());			
		}
*/		
		if (Clusterer.log != null)
			Clusterer.log.println();
		while (true) {
//			Vector curdata = Utils.merge(initDataPoints(),c.initDataPoints());
			if (Clusterer.log != null)
				Clusterer.log.println((this.getQuality() + c.getQuality()));
			boolean swapped = false;
			for (Iterator it = curdata.iterator(); it.hasNext();) {
				x = (ItemSet) it.next();
				Cluster c1 = (Cluster)x.getM_cluster();
				Cluster c2 = (c1!=this)?this:c;
				if (checkSwapConvenience(c1,c2,x)) {
					c2.assign_instance(x);
					//System.out.println(x.getId()+","+x.getWeight()+",Q="+this.getQuality());
					//int l = Console.readInt("");
					swapped = true;
				}
			}
			if (!swapped)
				break;
		}
	}

	/**
	 * @param c
	 */
	private ItemSet initCluster(Cluster c) {
		ItemSet x = null;
		double q = this.getQuality();
		for (Iterator it = this.elements.iterator(); it.hasNext(); ){
			ItemSet curr = (ItemSet)it.next(); 
			double cq = this.getCondQuality(curr, false)+ c.getCondQuality(curr, true);
			if (q < cq){
				x = curr;
				q = cq;
			}
		}
		return x;
	}

	/**
	 * Computes, for each itemset, a weight representing
	 * the mean frequency of the items contained within. 
	 * The, sorts the itemsets by increasing weight
	 * @return
	 */
	private Vector initDataPoints() {
		Vector curdata = new Vector(this.elements);
		int length = curdata.size();
		double weight;
		double prob;
		for (int i = 0; i<length; i++){
			weight = 0;			
			ItemSet curr = (ItemSet)curdata.get(i);
	if (curr.getId() == 2337)
		System.out.println();		
			for (int j = 0; j < curr.size(); j++){
				Item citem = (Item)curr.elementAt(j);
				prob = ((Double)this.frequencies.get(citem)).doubleValue();
				prob /= this.nOfItemSets;
//				weight += -prob*Math.log(prob) -(1-prob)*Math.log(1- prob);
				weight += 1 -prob*prob - (1-prob)*(1-prob);
			}
			weight /= curr.size();

if (curr.size() == 0)
	weight = -100000;

			
			curr.setWeight(-weight);
		}
/*		
		int length = curdata.size();
		double avg; 
		double weight;
		for (int i = 0; i<length; i++){
			ItemSet curr = (ItemSet)curdata.get(i);
			avg = 0;
			for (int j = 0; j < curr.size(); j++){
				Item citem = (Item)curr.elementAt(j);
				avg += ((Double)this.frequencies.get(citem)).doubleValue();
			}
			avg /= curr.size();
			double var = 0;
			for (int j = 0; j < curr.size(); j++){
				Item citem = (Item)curr.elementAt(j);
				double fr =((Double)this.frequencies.get(citem)).doubleValue(); 
				var += (fr -avg)*(fr -avg);
			}
			var = 1.0/curr.size()*Math.sqrt(var);
//			weight = Math.abs(avg - this.nOfItemSets/2.0);
			weight = Math.abs(this.nOfItemSets - avg + var);
//			weight = avg - var;
			curr.setWeight(weight);
		}
		*/
		Utils.sort(curdata);
		return curdata;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object) Note: this class
	 *      has a natural ordering that is inconsistent with equals
	 */
	public int compareTo(Object arg0) {
		// if (this.equals(arg0))
		// return 0;
		double val = (this.getQuality() - ((Cluster) arg0).getQuality());
		if (val < 0)
			return -1;
		if (val == 0)
			return 0;
		return 1;
	}

	/**
	 * @param result
	 * @param i
	 */
	public void print(PrintStream result, int i) {
		result.print("<CLUSTER id=\""+i+"\" quality=\""+this.getQuality()+"\">\n");
		result.print("\t<ITEMS size=\""+this.nOfItems+"\">\n");
		for (Enumeration e = frequencies.keys(); e.hasMoreElements();){
			Item curr = (Item)e.nextElement();
			double weight = partition.getWeight(curr);
			double frequency = ((Double)(frequencies.get(curr))).doubleValue()/this.nOfItemSets;
			result.print("\t\t<ITEM name=\""+curr+"\" frequency=\""+frequency+"\"/>\n");
		}
		result.print("\t</ITEMS>\n");
		result.print("\t<ITEMSETS size=\""+this.nOfItemSets+"\">\n");

		ItemSet currits;
		for (Iterator it = elements.iterator(); it.hasNext(); ){
			currits = (ItemSet)it.next();
			int sz = currits.size();
			result.print("\t\t<ITEMSET id=\""+currits.getId()+"\" size=\""+sz+"\" class=\""+currits.getM_class()+"\">\n");
			for (int j = 0; j < sz; j++){
				Item itm = (Item)currits.elementAt(j);
				result.print("\t\t\t<ITEM name=\""+itm+"\"/>\n");
			}
			result.print("\t\t</ITEMSET>\n");
		}	
		result.print("</ITEMSETS>\n");
		result.print("</CLUSTER>\n");
	}

	/**
	 * @param item
	 * @return
	 */
	public double getFrequency(Item item) {
		if (!frequencies.containsKey(item))
			return 0;
		return ((Double)(frequencies.get(item))).doubleValue();
	}

}