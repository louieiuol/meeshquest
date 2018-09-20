package cmsc420.meeshquest.part2;

import java.util.Comparator;

public class CityComp implements Comparator<City> {

	public int compare(City c1, City c2) {
        String s1 = c1.getName();
        String s2 = c2.getName();
        return -s1.compareTo(s2);
    }

}
