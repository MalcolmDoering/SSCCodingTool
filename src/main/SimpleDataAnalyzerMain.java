package main;

import java.text.DecimalFormat;

import net.sf.javaml.utils.ContingencyTables;

public class SimpleDataAnalyzerMain {
	private static final boolean VERBOSE = true;

	public static void main(String[] args) {
		
		int Y1Y2, Y1N2, N1Y2, N1N2 = 0; // for Kappa
		int YES1, YES2, TOTAL1, TOTAL2 = 0; // for Chi-squared


		//	Proactive: Rater1 (Phoebe), Rater2 (Nick)
		//	# of total examples: 2223
		//	# of test examples:225
		//	Rating: Y1Y2: 131, Y1N2: 14, N1Y2: 11, N1N2: 69
		//	R1 Yes: 145, Y2 Yes: 142
		
		// Proactive

		
		Y1Y2 = 131;
		Y1N2 = 14;
		N1Y2 = 11;
		N1N2 = 69;
		System.out.println("Proactive: ");
		calculateKappa(Y1Y2,Y1N2,N1Y2,N1N2);
		
		//	Passive: Rater1 (Phoebe), Rater2 (Carol)
		//	# of total examples: 1496
		//	# of test examples:150
		//	Rating: Y1Y2: 100, Y1N2: 5, N1Y2: 8, N1N2: 37
		//	R1 Yes: 105, Y2 Yes: 108 

		
		Y1Y2 = 100;
		Y1N2 = 5;
		N1Y2 = 8;
		N1N2 = 37;
		
		System.out.println("Passive:");
		calculateKappa(Y1Y2,Y1N2,N1Y2,N1N2);

		// CHI-SQUARED TEST
		
		// This tests between two conditions, e.g. passive vs proactive
		// Use only results from one coder.
		
		YES1 = 142; // calculated from Rater 2
		TOTAL1 = 225;
		
		YES2 = 108; // calculated from Rater 2
		TOTAL2 = 150;
	
		
		System.out.println("Comparison between proactive and passive. Rater 2 only.");
		chiSquaredTest(YES1,TOTAL1,YES2,TOTAL2);
		
	}
	
	public static void chiSquaredTest(int yes1, int total1, int yes2, int total2) {
		
		long no1 = total1 - yes1;
		long no2 = total2 - yes2;
		
		long totalYes = yes1 + yes2;
		long totalNo = no1 + no2;
		
		long total = total1 + total2;
		
		double expYes = totalYes / (double) total;
		double expNo = totalNo / (double) total;

		double expYes1 = expYes * total1;
		double expYes2 = expYes * total2;
		double expNo1 = total1 - expYes1;
		double expNo2 = total2 - expYes2;


		double[][] values = new double[][]{{yes1, no1},{yes2, no2}};
//		long[] observed = new long[2];
//		double[] expected = new double[2];
//
//
//		System.err.println("Expected " + Arrays.toString(expected));
//		System.err.println("Observed " + Arrays.toString(observed));
//		double p = TestUtils.chiSquareTest(expected, observed);
		DecimalFormat df = new DecimalFormat(".0000");
		DecimalFormat chiformat = new DecimalFormat(".000");
		System.out.println("Chi Squared Test");
		//			System.out.println("Dataset Comparison: p=" + df.format(TestUtils.chiSquareTestDataSetsComparison(observed1, observed2)));
		System.out.print("Standard Chi-squared: \t\t\t");
		double p = ContingencyTables.chiSquared(values, false);
		double chisq = ContingencyTables.chiVal(values, false);
		
		System.out.println("χ2(1, " + total/2 + ") = " + chiformat.format(chisq) + ", p = " + df.format(p));
		
		System.out.print("Chi-squared using Yates' correction:\t");
		p = ContingencyTables.chiSquared(values, true);
		chisq = ContingencyTables.chiVal(values, true);
		System.out.println("χ2(1, " + total/2 + ") = " + chiformat.format(chisq) + ", p = " + df.format(p));
		
		System.out.println();
		if (total1 != total2) {
			System.out.println("Warning: N is different for the two populations (" + total1 + " vs " + total2 + "). Not sure how to report this correctly.");
		}
		
		System.out.println();
		//			System.out.println("Dataset Comparison 2: p=" + df.format(TestUtils.chiSquareTest(values)));
		//			System.out.println("Chisquare = " + df.format(TestUtils.chiSquare(expected, observed)));
		//			System.out.println("p = " + df.format(p));
		//return p; 
	}

	public static double calculateKappa(int Y1Y2, int Y1N2, int N1Y2, int N1N2) {
		double total = Y1Y2 + Y1N2 + N1Y2 + N1N2;
		double probYes1 = (Y1Y2 + Y1N2) / total;
		double probYes2 = (Y1Y2 + N1Y2) / total;
		double pe = probYes1 * probYes2 + (1-probYes1) * (1-probYes2); // probability of agreeing by chance
		double p0 = (Y1Y2 + N1N2)/total; // proportionate agreement
		double kappa = (p0 - pe) / (1 - pe);

		DecimalFormat df = new DecimalFormat("0.000");
		DecimalFormat percent = new DecimalFormat("0.0");

		if (VERBOSE) {
			System.out.println("\t\t  R2");
			System.out.println("\t\tYes\tNo");
			System.out.println("\tYes\t" + Y1Y2 + "\t" + Y1N2);
			System.out.println("R1\tNo\t" + N1Y2 + "\t" + N1N2);
			System.out.println();
			System.out.println("Total: " + (int)total);
			System.out.println("R1: " + (Y1Y2 + Y1N2) + " / " + (int) total + " = " + percent.format(100.0*probYes1) + "%");
			System.out.println("R2: " + (Y1Y2 + N1Y2) + " / " + (int) total + " = " + percent.format(100.0*probYes2) + "%");
			
			System.out.println("Prob. of agreement by chance: " + df.format(pe));
			System.out.println("Observed (raw) agreement: " + df.format(p0));
		}
		System.out.println("Kappa: " + df.format(kappa));
		System.out.println();
		return kappa;
	}
}

