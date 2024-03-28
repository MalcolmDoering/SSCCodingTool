package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import data.Record;
import data.Record.InteractionRatings;
import data.Record.ShouldRespondRatings;
import main.Globals;

public class InputPanel extends JPanel {

	//private ArrayList<AttentionResponsePanel> attentionPanel = new ArrayList<AttentionResponsePanel>();
	private ArrayList<ShouldRespondResponsePanel> shouldRespondPanel = new ArrayList<ShouldRespondResponsePanel>();
	private ArrayList<InteractionResponsePanel> interactionPanels = new ArrayList<InteractionResponsePanel>();

	public InputPanel() {
		super();
		UIManager.put("ToggleButton.select", Color.YELLOW);
		this.setLayout(new GridLayout(0,1));
		{
			//AttentionResponsePanel panel = new AttentionResponsePanel(0);
			//attentionPanel.add(panel);
		}
		{
			ShouldRespondResponsePanel panel = new ShouldRespondResponsePanel(0);
			shouldRespondPanel.add(panel);
		}
		for (int i=0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
			InteractionResponsePanel panel = new InteractionResponsePanel(i);
			
			if (Globals.conditionNames[i] == "Proposed") {
				panel.highlight();
			}
			
			interactionPanels.add(panel);
		}
	}
	
	
	public void randomizeLayout(Record record) {
		this.removeAll();
//		if (Globals.listPanel.isCurrentlySelectedElementAnAttentionQuestion()) { // Attention
//			// No need to randomize attention, which has only one question
//		} else 
		if (Globals.listPanel.isCurrentlySelectedElementAShouldRespondQuestion()) { // Non rated question
			// No need to randomize should respond, which has only one question
		} else {
			Collections.sort(interactionPanels, 
					new Comparator<InteractionResponsePanel>() {
						public int compare(InteractionResponsePanel a, InteractionResponsePanel b) {
							return Integer.compare(a.index, b.index);
						}
					}
			);
			Random randomizer = record.getNewRandomizer();
			//Collections.shuffle(interactionPanels, randomizer);
		}
		
		
		layoutPanels();
	}
	
	
	private void layoutPanels() {
//		if (Globals.listPanel.isCurrentlySelectedElementAnAttentionQuestion()) { // Attention
//			//this.add(attentionPanel.get(0));
//		} else 
		if (Globals.listPanel.isCurrentlySelectedElementAShouldRespondQuestion()) { // Non-Rated
			this.add(shouldRespondPanel.get(0));
		} else { // Interaction
			for (int i=0; i < Globals.NUM_PREDICTION_CONDITIONS; ++i) {
				this.add(interactionPanels.get(i));
				//System.out.println(panels.get(i).index);
			}
		}
		
		//System.out.println("");
		
		this.revalidate();
		this.repaint();
	}
	
	
	public void refresh() {	
		randomizeLayout(Globals.dataManager.getCurrentRecord());
		
//		if (Globals.listPanel.isCurrentlySelectedElementAnAttentionQuestion()) { // Attention
//			//for (AttentionResponsePanel panel:attentionPanel) {
//			//	panel.update();
//			//}
//		} else 
		if (Globals.listPanel.isCurrentlySelectedElementAShouldRespondQuestion()) { // Non-Rated
			for (ShouldRespondResponsePanel panel:shouldRespondPanel) {
				panel.update();
			}
		} else {
			for (InteractionResponsePanel panel:interactionPanels) {
				panel.update();
			}
		}
		
		
	}
	
	private class ActionListenerWithIndex implements ActionListener {
		private int index;
		
		public ActionListenerWithIndex(int i) {
			index = i;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Toggle the selected value
			//Globals.dataManager.getCurrentRecord().ratersAttentionResults[Globals.REVIEWER_ID-1][index] = !Globals.dataManager.getCurrentRecord().ratersAttentionResults[Globals.REVIEWER_ID-1][index];
		}
		
	}
	
