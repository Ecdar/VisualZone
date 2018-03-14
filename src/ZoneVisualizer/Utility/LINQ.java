package ZoneVisualizer.Utility;

import java.util.ArrayList;
import java.util.Collection;

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
}
