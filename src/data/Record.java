package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import main.Globals;
import toolbox.DebugTools;
import org.apache.commons.csv.*;

public class Record {
	public CSVRecord csvRecord;
	
	public enum InteractionRatings {
		GOOD, 
		BAD, 
		UNSURE,
		INCOMPLETE,
	};
	
	public enum ShouldRespondRatings {
		YES,
		EITHER,
		NO,
		INCOMPLETE,
	};
	
	static String DELIMITER = ",";
	
	public String timestampStr = null;
	public Integer turnID = null;
	public boolean isBeforeShopkeeperAction = false;
	public Boolean isUsedForVerification = null;
	public Integer trialID = null;
	
	public Integer uniqueID = null;
	
	public String S1utterance = null;
	public String S2utterance = null;
	public String Cutterance = null;
	
	public String[] robotUtterances = new String[Globals.NUM_PREDICTION_CONDITIONS];
	
	public HashMap<Integer, Tuple<Double, Double>> uniqueIDToXY = new HashMap<Integer, Tuple<Double, Double>>();
	
	public InteractionRatings ratersInteractionResults[][] = new InteractionRatings[Globals.NUM_RATERS][Globals.NUM_PREDICTION_CONDITIONS];
	
	public ShouldRespondRatings ratersShouldRespondResults[] = new ShouldRespondRatings[Globals.NUM_RATERS];
	
	public String[][] raterReasons = new String[Globals.NUM_RATERS][Globals.NUM_PREDICTION_CONDITIONS];
	
	
	public String[][] conversationHistoryInTableFormat = null;
	
	private static HashMap<InteractionRatings, String> interactionRatingsToString = new HashMap<InteractionRatings, String>();
	private static HashMap<String, InteractionRatings> stringToInteractionRatings = new HashMap<String, InteractionRatings>();
	
	private static HashMap<ShouldRespondRatings, String> shouldRespondRatingsToString = new HashMap<ShouldRespondRatings, String>();
	private static HashMap<String, ShouldRespondRatings> stringToShouldRespondRatings = new HashMap<String, ShouldRespondRatings>();
	
	private long timeCreated = System.currentTimeMillis();
	
	public Record() {
		interactionRatingsToString.put(InteractionRatings.GOOD, "GOOD");
		interactionRatingsToString.put(InteractionRatings.BAD, "BAD");
		interactionRatingsToString.put(InteractionRatings.UNSURE, "UNSURE");
		interactionRatingsToString.put(InteractionRatings.INCOMPLETE, "INCOMPLETE");
		
		stringToInteractionRatings.put("GOOD", InteractionRatings.GOOD);
		stringToInteractionRatings.put("BAD", InteractionRatings.BAD);
		stringToInteractionRatings.put("UNSURE", InteractionRatings.UNSURE);
		stringToInteractionRatings.put("INCOMPLETE", InteractionRatings.INCOMPLETE);
		
		shouldRespondRatingsToString.put(ShouldRespondRatings.YES, "YES");
		shouldRespondRatingsToString.put(ShouldRespondRatings.NO, "NO");
		shouldRespondRatingsToString.put(ShouldRespondRatings.EITHER, "EITHER");
		shouldRespondRatingsToString.put(ShouldRespondRatings.INCOMPLETE, "INCOMPLETE");
		
		stringToShouldRespondRatings.put("YES", ShouldRespondRatings.YES);
		stringToShouldRespondRatings.put("NO", ShouldRespondRatings.NO);
		stringToShouldRespondRatings.put("EITHER", ShouldRespondRatings.EITHER);
		stringToShouldRespondRatings.put("INCOMPLETE", ShouldRespondRatings.INCOMPLETE);
	}
	
	private static boolean isNull(String rawStr) {
		return rawStr == null || rawStr.length() == 0 || rawStr.toLowerCase().equals("null");
	}
	
