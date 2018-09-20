package cmsc420.meeshquest.part4;

import javax.xml.transform.TransformerException;



import cmsc420.drawing.CanvasPlus;
import cmsc420.meeshquest.part4.CommandParser;
import cmsc420.xml.XmlUtility;

public class MeeshQuest {
	public static void main(String[] args) {
		//command parser reads from standardin 
		CommandParser mediator = new CommandParser();

	    try {
	    	//print results (from mediator class)
			XmlUtility.print(mediator.getResults());
		} catch (TransformerException e) {
			System.out.print("TransformerExcption Error");
			e.printStackTrace();
		}
	}
}