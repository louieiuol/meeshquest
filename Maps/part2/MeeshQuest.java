package cmsc420.meeshquest.part2;

import javax.xml.transform.TransformerException;

import cmsc420.drawing.CanvasPlus;
import cmsc420.meeshquest.part2.CommandParser;
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
	
	public void drawing(){
//		CanvasPlus canvas = new CanvasPlus("MeeshQuest", spatialWidth, spatialHeight);
//		canvas.addRectangle(0, 0, spatialWidth, spatialHeight, Color.BLACK, false);
//		canvas.addPoint("name", x, y, Color.BLACK);
//		
//		
//		canvas.removePoint("name", x, y, Color.BLACK);
//		canvas.addCircle(x, y, radius, Color.BLUE, false);
//		canvas.save("filename");
//		
//		canvas.draw();
//		
//		canvas.dispose();
		
	}
}