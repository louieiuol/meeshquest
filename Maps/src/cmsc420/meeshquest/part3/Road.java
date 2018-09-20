package cmsc420.meeshquest.part3;

import java.awt.geom.Line2D;

import org.w3c.dom.Element;

////The best way to implement a City is to have your 
//class extend Point2D.Float,and add data members (strings) 
//for name and color
public class Road extends Line2D.Float {
	public String startc;
	public String endc;
	public double startx;
	public double starty;
	public double endx;
	public double endy;
	public City startCity;
	public City endCity;

	public Road(City start, City end) {
		if(start.getName().compareTo(end.getName()) < 0){
			startc = start.getName();
			endc = end.getName();
		} else {
			startc = end.getName();
			endc = start.getName();
		}
		
		startCity = start;
		endCity = end;
		
		startx = start.getX();
		super.x1 = (float) startx;
		
		starty = start.getY();
		super.y1 = (float) starty;
		
		endx = end.getX();
		super.x2 = (float) endx;
		
		endy = end.getY();
		super.y2 = (float) endy;
		
	}
	
	public Boolean comp(Road r){
		if(r.getStart().compareTo(startc) == 0 && r.getEnd().compareTo(endc) == 0)
			return true;
		else if(r.getStart().compareTo(endc) == 0 && r.getEnd().compareTo(startc) == 0)
			return true;
		else return false;
	}
	
	public String getStart(){
		return startc;
	}
	
	public String getEnd(){
		return endc;
	}
	
//	public String toString(){
//		return " Road start: " + startCity.getName() + " end: " + endCity.getName() + " "+ getWeight();
//	}
	
	public double getWeight(){
		return startCity.distance(endCity.getX(),endCity.getY());
	}
	
	
	
}
