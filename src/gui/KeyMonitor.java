package gui;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

import main.Globals;


public class KeyMonitor implements KeyEventDispatcher {

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_PRESSED) {
			return keyPressed(e);
		}
		else if (e.getID() == KeyEvent.KEY_RELEASED ) {
			return keyReleased(e);
		}
		return false;
	}

	private boolean keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
		case KeyEvent.VK_UP:
			Globals.listPanel.moveBack();
			break;
		case KeyEvent.VK_DOWN:
			Globals.listPanel.moveAhead();
			break;
		}

		return false;
	}
	//		int keyCode = e.getKeyCode();
	//
	//		switch (keyCode) {
	//		case KeyEvent.VK_NUMPAD4:
	//		case KeyEvent.VK_LEFT:
	//			if (WadaCodingTool.actionManager.currentVisitProperty != null) {
	//				WadaCodingTool.actionManager.setResult(CodingResult.SAME);
	//				WadaCodingTool.actionManager.setPropertyYes();
	//				WadaCodingTool.actionManager.setNextVisitProperty();
	//			} else {
	//				WadaCodingTool.actionManager.setPrevABoutTopic();
	//			}
	//			break;			
	//		case KeyEvent.VK_NUMPAD5:
	//		case KeyEvent.VK_BACK_SPACE:
	//			if (WadaCodingTool.actionManager.currentVisitProperty != null) {
	//				WadaCodingTool.actionManager.setResult(CodingResult.NEITHER);
	//				WadaCodingTool.actionManager.setPropertyNo();
	//				WadaCodingTool.actionManager.setNextVisitProperty();
	//			}
	//			break;		
	//		case KeyEvent.VK_NUMPAD6:
	//		case KeyEvent.VK_RIGHT:
	//			if (WadaCodingTool.actionManager.currentVisitProperty != null) {
	//				WadaCodingTool.actionManager.setResult(CodingResult.DIFFERENT);
	//				WadaCodingTool.actionManager.setPropertyYes();
	//				WadaCodingTool.actionManager.setNextVisitProperty();
	//			} else {
	//				WadaCodingTool.actionManager.setNextAboutTopic();
	//			}
	//			break;	
	//
	//		case KeyEvent.VK_DELETE:
	//		case KeyEvent.VK_NUMPAD0:
	//		case KeyEvent.VK_SPACE:
	//		case KeyEvent.VK_NUMPAD3:
	//			WadaCodingTool.actionManager.setNextAboutTopic();
	//			break;
	//
	//		case KeyEvent.VK_INSERT:
	//		case KeyEvent.VK_NUMPAD1:
	//			WadaCodingTool.actionManager.setPrevABoutTopic();
	//			break;
	//
	//		case KeyEvent.VK_NUMPAD8:
	//		case KeyEvent.VK_UP:
	//			WadaCodingTool.actionManager.setPrevVisitProperty();
	//			break;	
	//
	//		case KeyEvent.VK_NUMPAD2:
	//		case KeyEvent.VK_DOWN:
	//			WadaCodingTool.actionManager.setNextVisitProperty();
	//			break;	
	//
	//			//		case KeyEvent.VK_SPACE:
	//			//			WadaCodingTool.actionManager.goToNextVisit();
	//			//			break;
	//
	//			//		case KeyEvent.VK_Y:
	//			//		case KeyEvent.VK_NUMPAD9:
	//			//			WadaCodingTool.actionManager.setPropertyYes();
	//			//			WadaCodingTool.actionManager.setNextVisitProperty();	
	//			//			break;
	//			//
	//			//		case KeyEvent.VK_N:
	//			//		case KeyEvent.VK_NUMPAD3:
	//			//			WadaCodingTool.actionManager.setPropertyNo();
	//			//			WadaCodingTool.actionManager.setNextVisitProperty();	
	//			//			break;
	//
	//
	//		case KeyEvent.VK_S:
	//			System.err.println(WadaCodingTool.dataManager.currentVisit.toJSONString());
	//			if(WadaCodingTool.connectToLM)
	//				WadaCodingTool.client.addForSending(WadaCodingTool.dataManager.currentVisit.toJSONString());
	//			break;
	//
	//			/*	case KeyEvent.VK_N:
	//			WadaCodingTool.actionManager.jumpToNextDifferentTopic();
	//			break;*/
	//
	//		case KeyEvent.VK_ENTER:
	//			//WadaCodingTool.actionManager.setNextVisitProperty();	
	//			WadaCodingTool.actionManager.saveToDB();
	//			break;
	//
	//		case KeyEvent.VK_PAGE_DOWN:
	//			WadaCodingTool.actionManager.jumpToNextID();
	//			break;
	//
	//		case KeyEvent.VK_PAGE_UP:
	//			WadaCodingTool.actionManager.jumpToPrevID();
	//			break;
	//			
	//		}
	//		return false;
	//	}

	private boolean keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