	/********
	private class AttentionResponsePanel extends JPanel {
		final int index;
		
		JToggleButton[] attentionButtons;
		
		JTextArea contentPanel;

		AttentionResponsePanel(final int index) {
			super();
			this.index = index;
			setLayout(new BorderLayout());
			JPanel ratingPanel = new JPanel();
//			if (Globals.listPanel == null || Globals.listPanel.isCurrentlySelectedElementAnAttentionQuestion()) { // Attention
			ratingPanel.setLayout(new GridLayout(1,Globals.NUM_CUSTOMERS));
			
			attentionButtons = new JToggleButton[Globals.NUM_CUSTOMERS];
			
			for (int i = 0; i < Globals.NUM_CUSTOMERS; i++) {
				JToggleButton button = new JToggleButton(Globals.customerJapaneseWord+Integer.toString(i+1));
				button.addActionListener(new ActionListenerWithIndex(i));
				attentionButtons[i] = button;
				ratingPanel.add(button);
			}
			// placeholder buttons to make the widths align with the table columns
			for (int i = Globals.NUM_CUSTOMERS; i < Globals.NUM_CUSTOMERS+1; i++) {
				JToggleButton button = new JToggleButton();
				button.setVisible(false);
				button.setEnabled(false);
				ratingPanel.add(button);
			}
			
			if (Globals.attentionQuestionJapaneseString.length() > 0) {
				contentPanel = new JTextArea();
				contentPanel.setLineWrap(true);
				contentPanel.setBorder(makeBorder());
				contentPanel.setFont(contentPanel.getFont().deriveFont(18f));
				contentPanel.setText(Globals.attentionQuestionJapaneseString);
				this.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
				this.add(ratingPanel, BorderLayout.EAST);
			} else {
				this.add(ratingPanel);
			}
			
			this.setPreferredSize(new Dimension(300,90));
		}

		void update() {
			for (int i = 0; i < Globals.NUM_CUSTOMERS; i++) {
				boolean selected = Globals.dataManager.getCurrentRecord().ratersAttentionResults[Globals.REVIEWER_ID-1][i];
				attentionButtons[i].setSelected(selected);
			}
		}
	}
	**********/
	
	
	private class ShouldRespondResponsePanel extends JPanel {
		final int index;
		
		JToggleButton yesButton;
		JToggleButton noButton;
		JToggleButton eitherButton;
		ButtonGroup group;
	
