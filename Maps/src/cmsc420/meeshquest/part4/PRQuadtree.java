package cmsc420.meeshquest.part4;

import org.w3c.dom.Element;
import cmsc420.geom.*;

import java.awt.List;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
		if(root.contains(c) == true){
			return "cityAlreadyMapped";
		} else if (c.getX() > width || c.getY() > height || c.getX() < 0 || c.getY() < 0){
			return "cityOutOfBounds";
		}
		else {
			root = root.insert(c, width/2, height/2, height/2);
			return null;
		}
	}
	public String insertRoad(Road r, City start, City end){
		if(outOfBounds(r) == true){
			return "roadOutOfBounds";
		}else{
			this.insert(start);
			this.insert(end);
			root = root.insertRoad(r, width/2, height/2, height/2);
		}
		return null;
	}
	private Boolean outOfBounds(Road r){		
		if(r.intersects(0,0,width,height) == true){
			return false;
		} return true;
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
	/*public boolean containsRoad(Road r) {
		return root.containsRoad(r);
	}*/
	
	public void remove(City c){
		root = root.remove(c);
	}
	
	public interface Node {
		public Node insert(City c, int x, int y, int h);
		public Node insertRoad(Road r, int x, int y, int h);
		public Node remove(City c);
		public Element print(Document d);
		public boolean contains(City c);
		public String isNode();
		public City getCity();
		//public boolean containsRoad(Road r);
		

		//public int size method? black=1, white=0, grey= add all children sizes
	}
	
	/*White NODE*/
	public class WhiteNode implements Node {

		public WhiteNode() {
			
		}

		public BlackNode insert(City c, int x, int y, int h) {
			BlackNode n = new BlackNode(c, x, y, h);
			return n;
		}
		
		public BlackNode insertRoad(Road r, int x, int y, int h) {
			BlackNode n = new BlackNode(r, x, y, h);
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
		/*public boolean containsRoad(Road r){
			return false;
		}*/
		public City getCity(){
			return null;
		}
	}
	
	/*GRAY NODE*/
	public class GrayNode implements Node {
		int x;
		int y;
		int hi;
		
		public Node[] children = new Node[4];
		Rectangle2D[] regions = new Rectangle2D[4];
		WhiteNode singleton = new WhiteNode();

		public GrayNode(int xc, int yc, int height){
			x = xc;
			y = yc;
			for(int i = 0; i < 4;i++){
				children[i] = singleton;
			}
			hi = height;
			
			regions[0] = new Rectangle2D.Double(x-hi,y,hi,hi);
			regions[1] = new Rectangle2D.Double(x,y,hi,hi);
			regions[2] = new Rectangle2D.Double(x-hi,y-hi,hi,hi);
			regions[3] = new Rectangle2D.Double(x,y-hi,hi,hi);
			
			//Rectangle2D r = new Rectangle2D.Double(x-hi,y-hi,2*hi,2*hi);
			//double centerx = r.getCenterX();
			//double centery = r.getCenterY();
			//System.out.println("top left x,y: " + (x-hi) + ", " + (y+hi));
			//System.out.println("height, width: " + r.getHeight() + ", " + r.getWidth());
			//System.out.println(centerx + ", " + centery);
			
		}
		public Node insert(City c, int xC, int yC, int height) {
			//double h = Math.ceil(this.hi/2);
			int h = this.hi/2;
			if(Inclusive2DIntersectionVerifier.intersects(((Point2D) c),regions[0])){	
				children[0] = children[0].insert(c, x-h, y + (h), h);	
			} 
			if(Inclusive2DIntersectionVerifier.intersects(((Point2D) c),regions[1])){
				children[1] = children[1].insert(c, x + (h), y + (h), h);	
			}
			if(Inclusive2DIntersectionVerifier.intersects(((Point2D) c),regions[2])){
				children[2] = children[2].insert(c, x-h, y-h, h);	
			} 
			if(Inclusive2DIntersectionVerifier.intersects(((Point2D) c),regions[3])) {
				children[3] = children[3].insert(c, x + (h), y - (h), h);	
			}
			return this;
		}
		
		public Node insertRoad(Road r,int xC, int yC, int height){
			int h = this.hi/2;
			
			if(Inclusive2DIntersectionVerifier.intersects(((Line2D) r),regions[0])){	
				children[0] = children[0].insertRoad(r, x-h, y + (h), h);

			} 
			if(Inclusive2DIntersectionVerifier.intersects(((Line2D) r),regions[1])){
				children[1] = children[1].insertRoad(r, x + (h), y + (h), h);

			}
			if(Inclusive2DIntersectionVerifier.intersects(((Line2D) r),regions[2])){
				children[2] = children[2].insertRoad(r, x-h, y-h, h);
				
			} 
			if (Inclusive2DIntersectionVerifier.intersects(((Line2D) r),regions[3])){
				children[3] = children[3].insertRoad(r, x + (h), y - (h), h);
				
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
			//TODO: change for inclusion in PM tree
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
			for(int i = 0; i < 4; i++){
				if(children[i].contains(c) == true){
					return true;
				}
			}
			return false;
		}
		
		/*public boolean containsRoad(Road r){
			for(int i = 0; i < 4; i++){
				if(children[i].containsRoad(r) == true){
					return true;
				}
			}
			return false;
		}*/
	}
	
	/*Black NODE*/
	public class BlackNode implements Node {
		City cty = null;
		int x;
		int h;
		int y;
		int card = 0;
		ArrayList<Road> roads = new ArrayList<Road>();
		

		public BlackNode(City c, int xC, int yC, int height){
			cty = c;
			x = xC;
			y = yC;
			h = height;
			card++;
		}
		
		public BlackNode(Road r, int xC, int yC, int height){
			roads.add(r);
			x = xC;
			y = yC;
			h = height;
			card++;
		}
		
		public Node insert(City c, int xc, int yc, int height) {
			  // System.out.println("City: " + c.getName() + " Gray Node x: " + g.x + " y: " +g.y+" height: " + g.hi);
			   if(cty != null){
				   GrayNode g = new GrayNode(this.x, this.y, this.h);
				   g = (GrayNode) g.insert(cty, this.x, this.y, this.h);
				   g = (GrayNode) g.insert(c, this.x, this.y, this.h);
				   
				   for(Road r : roads){
					   g.insertRoad(r, this.x, this.y, this.h);
				   }
				   return g;
			   } else{
				   cty = c;
				   card++;
			   }
			   return this;
		}
		
		public BlackNode insertRoad(Road r, int xc, int yc, int height) {
			roads.add(r);
			card++;
			return this;
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
			 b.setAttribute("cardinality", Integer.toString(card));
			 Element c = null;
			 if(cty != null){
				 if(cty.iso == true){
						c = results.createElement("isolatedCity");
					} else {
						c = results.createElement("city");
					}
					c.setAttribute("color", cty.getColor());
					c.setAttribute("name", cty.getName());
					c.setAttribute("radius", cty.getRadiusString());
					c.setAttribute("x", cty.getXString());
					c.setAttribute("y", cty.getYString());
					b.appendChild(c);
			 }
			 Collections.sort(roads, new Comparator<Road>(){
			     public int compare(Road r1, Road r2){
			    	 int comp = r1.getStart().compareTo(r2.getStart());
				       if(comp == 0 ){
				    	   return -(r1.getEnd().compareTo(r2.getEnd()));
				       } else {
				    	   return -(comp);
				       }
			     }
			});
			for(Road r : roads){
				Element st = results.createElement("road");
					st.setAttribute("start",r.getStart());
					st.setAttribute("end", r.getEnd());
				
				b.appendChild(st);
			}
			return b;
		}
		
		public boolean contains(City c) {
			if(c.equals(cty)) return true;
			else return false;
		}
		/*public boolean containsRoad(Road r) {
			for(Road ro : roads){
				if(ro.getEnd().compareTo(r.getEnd()) == 0 && ro.getStart().compareTo(r.getStart()) == 0){
					return true;
				} else if (ro.getEnd().compareTo(r.getStart()) == 0 && ro.getStart().compareTo(r.getEnd()) == 0){
					return true;
				}
			}
			return false;
		}*/
		public City getCity(){
			return cty;
		}

	}
}
