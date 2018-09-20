package cmsc420.meeshquest.part2;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class BlackNode implements Node {
	City cty = null;
	int x;
	int h;
	int y;

	public BlackNode(City c, int xC, int yC, int height){
		cty = c;
		x = xC;
		y = yC;
		h = height;
	}
	public GrayNode insert(City c, int xc, int yc, int height) {
		   GrayNode g = new GrayNode(this.x, this.y, this.h);
		  // System.out.println("City: " + c.getName() + " Gray Node x: " + g.x + " y: " +g.y+" height: " + g.hi);
		   g = (GrayNode) g.insert(cty, this.x, this.y, this.h);
		   g = (GrayNode) g.insert(c, this.x, this.y, this.h);
		return g;
	}
	public String isNode(){
		return "black";
	}
	public WhiteNode remove(City c) {
		if(c.equals(cty)){
			return new WhiteNode();
		}
		return null;
	}

	public Element print(Document results) {
		 Element b = results.createElement("black");
		 b.setAttribute("name", cty.getName());
		 b.setAttribute("x", cty.getXString());
		 b.setAttribute("y", cty.getYString());
		return b;
	}
	
	public boolean contains(City c) {
		if(c.equals(cty)) return true;
		else return false;
	}
	public City getCity(){
		return cty;
	}

}
