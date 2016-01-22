/*
 * Created on 4-dic-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package transaction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;
import java.util.Enumeration;

import core.ItemSet;
import core.data.*;

/**
 * @author Giuseppe Manco
 */

public class Clusterer {
	private static Database data;
	private static boolean arffile;
	private static String m_filename;
	private static String[] as;
	private static PrintStream out = System.out;
	public static PrintStream log;
	private static PrintStream result;

	/**
	 * @param database
	 * @param b
	 */
	public Clusterer(Database database) {
		data = database;
	}

	public static void main(String[] args) {
		try {
			processOptions(args);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		long start = System.currentTimeMillis(), end;
//		Clusterer c = new Clusterer(new ArffDatabase("C:/WUTemp/uci/vote.arff"));//synth/T10.D100.N100.C2.data
		Clusterer c = new Clusterer(data);
/*
for (Enumeration e = data.elements(); e.hasMoreElements();){
	ItemSet i = (ItemSet)e.nextElement();
	System.out.println(i);
}
*/
		Partition p = c.generatePartition();
		end = System.currentTimeMillis();
		out.println("\n"+p);
		out.println("Elapsed time: "+((end-start)/1000)+"s");
		p.print(result);
	}


	private static void processOptions(String[] options) throws FileNotFoundException {
		boolean trace = false,res = false;
			for (int i = 0; i < options.length; i++) {
				if (options[i].equals("-arff"))
					arffile = true;
				else if (options[i].equals("-a")){
					String tokens;
					StringTokenizer tok = new StringTokenizer(options[i+1],",");
					as = new String[tok.countTokens()];
					for (int j = 0; tok.hasMoreTokens(); j++)
						as[j] = tok.nextToken();
					 i++;
				} else if (options[i].equals("-out")){
					out = new PrintStream(new FileOutputStream(options[i+1]));
					i++;
				} else if (options[i].equals("-trace"))
					trace = true;
				 else if (options[i].equals("-print"))
					res = true;
				else
					m_filename = options[i];
			}
			long start = System.currentTimeMillis();
			System.out.print("Loading file "+m_filename+"... ");
			if (arffile){
				if (as != null)
					data = new ArffDatabase(m_filename,as);
				else
					data = new ArffDatabase(m_filename);
			}
			else
				data = new ValidationDatabase(m_filename);
			System.out.println("["+data.numOfItems()+" item(s), "+data.size()+" set(s)] done ["+((System.currentTimeMillis()-start)/1000.0)+"s].");
			if (trace){
				log = new PrintStream(new FileOutputStream(m_filename+".log"));
			}
			if (res){
				result = new PrintStream(new FileOutputStream(m_filename+".xml"));
			}

	}

	/**
	 * @return
	 */
	public Partition generatePartition() {
		boolean compare;
		Partition partition = new Partition(data);
		partition.initialize(data);
		System.out.println("Initial Cluster Quality: "+partition.getQuality());

		do {
			if (partition.generateClusters()){
				System.out.print("Stabilizing clusters... ");
				partition.stabilizeClusters();
				System.out.println("done [Overall quality: "+Math.floor(partition.getQuality()*10000)/10000+"].");
			}
			else
				break;
		} while (true);
		return partition;
	}
}
