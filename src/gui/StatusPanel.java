package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.Record;
import data.Tuple;
import main.Globals;

public class StatusPanel extends JPanel {
	
	private JLabel participantIdLabel;
	private JLabel trialIdLabel;
	
	public boolean showingMap = false;
	private JButton showMap;
	private JFrame mapFrame = null;
	private BufferedImage bImg;
	private BufferedImage backgroundImg;
	private BufferedImage backgroundImg2;
	private JLabel mapLabel;
	private Font mapFont = new Font("Serif", Font.PLAIN, 12);
	private int radius = 20;
	
	// These three values are taken from the generated map that is inputed into Layer2 and InteractionDebugger. \eclipse-workspace\InteractionDebugger\Maps\MultiRobotController\atr2f_ely\backgroundscale.ini
	//private double xOffset = -15376;
	//private double yOffset = 10449;
	//private double xyScale = 50;
	private double xOffset = 10300;
	private double yOffset = -50;
	private double xyScale = 10.0;
	private double imgWidth = 940;
	private double imgHeight = 1100;
	
	private double scale = 0.5;
	
	private int yOffsetImageInFrame = 0;//-80;
	
	public StatusPanel() {
		super();
		participantIdLabel = new JLabel(Globals.coderJapaneseWord + "ID: " + Globals.REVIEWER_ID);
		this.add(participantIdLabel);
		
		trialIdLabel = new JLabel(Globals.trialJapaneseWord + "ID: " + (Globals.dataManager.getCurrentRecord().trialID == null ? "" : Integer.toString(Globals.dataManager.getCurrentRecord().trialID)));
		
		trialIdLabel.setFont(new Font(trialIdLabel.getName(), Font.BOLD, 24));
		
		this.add(trialIdLabel);
		
		showMap = new JButton(Globals.mapJapaneseWord);
		showMap.setVisible(true);
		this.add(showMap);
		final StatusPanel currentPanel = this;
		showMap.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				showingMap = !showingMap;
//				if (showingMap) {
					showMap(Globals.dataManager.getCurrentRecord(), true);
//				} else {
//					hideMap();
//				}
			}
		});
	}
	
	public void showMap(Record record, boolean buttonClicked) {
		// We only show the map after the main frame has fully loaded, and 
		// if 
		if (Globals.mainFrame == null || (!buttonClicked && !showingMap)) {
			return;
		}
		
		showingMap = true;
//		System.out.println(mapFrame == null ? "null" : mapFrame.toString());
		if (mapFrame == null) {
			mapFrame = new JFrame(); //creates jframe f

//	        mapFrame.setUndecorated(true); //removes the surrounding border
	        mapFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
	        bImg = null;
			try {
				bImg = ImageIO.read(new File("data/atr2f_multipleshopkeepers_background.jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
	        int prefW = bImg.getWidth();
	        int prefH = bImg.getHeight();
	        
	        backgroundImg = new BufferedImage((int)Math.round(prefW * scale), (int)Math.round((prefH+yOffsetImageInFrame) * scale), BufferedImage.TYPE_INT_ARGB);
	        
	        AffineTransform at = new AffineTransform();
	        at.scale(scale, scale);
	        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
	        
	        backgroundImg = scaleOp.filter(bImg, backgroundImg);

	        backgroundImg2 = deepCopy(backgroundImg);
	        
	        
	        mapLabel = new JLabel(new ImageIcon(backgroundImg)); //puts the image into a jlabel

	        mapFrame.getContentPane().add(mapLabel); //puts label inside the jframe

	        mapFrame.setSize((int)Math.round(prefW * scale), (int)Math.round((prefH+yOffsetImageInFrame) * scale)); //gets h and w of image and sets jframe to the size
	        
	        
	        mapFrame.addWindowListener(new WindowAdapter() {
	            public void windowClosing(WindowEvent e) {
	                showingMap = false;
	              }
	            });
		}
		
		
		// Render the participants
		Graphics g = backgroundImg.getGraphics();
        g.drawImage(backgroundImg2, 0, yOffsetImageInFrame, mapLabel);
        g.setFont(mapFont);
        FontMetrics mapFontMetrics = g.getFontMetrics();
        
        // test
        //g.fillOval(0, 1100, radius, radius);
    	//g.setColor(Globals.shopkeeper1Color);
    	//g.drawOval(0, 1100, radius, radius);
        
        for (Map.Entry<Integer, Tuple<Double, Double>> entry : record.uniqueIDToXY.entrySet()) {
        	Integer uniqueID = entry.getKey();
        	Tuple<Double, Double> xy = entry.getValue();
        	if (xy.x != null && xy.y != null) {
	        	String text;
	        	if ((int)uniqueID == Globals.shopkeeper1UniqueID) { // shopkeeper 1 
	        		g.setColor(Globals.shopkeeper1Color);
	        		text = "S1";//Globals.shopkeeperJapaneseWord;
	        	} 
	        	else if ((int)uniqueID == Globals.customerUniqueID) { // customer
	        		g.setColor(Globals.customerColor);
	        		text = "C";
	        	}
	        	else if ((int)uniqueID == Globals.shopkeeper2UniqueID) { // shopkeeper 2
	        		g.setColor(Globals.shopkeeper2Color);
	        		text = "S2";
	        	}
	        	else {
	        		g.setColor(new Color(128, 128, 128));
	        		text = "INVALID";
	        	}
	        	
	        	double textWidth = mapFontMetrics.stringWidth(text);
	        	double textHeight = mapFontMetrics.getMaxAscent();
        		//int mapX = (int)Math.round((xy.x.doubleValue()*1000-xOffset)/xyScale);
            	//int mapY = (int)Math.round(-1*(xy.y.doubleValue()*1000-yOffset)/xyScale);
            	
	        	int mapX = (int)Math.round((xy.x.doubleValue() / xyScale) * scale);
            	int mapY = (int)Math.round((imgHeight - ((xy.y.doubleValue() ) / xyScale) + yOffset) * scale);
            	
            	//System.out.println(String.format("%s %s %s %s %s", uniqueID, xy.x.doubleValue(), xy.y.doubleValue(), mapX, mapY));
            	
//            	System.out.println(String.format("uniqueID %d, x %f, y%f, mapX %d, mapY %d", uniqueID, xy.x, xy.y, mapX, mapY));
            	g.fillOval(mapX-radius/2, mapY-radius/2+yOffsetImageInFrame, radius, radius);
            	g.setColor(new Color(0,0,0));
            	g.drawOval(mapX-radius/2, mapY-radius/2+yOffsetImageInFrame, radius, radius);
            	g.drawString(text, mapX-(int)(textWidth/2)+1, mapY+(int)(textHeight/2)+yOffsetImageInFrame-1);
        	}
        }
        
        if (buttonClicked) {
        	int x = Globals.mainFrame.getX() + Globals.mainFrame.getWidth();///2 - mapFrame.getSize().width/2; //These two lines are the dimensions
	        int y = Globals.mainFrame.getY() + Globals.mainFrame.getInsets().top + this.getSize().height + Globals.actionPanel.action1Table.getTableHeader().getHeight();
//	        System.out.println("Button Clicked");
	        mapFrame.setLocation(x, y); //sets the location of the jframe
	        mapFrame.toFront();
	        mapFrame.pack();
        }

        mapFrame.repaint();
        mapFrame.setVisible(true); //makes the jframe visible
	}
	
//	public void hideMap() {
//		showingMap = false;
//		mapFrame.toBack();
//		mapFrame.setVisible(false);
//	}
	
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}
	
	public void refresh() {
		trialIdLabel.setText(Globals.trialJapaneseWord + "ID: " + (Globals.dataManager.getCurrentRecord().trialID == null ? "" : Integer.toString(Globals.dataManager.getCurrentRecord().trialID)));
		trialIdLabel.setForeground(Color.black);
		showMap(Globals.dataManager.getCurrentRecord(), false);
	}
}
