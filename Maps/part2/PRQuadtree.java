package cmsc420.meeshquest.part2;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class PRQuadtree {
	int width;
	int height;
	Node root;
	
	public PRQuadtree(String w, String h) {
		width = Integer.parseInt(w);
		height = Integer.parseInt(h);
		root = new WhiteNode();
	}
	
	//return null if no error 
	public String insert(City c) {
		//is a city on the border out of bounds? remove = sign?
		if(root.contains(c) == true){
			return "cityAlreadyMapped";
		} else if (c.getX() >= width || c.getY() >= height || c.getX() < 0 || c.getY() < 0){
			return "cityOutOfBounds";
		}
		else {
			root = root.insert(c, width/2, height/2, height/2);
			return null;
		}
	}
	public Element printPRQuadtree(Document results) {
		return root.print(results);
	}
	
	public void clear() {
		root = new WhiteNode();
	}
	
	public int getWidth() {
		return width;
	}
	 
	public int getHeight() {
		return height;
	}
	
	public Node getRoot(){ 
		return root;
	}
	
	public boolean contains(City c) {
		return root.contains(c);
	}
	
	public void remove(City c){
		root = root.remove(c);
	}
}
