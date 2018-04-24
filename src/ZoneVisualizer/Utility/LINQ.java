package ZoneVisualizer.Utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

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

    public static <T> T first(Collection<T> collection, Predicate<T> predicate) {
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (predicate.test(next)) {
                return next;
            }
        }
        return null;
    }

    public static <TKey, TValue> void addToDeepMap(Map<TKey, Collection<TValue>> map, TKey key, TValue value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(value);
    }
}
