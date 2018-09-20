package cmsc420.meeshquest.part3;

import java.util.ArrayList;



import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cmsc420.xml.XmlUtility;
import cmsc420.geom.Shape2DDistanceCalculator;
import cmsc420.sortedmap.AVLCommands;
import cmsc420.sortedmap.AvlGTree;

import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

//import Command.QuadrantDistance;

public class CommandParser {
	
	TreeMap<String, City> nameTree = new TreeMap<String, City>(new CityNameComparator());
	TreeMap<City, String> coorTree = new TreeMap<City, String>(new CoorCompare());
	Document results = null;
	Element rootElement;
	PRQuadtree spatial = null;
	String order = null;
	private AVLCommands avlcommands = new AVLCommands();
	ArrayList<City> isolatedCities = new ArrayList<City>();
	AvlGTree<City, String> avltree = null;
	TreeMap<String,TreeSet<Road>> adjacencyList = new TreeMap<String,TreeSet<Road>>();
	TreeSet<Road> roads = new TreeSet<Road>(new Comparator<Road>(){
	     public int compare(Road ro, Road r){
	    	 if(ro.getEnd().compareTo(r.getEnd()) == 0 && ro.getStart().compareTo(r.getStart()) == 0){
					return 0;
			} else if (ro.getEnd().compareTo(r.getStart()) == 0 && ro.getStart().compareTo(r.getEnd()) == 0){
					return 0;
			} else{
				int compStart = ro.getStart().compareTo(r.getStart());
				int compEnd = -(ro.getEnd().compareTo(r.getEnd()));
			       if(compStart == 0 ){
			    	   return compEnd;
			       } else {
			    	   return -(compStart);
			       }
			}   
	     }
	});
	
