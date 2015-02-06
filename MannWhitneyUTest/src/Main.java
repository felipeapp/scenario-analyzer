import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.apache.commons.math3.util.FastMath;

public class Main {

	/**
     * @param Umin smallest Mann-Whitney U value
     * @param n1 number of subjects in first sample
     * @param n2 number of subjects in second sample
     * @return two-sided asymptotic p-value
     * @throws ConvergenceException if the p-value can not be computed
     * due to a convergence error
     * @throws MaxCountExceededException if the maximum number of
     * iterations is exceeded
     */
    public static double calculateAsymptoticPValue(final double Umin,
                                             final int n1,
                                             final int n2)
        throws ConvergenceException, MaxCountExceededException {

        /* long multiplication to avoid overflow (double not used due to efficiency
         * and to avoid precision loss)
         */
        final long n1n2prod = (long) n1 * n2;

        // http://en.wikipedia.org/wiki/Mann%E2%80%93Whitney_U#Normal_approximation
        final double EU = n1n2prod / 2.0;
        final double VarU = n1n2prod * (n1 + n2 + 1) / 12.0;

        final double z = (Umin - EU) / FastMath.sqrt(VarU);

        System.out.println("Z = " + z);
        
        // No try-catch or advertised exception because args are valid
        // pass a null rng to avoid unneeded overhead as we will not sample from this distribution
        final NormalDistribution standardNormal = new NormalDistribution(null, 0, 1);

        return 2 * standardNormal.cumulativeProbability(z);
    }

	
	public static void main(String[] args) {

		MannWhitneyUTest test = new MannWhitneyUTest();
		
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
		
		double[] s1 = {0.55, 0.67, 0.43, 0.51, 0.48, 0.60, 0.71, 0.53, 0.44, 0.65, 0.75};
		double[] s2 = {0.49, 0.68, 0.59, 0.72, 0.67, 0.75, 0.65, 0.77, 0.62, 0.48, 0.59};

		double u = test.mannWhitneyU(s1, s2);
		double p = test.mannWhitneyUTest(s1, s2);

		System.out.println("U = " + u);
		System.out.println("P-Value = " + p);
		System.out.println("TTest = " + TestUtils.tTest(s1, s2));
		System.out.println("My-p-value: " + Main.calculateAsymptoticPValue(10.5, s1.length, s2.length));
		
		// Nao podem, erro de tamanho difernte
		//System.out.println(new WilcoxonSignedRankTest().wilcoxonSignedRank(s1, s2));
		//System.out.println(new WilcoxonSignedRankTest().wilcoxonSignedRankTest(s1, s2, true));

	}

}
