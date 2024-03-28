package main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.stat.inference.TestUtils;

import data.Record;
import data.Record.Rating;
import net.sf.javaml.utils.ContingencyTables;

public class CodingDataAnalyzerMain {
	enum AnalysisType {UNAMBIGUOUS, AMBIGUOUS, ALL}
	private static final boolean ANALYZE_UNAMBIGUOUS_AND_AMBIGUOUS = true;
	private static final boolean SHOW_CONFUSION_MATRICES = false;

	private static Stats stats = new Stats();
	private static String[] names = new String[Globals.NUM_PREDICTION_CONDITIONS];
	
	
	public static void main(String[] args) {
		
		for (int i=0; i<Globals.NUM_PREDICTION_CONDITIONS; i++) {
			names[i] = Integer.toString(i+1) + String.format(" %-16s", Globals.conditionNames[i]);
		}
		
		Globals.initialize();
		for (int i = Globals.MIN_BATCH_NUMBER_FOR_ANALYSIS; i <= Globals.MAX_BATCH_NUMBER_FOR_ANALYSIS; ++i) {
			Globals.BATCH_NUMBER = i;
			Globals.fileIO.loadData();
		}

		StringBuilder results = new StringBuilder();
		// calculate average score for each of the five classifiers, per rater
		// calculate agreement between raters

		if (ANALYZE_UNAMBIGUOUS_AND_AMBIGUOUS) {
			for (AnalysisType analysisType:AnalysisType.values()) {
				computeResults(results, analysisType);
			}
		} else {
			computeResults(results, AnalysisType.ALL);
		}

		Globals.fileIO.saveAnalysisResults("statistics1.csv", results.toString());

		Globals.shutdown();

	}

	private static void computeResults(StringBuilder results, AnalysisType analysisType) {
		results.append("Condition,Predictor,R1,R2,Average,Kappa,Agreement\n");
		System.out.println();
		System.out.println("***** Analysis: " + analysisType);

		int total[] = new int[Globals.NUM_PREDICTION_CONDITIONS];
		int agree[] = new int[Globals.NUM_PREDICTION_CONDITIONS];
		int total1[] = new int[Globals.NUM_PREDICTION_CONDITIONS];
		int total2[] = new int[Globals.NUM_PREDICTION_CONDITIONS];
		HashMap<Integer,KappaData> ratingsMap = new HashMap<Integer,KappaData>();
		for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
			KappaData kd = new KappaData();
			ratingsMap.put(i,kd);
		}
		//		ArrayList<Record> disagreements = 