	public CommandParser () {
		try {
	    	System.setIn(new FileInputStream("testing.xml"));
	    	Document doc = XmlUtility.validateNoNamespace(System.in);
	    	results = XmlUtility.getDocumentBuilder().newDocument();
	    	rootElement = results.createElement("results");
			results.appendChild(rootElement);
			Element commandNode = doc.getDocumentElement();
			avltree = new AvlGTree<City, String>(new CityComp(), Integer.parseInt(commandNode.getAttribute("g")));
	    	spatial = new PRQuadtree(commandNode.getAttribute("spatialHeight"), commandNode.getAttribute("spatialWidth"));
	    	order = commandNode.getAttribute("pmOrder");
	    	final NodeList nl = commandNode.getChildNodes();
	    	for (int i = 0; i < nl.getLength(); i++) {
	    		if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
	    			commandNode = (Element) nl.item(i);
	    			String command = commandNode.getTagName();
	    			
	    			if (command.equals("createCity")) {
	    				createCity(command, commandNode);
	    			}else if (command.equals("listCities")){
	    				listCities(command, commandNode);
	    			} else if (command.equals("clearAll")){
	    				clearAll(commandNode);
	    			} else if(command.equals("deleteCity")){
	    				deleteCity(command, commandNode);
	    			} else if(command.equals("mapCity")){
	    				mapCity(command, commandNode);
	    			}else if(command.equals("unmapCity")){
	    				unmapCity(command, commandNode);
	    			}else if(command.equals("printPMQuadtree")){
	    				printPRQuadtree(command, commandNode);
	    			} else if (command.equals("saveMap")) {
	    				saveMap(commandNode);
	    			}else if (command.equals("rangeCities")) {
	    				rangeCities(commandNode);
	    			}else if(command.equals("nearestCity")){
	    				nearestCity(commandNode);
	    			}else if(command.equals("mapRoad")){
	    				mapRoad(commandNode);
	    			}else if (command.equals("nearestIsolatedCity")) {
	    				nearestIsolatedCity(commandNode);
	    			}else if (command.equals("rangeRoads")) {
	    				rangeRoads(commandNode);
	    			}else if(command.equals("nearestRoad")){
	    				nearestRoad(commandNode);
	    			}else if(command.equals("nearestCityToRoad")){
	    				nearestCityToRoad(commandNode);
	    			}else if(command.equals("printAvlTree")){
	    				rootElement.appendChild(avlcommands.printAVLTree(results, avltree, commandNode.getAttribute("id")));
	    			}else if(command.equals("shortestPath")){
	    				shortestPath(commandNode, commandNode.getAttribute("start"), commandNode.getAttribute("end"));
	    			}
	    		}
	    	}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			try {
				results = XmlUtility.getDocumentBuilder().newDocument();
				rootElement = results.createElement("fatalError");
				results.appendChild(rootElement);
			} catch (ParserConfigurationException e1) {
				System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
				//System.out.println("<fatalError/>");
			}    	
		}
	}
	
	private void nearestCityToRoad(Element commandNode) {
		String id = commandNode.getAttribute("id");
		double minDist = Integer.MAX_VALUE;
		String err = null;
		String start = commandNode.getAttribute("start");
		String end = commandNode.getAttribute("end");
		City st = nameTree.get(start);
		City ed = nameTree.get(end);
		Road r = new Road(st,ed);
		
		City cty = null;
		if(roads.contains(r) == true){
			for(Object city: nameTree.values().toArray()){
				if(!(((City) city).getName().compareTo(end) == 0) && !(((City) city).getName().compareTo(start) == 0)){
					if(spatial.contains((City) city)){
						double dis = r.ptSegDist(((City) city).getX(), ((City) city).getY());
						if(dis < minDist){
							cty = (City) city;
							minDist = dis;
						} else if (dis == minDist){
							if(cty.getName().compareTo(((City) city).getName()) < 0)
								cty = (City) city;
						}
					}
				
				}
				
			}
			if(cty == null){
				err = "noOtherCitiesMapped";
			}
		} else {err = "roadIsNotMapped"; }
		
		rootElement.appendChild(nearestCityToRoadXML(err, start, end, cty, id));
	}

	private Node nearestCityToRoadXML(String err, String start, String end, City city, String id) {
		Element s;
		if (err != null) {
			s = results.createElement("error");
			s.setAttribute("type", err);
		} else {s = results.createElement("success"); }
		Element c = results.createElement("command");
		c.setAttribute("name", "nearestCityToRoad");
		if(!id.isEmpty()){
			c.setAttribute("id", id);
		}
		s.appendChild(c);
		s.appendChild(c);
		Element p = results.createElement("parameters");
		Element n = results.createElement("start");
				n.setAttribute("value",start);
		Element xc = results.createElement("end");
				xc.setAttribute("value",end);
		
					
		p.appendChild(n);
		p.appendChild(xc);
	
		s.appendChild(p);
		if(err == null){
			Element o = results.createElement("output");
			Element cty = null;
				cty = results.createElement("city");
				cty.setAttribute("name", city.getName());
				cty.setAttribute("x", city.getXString());
				cty.setAttribute("y", city.getYString());
				cty.setAttribute("color", city.getColor());
				cty.setAttribute("radius", city.getRadiusString());
			
			o.appendChild(cty);
			
			s.appendChild(o);
		}
		
		return s;
	}

	private void mapRoad(Element commandNode) {
		String start = commandNode.getAttribute("start");
		String end = commandNode.getAttribute("end");
		String id = commandNode.getAttribute("id");
		City s = nameTree.get(start);
		City e = nameTree.get(end);
		String errorType = null;
		Road r = null;
		
		if(s == null){
			errorType = "startPointDoesNotExist";
		} else if(e == null){
			errorType = "endPointDoesNotExist";
		}
		else if(start.equals(end)) {
			errorType = "startEqualsEnd";
		} else if(s.iso == true || e.iso == true){
			errorType = "startOrEndIsIsolated";
		}else{
			r = new Road(s,e);
			if(roads.contains(r) == true){
				errorType = "roadAlreadyMapped";
			} else{
				errorType = spatial.insertRoad(r, s, e);
				if(errorType == null){
					roads.add(r);
					TreeSet<Road> adj = adjacencyList.get(start);
					if(adj == null){
						adj = new TreeSet<Road>(new Comparator<Road>(){
						     public int compare(Road r1, Road r2){
						 		if(r1.getWeight() < r2.getWeight()){
									return -1;
								} else if (r1.getWeight() > r2.getWeight()){
									return 1;
								} else {
									int comp = r1.getStart().compareTo(r2.getStart());
								    if(comp == 0 ){
								    	   return -(r1.getEnd().compareTo(r2.getEnd()));
								    } else {
								    	   return -(comp);
								    }
								}
							     }
							});
					} 
					adj.add(r);
					adjacencyList.put(start, adj);
					
					Road r2 = new Road(e,s);
					TreeSet<Road> adj2 = adjacencyList.get(end);
					if(adj2 == null){
						adj2 = new TreeSet<Road>(new Comparator<Road>(){
						     public int compare(Road r1, Road r2){
						 		if(r1.getWeight() < r2.getWeight()){
									return -1;
								} else if (r1.getWeight() > r2.getWeight()){
									return 1;
								} else {
									int comp = r1.getStart().compareTo(r2.getStart());
								    if(comp == 0 ){
								    	   return -(r1.getEnd().compareTo(r2.getEnd()));
								    } else {
								    	   return -(comp);
								    }
								}
							     }
							});
					}
					adj2.add(r2);
					adjacencyList.put(end, adj2);
				}
			}
			
		}
		rootElement.appendChild(mapRoadXML(r,start,end,errorType, id));
	}
	
	
	private Element mapRoadXML(Road r, String start, String end, String err, String id) {
		Element s;
		if (err != null) {
			s = results.createElement("error");
			s.setAttribute("type", err);
		} else {s = results.createElement("success"); }
		Element c = results.createElement("command");
		c.setAttribute("name", "mapRoad");
		if(!id.isEmpty()){
			c.setAttribute("id", id);
		}
		s.appendChild(c);
		Element p = results.createElement("parameters");
		Element n = results.createElement("start");
				n.setAttribute("value",start);
		Element xc = results.createElement("end");
				xc.setAttribute("value",end);
		
					
		p.appendChild(n);
		p.appendChild(xc);
	
		s.appendChild(p);
		if(err == null){
			Element o = results.createElement("output");
			Element st = results.createElement("roadCreated");
			st.setAttribute("start",start);
			st.setAttribute("end",end);
			o.appendChild(st);
			
			s.appendChild(o);
		}
		
		return s;
	}

	public void rangeCities(Element commandNode){
		Point2D p = new Point2D.Double(Integer.parseInt(commandNode.getAttribute("x")), Integer.parseInt(commandNode.getAttribute("y")));
		Comparator<City> c = new CityComp();
		double radius = Double.parseDouble(commandNode.getAttribute("radius"));
		TreeSet<City> ctys = new TreeSet<City>(c);
		String err = null;
		for(Object cty: nameTree.values().toArray()){
			if(spatial.contains((City) cty)){
				double dis = p.distance(((City) cty).getX(),((City) cty).getY());
				if(dis <= radius){
					ctys.add((City) cty);
				}
			}
		}
		if (ctys.isEmpty()){
			err = "noCitiesExistInRange";
		}

		rootElement.appendChild(rangeCitiesXML(err, commandNode.getAttribute("x"),commandNode.getAttribute("y"), commandNode.getAttribute("radius"), ctys, commandNode.getAttribute("saveMap"), commandNode.getAttribute("id")));
	}
	
	public void rangeRoads(Element commandNode){
		int y = Integer.parseInt(commandNode.getAttribute("y"));
		int x = Integer.parseInt(commandNode.getAttribute("x"));
		int r = Integer.parseInt(commandNode.getAttribute("radius"));
		String id = commandNode.getAttribute("id");
		TreeSet<Road> rds = new TreeSet<Road>(new Comparator<Road>(){
		     public int compare(Road r1, Road r2){
		    	 int comp = r1.getStart().compareTo(r2.getStart());
			       if(comp == 0 ){
			    	   return -(r1.getEnd().compareTo(r2.getEnd()));
			       } else {
			    	   return -(comp);
			       }
			     }
			});
		String err = null;

		for(Road road: roads){
			if(road.ptSegDist(x,y) <= r){
				rds.add(road);
			}
		}
		if (rds.isEmpty()){
			err = "noRoadsExistInRange";
		}

		rootElement.appendChild(rangeRoadsXML(id, err, commandNode.getAttribute("x"),commandNode.getAttribute("y"), commandNode.getAttribute("radius"), rds, commandNode.getAttribute("saveMap")));
	}
	
	private Node rangeRoadsXML(String id, String err, String xC, String yC, String rad, TreeSet<Road> roads2, String save) {
		Element s;
		if (err != null) {
			s = results.createElement("error");
			s.setAttribute("type", err);
		} else {s = results.createElement("success"); }
		Element com = results.createElement("command");
		if(!id.isEmpty()){
			com.setAttribute("id", id);
		}
		com.setAttribute("name", "rangeRoads");
		s.appendChild(com);
		Element p = results.createElement("parameters");
		Element x = results.createElement("x");
			x.setAttribute("value", xC);
		Element y = results.createElement("y");
			y.setAttribute("value", yC);
		
		Element r = results.createElement("radius");
			r.setAttribute("value", rad);
		
		p.appendChild(x);
		p.appendChild(y);
		p.appendChild(r);
		if(!save.isEmpty()){
			Element ave = results.createElement("saveMap");
			ave.setAttribute("value", save);
			p.appendChild(ave);
		}
		s.appendChild(p);
		if(err == null){
			Element o = results.createElement("output");
			Element l = results.createElement("roadList");
			for (Road c : roads2){
				Element cty = results.createElement("road");
				cty.setAttribute("end", c.getEnd());
				cty.setAttribute("start", c.getStart());
				l.appendChild(cty);
			}
			o.appendChild(l);
			s.appendChild(o);
		}
		return s;
	}

	
	public void nearestCity(Element commandNode){
		Point2D.Float p = new Point2D.Float(Integer.parseInt(commandNode.getAttribute("x")), Integer.parseInt(commandNode.getAttribute("y")));
		double minDist = Integer.MAX_VALUE;
		String err = null;
		City cty = null;
		
		if (spatial.root.isNode().equals("white")){
			err = "mapIsEmpty";
		}else {
			cty = nearestCityHelper2(spatial.root, p, false);
//			for(Object city: nameTree.values().toArray()){
//				if(((City) city).iso == false){
//					if(spatial.contains((City) city)){
//						double dis = p.distance(((City) city).getX(),((City) city).getY());
//						if(dis < minDist){
//							cty = (City) city;
//							minDist = dis;
//						} else if (dis == minDist){
//							if(cty.getName().compareTo(((City) city).getName()) < 0)
//								cty = (City) city;
//						}
//					}
//				}
//				
//			}
			
			
		}
		
		rootElement.appendChild(nearestCityXML(commandNode.getAttribute("id"), false, err, commandNode.getAttribute("x"),commandNode.getAttribute("y"), cty, null, false));
	}
	
	//FROM THE part2 conotical 
	private City nearestCityHelper2(PRQuadtree.Node root, Point2D.Float point, Boolean iso) {
		PriorityQueue<QuadrantDistance> q = new PriorityQueue<QuadrantDistance>();
		PRQuadtree.Node currNode = root;
		
		while (currNode.isNode() != "black") {
			PRQuadtree.GrayNode g = (PRQuadtree.GrayNode) currNode;
			for (int i = 0; i < 4; i++) {
				PRQuadtree.Node kid = g.children[i];
				if (kid.isNode() == "gray" || (kid.isNode() == "black" && kid.getCity() != null && kid.getCity().iso == iso)) {
					q.add(new QuadrantDistance(kid, point));
				}
			}
			currNode = q.remove().quadtreeNode;
		}

		return ((PRQuadtree.BlackNode) currNode).getCity();
	}

	class QuadrantDistance implements Comparable<QuadrantDistance> {
		public PRQuadtree.Node quadtreeNode;
		private double distance;

		public QuadrantDistance(PRQuadtree.Node node, Point2D.Float pt) {
			quadtreeNode = node;
			if (node.isNode() == "gray") {
				PRQuadtree.GrayNode gray = (PRQuadtree.GrayNode) node;
				distance = Shape2DDistanceCalculator.distance(pt, 
						new Rectangle2D.Float(gray.x, gray.y, gray.hi, gray.hi));
			} else if (node.isNode() == "black") {
				PRQuadtree.BlackNode leaf = (PRQuadtree.BlackNode) node;
				distance = pt.distance(leaf.getCity().getX(), leaf.getCity().getY());
			} else {
				throw new IllegalArgumentException("Only leaf or internal node can be passed in");
			}
		}

		public int compareTo(QuadrantDistance qd) {
			if (distance < qd.distance) {
				return -1;
			} else if (distance > qd.distance) {
				return 1;
			} else {
				if (quadtreeNode.isNode() != qd.quadtreeNode.isNode()) {
					if (quadtreeNode.isNode() == "gray") {
						return -1;
					} else {
						return 1;
					}
				} else if (quadtreeNode.isNode() == "black") {
					// both are leaves
					return ((PRQuadtree.BlackNode) qd.quadtreeNode).getCity().getName().compareTo(
							((PRQuadtree.BlackNode) quadtreeNode).getCity().getName());
				} else {
					// both are internals
					return 0;
				}
			}
		}
	}
	
	
	
	
	
	
	
	public void nearestRoad(Element commandNode){
		String id = commandNode.getAttribute("id");
		double minDist = Integer.MAX_VALUE;
		String err = null;
		Road rd = null;
		if (spatial.root.isNode().equals("white")){
			err = "mapIsEmpty";
		}else { 
			for(Road road: roads){
				double dis = road.ptSegDist(Double.parseDouble(commandNode.getAttribute("x")), Double.parseDouble(commandNode.getAttribute("y")));
				if(dis < minDist){
					rd = road;
					minDist = dis;
				} else if (dis == minDist){
						int comp = road.getStart().compareTo(rd.getStart());
						if(comp > 0)
							rd = road;
						else if (comp == 0){
							if (road.getEnd().compareTo(rd.getEnd()) > 0 )
								rd = road;
						}
							
					}
			}
		}
		
		rootElement.appendChild(nearestCityXML(id,false,err, commandNode.getAttribute("x"),commandNode.getAttribute("y"),null,rd,true));
	}
	
	public void nearestIsolatedCity(Element commandNode){
		Point2D.Float p = new Point2D.Float(Integer.parseInt(commandNode.getAttribute("x")), Integer.parseInt(commandNode.getAttribute("y")));
		double minDist = Integer.MAX_VALUE;
		String err = null;
		City cty = null;
		
		if (spatial.root.isNode().equals("white")){
			err = "mapIsEmpty";
		}else {
			cty = nearestCityHelper2(spatial.root, p, true);
//			for(City city: isolatedCities){
//					if(spatial.contains(city)){
//						double dis = p.distance(city.getX(),city.getY());
//						if(dis < minDist){
//							cty = city;
//							minDist = dis;
//						} else if (dis == minDist){
//							if(cty.getName().compareTo(city.getName()) < 0)
//								cty = city;
//						}
//					}
//			}
		}
		
		rootElement.appendChild(nearestCityXML(commandNode.getAttribute("id"), true, err, commandNode.getAttribute("x"),commandNode.getAttribute("y"), cty, null, false));
	}

	private void unmapCity(String command, Element commandNode) {
		String errorType = null;
		String name = commandNode.getAttribute("name");
		City c = nameTree.get(name);
		
		if(c == null){
			errorType = "nameNotInDictionary";
		} else if (!spatial.contains(c)){
			errorType = "cityNotMapped";
		}else {
			spatial.remove(c);
		}
		rootElement.appendChild(mapCityXML(errorType, name, "unmapCity", commandNode.getAttribute("id")));
	}

	private void saveMap(Element commandNode){
		String name = commandNode.getAttribute("name");
		Element s = results.createElement("success");
		String id = commandNode.getAttribute("id");
		Element c = results.createElement("command");
		if(!id.isEmpty()){
			c.setAttribute("id", id);
		}
		c.setAttribute("name", "saveMap");
		Element p = results.createElement("parameters");
		Element n = results.createElement("name");
		n.setAttribute("value", name);
		p.appendChild(n);
		s.appendChild(c);
		s.appendChild(p);
		Element o = results.createElement("output");
		s.appendChild(o);
		rootElement.appendChild(s);
	}
	
	private void printPRQuadtree(String command, Element commandNode) {
		String err = null;
		String id = commandNode.getAttribute("id");
		if(spatial.root.isNode().equals("white")){
			err = "mapIsEmpty";
		}
		Element s; 
		if (err != null) {
			s = results.createElement("error");
			s.setAttribute("type", err);
		} else {s = results.createElement("success"); }
		Element c = results.createElement("command");
		c.setAttribute("name", "printPMQuadtree");
		if(!id.isEmpty()){
			c.setAttribute("id", id);
		}
		Element p = results.createElement("parameters");
		s.appendChild(c);
		s.appendChild(p);
		
		if(err == null){
			Element o = results.createElement("output");
			Element q = results.createElement("quadtree");
			q.setAttribute("order", order);
			q.appendChild(spatial.printPRQuadtree(results));
			o.appendChild(q);
			s.appendChild(o);
		}
		
		rootElement.appendChild(s);
	}

	private void mapCity(String command, Element commandNode) {
		String errorType = null;
		String name = commandNode.getAttribute("name");
		
		City c = nameTree.get(name);
		
		if(c == null){
			errorType = "nameNotInDictionary";
		} else {
			errorType = spatial.insert(c);
			if(errorType == null){
				c.iso = true;
				isolatedCities.add(c);
			}
		}

		rootElement.appendChild(mapCityXML(errorType, name, "mapCity", commandNode.getAttribute("id")));
	}

	public void deleteCity(String command, Element commandNode) {
		String name = commandNode.getAttribute("name");
		String errorType = null;  
		City r = null;
		//City c = nameTree.get(name);
		if(nameTree.containsKey(name)){ //if city exists
			//TODO: Note that if the city has been mapped, 
			//then it must be removed from the PR quadtree first (spatial.remove(c)?), 
			//and then deleted
			r = nameTree.remove(name);
			coorTree.remove(r);
		}else{//if city doesnt exits, error
			errorType = "cityDoesNotExist";
		}
		rootElement.appendChild(deleteCityXML(errorType, r, name, commandNode.getAttribute("id")));
	}
	
	public void createCity(String command, Element commandNode){
		boolean error = false;
		String name = commandNode.getAttribute("name");
		String color = commandNode.getAttribute("color");
		String x = null;
		String y = null;
		String radius = null;
		String errorType = null;
		
		x = commandNode.getAttribute("x");
		y = commandNode.getAttribute("y");
		
		String remoteX = commandNode.getAttribute("remoteX");
		String remoteY = commandNode.getAttribute("remoteY");
		radius = commandNode.getAttribute("radius");
		
		//create city
		City city = new City(name, x, y, radius, color);
		
		//coordinateMap.contanins? no? 
		if(!coorTree.containsKey(city)){
			//nameMap.contains? no?
			if(!nameTree.containsKey(name)){
				//add to both maps
				nameTree.put(name, city);
				coorTree.put(city,name);
				avltree.add(city, city.getName());
			} else {error = true; errorType = "duplicateCityName";  }
			
		} else {error = true; errorType = "duplicateCityCoordinates";  }
		
		rootElement.appendChild(createCityXML(city, error, errorType, commandNode.getAttribute("id")));
	}
	
	private void listCities(String command, Element commandNode) {
		String sort = commandNode.getAttribute("sortBy");
		rootElement.appendChild(creatListXML(sort,commandNode.getAttribute("id")));
	}
	
	private void clearAll(Element commandNode){
		nameTree.clear();
		coorTree.clear();
		avltree.clear();
		//TODO: clear adjacency list
		String id = commandNode.getAttribute("id");
		spatial.clear();
		Element s = results.createElement("success");
		Element c = results.createElement("command");
		c.setAttribute("name", "clearAll");
		if(!(id.isEmpty())){
			c.setAttribute("id", id);
		}
		s.appendChild(c);
		Element p = results.createElement("parameters");
		Element o = results.createElement("output");
		s.appendChild(p);
		s.appendChild(o);
		rootElement.appendChild(s);
		
	}
	
	public Element createCityXML(City cty, boolean err, String type, String id){
		Element s;
		if (err) {
			s = results.createElement("error");
			s.setAttribute("type", type);
		} else {s = results.createElement("success"); }
		Element c = results.createElement("command");
		c.setAttribute("name", "createCity");
		if(!id.isEmpty()){
			c.setAttribute("id", id);
		}
		s.appendChild(c);
		Element p = results.createElement("parameters");
		Element n = results.createElement("name");
				n.setAttribute("value",cty.getName());
		Element xc = results.createElement("x");
				xc.setAttribute("value",cty.getXString());
		Element yc = results.createElement("y");
				yc.setAttribute("value",cty.getYString());
		Element rad = results.createElement("radius");
				rad.setAttribute("value",cty.getRadiusString());
		Element col = results.createElement("color");
				col.setAttribute("value",cty.getColor());
					
		p.appendChild(n);
		p.appendChild(xc);
		p.appendChild(yc);
		p.appendChild(rad);
		p.appendChild(col);
		s.appendChild(p);
		if(!err){
			Element o = results.createElement("output");
			s.appendChild(o);
		}
		
		return s;
	}

	private Element creatListXML(String sortBy, String id) {
		Object[] sorted = null;
		Element s;
		if (sortBy.equals("coordinate")){
			//sort by coordinate
			sorted = coorTree.keySet().toArray();

		} else {
			//sort by name
			sorted = nameTree.values().toArray();
		}
		if(sorted.length <= 0) {
			s = results.createElement("error");
			s.setAttribute("type", "noCitiesToList");
		} else {
			s = results.createElement("success");
		}
		
		Element com = results.createElement("command");
		com.setAttribute("name", "listCities");
		if(!id.isEmpty()){
			com.setAttribute("id", id);
		}
		s.appendChild(com);
		Element p = results.createElement("parameters");
		Element sortby = results.createElement("sortBy");
			sortby.setAttribute("value", sortBy);
		p.appendChild(sortby);
		s.appendChild(p);
		
		Element l = results.createElement("cityList");
		if(sorted.length > 0){
			Element o = results.createElement("output");
			for (Object c : sorted){
				Element cty = results.createElement("city");
				cty.setAttribute("color", ((City) c).getColor());
				cty.setAttribute("name", ((City) c).getName());
				cty.setAttribute("radius", ((City) c).getRadiusString());
				cty.setAttribute("x", ((City) c).getXString());
				cty.setAttribute("y", ((City) c).getYString());
		
				l.appendChild(cty);
			}
			o.appendChild(l);
			s.appendChild(o);
		}
		
		
		return s;
	}
	
	public Element deleteCityXML(String errorType, City cty, String cityName, String id){
		Element s;
		String name;
		if(errorType != null) {
			s = results.createElement("error");
			s.setAttribute("type", errorType);
			name = cityName;
		} else {s = results.createElement("success"); 
		name = cty.getName();}
		Element c = results.createElement("command");
		c.setAttribute("name", "deleteCity");
		if(!id.isEmpty()){
			c.setAttribute("id", id);
		}
		s.appendChild(c);
		Element p = results.createElement("parameters");
		Element n = results.createElement("name");
				n.setAttribute("value",name);
		
		p.appendChild(n);
		s.appendChild(p);
		if(errorType == null){ 
			Element o = results.createElement("output");
			if(spatial.contains(cty) == true){ 
				spatial.remove(cty);
			 Element unmapped = results.createElement("cityUnmapped");
			 unmapped.setAttribute("name", name);
			 unmapped.setAttribute("x", cty.getXString());
			unmapped.setAttribute("y", cty.getYString());
			 unmapped.setAttribute("color", cty.getColor());
			 unmapped.setAttribute("radius", cty.getRadiusString());
			 o.appendChild(unmapped);
			}
			
			s.appendChild(o);
		}
		return s;
	}
	
	public Element mapCityXML(String error, String name, String comm, String id) {
		Element s;
		if (error != null) {
			s = results.createElement("error");
			s.setAttribute("type", error);
		} else {s = results.createElement("success"); }
		Element c = results.createElement("command");
		c.setAttribute("name", comm);
		if(!id.isEmpty()){
			c.setAttribute("id", id);
		}
		s.appendChild(c);
		Element p = results.createElement("parameters");
		Element n = results.createElement("name");
				n.setAttribute("value", name);
		p.appendChild(n);
		s.appendChild(p);
		if(error == null){
			Element o = results.createElement("output");
			s.appendChild(o);
		}
	    return s;
	}
	
	private Node nearestCityXML(String id, Boolean iso, String err, String xC, String yC, City city, Road rd, Boolean rod) {
		Element s;
		
		if (city == null && rod == false){
			err = "cityNotFound";
		} else if (rd == null && rod == true){
				err = "roadNotFound";
		}
		if (err != null) {
			s = results.createElement("error");
			s.setAttribute("type", err);
		} else {s = results.createElement("success"); }
		Element com = results.createElement("command");
		if(!id.isEmpty()){
			com.setAttribute("id", id);
		}
		if(iso == true){
			com.setAttribute("name", "nearestIsolatedCity");
		} else{
			if(rod == true){
				com.setAttribute("name", "nearestRoad");
			} else {
				com.setAttribute("name", "nearestCity");
			}
			
		}
		
		s.appendChild(com);
		Element p = results.createElement("parameters");
		Element x = results.createElement("x");
			x.setAttribute("value", xC);
		Element y = results.createElement("y");
			y.setAttribute("value", yC);
			p.appendChild(x);
			p.appendChild(y);
			s.appendChild(p);
			if(err == null){
				Element o = results.createElement("output");
				
				Element cty = null;
				if(iso == true){
					cty = results.createElement("isolatedCity");
					cty.setAttribute("name", city.getName());
					cty.setAttribute("x", city.getXString());
					cty.setAttribute("y", city.getYString());
			
					cty.setAttribute("color", city.getColor());
					cty.setAttribute("radius", city.getRadiusString());
				} else if (rod == false){
					cty = results.createElement("city");
					cty.setAttribute("name", city.getName());
					cty.setAttribute("x", city.getXString());
					cty.setAttribute("y", city.getYString());
					
					cty.setAttribute("color", city.getColor());
					cty.setAttribute("radius", city.getRadiusString());
				} else {
					cty = results.createElement("road");
					cty.setAttribute("start", rd.getStart());
					cty.setAttribute("end", rd.getEnd());
				}
				
				o.appendChild(cty);
				s.appendChild(o);
			} return s;
	}
	

	private Node rangeCitiesXML(String err, String xC, String yC, String rad, TreeSet<City> ctys, String save, String id) {
		Element s;
		if (err != null) {
			s = results.createElement("error");
			s.setAttribute("type", err);
		} else {s = results.createElement("success"); }
		Element com = results.createElement("command");
		com.setAttribute("name", "rangeCities");
		if(!id.isEmpty()){
			com.setAttribute("id", id);
		}
		s.appendChild(com);
		Element p = results.createElement("parameters");
		Element x = results.createElement("x");
			x.setAttribute("value", xC);
		Element y = results.createElement("y");
			y.setAttribute("value", yC);
		
		Element r = results.createElement("radius");
			r.setAttribute("value", rad);
		
		p.appendChild(x);
		p.appendChild(y);
		p.appendChild(r);
		if(!save.isEmpty()){
			Element ave = results.createElement("saveMap");
			ave.setAttribute("value", save);
			p.appendChild(ave);
		}
		s.appendChild(p);
		if(err == null){
			Element o = results.createElement("output");
			Element l = results.createElement("cityList");
			for (Object c : ctys){
				Element cty = results.createElement("city");
				cty.setAttribute("color", ((City) c).getColor());
				cty.setAttribute("name", ((City) c).getName());
				cty.setAttribute("radius", ((City) c).getRadiusString());
				cty.setAttribute("x", ((City) c).getXString());
				cty.setAttribute("y", ((City) c).getYString());
				l.appendChild(cty);
			}
			o.appendChild(l);
			s.appendChild(o);
		}
		return s;
	}

	public Document getResults(){
		return results;
	}
	public TreeMap<String, City> getNameTree() {
		return  nameTree;
	}
	public TreeMap<City, String> getCoorTree() {
		return  coorTree;
	}
	public PRQuadtree getPR(){
		return spatial;
	}
	
	/*Beginning code from the spec*/
	public String getAngle(Point2D.Float p1p, Point2D.Float p2p, Point2D.Float p3p){
			Point2D.Float p1 = p3p;
		    Point2D.Float p2 = p2p;
		    Point2D.Float p3 = p1p;
		    
		    Arc2D.Double arc = new Arc2D.Double();
		    
		    arc.setArcByTangent(p1,p2,p3,1);
		    double ang = (arc.getAngleExtent());
		    
		    if(ang > 45) 
		    	return "left";
		    else if(ang <= -45)
		    	return "right";
		    else
		    	return "straight";
	    
	}
	
	/*Parts taken and edited from old 132 project*/
	public void shortestPath(Element commandNode, String start, String end){
		ArrayList<String> path = new ArrayList<String>();
		String err= null;
		City startCity = nameTree.get(start);
		City endCity = nameTree.get(end);
		try{
			if(spatial.contains(startCity) == false){
				err = "nonExistentStart";
			} 
		} catch(NullPointerException e){ err = "nonExistentStart"; }
		
		try{
			if(err == null && spatial.contains(endCity) == false){
				err = "nonExistentEnd";
			}
		} catch(NullPointerException e){ err = "nonExistentEnd"; }
		
		if(!start.equals(end) && err == null){
			HashMap<String, Double> costs = new HashMap<String,Double>();
			HashMap<String, String> pred = new HashMap<String,String>();
			
			costs.put(start, 0.0);
			PriorityQueue<String> q = new PriorityQueue<String>(new Comparator<String>(){
				@Override
				public int compare(String arg0, String arg1) {
					return (costs.get(arg0).compareTo(costs.get(arg1)));
				}
			});
			
			for(String c: nameTree.keySet()){
				if(!c.equals(start))
				costs.put(c, Double.MAX_VALUE);
				pred.put(c, "None");
				q.add(c);
			}
			
			while(!q.isEmpty()){
				String u = q.poll();
				TreeSet<Road> tr = adjacencyList.get(u);
				
				if(tr != null){
					for(Road r: tr){
						double dist = costs.get(u) + r.getWeight();
						
						String v = r.endCity.getName();	

						if(dist < costs.get(v)){
							costs.put(v, dist);
							pred.put(v, u);
							q.remove(v);
							q.add(v);
						}
					
					}
				}
			
			}
			if(pred.get(end) == "None"){
				err = "noPathExists";
			} else if (err == null){
				/*Taken from old 132 project*/
				Stack<String> order = new Stack<String>();
				order.push(end);
				String cur = end;
				while (!pred.get(cur).equals("None")) {
					cur = pred.get(cur);
					order.push(cur);
				}
				//reverse order
				while (order.size() > 0) {
					path.add(order.pop());
				}	
			}
			try{
				rootElement.appendChild(shortestPathXML(commandNode, path, err, costs.get(end)));
			} catch (NullPointerException e){
				rootElement.appendChild(shortestPathXML(commandNode, path, err, 0.000));
			}
			
		} 
		else  
			rootElement.appendChild(shortestPathXML(commandNode, path, err, 0.000));
		
	}
	
	public Element shortestPathXML(Element commandNode, ArrayList<String> path, String err, double cost){
		Element s;
		if (err != null) {
			s = results.createElement("error");
			s.setAttribute("type", err);
		} else {s = results.createElement("success"); }
		Element com = results.createElement("command");
		com.setAttribute("name", "shortestPath");
		if(!commandNode.getAttribute("id").isEmpty()){
			com.setAttribute("id", commandNode.getAttribute("id"));
		}
		s.appendChild(com);
		Element p = results.createElement("parameters");
		Element x = results.createElement("start");
			x.setAttribute("value", commandNode.getAttribute("start"));
		Element y = results.createElement("end");
			y.setAttribute("value", commandNode.getAttribute("end"));
		p.appendChild(x);
		p.appendChild(y);
		if(!commandNode.getAttribute("saveMap").isEmpty()){
			Element ave = results.createElement("saveMap");
			ave.setAttribute("value", commandNode.getAttribute("saveMap"));
			p.appendChild(ave);
		}
		if(!commandNode.getAttribute("saveHTML").isEmpty()){
			Element ave = results.createElement("saveHTML");
			ave.setAttribute("value", commandNode.getAttribute("saveHTML"));
			p.appendChild(ave);
		}
		
		s.appendChild(p);
		if(err == null){
			Element o = results.createElement("output");
			Element pth = results.createElement("path");
			DecimalFormat df = new DecimalFormat("0.000");
			String c = df.format(cost);
			pth.setAttribute("length", c);
			if(path.size() == 0)
				pth.setAttribute("hops", "0");
			else 
				pth.setAttribute("hops", Integer.toString(path.size()-1));
			
			for(int i = 0; i <= path.size()-3; i++){
				String first = path.get(i);
				String second = path.get(i+1);
				String third = path.get(i+2);
				
				Element cty = results.createElement("road");
				cty.setAttribute("end", second);
				cty.setAttribute("start", first);
				pth.appendChild(cty);
				
				String dir = getAngle(nameTree.get(first), nameTree.get(second), nameTree.get(third));
				
				Element d = results.createElement(dir);
				pth.appendChild(d);
				if((i+2) == path.size()-1){
					Element cty2 = results.createElement("road");
					cty2.setAttribute("end", third);
					cty2.setAttribute("start", second);
					pth.appendChild(cty2);
				}
			}
			
			if(path.size() == 2){
				String first = path.get(0);
				String second = path.get(1);
				
				Element cty = results.createElement("road");
				cty.setAttribute("end", second);
				cty.setAttribute("start", first);
				pth.appendChild(cty);
			}
			
			
			o.appendChild(pth);
			s.appendChild(o);
		}
		
		
		
		
		return s;
	}
	
}