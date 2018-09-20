package cmsc420.meeshquest.part2;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class WhiteNode implements Node {
	
	public WhiteNode() {
		
	}

	public BlackNode insert(City c, int x, int y, int h) {
		BlackNode n = new BlackNode(c, x, y, h);
		return n;
	}

	public Node remove(City c) {
		return null;
	}
	public String isNode(){
		return "white";
	}

	public Element print(Document results) {
		return results.createElement("white");
	}
	
	public boolean contains(City c){
		return false;
	}
	public City getCity(){
		return null;
	}
}
