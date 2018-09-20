package cmsc420.sortedmap;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public class AvlGTree<K, V> extends AbstractMap <K,V> implements SortedMap<K, V> {
	V returnVal = null;
	// Java program for insertion in AVL Tree
	public class Node implements Map.Entry<K,V> {
		K key = null;
		V value = null;
	    int height;
	    Node left, right;
	 
	    Node(K k, V val) {
	        key = k;
	        value = val;
	        height = 1;
	    }
	    
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V arg0) {
			value = arg0;
			return value;
		}
		
		/*adopted from treemap*/
		@Override
		public boolean equals(Object n) {
			if(compareKeys(((Node) n).key, this.key) == 0) {
				if(((Comparable<? super V>)((Node) n).value).compareTo(this.value) == 0)
					return true;
				else return false;
			}
			else return false;
		}
		
		/*Copied from AbstractMap*/
		@Override
		public String toString() {
			return key + "=" + value;
		}
		
		/*Copied from AbstractMap*/
		@Override
		 public int hashCode() {
			int keyHash = (key==null ? 0 : key.hashCode());
			int valueHash = (value==null ? 0 : value.hashCode());
			return keyHash ^ valueHash;
		}
	}
		
	/*the next dozen methods are from GeeksforGeeks AVL tree implementation*/
	    Node root = null;
	    int g = 1;
	    Comparator<? super K> compr = null;
	    int modCount;
	    K low;
	    K high;
	    
	    public AvlGTree(){
	    	modCount = 0;
	    	
	    }
	    public AvlGTree(Comparator c){
	    	modCount = 0;
	    	compr = c;
	    }
	    public AvlGTree(final int gParam){
	    	modCount = 0;
	    	g = gParam;
	    }
	    public AvlGTree(final Comparator comp, final int gParam){
	    	modCount = 0;
	    	g = gParam;
	    	compr = comp;
	    }
	 
	    public AvlGTree(Map<K, ? extends V> m) {
	    	this.putAll(m);
	    		
	    }
	   
	    // A utility function to get height of the tree
	    int height(Node N) {
	        if (N == null)
	            return 0;
	 
	        return N.height;
	    }
	    
	    int height(){
	    	return height(root);
	    }
	 
	    // A utility function to get maximum of two integers
	    int max(int a, int b) {
	        return (a > b) ? a : b;
	    }
	    
	    //generic comparator?
	    final int compareKeys(Object k1, Object k2) {
	    	return this.compr==null ? ((Comparable)k1).compareTo((K)k2)
	   	            : this.compr.compare((K)k1, (K)k2);
	    }
	 
	    // A utility function to right rotate subtree rooted with y
	    // See the diagram given above.
	    Node rightRotate(Node y) {
	        Node x = y.left;
	        Node T2 = x.right;
	 
	        // Perform rotation
	        x.right = y;
	        y.left = T2;
	 
	        // Update heights
	        y.height = max(height(y.left), height(y.right)) + 1;
	        x.height = max(height(x.left), height(x.right)) + 1;
	 
	        // Return new root
	        return x;
	    }
	 
	    // A utility function to left rotate subtree rooted with x
	    // See the diagram given above.
	    Node leftRotate(Node x) {
	        Node y = x.right;
	        Node T2 = y.left;
	 
	        // Perform rotation
	        y.left = x;
	        x.right = T2;
	 
	        //  Update heights
	        x.height = max(height(x.left), height(x.right)) + 1;
	        y.height = max(height(y.left), height(y.right)) + 1;
	 
	        // Return new root
	        return y;
	    }
	 
	    // Get Balance factor of node N
	    int getBalance(Node N) {
	        if (N == null)
	            return 0;
	        return height(N.left) - height(N.right);
	    }
	 
	    Node insert(Node node, K key, V val) {
	    	modCount++;
	        /* 1.  Perform the normal BST insertion */
	        if (node == null){
	        	return (new Node(key, val));
	        }
	            
	        
	        if (compareKeys(key,node.key) < 0)
	            node.left = insert(node.left, key, val);
	        else if (compareKeys(key,node.key) > 0)
	            node.right = insert(node.right, key, val);
	        else{
	        	// Duplicate keys not allowed
	        	returnVal = node.getValue();
	        		node.setValue(val);
	        	  return node;
	        }
	          
	 
	        /* 2. Update height of this ancestor node */
	        node.height = 1 + max(height(node.left),
	                              height(node.right));
	 
	        /* 3. Get the balance factor of this ancestor
	              node to check whether this node became
	              unbalanced */
	        int balance = getBalance(node);
	 
	        // If this node becomes unbalanced, then there
	        // are 4 cases Left Left Case
	        if (balance > g && compareKeys(key,node.left.key) < 0){
	        	//System.out.println("1 " + g + " " + node.value + " height left: " + height(node.left) + " height right: " + height(node.right));
	        	return rightRotate(node);
	        }
	            
	 
	        // Right Right Case
	        if (balance < -g && compareKeys(key,node.right.key) > 0){
	        	//System.out.println("2" + node.value + " height left: " + height(node.left) + " height right: " + height(node.right));	
	        	return leftRotate(node);
	        }
	        	
	 
	        // Left Right Case
	        if (balance > g && compareKeys(key,node.left.key) > 0) {
	        	//System.out.println("3" + node.value + " height left: " + height(node.left) + " height right: " + height(node.right));
	            node.left = leftRotate(node.left);
	            return rightRotate(node);
	        }
	 
	        // Right Left Case
	        if (balance < -g && compareKeys(key,node.right.key) < 0) {
	        	//System.out.println("4" + node.value + " height left: " + height(node.left) + " height right: " + height(node.right));
	            node.right = rightRotate(node.right);
	            return leftRotate(node);
	        }
	        /* return the (unchanged) node pointer */
	        return node;
	        
	    }
	 
	    // A utility function to print preorder traversal
	    // of the tree.
	    void preOrder(Node node) {
	        if (node != null) {
	            System.out.println(node.value + " ");
	            preOrder(node.left);
	            preOrder(node.right);
	        }
	    }
	    
	    /*The above methods dozen or so methods are from GeeksforGeeks AVL implementation*/
	
	    //returns true if k is in the tree
	   private boolean contains(Node t, K x) {
		   if (t == null){
			      return false; // The node was not found
			    } else if (compareKeys(x,t.key) < 0){
			      return contains(t.left, x);
			    } else if (compareKeys(x,t.key) > 0){
			      return contains(t.right,x); 
			    }

			    return true; // Can only reach here if node was found
	     }
	
	   /*borrowed from stack overflow*/
	public int getHeight(Node node){
		if (node == null)
            return 0;
        else
        {
            /* compute the depth of each subtree */
            int lDepth = getHeight(node.left);
            int rDepth = getHeight(node.right);
  
            /* use the larger one */
            if (lDepth > rDepth)
                return (lDepth + 1);
             else
                return (rDepth + 1);
        }
	}
	
	@Override
	public void clear() {
		root = null;
	}
	
	int sizeHelper(Node node){
	   if (node == null)
         return 0;
	   else
         return(sizeHelper(node.left) + 1 + sizeHelper(node.right));
	}
	
	@Override
	public int size(){
		return sizeHelper(root);
	}

	@Override
	public Comparator comparator() {
		return compr;
	}
	
	@Override
	public boolean containsKey(Object arg0) {
		try{
		return contains(root, (K) arg0);
		} catch(ClassCastException e) {return false;}
	}
	

	
	//give it key, it returns the value
	public V get(Object key){
			return getHelper(root, (K) key);
	}
	public V getHelper(Node n, K key){
		if (n == null) return null;
        int cmp = compareKeys(key,n.key);
        if      (cmp < 0) return getHelper(n.left, key);
        else if (cmp > 0) return getHelper(n.right, key);
        else              return n.value;
	}    

	//give it value, it returns key
	public K getKey(V val){
		return containsValueHelper(root, val);
	}
	
	//give it value, returns true or false
	@Override
	public boolean containsValue(Object arg0) {
		if(containsValueHelper(root, (V)arg0) != null){
			return true;
		}
		return false;
	}
	//HELPER give it value, it returns key
	public K containsValueHelper(Node n, V val){
		while(n != null){
			if(n.value.equals(val)) return n.key;
			else {
				K key = containsValueHelper(n.right, val);
				if(key == null){
					return containsValueHelper(n.left, val);
				} else return key;
			}
		}
		return null;
	}
	

	@Override
	public boolean isEmpty() {
		if(root == null) return true; else return false;
	}
	
	@Override
	public V put(K key, V value) {
		this.root =  insert(root,key,value);
		
		if(returnVal != null){
			V v = returnVal;
			returnVal = null;
			return v;
		}
		else return null;
	}
	
	 public void add(K k, V val){
	    this.root = insert(root, k, val);
	 }

	@Override
	public Set entrySet() {
		return new EntrySet();
	}
	
	@Override
	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		if (compareKeys(fromKey, toKey) > 0) {
			throw new IllegalArgumentException("fromKey > toKey");
		}
		return new SubMap(fromKey, toKey);
	}
	
	/*Copied from abstract map*/
	public int hashCode() {
		int h = 0;
		Iterator<Entry<K,V>> i = entrySet().iterator();
		while (i.hasNext())
		    h += i.next().hashCode();
		return h;
	}
	
	/*Copied and modified from TreeMap*/
	@Override
	public boolean equals(Object o) {
	    if (o == this)
	    	return true;
		if (!(o instanceof Map))
			return false;
		
		Map<K,V> m = (Map<K,V>) o;
		
		if (m.size() != size())
			return false;
		
		try {
		Iterator<Entry<K,V>> i = entrySet().iterator();
		      while (i.hasNext()) {
		          Entry<K,V> e = i.next();
		          K key = e.getKey();
		          V value = e.getValue();
		          if (!value.equals(m.get(key)))
		             return false;
		          }
		          
		       } catch (ClassCastException unused) {
		          return false;
		       } catch (NullPointerException unused) {
		            return false;
		       }
		
		        return true;
		}
	

	@Override
	public K firstKey() {
		if(this.isEmpty()) throw new NoSuchElementException();
		else
			return firstKeyHelper(root);
	}
	public K firstKeyHelper(Node n){
		Node current = n;
        while (current.left != null) {
            current = current.left;
        }
        return (current.key);
	}
	
	@Override
	public K lastKey() {
		if(this.isEmpty()) throw new NoSuchElementException();
		else
			return LastKeyHelper(root);
	}
	public K LastKeyHelper(Node n){
		Node current = n;
        while (current.right != null) {
            current = current.right;
        }
        return (current.key);
	}   

	
	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		for(java.util.Map.Entry<? extends K, ? extends V> e : arg0.entrySet()){
			this.put(e.getKey(), e.getValue());
		}
	}
	
	/*Copied from AbstractMap*/
	@Override
	public String toString() {
		Iterator<Entry<K,V>> i = entrySet().iterator();
		if (! i.hasNext())
		     return "{}";
		
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		 for (;;) {
		      Entry<K,V> e = i.next();
		      K key = e.getKey();
		      V value = e.getValue();
		      sb.append(key   == this ? "(this Map)" : key);
		      sb.append('=');
		      sb.append(value == this ? "(this Map)" : value);
		      if (! i.hasNext())
		          return sb.append('}').toString();
		      sb.append(", ");
		 }
	}
	
	/*Implement in part4*/
	@Override
	public V remove(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*Dont implement- these throw unsupported opperation exceptions*/
	@Override
	public Set keySet() {
		throw new UnsupportedOperationException();
	}
	@Override
	public Collection values() {
		throw new UnsupportedOperationException();
	}
	@Override
	public SortedMap<K, V> headMap(K toKey) {
		throw new UnsupportedOperationException();
	}
	public SortedMap<K, V> tailMap(K fromKey) {
		throw new UnsupportedOperationException();
	}
	

	protected class EntrySet extends AbstractSet<Map.Entry<K,V>> implements Set<Map.Entry<K,V>> {
	        public boolean remove(Object o) {
	        	throw new UnsupportedOperationException();
	        }
	        
	        public boolean contains(Object o) {
	            try {
	            	Map.Entry me = (Map.Entry)o;
	            	Object val = me.getValue();
		            Object key = me.getKey();
		            V k = AvlGTree.this.get(key);
		            if(k!= null && k.equals(val))
		            return true;
		            else return false;
	            } catch (ClassCastException e) {
	            	return false;
	            }
	            
	        }

			/*The hashcode and toStrings taken from Abstact map/ treemap*/
			@Override
			public int hashCode(){
				return AvlGTree.this.hashCode();
			}
			@Override
			public String toString(){
				Iterator<Entry<K,V>> i = entrySet().iterator();
				if (! i.hasNext())
				     return "[]";
				
				StringBuilder sb = new StringBuilder();
				sb.append('[');
				 for (;;) {
				      Entry<K,V> e = i.next();
				      K key = e.getKey();
				      V value = e.getValue();
				      sb.append(key   == this ? "(this Map)" : key);
				      sb.append('=');
				      sb.append(value == this ? "(this Map)" : value);
				      if (! i.hasNext())
				          return sb.append(']').toString();
				      sb.append(", ");
				 }
			}
			
//			@Override
//			public boolean addAll(Collection arg0) {
//				Iterator<Map.Entry<K, V>> i = arg0.iterator();
//				while(i.hasNext()){
//					this.add(i.next());	
//				}
//				return true;
//			}
			
			@Override
			public boolean add(Entry arg0) {
				throw new UnsupportedOperationException();
//				try{
//					K key = ((K) arg0.getKey());
//					V val = ((V)arg0.getValue());
//					AvlGTree.this.root = insert(root,key,val);
//					return true;
//				} catch (ClassCastException e){return false; }
			}

			@Override
			public void clear() {
				AvlGTree.this.clear();
			}
//			@Override
//			public boolean containsAll(Collection arg0) {
//				Iterator<Map.Entry<K, V>> i = arg0.iterator();
//				while(i.hasNext()){
//					if(!this.contains(i))
//						return false;
//				}
//				return true;
//			}
			@Override
			public boolean isEmpty() {
				return AvlGTree.this.isEmpty();
			}
			
			@Override
			public Iterator iterator() {
				return new EntrySetIterator<Map.Entry<K, V>>();
			}
			
			class EntrySetIterator<T> implements Iterator<Map.Entry<K, V>> {
				int expectedCount;
				private Iterator<java.util.Map.Entry<K, V>> it;
				
				public EntrySetIterator() {
					this.expectedCount = modCount;
					LinkedList<java.util.Map.Entry<K, V>> entryList = new LinkedList<>();
					this.createList(entryList, AvlGTree.this.root);
					this.it = entryList.iterator();
				}
					
				private void createList(LinkedList<Map.Entry<K, V>> list, AvlGTree<K,V>.Node node) {
					if (node == null) {
						return;
					}
					this.createList(list, node.left);
					list.add(node);
					this.createList(list, node.right);
				}
					 
				@Override
				public boolean hasNext() {
						return it.hasNext();
				}

				@Override
				public Entry<K, V> next() {
					if(hasNext() == false)
						throw new  NoSuchElementException();
					if(expectedCount != modCount){
						throw new ConcurrentModificationException();
					}
					return it.next();
				}
					 
			}
			 
			
			@Override
			public boolean removeAll(Collection c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int size() {
				return AvlGTree.this.size();
			}
	}
	

	protected class SubMap extends AbstractMap implements SortedMap {
		K from;
		K to;
		int size = 0;
		public SubMap(K fromKey, K tokey){
			from = fromKey;
			to = tokey;
		}

		@Override
		public void clear() {
			AvlGTree.this.clear();
			
		}

		@Override
		public boolean containsKey(Object key) {
			if(AvlGTree.this.compareKeys(key, from) >= 0 && AvlGTree.this.compareKeys(key, to) < 0 ){
				return AvlGTree.this.containsKey(key);
			} return false;
			
		}

		@Override
		public boolean containsValue(Object value) {
			try{
				K key = getKey((V)value);
				if(key == null) return false;
				else {
					if(AvlGTree.this.compareKeys(key, from) >= 0 && AvlGTree.this.compareKeys(key, to) < 0 ){
						return true;
					} return false;
				}
			} catch(ClassCastException e){
				return false;
			}
			
		}
		

		@Override
		public Object get(Object key) {
			if(AvlGTree.this.compareKeys(key, from) >= 0 && AvlGTree.this.compareKeys(key, to) < 0 ){
				return AvlGTree.this.get(key);
			} return null;
		}

		@Override
		public boolean isEmpty() {	
			Iterator<Entry<K,V>> i = new SubMapIterator<Map.Entry<K, V>>(this.from, this.to);
			 while (! i.hasNext()) return true;
			 return false;
			     
		}

		@Override
		public Object put(Object key, Object value) {
			try{
				AvlGTree.this.root = AvlGTree.this.insert(root,(K)key,(V)value);
				return true;
			} catch(ClassCastException e){
				return false;
			}
		}

		@Override
		public void putAll(Map m) {
			AvlGTree.this.putAll(m);
		}

		@Override
		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			int s = 0;
			Iterator<Entry<K,V>> i = new SubMapIterator<Map.Entry<K, V>>(this.from,this.to);
			 while (i.hasNext()) {
			      s++;
			     i.next();
			 }
			 return s;
		}

		@Override
		public Comparator comparator() {
			return AvlGTree.this.compr;
		}

		@Override
		public Set entrySet() {
			return new SubMapEntrySet();
		}
		
		public class SubMapEntrySet extends AbstractSet {
	        public boolean remove(Object o) {
	        	throw new UnsupportedOperationException();
	        }
	        public boolean contains(Object o) {
	        	try{
	        		Map.Entry me = (Map.Entry)o;
		            Object val = me.getValue();
		            Object key = me.getKey();
		            //return SubMap.this.containsKey(key) &&
		              if(SubMap.this.containsKey(key) == true){
		            	  K k = getKey((V)val);
		            	  if(k != null && compareKeys(k, key) == 0)
		            		  return true;
		            	else return false; 
		              }
		              return false;
	        	} catch(ClassCastException e){
	        		return false;
	        	}
	            
	        }
	        
			@Override
			public boolean add(Object arg0) {
				try {
					K key = ((K) ((Map.Entry) arg0).getKey());
					V val = ((V) ((Map.Entry) arg0).getValue());
					AvlGTree.this.root = insert(root,key,val);
					return true;
				} catch (ClassCastException e){
					return false;
				}
			}
			
			@Override
			public int hashCode(){
				return SubMap.this.hashCode();
			}
			@Override
			public String toString(){
				Iterator<Entry<K,V>> i = entrySet().iterator();
				if (! i.hasNext())
				     return "[]";
				
				StringBuilder sb = new StringBuilder();
				sb.append('[');
				 for (;;) {
				      Entry<K,V> e = i.next();
				      K key = e.getKey();
				      V value = e.getValue();
				      sb.append(key   == this ? "(this Map)" : key);
				      sb.append('=');
				      sb.append(value == this ? "(this Map)" : value);
				      if (! i.hasNext())
				          return sb.append(']').toString();
				      sb.append(", ");
				 }
			}
			
			@Override
			public boolean addAll(Collection arg0) {
				Iterator<Map.Entry<K, V>> i = arg0.iterator();
				while(i.hasNext()){
					this.add(i);	
				}
				return true;
			}
			@Override
			public void clear() {
				SubMap.this.clear();
			}
			@Override
			public boolean containsAll(Collection arg0) {
				Iterator<Map.Entry<K, V>> i = arg0.iterator();
				while(i.hasNext()){
					if(!this.contains(i))
						return false;
				}
				return true;
			}
			@Override
			public boolean isEmpty() {
				return SubMap.this.isEmpty();
			}
			@Override
			public Iterator iterator() {
//				count = modCount;
				return new SubMapIterator<Map.Entry<K, V>>(SubMap.this.from, SubMap.this.to);
			}
			
			@Override
			public boolean removeAll(Collection c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int size() {
				return SubMap.this.size();
			}
		}
		
		@Override
		public Object firstKey() {
			if(this.isEmpty()) throw new NoSuchElementException();
			else{
				Iterator<Entry<K,V>> i = new SubMapIterator<Map.Entry<K, V>>(this.from, this.to);
				return i.next().getKey();
			}
		}

		@Override
		public Object lastKey() {
			if(this.isEmpty()) throw new NoSuchElementException();
			else{
				Iterator<Entry<K,V>> i = new SubMapIterator<Map.Entry<K, V>>(this.from, this.to);
				K key = null;
				while(i.hasNext()){
					key = (K) i.next().getKey();
				}
				return key;
			}
		}
		
		public Iterator iterator() {
			return new SubMapIterator<Map.Entry<K, V>>(this.from, this.to);
		}
		
		@Override
		public int hashCode() {
			int h = 0;
			Iterator<Entry<K,V>> i = new SubMapIterator<Map.Entry<K, V>>(this.from, this.to);
			while (i.hasNext())
			    h += i.next().hashCode();
			return h;
		}
		
		 class SubMapIterator<T> implements Iterator<Map.Entry<K, V>> {
				private Iterator<java.util.Map.Entry<K, V>> it;
				K from = null;
				K to = null; 
				public SubMapIterator(K f, K t) {
					from = f;
					to = t;
					LinkedList<java.util.Map.Entry<K, V>> entryList = new LinkedList<>();
					this.createList(entryList, AvlGTree.this.root);
					this.it = entryList.iterator();
				}
				
				private void createList(LinkedList<Map.Entry<K, V>> list, AvlGTree<K,V>.Node node) {
					if (node == null) {
						return;
					}
					this.createList(list, node.left);
					if(AvlGTree.this.compareKeys(node.key, this.from) >= 0 && AvlGTree.this.compareKeys(node.key, this.to) < 0 ){
						list.add(node);
						SubMap.this.size++;
					}
						
					this.createList(list, node.right);
				}
				 
				@Override
				public boolean hasNext() {
						return it.hasNext();
				}

				@Override
				public Entry<K, V> next() {
					return it.next();
				}
				 
		}
		 
		/*taken from abstract map*/
		@Override
		public String toString() {
				Iterator<Entry<K,V>> i = new SubMapIterator<Map.Entry<K, V>>(this.from, this.to);
				if (! i.hasNext())
				     return "{}";
				
				StringBuilder sb = new StringBuilder();
				sb.append('{');
				 for (;;) {
				      Entry<K,V> e = i.next();
				      K key = e.getKey();
				      V value = e.getValue();
				      sb.append(key   == this ? "(this Map)" : key);
				      sb.append('=');
				      sb.append(value == this ? "(this Map)" : value);
				      if (! i.hasNext())
				          return sb.append('}').toString();
				      sb.append(", ");
				 }
			}

		@Override
		public SortedMap headMap(Object toKey) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set keySet() {
			throw new UnsupportedOperationException();
		}

		@Override
		public SortedMap subMap(Object fromKey, Object toKey) {
			if(compareKeys(this.from, fromKey) > 0 || compareKeys(this.to, fromKey) < 0)
				throw new IllegalArgumentException("fromKey out of range");
			if(compareKeys(this.from, toKey) > 0 || compareKeys(this.to, toKey) < 0)
				throw new IllegalArgumentException("toKey out of range");
			if(compareKeys(fromKey, toKey) > 0)
				throw new IllegalArgumentException("fromKey > toKey");
			return new AvlGTree.SubMap(fromKey,toKey);
		}
		
		@Override
		public SortedMap tailMap(Object fromKey) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection values() {
			throw new UnsupportedOperationException();
		}	
	
		
	}
	
	

}
