package cmsc420.sortedmap;
import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cmsc420.meeshquest.part4.City;

public class AVLCommands<K,V> {
	 Document results;
	
	 public AVLCommands () {
		 
	 }
	 
	public Element printAVLTree(Document r, AvlGTree<City, String> avltree, String id){
		Element s;
		results = r;
		if(avltree.root == null){
			s = results.createElement("error");
			s.setAttribute("type", "emptyTree");
		} else {s = results.createElement("success"); }
		Element c = results.createElement("command");
		c.setAttribute("name", "printAvlTree");
		if(!id.isEmpty()){
			c.setAttribute("id", id);
		}
	
		Element p = results.createElement("parameters");
		s.appendChild(c);
		s.appendChild(p);
		
		if(avltree.root != null){
			Element o = results.createElement("output");
			Element tree = results.createElement("AvlGTree");
			tree.setAttribute("cardinality", Integer.toString(avltree.size()));
			tree.setAttribute("height", Integer.toString(avltree.getHeight(avltree.root)));
			tree.setAttribute("maxImbalance", Integer.toString(avltree.g));
				o.appendChild(tree);
			preOrder(avltree.root,tree);
			s.appendChild(o);
		}
		
		
		return s;
	}
	
	public void preOrder(AvlGTree.Node node, Element o) {
		Element n = results.createElement("node");
        if (node != null) {
			n.setAttribute("key",((City)node.getKey()).name);
			String val = "("+((City)node.getKey()).getXString() + ","+((City)node.getKey()).getYString()+")";
			n.setAttribute("value",val);
            preOrder(node.left, n);
            preOrder(node.right,n);
            o.appendChild(n);
        } else{
        	Element e = results.createElement("emptyChild");
        	 o.appendChild(e);
        }
       
    }
	
}