		ShouldRespondResponsePanel(final int index) {
			super();
			this.index = index;
			setLayout(new BorderLayout());
			JPanel ratingPanel = new JPanel();
//			if (Globals.listPanel == null || Globals.listPanel.isCurrentlySelectedElementAnAttentionQuestion()) { // Attention
			ratingPanel.setLayout(new GridLayout(1,3));
			
			group = new ButtonGroup();
			
			yesButton = new JToggleButton(Globals.shopkeeperShouldRespondYesJapanese);
			yesButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Globals.dataManager.getCurrentRecord().ratersShouldRespondResults[Globals.REVIEWER_ID-1] = ShouldRespondRatings.YES;
				}
			});
			
			eitherButton = new JToggleButton(Globals.shopkeeperShouldRespondEitherJapanese);
			eitherButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Globals.dataManager.getCurrentRecord().ratersShouldRespondResults[Globals.REVIEWER_ID-1] = ShouldRespondRatings.EITHER;
				}
			});
			
			noButton = new JToggleButton(Globals.shopkeeperShouldRespondNoJapanese);
			noButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Globals.dataManager.getCurrentRecord().ratersShouldRespondResults[Globals.REVIEWER_ID-1] = ShouldRespondRatings.NO;
				}
			});
			
			SwingUtilities.updateComponentTreeUI(yesButton);
			SwingUtilities.updateComponentTreeUI(eitherButton);
			SwingUtilities.updateComponentTreeUI(noButton);
			
			group.add(yesButton);
			group.add(eitherButton);
			group.add(noButton);
			
			ratingPanel.add(yesButton);
			ratingPanel.add(eitherButton);
			ratingPanel.add(noButton);
			
			this.add(ratingPanel);
			
			this.setPreferredSize(new Dimension(300,90));
		}

		void update() {
			ShouldRespondRatings rating = Globals.dataManager.getCurrentRecord().ratersShouldRespondResults[Globals.REVIEWER_ID-1];
			switch(rating) {
			case NO:
				noButton.setSelected(true);
				break;
			case EITHER:
				eitherButton.setSelected(true);
				break;
			case YES:
				yesButton.setSelected(true);
				break;
			case INCOMPLETE:
				group.clearSelection();
				break;
			}
		}
	}
	
	private class InteractionResponsePanel extends JPanel {
		final int index;
		
		JToggleButton goodButton;
		JToggleButton badButton;
		JToggleButton unsureButton;
		JTextArea reasonText;
		JScrollPane reasonTextScrollPane;
		
		ButtonGroup group;
		JTextArea contentPanel;

		InteractionResponsePanel(final int index) {
			super();
			this.index = index;
			setLayout(new BorderLayout());
			JPanel ratingPanel = new JPanel();
			ratingPanel.setLayout(new GridLayout(1,3));

			goodButton = new JToggleButton(Globals.goodJapaneseWord+"\r\n");
			goodButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Globals.dataManager.getCurrentRecord().ratersInteractionResults[Globals.REVIEWER_ID-1][index] = InteractionRatings.GOOD;
				}
			});

			badButton = new JToggleButton(Globals.badJapaneseWord+"\r\n");
			badButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Globals.dataManager.getCurrentRecord().ratersInteractionResults[Globals.REVIEWER_ID-1][index] = InteractionRatings.BAD;
				}
			});


			unsureButton = new JToggleButton("?\r\n");
			unsureButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Globals.dataManager.getCurrentRecord().ratersInteractionResults[Globals.REVIEWER_ID-1][index] = InteractionRatings.UNSURE;
				}
			});
			
			reasonText = new JTextArea();
			reasonText.setLineWrap(true);
			reasonText.setWrapStyleWord(true);
			reasonText.getDocument().addDocumentListener(new DocumentListener() {
					
				@Override
				public void insertUpdate(DocumentEvent e) {
					saveText();
				}
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					saveText();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					saveText();
				}
				
				public void saveText() {
					if (Globals.REVIEWER_ID == 1) {
						Globals.dataManager.getCurrentRecord().raterReasons[Globals.REVIEWER_ID-1][index] = reasonText.getText();
					} else if (Globals.REVIEWER_ID == 2) {
						Globals.dataManager.getCurrentRecord().raterReasons[Globals.REVIEWER_ID-1][index] = reasonText.getText();
					}
				}
			});
			
			
			SwingUtilities.updateComponentTreeUI(goodButton);
			SwingUtilities.updateComponentTreeUI(badButton);
			SwingUtilities.updateComponentTreeUI(unsureButton);
			SwingUtilities.updateComponentTreeUI(reasonText);
			
			group = new ButtonGroup();
			group.add(goodButton);
			group.add(badButton);
			group.add(unsureButton);

			ratingPanel.add(goodButton);
			ratingPanel.add(badButton);
			ratingPanel.add(unsureButton);

			//reasonText.setPreferredSize(new Dimension(270,90));
			reasonTextScrollPane = new JScrollPane(reasonText);
			//reasonTextScrollPane.setViewportView(reasonText);
			reasonTextScrollPane.setPreferredSize(new Dimension(270,90));
			
			ratingPanel.add(reasonTextScrollPane);
			
			this.add(ratingPanel, BorderLayout.EAST);

			contentPanel = new JTextArea();
			contentPanel.setLineWrap(true);
			contentPanel.setBorder(makeBorder());
			contentPanel.setFont(contentPanel.getFont().deriveFont(18f));
			this.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
			this.setPreferredSize(new Dimension(300,90));
		}

		void update() {
			InteractionRatings rating = Globals.dataManager.getCurrentRecord().ratersInteractionResults[Globals.REVIEWER_ID-1][index];
			switch(rating) {
			case GOOD:
				goodButton.setSelected(true);
				break;
			case BAD:
				badButton.setSelected(true);
				break;
			case UNSURE:
				unsureButton.setSelected(true);
				break;
			case INCOMPLETE:
				group.clearSelection();
				break;
			}
			String text = Globals.dataManager.getCurrentRecord().robotUtterances[index];
			contentPanel.setText(text);
			
			reasonText.setText(Globals.dataManager.getCurrentRecord().raterReasons[Globals.REVIEWER_ID-1][index]);
			
			
			
		}
		
		public void highlight() {
			contentPanel.setBackground(Color.PINK);
		}
	}


	private Border makeBorder() {
		return BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),
				BorderFactory.createEmptyBorder(4, 4, 4, 4));
	}
}