	public static Record fromCSVRecord(CSVRecord csvRecord) {
		//Timestamp	Turn ID	Is Used For Verification	Trial ID	Unique ID	Utterance	Robot Utterance Proposed	Robot Utterance Closest_To_Phoebes	Unique ID 2 X	Unique ID 2 Y	Unique ID 3 X	Unique ID 3 Y	Unique ID 1 X	Unique ID 1 Y	Rater1 Should Shopkeeper Respond	Rater1 Proposed	Rater1 Closest_To_Phoebes	Rater2 Should Shopkeeper Respond	Rater2 Proposed	Rater2 Closest_To_Phoebes	Rater3 Should Shopkeeper Respond	Rater3 Proposed	Rater3 Closest_To_Phoebes

		Record record = new Record();
		
		record.csvRecord = csvRecord;
		
		try {
			record.timestampStr = csvRecord.get("Timestamp");
		} catch (Exception e) {
			record.timestampStr = null;
		}
		
		String turnIDStr;
		try {
			turnIDStr = csvRecord.get("Turn ID");
		} catch (Exception e) {
			turnIDStr = csvRecord.get("\uFEFFTurn ID");
		}
		record.turnID = isNull(turnIDStr) ? null : Integer.parseInt(turnIDStr);
		
		String isUsedForVerificationString = csvRecord.get("Is Used For Verification");
		record.isUsedForVerification = isNull(isUsedForVerificationString) ? null : ((int)Integer.parseInt(isUsedForVerificationString) == 1);
		
		record.trialID = Integer.parseInt(csvRecord.get("Trial ID"));
		
		record.uniqueID = csvRecord.get("Unique ID").equals("NoAction") ? null : Integer.parseInt(csvRecord.get("Unique ID"));
		
		record.S1utterance = csvRecord.get("S1 Utterance"); // the most recent utterance from any participant
		record.S2utterance = csvRecord.get("S2 Utterance"); // the most recent utterance from any participant
		record.Cutterance = csvRecord.get("C Utterance"); // the most recent utterance from any participant
		
		
		for (int i=0; i<Globals.NUM_PREDICTION_CONDITIONS; i++) {
			String conditionName = Globals.conditionNames[i];
			String robotUtteranceRaw = csvRecord.get(String.format("Robot Utterance %s", conditionName));
			record.robotUtterances[i] = isNull(robotUtteranceRaw) ? null : robotUtteranceRaw;
		}
		
		
		String xStr, yStr;
		Double x, y;
		
		// get customer location
		xStr = csvRecord.get("Unique ID 2 X");
		x = isNull(xStr) ? null : Double.parseDouble(xStr);
		yStr = csvRecord.get("Unique ID 2 Y");
		y = isNull(yStr) ? null : Double.parseDouble(yStr);
		record.uniqueIDToXY.put(2, new Tuple<Double, Double>(x, y));
		
		// get senior shopkeeper location
		xStr = csvRecord.get("Unique ID 1 X");
		x = isNull(xStr) ? null : Double.parseDouble(xStr);
		yStr = csvRecord.get("Unique ID 1 Y");
		y = isNull(yStr) ? null : Double.parseDouble(yStr);
		record.uniqueIDToXY.put(1, new Tuple<Double, Double>(x, y));
		
		// get junior shopkeeper (robot) location
		xStr = csvRecord.get("Unique ID 3 X");
		x = isNull(xStr) ? null : Double.parseDouble(xStr);
		yStr = csvRecord.get("Unique ID 3 Y");
		y = isNull(yStr) ? null : Double.parseDouble(yStr);
		record.uniqueIDToXY.put(3, new Tuple<Double, Double>(x, y));
		
				
		for (int raterI = 0; raterI < Globals.NUM_RATERS; raterI++) {
			
			// get the should respond data
			String raterShouldRespondResult;
			try {
				raterShouldRespondResult = csvRecord.get(String.format("Rater%d Should Shopkeeper Respond", raterI+1));
			} catch (IllegalArgumentException e) {
				raterShouldRespondResult = null;
			}
			if (isNull(raterShouldRespondResult)) {
				record.ratersShouldRespondResults[raterI] = ShouldRespondRatings.INCOMPLETE;
			} else {
				if (stringToShouldRespondRatings.containsKey(raterShouldRespondResult)) {
					record.ratersShouldRespondResults[raterI] = stringToShouldRespondRatings.get(raterShouldRespondResult);
				} else {
					record.ratersShouldRespondResults[raterI] = ShouldRespondRatings.INCOMPLETE;
				}
			}
			
			// Then, get the interaction data
			for (int i=0; i<Globals.NUM_PREDICTION_CONDITIONS; i++) {
				String conditionName = Globals.conditionNames[i];
				String raterResult;
				try {
					raterResult = csvRecord.get(String.format("Rater%d %s", raterI+1, conditionName));
				} catch (IllegalArgumentException e) {
					raterResult = null;
				}
				if (isNull(raterResult)) {
					record.ratersInteractionResults[raterI][i] = InteractionRatings.INCOMPLETE;
				} else {
					if (stringToInteractionRatings.containsKey(raterResult)) {
						record.ratersInteractionResults[raterI][i] = stringToInteractionRatings.get(raterResult);
//						System.out.println(String.format("conditionName %s raterResult %s record.ratersInteractionResults[raterI][i] %s record.robotUtterances[i] %s", conditionName, raterResult, record.ratersInteractionResults[raterI][i], record.robotUtterances[i]));
					} else {
						record.ratersInteractionResults[raterI][i] = InteractionRatings.INCOMPLETE;
					}
				}
				
				String raterReason;
				try {
					raterReason = csvRecord.get(String.format("Rater%d %s Reason", raterI+1, conditionName));
				} catch (IllegalArgumentException e) {
					raterReason = null;
				}
				if (isNull(raterReason)) {
					record.raterReasons[raterI][i] = "";
				} else {
					record.raterReasons[raterI][i] = raterReason;
				}
				
			}
			
			
			
		}
		record.timeCreated = System.currentTimeMillis();

		return record;
	}
	
	
	public ArrayList<String> toArrayList() {
		Map<String, String> recordMap = this.csvRecord.toMap();
		
		// only need to update the ratings
		
		//Rater1 Should Shopkeeper Respond	Rater1 Proposed	Rater1 Baseline	Rater2 Should Shopkeeper Respond	Rater2 Proposed	Rater2 Baseline	Rater3 Should Shopkeeper Respond	Rater3 Proposed	Rater3 Baseline

		for (int raterI = 0; raterI < Globals.NUM_RATERS; raterI++) {
			
			recordMap.put(
					"Rater" + Integer.toString(raterI+1) + " Should Shopkeeper Respond",
					shouldRespondRatingsToString.get(ratersShouldRespondResults[raterI])
					);
			
			for (int i = 0; i<Globals.NUM_PREDICTION_CONDITIONS; i++) {
				
				recordMap.put(
						"Rater" + Integer.toString(raterI+1) + " " + Globals.conditionNames[i],
						interactionRatingsToString.get(ratersInteractionResults[raterI][i])
						);	
				
				recordMap.put(
						"Rater" + Integer.toString(raterI+1) + " " + Globals.conditionNames[i] + " Reason",
						raterReasons[raterI][i]
						);	
				
			}
		}
		
		
		ArrayList<String> recordList = new ArrayList<String>();
		
		for (int i=0; i<Globals.dataManager.getHeaders().size(); i++) {
			recordList.add(recordMap.get(Globals.dataManager.getHeaders().get(i)));
		}
		
		return recordList;
	}
	
	
	public static String generateHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append("Timestamp").append(DELIMITER);
		sb.append("Turn ID").append(DELIMITER);
		sb.append("Is Used For Verification").append(DELIMITER);
		sb.append("Trial ID").append(DELIMITER);
		sb.append("Unique ID").append(DELIMITER);
		sb.append("Utterance").append(DELIMITER);
		
