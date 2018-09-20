package cmsc420.meeshquest.part2;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

public interface Node {

	//midpoint x and y coordintes?
	public Node insert(City c, int x, int y, int h);
	public Node remove(City c);
	public Element print(Document d);
	public boolean contains(City c);
	public String isNode();
	public City getCity();

	//public int size method? black=1, white=0, grey= add all children sizes
}
