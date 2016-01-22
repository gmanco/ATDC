/*
 * Created on 8-dic-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package transaction;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import core.Item;
import core.ItemSet;
import core.data.Database;

/**
 * @author Giuseppe Manco
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class Partition {
	boolean useWeightedQuality = false;

	Vector clusters;

	Database data;

	public double getQuality() {
		double quality = 0;
		if (!useWeightedQuality) {
			for (int i = 0; i < clusters.size(); i++) {
				Cluster curr = (Cluster) clusters.get(i);
				quality += curr.getSizeFactor() * curr.getQuality();
			}
		} else {
			double currQuality;
			double nOfItemSets = data.size();
			for (int i = 0; i < clusters.size(); i++) {
				Cluster curr = (Cluster) clusters.get(i);
				currQuality = 0;
				double lsize = curr.size();
				for (Enumeration e = curr.frequencies.keys(); e
						.hasMoreElements();) {
					Item a = (Item) e.nextElement();
					double lf = curr.getFrequency(a);
					double gf = (double) Item.getFrequency(a);
					double lp = lf / lsize;
					double gp = gf / nOfItemSets;
					currQuality += getWeight(a) * (lp * lp - gp * gp);
				}
				double sf = curr.getSizeFactor();
				quality += sf * currQuality;
			}
		}
		return quality;

	}

	public Partition(Database d) {
		clusters = new Vector();
		data = d;
	}

	public Cluster getCluster(int i) {
		return (Cluster) clusters.get(i);
	}

	public Cluster generateNewCluster() {
		Cluster c = new Cluster(data, this);
		clusters.add(c);
		return c;
	}

	/**
	 * @param data2
	 */
	public void initialize(Database data) {
		Cluster c = new Cluster(data, this);
		for (Enumeration e = data.elements(); e.hasMoreElements();)
			c.assign_instance((ItemSet) e.nextElement());
		clusters.add(c);
	}

	/**
	 * @param partition
	 * @param p
	 */
	public void stabilizeClusters() {
		for (Enumeration e = data.elements(); e.hasMoreElements();) {
			ItemSet x = ((ItemSet) e.nextElement());
			Cluster c_v = this.getMaxCondQualityCluster(x);
			if (c_v != null) {
				Cluster c_u = (Cluster) x.getM_cluster();
				double value1 = this.getQuality();
				c_v.assign_instance(x);
				double value2 = this.getQuality();
				if (value1 > value2)
					c_u.assign_instance(x);
				else {
					if (c_u.size() == 0)
						this.remove(c_u);
				}
			}// endif
		}// for
	}

	public String printConfusionMatrix() {
		String res = "";
		Object[] classes = data.getClasses();
		int cidx = 0;

		if (classes != null) {
			res += "Class frequencies:\n";
			res += "Actual class->\t";
			for (int i = 0; i < classes.length; i++)
				res += classes[i] + "\t";
			for (int i = 0; i < this.size(); i++) {
				res += "\nCluster " + cidx + ":\t";
				Hashtable t = this.get(i).getClassFrequencies();
				for (int j = 0; j < classes.length; j++)
					if (t.containsKey(classes[j]))
						res += t.get(classes[j]) + "\t";
					else
						res += "0.0\t";
				cidx++;
			}
		}

		return res;
	}

	public String toString() {
		Evaluator evaluator = new Evaluator(getConfusionMatrix(), true);
		String result = "Quality = " + getQuality();
		Cluster c;
		for (int i = 0; i < clusters.size(); i++) {
			c = (Cluster) clusters.get(i);
			result += "\n" + "Cluster" + i + "\n\n" + c;
			result += "\n\n";
		}
		result += "\n" + printConfusionMatrix();
		result += "\n\nF-index:\t\t " + evaluator.getFMeasure();
		result += "\nJaccard statistic:\t " + evaluator.getJaccMeasure();
		result += "\nRand statistic:\t\t " + evaluator.getRandMeasure();
		result += "\nFowles statistic:\t " + evaluator.getFowlMeasure();
		result += "\ngamma statistic:\t " + evaluator.getGammaMeasure();
		result += "\nError:\t\t\t " + evaluator.getError();
		result += "\nWeigthed Error:\t\t " + evaluator.getWeigthedError();

		return result;
	}

	private double[][] getConfusionMatrix() {
		Object[] classes = data.getClasses();
		if (classes != null) {
			double[][] mat = new double[this.size() + 1][classes.length + 1];
			for (int i = 0; i < this.size(); i++) {
				Hashtable t = this.get(i).getClassFrequencies();
				for (int j = 0; j < classes.length; j++) {
					if (t.containsKey(classes[j]))
						mat[i][j] = ((Double) t.get(classes[j])).doubleValue();
					else
						mat[i][j] = 0;
					mat[i][mat[0].length - 1] += mat[i][j];
					mat[mat.length - 1][j] += mat[i][j];
					mat[mat.length - 1][mat[0].length - 1] += mat[i][j];
				}
			}
			return mat;
		}
		return null;
	}

	// /**
	// * @return
	// */
	// public double getFMeasure() {
	// double mat[][] = getConfusionMatrix();
	// if (mat != null){
	// int m= mat.length, n =mat[0].length;
	// double f[] = new double[n-1];
	// double prec, rec,find;
	//			
	// for (int i = 0; i < m-1; i++)
	// for (int j = 0; j < n-1; j++){
	// prec = mat[i][j]/mat[i][n-1];
	// rec = mat[i][j]/mat[m-1][j];
	// if(prec != 0 && rec != 0)
	// find = 2.0*prec*rec/(prec + rec);
	// else
	// find = 0;
	// if (find > f[j])
	// f[j] = find;
	// }
	// find = 0;
	// for (int j = 0; j < f.length; j++)
	// find += f[j]*mat[m-1][j]/mat[m-1][n-1];
	// return find;
	// }
	// return -1;
	// }

	/*
	 * aggiunto il 10/12/2004
	 * 
	 * Francesco & Eugenio
	 * 
	 */
	public Cluster getMaxCondQualityCluster(ItemSet x) {
		boolean compare;
		Cluster c = null;
		double qual = Double.NEGATIVE_INFINITY;
		double a_q, a_cq;

		Cluster act = (Cluster) x.getM_cluster();
		a_cq = act.getCondQuality(x, false);
		a_q = act.getQuality();
		for (int i = 0; i < this.size(); i++) {
			Cluster curr = this.get(i);
			if (curr != act) {
				double c_q = curr.getQuality(), c_cq = curr.getCondQuality(x,
						true);
				if ((a_q + c_q < a_cq + c_cq) && (qual < a_cq + c_cq)) {
					qual = a_cq + c_cq;
					c = curr;
				}
			}
		}
		return c;
	}

	/**
	 * @param ck
	 */
	public void remove(Cluster c) {
		clusters.remove(c);
	}

	/**
	 * @return
	 */
	public int size() {
		return clusters.size();
	}

	/**
	 * @param i
	 * @return
	 */
	public Cluster get(int i) {
		return (Cluster) clusters.get(i);
	}

	/**
	 * @return
	 */
	public boolean generateClusters() {
		boolean compare;
		boolean newclusters = false;
		Cluster C1, C2;
		TreeSet queue = new TreeSet(clusters);
		for (Iterator it = queue.iterator(); it.hasNext();) {
			double value1 = this.getQuality();
			C1 = (Cluster) it.next();
			System.out.print("Checking cluster " + C1 + "...");
			C2 = this.generateNewCluster();
			C1.Split(C2);
			System.out.print("done.");
			double value2 = this.getQuality();
			compare = (value2 <= value1);
			if ((C2.size() == 0) || compare) {
				System.out.println("[Split rejected].");
				C1.restore(C2);
				this.remove(C2);
			} else {
				// System.err.print(" -> " + value2);
				System.out.println("[Split accepted. Overall quality: "
						+ Math.floor(value2 * 10000) / 10000 + "].");
				C1.validate();
				C2.validate();
				newclusters = true;
				break;
			}
		}
		return newclusters;
	}

	/**
	 * @param result
	 */
	public void print(PrintStream result) {
		if (result != null) {
			result.print("<XML>\n");
			for (int i = 0; i < clusters.size(); i++)
				((Cluster) clusters.get(i)).print(result, i);
			result.print("</XML>\n");
		}
	}

	/**
	 * @param itm
	 * @return
	 */
	public double getWeight(Item item) {
		if (clusters.size() <= 1)
			return 1;
		int sz = clusters.size();
		double w = 1.0;// + 1.0 / Math.log(sz);
		for (int i = 0; i < sz; i++) {
			Cluster c = (Cluster) clusters.get(i);
			double prob = 0;
			if (c.size() != 0)
				prob = c.getFrequency(item) / c.size();
			if (prob != 0)
				w += prob * Math.log(prob);
		}
		return w;
	}

}
