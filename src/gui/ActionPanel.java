package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultCaret;

import data.Record;
import main.Globals;

public class ActionPanel extends JPanel{

	private static DefaultTableModel tableModel = new DefaultTableModel();
	private Object[] columnNames;
	public JTable action1Table = getTable();

	private JScrollPane conversationScrollPane;
	private int previousTrialId = 0; 

	private final float fontSize = (float) 14.0;

	private ListSelectionListener tableListener;

	private String[][] displayedData;

	private Border border = BorderFactory.createLineBorder(Color.RED, 4);

	public ActionPanel() {

		super();
		this.setLayout(new GridLayout(1,1));

		conversationScrollPane = new JScrollPane(action1Table);

		conversationScrollPane.setViewportView(action1Table);

		this.add(conversationScrollPane);

		this.setPreferredSize(new Dimension(500,500));
	}
	/*
	 * NOTE: Much of the JTable code is adapted from https://stackoverflow.com/a/26461082
	 * and https://docs.oracle.com/javase/tutorial/2d/text/drawmulstring.html
	 */
	private JTable getTable() {

		JTable table = new JTable(tableModel);
		table.setFocusable(false);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		table.setFocusable(false);
		table.setShowGrid(true);

		// Set the column names
		columnNames = new Object[Globals.NUM_PARTICIPANTS];

		columnNames[Globals.customerUniqueID-1] = Globals.customerJapaneseWord;
		columnNames[Globals.shopkeeper1UniqueID-1] = Globals.shopkeeperJapaneseWord + " 1";
		columnNames[Globals.shopkeeper2UniqueID-1] = Globals.shopkeeperJapaneseWord + " 2　（ロボット）";
		
		
		//		for (int i = 0; i < Globals.NUM_PREDICTION_CONDITIONS; i++) {
		//			columnNames[Globals.NUM_CUSTOMERS+1+i] = Globals.robotJapaneseWord+Integer.toString(i);
		//		}
		tableModel.setDataVector(new Object[][] {{}}, columnNames);

		for (int colI = 0; colI < columnNames.length; colI++) {
			table.getColumn(columnNames[colI]).setCellRenderer(new TextAreaRenderer());
			table.getColumn(columnNames[colI]).setCellEditor(new TextAreaEditor());
		}

		tableListener = new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				// do some actions here, for example
				// print first column value from selected row
				updateAllRowHeights();
//				if (Globals.listPanel.isCurrentlySelectedElementAnAttentionQuestion()) {
//					action1Table.setBorder(border);
//				} else {
//					action1Table.setBorder(null);
//				}
				action1Table.setBorder(null);
			}
		};

		table.getSelectionModel().addListSelectionListener(tableListener);

		return table;
	}

	class TextAreaRenderer extends JScrollPane implements TableCellRenderer
	{
		JTextArea textarea;

		public TextAreaRenderer() {
			textarea = new JTextArea();
			textarea.setFont(textarea.getFont().deriveFont(fontSize));
			textarea.setLineWrap(true);
			textarea.setWrapStyleWord(true);
			textarea.setEditable(false);
			textarea.setBorder(null);
			//	      textarea.setFocusable(false);
			//	      textarea.setHighlighter(null);
			this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			this.setBorder(null);
			//	      textarea.setBorder(new TitledBorder("This is a JTextArea"));
			getViewport().add(textarea);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus,
				int row, int column)
		{
			//		  System.out.println(String.format("getTableCellRendererComponent row %d col %d value %s, value length %d", row, column, value.toString(), ((String) value).length()));
			//	      if (isSelected) {
			//	    	 System.out.println("Is Selected");
			//	         setForeground(table.getSelectionForeground());
			//	         setBackground(table.getSelectionBackground());
			//	         textarea.setForeground(table.getSelectionForeground());
			//	         if (((String) value).length() == 0) {
			//	        	 textarea.setBackground(new Color(0, 0, 0));
			//	         } else {
			//	        	 textarea.setBackground(table.getSelectionBackground());
			//	         }
			//	         
			//	      } else {
			//    	 System.out.println("Is NOT Selected");

			setForeground(table.getForeground());
			setBackground(table.getBackground());
			if (value == null || ((String) value).length() == 0 || (column >= Globals.NUM_PARTICIPANTS && ((String) value).replaceAll("\\s","").contains(Globals.emptyQuotesJapanese))) {
				//        	 System.out.println("String length is 0");
				textarea.setBackground(new Color(0, 0, 0));
				if (value == null || ((String) value).replaceAll("\\s","").contains(Globals.emptyQuotesJapanese)) {
					textarea.setForeground(new Color(255, 255, 255));
				} else {
					textarea.setForeground(new Color(0, 0, 0));
				}
				textarea.setBorder(null);
				this.setBorder(null);
			} else {
				//        	 System.out.println("String length is NOT 0");
				textarea.setForeground(new Color(0, 0, 0));
				
				if (column > Globals.NUM_PARTICIPANTS) {
					textarea.setBackground(new Color(255, 255, 255));
				} 
				
				else if (column == Globals.shopkeeper1UniqueID-1) { // shopkeeper 1
					textarea.setBackground(Globals.shopkeeper1Color);
				} 
				else if (column == Globals.shopkeeper2UniqueID-1) { // shopkeeper 2
					textarea.setBackground(Globals.shopkeeper2Color);
				} 
				else if (column == Globals.customerUniqueID-1) { // customer
					textarea.setBackground(Globals.customerColor);
				}
				
//				if (!Globals.listPanel.isCurrentlySelectedElementAnAttentionQuestion() && row == table.getRowCount() - 1) {
//					textarea.setBorder(border);
//					this.setBorder(border);
//				} else {
				textarea.setBorder(null);
				this.setBorder(null);
				//}
			}

			//	      }

			//	      System.out.println(String.format("Text area background %s forground %s", textarea.getBackground().toString(), textarea.getForeground().toString()));

			textarea.setText((String) value);
			//	      textarea.setCaretPosition(0);

			//	      if (Globals.statusPanel.showingMap) {
			//        	  Globals.statusPanel.hideMap();
			//          }

			return this;
		}
	}

	class TextAreaEditor extends DefaultCellEditor {
		protected JScrollPane scrollpane;
		protected JTextArea textarea;

		public TextAreaEditor() {
			super(new JCheckBox());
			scrollpane = new JScrollPane();
			scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollpane.setBorder(null);
			textarea = new JTextArea(); 
			textarea.setFont(textarea.getFont().deriveFont(fontSize));
			textarea.setLineWrap(true);
			textarea.setWrapStyleWord(true);
			textarea.setEditable(false);
			textarea.setBorder(null);
			//	      textarea.setBorder(new TitledBorder("This is a JTextArea"));
			scrollpane.getViewport().add(textarea);
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {

			//System.out.println(String.format("getTableCellEditorComponent row %d col %d value %s, value length %d", row, column, value.toString(), ((String) value).length()));
			textarea.setText((String) value);

			setForeground(table.getForeground());
			setBackground(table.getBackground());

			if (value == null || ((String) value).length() == 0 || (column >= Globals.NUM_PARTICIPANTS && ((String) value).replaceAll("\\s","").contains(Globals.emptyQuotesJapanese))) {
				//         	 	System.out.println("String length is 0");
				textarea.setBackground(new Color(0, 0, 0));
				if (value == null || ((String) value).replaceAll("\\s","").contains(Globals.emptyQuotesJapanese)) {
					textarea.setForeground(new Color(255, 255, 255));
				} else {
					textarea.setForeground(new Color(0, 0, 0));
				}
				scrollpane.setBorder(null);
				textarea.setBorder(null);
			} 
			else {
				//  		System.out.println("String length is NOT 0");
				textarea.setForeground(new Color(0, 0, 0));
				if (column > Globals.NUM_PARTICIPANTS) {
					textarea.setBackground(new Color(255, 255, 255));
				} 
				else if (column == Globals.shopkeeper1UniqueID-1) { // shopkeeper 1
					textarea.setBackground(Globals.shopkeeper1Color);
				} 
				else if (column == Globals.shopkeeper2UniqueID-1) { // shopkeeper 2
					textarea.setBackground(Globals.shopkeeper2Color);
				}
				else if (column == Globals.customerUniqueID-1) { // customer
					textarea.setBackground(Globals.customerColor);
				}

//				if (!Globals.listPanel.isCurrentlySelectedElementAnAttentionQuestion() && row == table.getRowCount() - 1) {
//					scrollpane.setBorder(border);
//					textarea.setBorder(border);
//				} else 
//				{
				scrollpane.setBorder(null);
				textarea.setBorder(null);
				//}

			}

			//          if (Globals.statusPanel.showingMap) {
			//        	  Globals.statusPanel.hideMap();
			//          }

			return scrollpane;
		}

		public Object getCellEditorValue() {
			return textarea.getText();
		}
	}

	private void updateOneRowHeight(int rowI) {
		int rowHeight = action1Table.getRowHeight();
		for (int colI = 0; colI < action1Table.getColumnCount(); colI++)
		{
			Component comp = action1Table.prepareRenderer(action1Table.getCellRenderer(rowI, colI), rowI, colI);
			rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
		}
		action1Table.setRowHeight(rowI, rowHeight);
	}

	private void updateAllRowHeights() {
		for (int rowI = 0; rowI < action1Table.getRowCount(); rowI++) {
			updateOneRowHeight(rowI);
		}
	}

	public void refresh() {
		//		final int oldVerticalScrollBarPosition = conversationScrollPane.getVerticalScrollBar().getValue();
		//		System.out.println(String.format("oldVerticalScrollBarPosition %d", oldVerticalScrollBarPosition));
		final Record currentRecord = Globals.dataManager.getCurrentRecord();

		//		action1Label.setText(currentRecord.conversationHistory);
		//		boolean isAttentionQuestion = Globals.listPanel == null ? true : Globals.listPanel.isCurrentlySelectedElementAnAttentionQuestion();
		//		boolean isNonRatedQuestion = Globals.listPanel == null ? true : Globals.listPanel.isCurrentlySelectedElementAShouldRespondQuestion();
		if (currentRecord.conversationHistoryInTableFormat.length > 0) {
			displayedData = new String[currentRecord.conversationHistoryInTableFormat.length][currentRecord.conversationHistoryInTableFormat[0].length];
		}
		action1Table.getSelectionModel().removeListSelectionListener(tableListener);
		tableModel.setRowCount(0); // clear all rows
		for (int rowI = 0; rowI < currentRecord.conversationHistoryInTableFormat.length; rowI++) {
			for (int colI = 0; colI < currentRecord.conversationHistoryInTableFormat[rowI].length; colI++) {
				//				if (isAttentionQuestion || isNonRatedQuestion) {
				//					displayedData[rowI][colI] = colI > Globals.NUM_CUSTOMERS ? "" : currentRecord.conversationHistoryInTableFormat[rowI][colI];
				//				} else {
				displayedData[rowI][colI] = currentRecord.conversationHistoryInTableFormat[rowI][colI];
				//				}
			}
			tableModel.addRow(displayedData[rowI]);
			//			tableModel.fireTableRowsInserted(0, rowI);

			//			// NOTE (amal): There is some race condition here that makes it 
			//			// such that sometimes even though we set the row data, the preferred height 
			//			// of the component has not yet updated. The below lines of code hackily fix it.
			//			tableModel.getDataVector().toString();
			//			try {
			//				TimeUnit.MILLISECONDS.sleep(1);
			//			} catch (InterruptedException e) {
			//				e.printStackTrace();
			//			}
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				updateAllRowHeights();
				action1Table.getSelectionModel().addListSelectionListener(tableListener);
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						//	    		System.out.println(String.format("currentRecord %s", currentRecord.toString()));
						//			    		System.out.println(String.format("currentRecord.trialID %d, previousTrialId %d", currentRecord.trialID, previousTrialId));
						if (!currentRecord.trialID.equals(previousTrialId)) {
							// reset the scroll bar to the top of the conversation
							conversationScrollPane.getVerticalScrollBar().setValue(0);
							previousTrialId = currentRecord.trialID;
						} else {
							//			    			System.out.println("Setting scroll bar to old position");
							//	    			conversationScrollPane.getVerticalScrollBar().setValue(oldVerticalScrollBarPosition);
							conversationScrollPane.getVerticalScrollBar().setValue(conversationScrollPane.getVerticalScrollBar().getMaximum());
						}
					}
				});
//				if (Globals.listPanel.isCurrentlySelectedElementAnAttentionQuestion()) {
//					action1Table.setBorder(border);
//				} else {
				action1Table.setBorder(null);
				//}
			}
		});

	}
}
