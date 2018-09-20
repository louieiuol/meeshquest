package cmsc420.meeshquest.part4;
import java.util.Comparator;

public class CityNameComparator implements Comparator<String> {

	public int compare(String o1, String o2) {
        String s1 = o1;
        String s2 = o2;
        return -s1.compareTo(s2);
    }
}
