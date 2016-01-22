/*
 * Created on 26-gen-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package transaction;

/**
 * @author Giuseppe Manco
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Evaluator {
	double[][] ct;
	int nrows;
	int ncols;
	double n;
	double Z;
	double snr, snc;

	public Evaluator(double[][] mat,boolean summaries) {
		if (mat != null) {
			n = 0;
			if (summaries){
				nrows = mat.length-1;
				ncols = mat[0].length-1;				
			} else {
				nrows = mat.length;
				ncols = mat[0].length;
			}
			ct = new double[nrows+1][ncols+1];

			for (int i = 0; i < nrows; i++) {
				for (int j = 0; j < ncols; j++){
					ct[i][j] = mat[i][j];
					n+= ct[i][j];
					Z += ct[i][j]*ct[i][j];
					ct[i][ncols] += ct[i][j];
					ct[nrows][j] += ct[i][j];
					ct[nrows][ncols] += ct[i][j];
				}
			}
			snr = 0;
			for (int i = 0; i < nrows; i++)
				snr += ct[i][ncols]*ct[i][ncols];
			
			snc = 0;
			for (int i = 0; i < ncols; i++)
				snc += ct[nrows][i]*ct[nrows][i];
			

		}
	}
	public double getRandMeasure(){
		double a,d,M;
		a = getA();
		d = getD();
		M = getM();
			
		return (a+d)/M;
	}
	/**
	 * @return
	 */
	private double getM() {
		// TODO Auto-generated method stub
		return n*(n-1)/2;
	}
	/**
	 * @return
	 */
	private double getA() {
		return 1.0/2*Z - n/2;
	}	
	/**
	 * @return
	 */
	private double getB() {
		return 1.0/2*(snc - Z);
	}
	private double getC() {
		return 1.0/2*(snr - Z);
	}
/**
	 * @return
	 */
	private double getD() {
		//return getM() -getA() -getB() -getC();
		return (n*n + Z - (snr +snc))/2;
	}
	/**
	 * @return
	 */
	public double getGammaMeasure() {
		double a = getA();
		double b = getB();
		double c = getC();
		double d = getD();
		double M = getM();
		double m1 = a+b, m2 = a+c;
		double gamma = (M*a -m1*m2)/Math.sqrt(m1*m2*(M-m1)*(M-m2));
		
		return gamma;
	}
	/**
	 * @return
	 */
	public double getFowlMeasure() {
		double a = getA();
		double b = getB();
		double c = getC();
		
		return a/(Math.sqrt((a+c)*(a+b)));
	}
	/**
	 * @return
	 */
	public double getJaccMeasure() {
		double a = getA();
		double b = getB();
		double c = getC();
		return a/(a+b+c);
	}
	public double getFMeasure(){
		double f[] = new double[ncols];
		double prec, rec,find;
		
		for (int i = 0; i < nrows; i++)
			for (int j = 0; j < ncols; j++){
				prec = ct[i][j]/ct[i][ncols];
				rec = ct[i][j]/ct[nrows][j];
				if(prec != 0 && rec != 0)
					find = 2.0*prec*rec/(prec + rec);
				else
					find = 0;
				if (find > f[j])
					f[j] = find;
			}
		find = 0;
		for (int j = 0; j < f.length; j++)
			find += f[j]*ct[nrows][j]/ct[nrows][ncols];
		return find;
	}
	/**
	 * @return
	 */
	public double getError() {
		int[] cm = new int[nrows];
		
		for (int i = 0; i < nrows; i++){
			cm[i] = -1;
			double max = 0;
			for (int j = 0; j < ncols; j++)
				if (ct[i][j] > max){
					cm[i] = j;
					max = ct[i][j];
				}
		}
		double err = 0;
		for (int i = 0; i < nrows; i++)
			for (int j = 0; j < ncols; j++)
				if (j!=cm[i])
					err += ct[i][j];
		err /= ct[nrows][ncols];
		
		return err;
	}
	public double getWeigthedError(){
		double err = getError();
		if (nrows > ncols)
			err *= nrows*1.0/ncols;
		else
			err *= ncols*1.0/nrows;

		return err;
	}

	
	public static void main(String[] args) {
		double[][] a = {
				 {156, 	720}, 
				 {192, 	0}, 
				 {0, 	512}, 
				 {0, 	288}, 
				 {1728, 	0}, 
				 {0, 	64}, 
				 {0, 	768}, 
				 {288, 	0}, 
				 {0, 	48 },
				 {0, 	1680 },
				 {1296, 	0 },
				 {0, 	128 },
				 {256, 	0 }
				};
		Evaluator e = new Evaluator(a,false);
		System.out.println("F-Index:\t\t"+e.getFMeasure());		
		System.out.println("Jaccard Statistics:\t"+e.getJaccMeasure());
		System.out.println("Rand Statistics:\t"+e.getRandMeasure());
		System.out.println("Fowlkes Statistics:\t"+e.getFowlMeasure());
		System.out.println("Gamma Statistics:\t"+e.getGammaMeasure());
		System.out.println("Error:\t\t\t"+e.getError());		
	}
}
