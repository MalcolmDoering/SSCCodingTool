package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import main.Globals;
import toolbox.notification.NotificationHandler;
import toolbox.notification.NotificationListener;

public class View extends JPanel implements NotificationListener {
	private InputPanel inputPanel = new InputPanel();
	private ActionPanel actionPanel = new ActionPanel();
	private StatusPanel statusPanel = new StatusPanel();
	private ListPanel listPanel = new ListPanel();
	//private JButton proactiveButton;
	
	public View() {
		super();
		Globals.listPanel = listPanel;
		Globals.statusPanel = statusPanel;
		Globals.actionPanel = actionPanel;
		this.setLayout(new BorderLayout());
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel,BoxLayout.Y_AXIS));
		centerPanel.setPreferredSize(new Dimension(300,1000));
		centerPanel.add(actionPanel);
		
		JPanel spacer = new JPanel();
		spacer.setMaximumSize(new Dimension(100,100));
		//spacer.setLayout(new BorderLayout());
		//spacer.add(getProactiveButton());
		centerPanel.add(spacer);
		centerPanel.add(inputPanel);
		
		this.add(listPanel, BorderLayout.WEST);
		this.add(statusPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
		//this.add(inputPanel, BorderLayout.SOUTH);
		
		respondToNotification();
		
		NotificationHandler.registerListener(Globals.Notifications.LIST_SELECTION, this);
	}

//	private JButton getProactiveButton() {
//		if (proactiveButton == null) {
//			proactiveButton = new JButton("Reactive");
//			proactiveButton.addActionListener(new ActionListener() {
//
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					Globals.dataManager.getCurrentRecord().isProactive =!Globals.dataManager.getCurrentRecord().isProactive;
//					refreshButton();
//				}
//				
//			});
//		}
//		return proactiveButton;
//	}

	@Override
	public void respondToNotification() {
		//listPanel.refresh();
		actionPanel.refresh();
		inputPanel.refresh();
		statusPanel.refresh();
		Globals.fileIO.saveData();
		//refreshButton();
	}

//	private void refreshButton() {
//		if (Globals.dataManager.getCurrentRecord().isProactive == true) {
//			getProactiveButton().setText("Proactive");
//		} else {
//			getProactiveButton().setText("Reactive");
//		}
//	}
}
