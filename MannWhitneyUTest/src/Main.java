import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TestUtils;

public class Main {
	
	public static void main(String[] args) {

		MannWhitneyUTest test = new MannWhitneyUTest();
		
		double[] s1 = { 2, 2, 2, 2, 2, 2 };
		double[] s2 = { 2, 2, 2, 2 };
		
//		double[] s1 = { 3, 4, 2, 6, 2, 5 };
//		double[] s2 = { 9, 7, 5, 10, 6, 8 };
		
//		double[] s1 = { 25, 25, 19, 21, 22, 19, 15 , 4, 5, 2, 6, 7, 8, 9};
//		double[] s2 = { 18, 14, 13, 15, 17, 19, 18, 20, 19, 2, 1, 9, 10, 22, 21, 19 };
		
//		double[] s1 = new double[10000];
//		double[] s2 = new double[1000];
//		
//		for (int i = 0; i < s1.length; i++)
//			s1[i] = Math.random();
//		
//		for (int i = 0; i < s2.length; i++)
//			s2[i] = Math.random();
		
//		double[] s1 = {0.55, 0.67, 0.43, 0.51, 0.48, 0.60, 0.71, 0.53, 0.44, 0.65, 0.75};
//		double[] s2 = {0.49, 0.68, 0.59, 0.72, 0.67, 0.75, 0.65, 0.77, 0.62, 0.48, 0.59};

		double u = test.mannWhitneyU(s1, s2);
		double p = test.mannWhitneyUTest(s1, s2);
		
		System.out.println("U = " + u);
		System.out.println("P-Value (UTest) = " + p);
		System.out.println("P-Value (TTest) = " + TestUtils.tTest(s1, s2));
		System.out.println("Mean = " + new Mean().evaluate(s1));
		
		// Nao podem, erro de tamanho difernte
		//System.out.println(new WilcoxonSignedRankTest().wilcoxonSignedRank(s1, s2));
		//System.out.println(new WilcoxonSignedRankTest().wilcoxonSignedRankTest(s1, s2, true));

	}

}
