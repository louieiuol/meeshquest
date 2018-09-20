package cmsc420.meeshquest.part3;

import java.util.Comparator;

public class CoorCompare implements Comparator<City> {

	//checks x coordinate first and if same then checks y
	public int compare(City c1, City c2) {
		if (c1.y == c2.y) {
			if (c1.x == c2.x) {
				return 0;
			} else if (c1.x > c2.x) {
				return 1;
			} else { // c1.x < c2.x
				return -1;
			}
			
		} else if (c1.y > c2.y) {
			return 1;
			
		} else { // c1.y < c2.y
			return -1;
		}
	}

}