package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.Record;
import data.Record.InteractionRatings;
import data.Record.ShouldRespondRatings;
import main.Globals;

public class ListPanel extends JPanel {

	JScrollPane idListPane;
	JList<String> idList;
	
	int numSubQuestions = 3;

	public ListPanel() {
		super();
		setLayout(new GridLayout(1,1));
		this.add(getIDListPane());
		this.setPreferredSize(new Dimension(70,100));
		setUncodedCellRenderer();
		refresh();
	}

	private JScrollPane getIDListPane() {
		if (idListPane == null) {
			idListPane = new JScrollPane(getIdList());
			idListPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return idListPane;
	}
	private JList<String> getIdList() {
		if (idList == null) {
			idList = new JList<String>(); //data has type Object[]
			idList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			idList.setMinimumSize(new Dimension(50,100));
			idList.setFocusable(false);
			idList.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) {
						return;
					}
//					System.out.println(String.format("idList.getSelectedValue() %d", idList.getSelectedValue()));
					String selectedString = idList.getSelectedValue();
					// Remove the last character, which is either A or B, from the string
					Globals.dataManager.setSelectedRecord(Integer.parseInt(selectedString.substring(0, selectedString.length()-1)));
					//idList.ensureIndexIsVisible(idList.getSelectedIndex());
				}
			});
		}
		return idList;
	}
	
//	public boolean isCurrentlySelectedElementAnAttentionQuestion() {
//		String currentlySelectedTurnIDString = Globals.listPanel.getIdList().getSelectedValue();
//		return currentlySelectedTurnIDString.charAt(currentlySelectedTurnIDString.length()-1) == 'A';
//	}
	
	public boolean isCurrentlySelectedElementAShouldRespondQuestion() {
		String currentlySelectedTurnIDString = Globals.listPanel.getIdList().getSelectedValue();
		return currentlySelectedTurnIDString.charAt(currentlySelectedTurnIDString.length()-1) == 'A';
	}
	
//	public boolean isCurrentlySelectedElementANonRatedQuestion() {
//		String currentlySelectedTurnIDString = Globals.listPanel.getIdList().getSelectedValue();
//		return currentlySelectedTurnIDString.charAt(currentlySelectedTurnIDString.length()-1) == 'X';
//	}

	private void setUncodedCellRenderer() {
		idList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				
				String valueString = (String)value;
				
				Record record = Globals.dataManager.getRecord(Integer.parseInt(valueString.substring(0, valueString.length()-1)));
				boolean coded = true;
//				if (valueString.charAt(valueString.length()-1) == 'A') { // Attention Question
//					boolean atLeastOneTrue = false;
//					//for (int i = 0; i < Globals.NUM_CUSTOMERS; ++i) {
//					//	atLeastOneTrue = atLeastOneTrue || record.ratersAttentionResults[Globals.REVIEWER_ID-1][i];
//					//}
//					coded = atLeastOneTrue;
//				} else 
				if (valueString.charAt(valueString.length()-1) == 'A') { // Should Respond Question
					if (record.ratersShouldRespondResults[Globals.REVIEWER_ID-1].equals(ShouldRespondRatings.INCOMPLETE)) {
						coded = false;
					}
				} else if (valueString.charAt(valueString.length()-1) == 'X') { // Non Rated question
					coded = true; // Non Rated questions are always completed
				} else { // Interaction Question
					for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
						if (record.ratersInteractionResults[Globals.REVIEWER_ID-1][i].equals(InteractionRatings.INCOMPLETE)) {
							coded = false;
							break;
						}
					}
				}

				if (!coded) {
					label.setForeground(Color.RED);
				} else {
					label.setForeground(Color.BLACK);
				}

				if (isSelected) {
					label.setBorder(BorderFactory.createLineBorder(Color.black));
				}
				if (/*Globals.IS_VERIFICATION && */record.isUsedForVerification != null && record.isUsedForVerification) {
					if (Globals.trialsForEvaluation.contains(record.trialID)) {
						label.setBackground(new Color(0, 255, 0));
					}
					else {
						label.setBackground(new Color(255, 255, 0));
					}
				} else if (record.isBeforeShopkeeperAction) {
//					label.setBackground(new Color(30, 144, 255));
				}
				
				return label;
			}
		});
	}

	public void moveAhead() {
		int idx = idList.getSelectedIndex();
		idList.setSelectedIndex(idx+1);
	}

	public void moveBack() {
		int idx = idList.getSelectedIndex();
		if (idx > 0)
			idList.setSelectedIndex(idx-1);
	}

	private void refresh() {
		Integer[] recordIDs = Globals.dataManager.getRecordIDs();
		ArrayList<String> listNames = new ArrayList<String>();
		for (int i = 0; i < recordIDs.length; i++) {
			if (Globals.dataManager.getRecord(recordIDs[i]).isBeforeShopkeeperAction) {
				listNames.add(Integer.toString(recordIDs[i])+"A");
				listNames.add(Integer.toString(recordIDs[i])+"B");
				//listNames.add(Integer.toString(recordIDs[i])+"C");
			} else {
//				listNames.add(Integer.toString(recordIDs[i])+"X");
				listNames.add(Integer.toString(recordIDs[i])+"A");
				listNames.add(Integer.toString(recordIDs[i])+"B");
				//listNames.add(Integer.toString(recordIDs[i])+"C");
			}
		}
		String[] listNamesArray = new String[listNames.size()];
		listNames.toArray(listNamesArray);
		getIdList().setListData(listNamesArray);
		getIdList().setSelectedIndex(0);
		//getIdList().setSelectedValue(Globals.dataManager.getCurrentRecordID(), true);
	}

}
