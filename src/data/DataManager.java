package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import main.Globals;
import toolbox.DebugTools;
import toolbox.notification.NotificationHandler;

public class DataManager {
		
	private ArrayList<Record> allRecords = new ArrayList<Record>();
	private HashMap<Integer,Record> recordsToCode = new HashMap<Integer,Record>();
	
	private Record currentRecord = new Record();
	private ArrayList<String> headers;
	
	
	public void setHeaders(ArrayList<String> headers) {
		this.headers = headers;
	}
	
	public ArrayList<String> getHeaders() {
		return this.headers;
	}
	
	public int getLastRecordID() {
//		if (recordsToCode.isEmpty()) {
//			return 0;
//		}
//		return recordsToCode.size();
		return allRecords.size();
	}
	
	// this is called when loading the data from csv
	// the turns have to be in order for the conversation history to be properly generated
	//
	public void addRecord(Record record) {
		//System.out.println(String.format("Adding record with turnID %x, trialID %x", record.turnID, record.trialID));
		
		allRecords.add(record);
		addRecordToCode(record);
		
	}
	
	
	public void generateConversationHistories() {
		
		Integer previousTrialId = null;
		Integer currentTrialId = null;
		
		ArrayList<ArrayList<String>> currentConversationHistoryAsTable = null;
		HashSet<Integer> uniqueIDsInThisExperiment = new HashSet<Integer>();
		
		
		for (Record record : allRecords) {
			
			currentTrialId = record.trialID;
			
			if (!currentTrialId.equals(previousTrialId)) {
				currentConversationHistoryAsTable = new ArrayList<ArrayList<String>>();
				for (int i = 0; i < Globals.NUM_PARTICIPANTS; i++) {
					currentConversationHistoryAsTable.add(new ArrayList<String>());
				}
				uniqueIDsInThisExperiment.clear();
			}
			
			previousTrialId = currentTrialId;
			
			if (!record.S1utterance.contentEquals("")) {
				currentConversationHistoryAsTable.get(0).add(record.S1utterance + "\n");
			}
			else {
				currentConversationHistoryAsTable.get(0).add("");
			}
			
			if (!record.Cutterance.contentEquals("")) {
				currentConversationHistoryAsTable.get(1).add(record.Cutterance + "\n");
			}
			else {
				currentConversationHistoryAsTable.get(1).add("");
			}
			
			if (!record.S2utterance.contentEquals("")) {
				currentConversationHistoryAsTable.get(2).add(record.S2utterance + "\n");
			}
			else {
				currentConversationHistoryAsTable.get(2).add("");
			}
			
//			for (int i=0; i < Globals.NUM_PARTICIPANTS; i++) {
//				if (i == record.uniqueID-1) {
//					currentConversationHistoryAsTable.get(i).add(record.utterance + "\n");
//				}
//				else {
//					currentConversationHistoryAsTable.get(i).add("");
//				}
//			}
			
			record.shuffleAndSetConversationHistory((ArrayList<ArrayList<String>>) currentConversationHistoryAsTable.clone()); // this is a shallow copy, but that is all I need since all I do is shuffle a subset of the elements in the first dimension
			
		}
	}
	
	
	public void addRecordToCode(Record record) {
		recordsToCode.put(record.turnID, record);
	}

	public void clear() {
		allRecords.clear();
	}

	public ArrayList<Record> getRecords() {
		return allRecords;
	}

	public Record getCurrentRecord() {
		return currentRecord;
	}

	public void setSelectedRecord(int selectedIndex) {
//		DebugTools.print("Setting selected record index to " + selectedIndex);
		currentRecord = recordsToCode.get(selectedIndex);
		//DebugTools.print("Current record: " + currentRecord.toCSVString());
		NotificationHandler.notify(Globals.Notifications.LIST_SELECTION);
	}

	public Integer[] getRecordIDs() {
		ArrayList<Integer> indicesList = new ArrayList<Integer>(recordsToCode.keySet());
		Collections.sort(indicesList);
		Integer[] indices = indicesList.toArray(new Integer[recordsToCode.size()]);
		return indices;
	}

//	public int getCurrentRecordID() {
//		if (currentRecord == null) {
//			return -1;
//		} else {
//			return currentRecord.turnID;
//
//		}
//	}

	public Record getRecord(int id) {
		return recordsToCode.get(id);
	}
}