		for (int i=0; i<Globals.NUM_PREDICTION_CONDITIONS; i++) {
			String conditionName = Globals.conditionNames[i];
			sb.append("Robot Utterance ").append(conditionName).append(DELIMITER);
		}
		
		sb.append("Unique ID 1 X").append(DELIMITER);
		sb.append("Unique ID 1 Y").append(DELIMITER);
		
		sb.append("Unique ID 2 X").append(DELIMITER);
		sb.append("Unique ID 2 Y").append(DELIMITER);
		
		sb.append("Unique ID 3 X").append(DELIMITER);
		sb.append("Unique ID 3 Y").append(DELIMITER);
		
		
		for (int raterI = 0; raterI < Globals.NUM_RATERS; raterI++) {
			
			sb.append("Rater").append(Integer.toString(raterI+1)).append(" Should Shopkeeper Respond").append(DELIMITER);
			
			for (int i=0; i<Globals.NUM_PREDICTION_CONDITIONS; i++) {
				String conditionName = Globals.conditionNames[i];
				sb.append("Rater").append(Integer.toString(raterI+1)).append(" ").append(conditionName).append(DELIMITER);
			}
		}
		
		sb.deleteCharAt(sb.length()-1); // remove the last delimiter
		
		return sb.toString();
	}
	
	
	public Random getNewRandomizer() {
		long seed = this.timeCreated - (this.turnID == null ? 0 : this.turnID)*100;
		Random randomizer = new Random(seed);
		randomizer.nextInt(2);
		return randomizer;
	}
	
	
	public void shuffleAndSetConversationHistory(ArrayList<ArrayList<String>> currentConversationHistoryAsTable) {
//		// Randomize the last four columns with seed turnID so it is the same permutation as the inputActions
//		long seed = getRandomSeed();
		
//		for (int i = Globals.NUM_CUSTOMERS+1; i < currentConversationHistoryAsTable.size(); i++) {
//			for (int j = 0; j < currentConversationHistoryAsTable.get(i).size(); j++) {
//				System.out.println(String.format("pre-shuffle i %d, j %d, %s", i, j, currentConversationHistoryAsTable.get(i).get(j)));
//			}
//		}
//		Random randomizer = new Random(seed);
//		randomizer.nextInt(2);
//		Collections.shuffle(currentConversationHistoryAsTable.subList(Globals.NUM_CUSTOMERS+1, Globals.NUM_CUSTOMERS+1+Globals.NUM_PREDICTION_CONDITIONS), randomizer);
//		for (int i = Globals.NUM_CUSTOMERS+1; i < currentConversationHistoryAsTable.size(); i++) {
//			for (int j = 0; j < currentConversationHistoryAsTable.get(i).size(); j++) {
//				System.out.println(String.format("post-shuffle i %d, j %d, %s", i, j, currentConversationHistoryAsTable.get(i).get(j)));
//			}
//		}
		// Transpose and copy the list
		this.conversationHistoryInTableFormat = new String[currentConversationHistoryAsTable.get(0).size()][currentConversationHistoryAsTable.size()];
//		System.out.println(String.format("Num Cols %d, Num Rows %d, sublist size %d", currentConversationHistoryAsTable.size(), currentConversationHistoryAsTable.get(0).size(), currentConversationHistoryAsTable.subList(Globals.NUM_CUSTOMERS+1, Globals.NUM_CUSTOMERS+1+Globals.NUM_PREDICTION_CONDITIONS).size()));
		for (int i = 0; i < currentConversationHistoryAsTable.size(); i++) {
			for (int j = 0; j < currentConversationHistoryAsTable.get(i).size(); j++) {
//				if (j == 3) {
//					System.out.println(String.format("currentConversationHistoryAsTable.get(%d).get(%d) %s", i, j, currentConversationHistoryAsTable.get(i).get(j)));
//				}
				this.conversationHistoryInTableFormat[j][i] = currentConversationHistoryAsTable.get(i).get(j);
			}
		}
	}
	
}
