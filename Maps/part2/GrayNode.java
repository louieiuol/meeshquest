package cmsc420.meeshquest.part2;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class GrayNode implements Node {
	int x;
	int y;
	int hi;
	Node[] children = new Node[4];
	WhiteNode singleton = new WhiteNode();

	public GrayNode(int xc, int yc, int height){
		x = xc;
		y = yc;
		for(int i = 0; i < 4;i++){
			children[i] = singleton;
		}
		hi = height;
	}
	public Node insert(City c, int xC, int yC, int height) {
		int h = this.hi/2;
		int index = getIndex(c);
		if(index == 0){
			children[index] = children[index].insert(c, x-h, y + (h), h);
		} else if(index == 1){
			children[index] = children[index].insert(c, x + (h), y + (h), h);
		}else if(index == 2){
			children[index] = children[index].insert(c, x-h, y-h, h);
		} else {
			children[index] = children[index].insert(c, x + (h), y - (h), h);
		}
		return this;
	}

	public String isNode(){
		return "gray";
	}
	public City getCity(){
		return null;
	}
	public Node remove(City c) {
		children[getIndex(c)] = children[getIndex(c)].remove(c);
		
		int whiteCount = 0; 
		int blackCount = 0;
		
		for(Node child: children){
			if (child.isNode().equals("black"))
				blackCount += 1;
			else if (child.isNode().equals("white"))
				whiteCount += 1;
		}
		if((whiteCount == 3 && blackCount == 1) || whiteCount == 4){
			for(Node child: children){
				if (child.isNode().equals("black"))
					return new BlackNode(child.getCity(), this.x, this.y, this.hi);
			}
		} 
		return this;
	}

	
	public Element print(Document results) {
		Element b = results.createElement("gray");
		 b.setAttribute("x", Integer.toString(x));
		 b.setAttribute("y", Integer.toString(y));
		 for(Node n: children){
			 b.appendChild(n.print(results));
		 }
		return b;
	}
	
	public int getIndex(City c){//correct index - 1 for array indexing
		if (c.getX() < x){
			if(c.getY() < y){
				return 2;
			} else {
				return 0;
			}
		} else{
			if(c.getY() >= y){
				return 1;
			} else {
				return 3;
			}
		}
	}
	
	public boolean contains(City c){
		return children[getIndex(c)].contains(c);
	}

}
