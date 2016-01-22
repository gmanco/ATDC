/*
 * Created on 3-giu-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package utils.Rock;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import transaction.Evaluator;
import core.Item;
import core.ItemSet;
import core.data.ArffDatabase;
import core.data.Database;
import core.data.ValidationDatabase;

/**
 * @author Giuseppe Manco
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Rock {

	private static boolean arffile;
	private static String[] as;
	private static String m_filename;
	private static Database data;
	private static PrintStream out;
	private static double theta = 0.77;
	private static int nclasses = 7;
	private static String outfile;
	static Hashtable classnames;

	public static void main(String[] args) throws Exception {
		processOptions(args);
		
		int maxk = 2; 
		double maxtheta = 1;
		double minerr = -1;
		prepareInput();
		for (nclasses = 2; nclasses < 4; nclasses++)
			for (theta = 1; theta > 0.1; theta -= 0.01){
				runRock(nclasses,theta,false);
				double err = printResults();
				if (minerr < err){
					maxk = nclasses;
					maxtheta = theta;
					minerr = err;
				}
			}
		nclasses = maxk;
		theta = maxtheta;
		System.out.println("\n\n\n");
		runRock(nclasses,theta,true);
		printResults();
	}
	
	/**
	 * @throws Exception
	 * 
	 */
	private static double printResults() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(outfile+"-partition"));
		double[][] confmatrix = getConfusionMatrix(reader);
		Evaluator evaluator = new Evaluator(confmatrix,false);
		String result = "";
		result += "\n"+printConfusionMatrix(confmatrix);
		result += "\n\nF-index:\t\t "+evaluator.getFMeasure();
		result += "\nJaccard statistic:\t "+evaluator.getJaccMeasure();
		result += "\nRand statistic:\t\t "+evaluator.getRandMeasure();
		result += "\nFowles statistic:\t "+evaluator.getFowlMeasure();
		double err = evaluator.getGammaMeasure();
		result += "\ngamma statistic:\t "+err;
		result += "\nError:\t\t\t "+evaluator.getError();
		result += "\nWeigthed Error:\t\t "+evaluator.getWeigthedError();
		
		System.out.println(result);

		return Math.abs(err);
	}
	
	/**
	 * @param confmatrix
	 * @return
	 * @throws IOException
	 */
	private static String printConfusionMatrix(double[][] confmatrix) {
		String res = "";
		int cidx = 0;

		res += "Class frequencies:\n";
		res += "Actual class->\t";
		for (Enumeration e = classnames.keys(); e.hasMoreElements();){
			String cname = (String)e.nextElement();
			res += cname + "\t";
		}
		for (int j = 0; j < confmatrix.length; j++){
			res += "\nCluster " + cidx + ":\t";
			for (Enumeration e = classnames.keys(); e.hasMoreElements();){
				String cname = (String)e.nextElement();
				int pos = ((Integer)classnames.get(cname)).intValue();
				res += confmatrix[j][pos] + "\t";
			}
			cidx++;	
		}
		return res;
	}
	/**
	 * @throws IOException
	 * 
	 */
	private static double[][] getConfusionMatrix(BufferedReader reader) throws Exception {
		double vals[][] = new double[nclasses][classnames.size()];
		String line;
		int row,col; 

		for (Enumeration e = data.elements(); e.hasMoreElements();){
			ItemSet x = (ItemSet)e.nextElement();
			String classname = x.getM_class();
			col = ((Integer)classnames.get(classname)).intValue();
			
			line = reader.readLine();
			if (line == null)
				break;
			row = Integer.parseInt(line);
			vals[row][col]++;
		}

		return vals;			
	} 


	/**
	 * @param nclasses2
	 * @param theta2
	 * @throws Exception
	 */
	private static void runRock(int nclasses2, double theta2, boolean print) throws Exception {
		try {
			String command = "C:/WUTemp/Rock.exe  -k"+nclasses2+" -t"+theta2+" "+outfile;
			System.out.println("Running "+command);
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader in
			   = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while (true){
				String line = in.readLine();
				if (line == null)
					break;
				if (print)
					System.out.println(line);
			}
			p.destroy();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static void prepareInput() {
		Enumeration e1, e2;
		classnames = new Hashtable();
		int pos = 0; 
		
		out.println("1 "+data.size()+" 1");
		
		int j,i = 0; 
		for (e1 = data.elements(); e1.hasMoreElements();){
			ItemSet x = (ItemSet) e1.nextElement();
			String classname = x.getM_class();
			if (!classnames.containsKey(classname)){
				classnames.put(classname,new Integer(pos));
				pos++;
			}
			
			j = 0;
			for (e2 = data.elements(); e2.hasMoreElements();){
				if (j <= i){
					e2.nextElement();
				}
				else {
					ItemSet y = (ItemSet)e2.nextElement();
					out.println((1-getDistance(x,y))+" "+(i+1)+" "+(j+1));
				}
				j++;
			}
			i++;
		}			
		out.flush();
	}

	public static double getDistance(ItemSet is1, ItemSet is2) {
		int i, j;
		int size_is1 = is1.size();
		int size_is2 = is2.size();
		int size_intersec = 0;
		int size_union = 0;
		for (i = 0, j = 0; (i < size_is1) && (j < size_is2);) {
			if (((Item) is1.elementAt(i)).equals((Item) is2.elementAt(j))) {
				size_intersec++;
				i++;
				j++;
			} else {
				if (((Item) is1.elementAt(i)).lessThan((Item) is2.elementAt(j)))
					// vedere sul codice in C++ cosa torna x[i]->id
					i++;
				else
					j++;
			}
			size_union++;
		}
		size_union += (size_is1 - i) + (size_is2 - j);
		if (size_union != 0)
			return (double) (1.0 - (double) size_intersec / size_union);
		else
			return 0.0;
	} // getDistance()

	
	private static void processOptions(String[] options) throws FileNotFoundException {
		outfile = "";
			for (int i = 0; i < options.length; i++) {
				if (options[i].equals("-arff"))
					arffile = true;
				if (options[i].equals("-out"))
					outfile = options[i+1];
				else if (options[i].equals("-a")){
					String tokens;
					StringTokenizer tok = new StringTokenizer(options[i+1],",");
					as = new String[tok.countTokens()];
					for (int j = 0; tok.hasMoreTokens(); j++)
						as[j] = tok.nextToken();
					 i++;
				}else
					m_filename = options[i];
			}
			long start = System.currentTimeMillis();
			System.out.print("Loading file "+m_filename+"... ");
			if (!outfile.equals(""))
				out = new PrintStream(outfile);
			else
				out = System.out;
			if (arffile){
				if (as != null)
					data = new ArffDatabase(m_filename,as);
				else
					data = new ArffDatabase(m_filename);
			}
			else 
				data = new ValidationDatabase(m_filename);
			System.out.println("["+data.numOfItems()+" item(s), "+data.size()+" set(s)] done ["+((System.currentTimeMillis()-start)/1000.0)+"s].");
	}

}
