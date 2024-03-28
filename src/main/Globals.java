package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;

import data.DataManager;
import data.FileIO;
import gui.ActionPanel;
import gui.ListPanel;
import gui.StatusPanel;
import toolbox.notification.NotificationHandler;

public class Globals {
	
	// VALUES TO CHANGE PER CODER-TRIAL!!!!
	public static int REVIEWER_ID = 2;
	public static String SCENARIO_NAME = "main";
	
	// Do not need to be changed
//	public static boolean IS_VERIFICATION = true;
	// StatusPanel
	public static String trialJapaneseWord = "トライル";
	public static String coderJapaneseWord = "コーダー";
	public static String mapJapaneseWord = "地図";
	// ActionTable
	public static String customerJapaneseWord = "顧客";
	public static String shopkeeperJapaneseWord = "店員";
	public static String robotJapaneseWord = "ロボット";
	public static String noActionJapanese = "何もやらない";
	public static String emptyQuotesJapanese = "「」";
	// InputPanel
	public static String goodJapaneseWord = "良い";
	public static String badJapaneseWord = "良くない";
	public static String attentionQuestionJapaneseString = "";//"どの顧客が一番大事ですか？";
	//public static String shopkeeperShouldRespondYesJapanese = "対応した方が良い";
	//public static String shopkeeperShouldRespondEitherJapanese = "どちらでも良い";
	//public static String shopkeeperShouldRespondNoJapanese = "対応しない方が良い";
	public static String shopkeeperShouldRespondYesJapanese = "この場合に行動した方がいい";
	public static String shopkeeperShouldRespondEitherJapanese = "どちらでも良い";
	public static String shopkeeperShouldRespondNoJapanese = "この場合に行動しない方が良い";
	
	// MalcolmCodingToolMain
	public static String dataSaveErrorMessageJapanese = "データ保存エラー。もう一回試してください。";
	public static String yesOptionJapanese = "はい";
	public static String noOptionJapanese = "いいえ";
	public static String errorJapanese = "エラー";
	public static String cancelOptionJapanese = "キャンセル";
	
//	public static Integer[] trialsForEvaluation = {179, 388, 299, 618, 231, 265, // fold 0
//									  			   346, 293, 422, 486, 195, 222, // fold 1
//									  			   327, 256, 193, 358, 280, 444, // fold 2
//									  			   225, 557, 458, 442, 169, 308, // fold 3
//									  			   397, 850, 814, 886, 730, 608, // fold 4
//									  			   174, 665, 558, 365, 699, 896, // fold 5
//									  			   547, 732, 826, 644, 854, 495, // fold 6
//									  			   649, 883, 857, 554, 736, 432}; // fold 7
	
	public static List<Integer> trialsForEvaluation = Arrays.asList(
			618, // fold 0
		   486, // fold 1
		   327, 280, // fold 2
		   225, 169, // fold 3
		   850, 608, // fold 4
		   665, 794, // fold 5
		   826, // fold 6
		   857); // fold 7);
			

//	
//	(new Integer[]{618, // fold 0
//										   486, // fold 1
//										   327, 280, // fold 2
//										   225, 169, // fold 3
//										   850, 608, // fold 4
//										   665, 896, // fold 5
//										   826, // fold 6
//										   857}); // fold 7


	
	public static int NUM_RATERS = 3;
	
	//public static int NUM_CUSTOMERS = 5;
	
	public static int NUM_PARTICIPANTS = 3; // S1 S2 C
	
	public static int shopkeeper1UniqueID = 1;
	public static int shopkeeper2UniqueID = 3;
	public static int customerUniqueID = 2;
	
	
	public static String conditionNames[] = {"Proposed", "Baseline"};
	public static int NUM_PREDICTION_CONDITIONS = conditionNames.length;
	
	public static Color shopkeeper1Color = new Color(255, 255, 100); // yellow
	public static Color shopkeeper2Color = new Color(255, 100, 100); // red
	public static Color customerColor = new Color(100, 100, 255); // blue
	
	
	public static FileIO fileIO;
	public static DataManager dataManager;
	public static ListPanel listPanel;
	public static StatusPanel statusPanel;
	public static ActionPanel actionPanel;

	public static String attentionConditionName = "Attention";
	public static JFrame mainFrame;
	
	public enum Notifications {LIST_SELECTION, LIST_CONTENTS}
	
	public static void initialize() {
		NotificationHandler.init(Notifications.values());
		fileIO = new FileIO();
		dataManager = new DataManager();
		
		//customerColors.put(1, new Color(255, 179, 179)); // light red
		//customerColors.put(2, new Color(236, 255, 230)); // light green
		//customerColors.put(3, new Color(255, 230, 255)); // light purple
		//customerColors.put(4, new Color(255, 238, 230)); // light brown
		//customerColors.put(5, new Color(179, 236, 255)); // light cyan
	}

	public static void shutdown() {
		NotificationHandler.destroy();
	}

	public static String getDataFileName() {
		//return "group_predictions_test_" + SCENARIO_NAME + ".csv";
		//return "predictions_BL.csv";
		//return "predictions_BL.csv";
		//return "predictions_BL_Reason.csv";
		return "predictions_KM_testxy.csv";
	}

}
