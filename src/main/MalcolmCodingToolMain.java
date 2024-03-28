package main;

import java.awt.BorderLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gui.KeyMonitor;
import gui.View;
//import toolbox.notification.NotificationHandler;

public class MalcolmCodingToolMain {

	public static void main(String[] args) {
		
		Globals.initialize();
		System.out.println("Initialized Globals");
		
		Globals.fileIO.loadData();
		System.out.println("Loaded Data.");
		
		
		// TODO generate interaction histories to display
		Globals.dataManager.generateConversationHistories();
		
		
		// setup GUI
		final JFrame frame = new JFrame();
		JPanel mainPanel = new View();
		KeyMonitor keyMonitor = new KeyMonitor();

		frame.setTitle("Coding " + Globals.SCENARIO_NAME);
		frame.add(mainPanel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				while (!Globals.fileIO.saveData()) {
					int result = JOptionPane.showOptionDialog(
							frame, Globals.dataSaveErrorMessageJapanese, Globals.errorJapanese,
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, 
							null, new String[] {Globals.yesOptionJapanese,Globals.noOptionJapanese,Globals.cancelOptionJapanese}, null
						);
					// JOptionPane.showInternalConfirmDialog(desktop, "Continue printing?");
					if (result == JOptionPane.CANCEL_OPTION) {
						System.out.println("Canceling.");
						return;
					} else if (result == JOptionPane.YES_OPTION) {
						System.out.println("Trying to save again.");
						continue;
					} else if (result == JOptionPane.NO_OPTION) {
						System.out.println("Not saving.");
						//frame.dispose();
						break;
					}
				}
				System.out.println("Disposing frame.");
				frame.dispose();
				System.exit(0);
			}
		});		

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyMonitor);

		//dataManager.getAllVisits();
		frame.pack();
		frame.setSize(1500,900);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		Globals.mainFrame = frame;
	
	}

}