		ArrayList<ArrayList<Integer>> predictorResultsMap = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
			predictorResultsMap.add(new ArrayList<Integer>());
		}

		stats.clear();

		for (Record record:Globals.dataManager.getRecords()) {
			switch(analysisType) {
			case AMBIGUOUS:
				if (record.isAmbiguous == false) continue;
				break;
			case UNAMBIGUOUS:
				if (record.isAmbiguous == true) continue;
				break;
			case ALL:
				// do nothing - we want to process all records.
				break;
			}

			boolean allRecordsAvailable = true;
			// Process records
			for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
				// First check if either rater ignored the record
				if (Globals.SINGLE_CODER == true) {
					if (Globals.REVIEWER_ID == 1) {
						// Skip if coder 1 ignored this record
						if (record.rater1[i] == Rating.INCOMPLETE) {
							allRecordsAvailable = false;
							continue;
						}
					}
					if (Globals.REVIEWER_ID == 2) {
						// Skip if coder 2 ignored this record
						if (record.rater2[i] == Rating.INCOMPLETE) {
							allRecordsAvailable = false;
							continue;
						}
					}
				} else {
					if (record.rater1[i] == Rating.INCOMPLETE || record.rater2[i] == Rating.INCOMPLETE)
						continue;
				}

				if (Globals.SINGLE_CODER == false) {
					// Only update Kappa data if there are two coders
					KappaData kappaData = ratingsMap.get(i);
					kappaData.update(record, i);
				}

				ArrayList<Integer> predictorResultsList = predictorResultsMap.get(i);

				if (record.rater1[i] == Rating.YES) {
					if (record.rater1[i] == Rating.YES) {
						total1[i] += 1;
						predictorResultsList.add(1);
					} else {
						predictorResultsList.add(0);
					}
				}

				if (record.rater2[i] == Rating.YES) {
					total2[i] += 1;
				}

				total[i] += 1;
				if (record.rater1[i] == record.rater2[i]) {
					++agree[i];
				}
			}

			if (allRecordsAvailable == true) {
				for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
					Rating rating = Rating.BLANK;
					if (Globals.REVIEWER_ID == 1) {
						// Skip if coder 1 ignored this record
						rating = record.rater1[i];
					} else if (Globals.REVIEWER_ID == 2) {
						rating = record.rater2[i];
					}
					if (rating == Rating.YES) {
						stats.addData(1.0, i);
					} else if (rating == Rating.NO) {
						stats.addData(0.0, i);
					}
				}
			}

		}
		//		stats.printResults();
		//		stats.saveResults(analysisType);
		stats.chiSquaredTest(analysisType);

		System.out.println("" + total[1] + " records processed.");
		for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
			if (Globals.SINGLE_CODER == true) {
				double percentage1 =  100.0*total1[i]/(double)total[i];
				double percentage2 = 100.0*total2[i]/(double)total[i];

				DecimalFormat df = new DecimalFormat("0.0");

				results.append(analysisType).append(",");
				results.append("Predictor " + (i+1)).append(",");
				results.append(percentage1).append(",");
				results.append(percentage2).append(",");
				results.append(0.0).append(",");
				results.append(0.0*100).append(",");
				results.append(0).append("\n");

				System.out.println("Predictor " + (i+1) + ", R1: " + df.format(percentage1) + "%, R2: " 
						+ df.format(percentage2) 
						+ "%, Average: " + df.format(0)
						+ "%, Agreement: " + df.format(0)  + "%, "
						+ "Kappa: " +  df.format(0)
						);
			} else {
				double percentage1 =  100.0*total1[i]/(double)total[i];
				double percentage2 = 100.0*total2[i]/(double)total[i];
				double avg = (percentage1 + percentage2) / 2.0;
				double agreement = 100.0*agree[i]/(double)total[i];
				double kappa = ratingsMap.get(i).getKappa();

				DecimalFormat df = new DecimalFormat("0.000");


				results.append(analysisType).append(",");
				results.append("Predictor " + (i+1)).append(",");
				results.append(percentage1).append(",");
				results.append(percentage2).append(",");
				results.append(avg).append(",");
				results.append(kappa*100).append(",");
				results.append(agreement).append("\n");
				//results.append(kappa).append("\n");
				System.out.println("Predictor " + (i+1) + ", R1: " + df.format(percentage1) + "%, R2: " 
						+ df.format(percentage2) 
						+ "%, Average: " + df.format(avg)
						+ "%, Agreement: " + df.format(agreement)  + "%, "
						+ "Kappa: " +  df.format(kappa)
						);
			}
		}



		results.append("Coding batch: " + Globals.MIN_BATCH_NUMBER_FOR_ANALYSIS + " - " + Globals.MAX_BATCH_NUMBER_FOR_ANALYSIS);

	}


	static class Stats {
		ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();

		Stats() {
			for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
				data.add(new ArrayList<Double>());
			}
		}

		void clear() {
			for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
				data.get(i).clear();
			}
		}

		void addData(double value, int i) {
			data.get(i).add(value);
		}
		
		
		
		
		void chiSquaredTest(AnalysisType analysisType) {
			long totalYes[] = new long[Globals.NUM_PREDICTION_CONDITIONS];
			long totalNo[] = new long[Globals.NUM_PREDICTION_CONDITIONS];

			long total = 0;

			for (int i = 0; i < data.get(0).size(); ++i) {
				for (int j = 0; j < Globals.NUM_PREDICTION_CONDITIONS; ++j) {
					double rating = data.get(j).get(i);
					String result = "";
					if (rating == 1) {
						++totalYes[j];	
					} else {
						++totalNo[j];
					}
				}
			}
			total = totalYes[0] + totalNo[0];
			
			
			// compute Chi squared for each pair of conditions
			
			for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
				for (int j = i+1; j < Globals.NUM_PREDICTION_CONDITIONS; ++j) {
					
					double avgYes = totalYes[i] + totalYes[j];
					double avgNo = totalNo[i] + totalNo[j];
					
					double sum = avgYes + avgNo;
					avgYes /= sum;
					avgNo /= sum;
					
					long[] observed1 = new long[]{totalYes[i],totalNo[i]};
					long[] observed2 = new long[]{totalYes[j],totalNo[j]};
					
					double[][] values = new double[][]{{totalYes[i],totalNo[i]},{totalYes[j],totalNo[j]}};
					long[] observed = new long[2];
					double[] expected = new double[2];
					
					observed[0] = totalYes[i];
					observed[1] = totalNo[i];
					
					expected[0] = totalYes[j];
					expected[1] = totalNo[j];
					
					
					double p = TestUtils.chiSquareTest(expected, observed);
					
					System.out.println(names[i] + " and " + names[j]);
					System.out.println("N=" + Long.toString(total));
					DecimalFormat df = new DecimalFormat(".0######");
					System.out.println("Chi Squared Test");
					
					System.out.println("Using Yates' correction:     p=" + df.format(ContingencyTables.chiSquared(values, true)));
					System.out.println("Using Yates' correction:   val=" + df.format(ContingencyTables.	chiVal(values, true)));
					
					System.out.println("Without Yates' correction:   p=" + df.format(ContingencyTables.chiSquared(values, false)));
					System.out.println("Without Yates' correction: val=" + df.format(ContingencyTables.	chiVal(values, false)));
					
					System.out.println();
				}
			}
		}
		
		
		
		
		void saveResults(AnalysisType analysisType) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
				sb.append(names[i]).append(",");
			}
			sb.append("\n");

			for (int i = 0; i < data.get(0).size(); ++i) {
				for (int j = 0; j < Globals.NUM_PREDICTION_CONDITIONS; ++j) {
					double rating = data.get(j).get(i);
					String result = "";
					if (rating == 1) {
						result = "Y";		
					} else {
						result = "N";
					}
					sb.append(result).append(",");
				}
				sb.append("\n");
			}
			Globals.fileIO.saveAnalysisResults("coder-ratings-"+analysisType + ".csv", sb.toString());
		}

		void printResults() {
			// Confirm that all data are same length
			//			System.out.println("sizes: ");
			//			for (int i = 0; i < 5; ++i) {
			//				System.out.print(data.get(i).size() + " ");
			//			}
			//			System.out.println();

			for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS-1; ++i) {
				for (int j = i+1; j < Globals.NUM_PREDICTION_CONDITIONS; ++j) {
					Double[] first = data.get(i).toArray(new Double[data.get(i).size()]);
					Double[] second = data.get(j).toArray(new Double[data.get(j).size()]);


					double[] a = toPrimitive(first);
					double[] b = toPrimitive(second);
					double result = TestUtils.pairedTTest(a, b);
					DecimalFormat df = new DecimalFormat(".000");
					System.out.print("[" + names[i] + "vs. " + names[j] + "] ");
					if (result < 0.001) {
						System.out.print("p<.001");
					} else {
						System.out.print("p=" + df.format(result));
					}
					if (result < 0.05) {
						System.out.println(" *");
					} else {
						System.out.println();
					}
				}
			}

		}

		double[] toPrimitive(Double[] array) {
			double[] primitive = new double[array.length];
			for (int i = 0; i < array.length; ++i) {
				primitive[i] = (double) array[i];
			}
			return primitive;
		}

	}


	static class KappaData {
		int Y1Y2 = 0;
		int Y1N2 = 0;
		int N1Y2 = 0;
		int N1N2 = 0;

		void clear() {
			Y1Y2 = 0;
			Y1N2 = 0;
			N1Y2 = 0;
			N1N2 = 0;
		}
		void update(Record record, int predictor) {
			Rating r1 = record.rater1[predictor];
			Rating r2 = record.rater2[predictor];
			if (r1 == Rating.YES && r2 == Rating.YES) ++Y1Y2;
			if (r1 == Rating.YES && r2 == Rating.NO) ++Y1N2;
			if (r1 == Rating.NO && r2 == Rating.YES) ++N1Y2;
			if (r1 == Rating.NO && r2 == Rating.NO) ++N1N2;
		}

		double getKappa() {
			double total = Y1Y2 + Y1N2 + N1Y2 + N1N2;
			double probYes1 = (Y1Y2 + Y1N2)/total;
			double probYes2 = (Y1Y2 + N1Y2)/total;
			double pe = probYes1 * probYes2 + (1-probYes1) * (1-probYes2); // probability of agreeing by chance
			double p0 = (Y1Y2 + N1N2)/total; // proportionate agreement
			double kappa = (p0 - pe) / (1 - pe);

			DecimalFormat df = new DecimalFormat("0.00");

			if (SHOW_CONFUSION_MATRICES) {
				System.out.println();
				System.out.println("\t\t  R2");
				System.out.println("\t\tYes\tNo");
				System.out.println("\tYes\t" + Y1Y2 + "\t" + Y1N2);
				System.out.println("R1\tNo\t" + N1Y2 + "\t" + N1N2);
				System.out.println();
			}
			//			System.out.println("Total: " + total);
			//			System.out.println("ProbYes1: " + df.format(probYes1) + "\tProbYes2: " + df.format(probYes2));
			//			System.out.println("Prob. of agreement by chance: " + pe);
			//			System.out.println("Observed agreement: " + p0);
			//			System.out.println("Kappa: " + kappa);
			return kappa;
		}
	}

}
