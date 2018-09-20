package cmsc420.meeshquest.part3;

import java.awt.geom.Point2D;

import org.w3c.dom.Element;

////The best way to implement a City is to have your 
//class extend Point2D.Float,and add data members (strings) 
//for name and color
public class City extends Point2D.Float{
	public String name;
	public String color;
	public int radius;
	public boolean iso = false;

	public City(String n, String xC, String yC, String r, String c) {
		name = n;
		try {
			super.x = Integer.parseInt(xC);
			super.y = Integer.parseInt(yC);
			radius = Integer.parseInt(r);
		} catch (NumberFormatException e) {} 
		
		color = c;
		
	}
	/*public void printCity() {
		return name;
		/*System.out.println(super.x);
		System.out.println(super.y);
		System.out.println(radius);
		System.out.println(color);
	}*/
	
	public double getX(){
		return super.x;
	}
	public double getY(){
		return super.y;
	}
	public String getXString(){
		int x1 = (int) super.x;
		return Integer.toString(x1);
	}
	public String getYString(){
		int y1 = (int) super.y;
		return Integer.toString(y1);
	}
	public int getRadius(){
		return this.radius;
	}
	public String getRadiusString(){
		return Integer.toString(radius);
	}
	public String getName(){
		return this.name;
	}
	public String getColor(){
		return this.color;
	}
	public Boolean isIso(){
		return iso;
	}
	
}
