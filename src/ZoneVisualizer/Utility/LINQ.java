package ZoneVisualizer.Utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class LINQ {

    public static <T1, T2> Collection<T2> ofType(Collection<T1> collection) {
        ArrayList<T2> result = new ArrayList<>();
        for (T1 obj : collection) {
            try {
                T2 castObj = (T2)obj;
                result.add(castObj);
            }
            catch (ClassCastException e){

            }
        }
        return result;
    }

    public static <T> boolean overlaps(Collection<T> collection1, Collection<T> collection2) {
        for (T i : collection1) {
            for (T j : collection2) {
                if (i != null && i.equals(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> T first(Collection<T> collection) {
        Iterator<T> iterator = collection.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}
