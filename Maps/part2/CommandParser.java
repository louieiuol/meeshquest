package cmsc420.meeshquest.part2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cmsc420.xml.XmlUtility;

import java.awt.Component;
import java.awt.List;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class CommandParser {
	
	TreeMap<String, City> nameTree = new TreeMap<String, City>(new CityNameComparator());
	TreeMap<City, String> coorTree = new TreeMap<City, String>(new CoorCompare());
	Document results = null;
	Element rootElement;
	PRQuadtree spatial = null;
	
	public CommandParser () {
		try {
	    	System.setIn(new FileInputStream("part1.quadtreeTest1range.input.xml"));
	    	Document doc = XmlUtility.validateNoNamespace(System.in);
	    	results = XmlUtility.getDocumentBuilder().newDocument();
	    	rootElement = results.createElement("results");
			results.appendChild(rootElement);
	
	    	Element commandNode = doc.getDocumentElement();
	    	spatial = new PRQuadtree(commandNode.getAttribute("spatialHeight"), commandNode.getAttribute("spatialWidth"));
	    	
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
	    				clearAll();
	    			} else if(command.equals("deleteCity")){
	    				deleteCity(command, commandNode);
	    			} else if(command.equals("mapCity")){
	    				mapCity(command, commandNode);
	    			}else if(command.equals("unmapCity")){
	    				unmapCity(command, commandNode);
	    			}else if(command.equals("printPRQuadtree")){
	    				printPRQuadtree(command, commandNode);
	    			} else if (command.equals("saveMap")) {
	    				saveMap(commandNode);
	    			}else if (command.equals("rangeCities")) {
	    				rangeCities(commandNode);
	    			}else if(command.equals("nearestCity")){
	    				nearestCity(commandNode);
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
	
	public void rangeCities(Element commandNode){
		Point2D p = new Point2D.Double(Integer.parseInt(commandNode.getAttribute("x")), Integer.parseInt(commandNode.getAttribute("y")));
		Comparator<City> c = new CityComp();
		ArrayList<City> ctys = new ArrayList<City>();
		String err = null;
		for(Object cty: nameTree.values().toArray()){
			if(spatial.contains((City) cty)){
				double dis = p.distance(((City) cty).getX(),((City) cty).getY());
				if(dis <= Double.parseDouble(commandNode.getAttribute("radius"))){
					ctys.add((City) cty);
				}
			}
		}
		if (ctys.isEmpty()){
			err = "noCitiesExistInRange";
		}else {
			ctys.sort(c);
		}
		
	
		rootElement.appendChild(rangeCitiesXML(err, commandNode.getAttribute("x"),commandNode.getAttribute("y"), commandNode.getAttribute("radius"), ctys, commandNode.getAttribute("saveMap")));
	}
	
	public void nearestCity(Element commandNode){
		Point2D p = new Point2D.Double(Integer.parseInt(commandNode.getAttribute("x")), Integer.parseInt(commandNode.getAttribute("y")));
		double minDist = Integer.MAX_VALUE;
		String err = null;
		City cty = null;
		
		if (spatial.root.isNode().equals("white")){
			err = "mapIsEmpty";
		}else {
			for(Object city: nameTree.values().toArray()){
				if(spatial.contains((City) city)){
					double dis = p.distance(((City) city).getX(),((City) city).getY());
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
		
		rootElement.appendChild(nearestCityXML(err, commandNode.getAttribute("x"),commandNode.getAttribute("y"), cty));
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
		rootElement.appendChild(mapCityXML(errorType, name, "unmapCity"));
	}

	private void saveMap(Element commandNode){
		String name = commandNode.getAttribute("name");
		Element s = results.createElement("success");
		Element c = results.createElement("command");
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
		if(spatial.root.isNode().equals("white")){
			err = "mapIsEmpty";
		}
		Element s; 
		if (err != null) {
			s = results.createElement("error");
			s.setAttribute("type", err);
		} else {s = results.createElement("success"); }
		Element c = results.createElement("command");
		c.setAttribute("name", "printPRQuadtree");
		Element p = results.createElement("parameters");
		s.appendChild(c);
		s.appendChild(p);
		
		if(err == null){
			Element o = results.createElement("output");
			Element q = results.createElement("quadtree");
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
		}

		rootElement.appendChild(mapCityXML(errorType, name, "mapCity"));
	}

	public void deleteCity(String command, Element commandNode) {
		String name = commandNode.getAttribute("name");
		String errorType = null;  
		City r = null;
		City c = nameTree.get(name);
		if(nameTree.containsKey(name)){ //if city exists
			//TODO: Note that if the city has been mapped, 
			//then it must be removed from the PR quadtree first (spatial.remove(c)?), 
			//and then deleted
			r = nameTree.remove(name);
			coorTree.remove(r);
		}else{//if city doesnt exits, error
			errorType = "cityDoesNotExist";
		}
		rootElement.appendChild(deleteCityXML(errorType, r, name));
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
			} else {error = true; errorType = "duplicateCityName";  }
			
		} else {error = true; errorType = "duplicateCityCoordinates";  }
		
		rootElement.appendChild(createCityXML(city, error, errorType));
	}
	
	private void listCities(String command, Element commandNode) {
		String sort = commandNode.getAttribute("sortBy");
		rootElement.appendChild(creatListXML(sort));
	}
	
	private void clearAll(){
		nameTree.clear();
		coorTree.clear();
		spatial.clear();
		Element s = results.createElement("success");
		Element c = results.createElement("command");
		c.setAttribute("name", "clearAll");
		s.appendChild(c);
		Element p = results.createElement("parameters");
		Element o = results.createElement("output");
		s.appendChild(p);
		s.appendChild(o);
		rootElement.appendChild(s);
		
	}
	
	public Element createCityXML(City cty, boolean err, String type){
		Element s;
		if (err) {
			s = results.createElement("error");
			s.setAttribute("type", type);
		} else {s = results.createElement("success"); }
		Element c = results.createElement("command");
		c.setAttribute("name", "createCity");
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

	private Element creatListXML(String sortBy) {
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
	
	public Element deleteCityXML(String errorType, City cty, String cityName){
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
			 unmapped.setAttribute("x",cty.getXString() );
			 unmapped.setAttribute("y", cty.getYString());
			 unmapped.setAttribute("color", cty.getColor());
			 unmapped.setAttribute("radius", cty.getRadiusString());
			 o.appendChild(unmapped);
			}
			
			s.appendChild(o);
		}
		return s;
	}
	
	public Element mapCityXML(String error, String name, String comm) {
		Element s;
		if (error != null) {
			s = results.createElement("error");
			s.setAttribute("type", error);
		} else {s = results.createElement("success"); }
		Element c = results.createElement("command");
		c.setAttribute("name", comm);
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
	
	private Node nearestCityXML(String err, String xC, String yC, City city) {
		Element s;
		if (city == null){
			err = "mapIsEmpty";
		}
		if (err != null) {
			s = results.createElement("error");
			s.setAttribute("type", err);
		} else {s = results.createElement("success"); }
		Element com = results.createElement("command");
		com.setAttribute("name", "nearestCity");
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
				Element cty = results.createElement("city");
				cty.setAttribute("name", city.getName());
				cty.setAttribute("x", city.getXString());
				cty.setAttribute("y", city.getYString());
				cty.setAttribute("color", city.getColor());
				cty.setAttribute("radius", city.getRadiusString());
				o.appendChild(cty);
				s.appendChild(o);
			} return s;
	}

	private Node rangeCitiesXML(String err, String xC, String yC, String rad, ArrayList<City> ctys, String save) {
		Element s;
		if (err != null) {
			s = results.createElement("error");
			s.setAttribute("type", err);
		} else {s = results.createElement("success"); }
		Element com = results.createElement("command");
		com.setAttribute("name", "rangeCities");
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
	
}