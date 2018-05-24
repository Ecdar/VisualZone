package ZoneVisualizer.Utility;

import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.SingleClockConstraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LINQ {

    public static Collection<SingleClockConstraint> ofTypeSCC(Collection<Constraint> collection) {
        return collection.stream()
                .filter(c -> c instanceof SingleClockConstraint)
                .map(c -> (SingleClockConstraint)c)
                .collect(Collectors.toList());
    }

    public static Collection<TwoClockConstraint> ofTypeTCC(Collection<Constraint> collection) {
        return collection.stream()
                .filter(c -> c instanceof TwoClockConstraint)
                .map(c -> (TwoClockConstraint)c)
                .collect(Collectors.toList());
    }

    public static <T1, T2> Collection<T2> cast(Collection<T1> collection) {
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

    public static <T> Collection<Set<T>> subsets(Collection<T> collection, int minimumSize) {
        Collection<Set<T>> sets = new ArrayList<>();
        if (collection.size() < minimumSize) {
            return sets;
        }
        if (collection.size() == minimumSize || collection.isEmpty()) {
            sets.add(new HashSet<>(collection));
            return sets;
        }
        T head = first(collection);
        List<T> rest = new ArrayList<>(collection);
        rest.remove(head);
        for (Set<T> subset : subsets(rest, minimumSize - 1)) {
            Set<T> newSet = new HashSet<>(subset);
            newSet.add(head);
            sets.add(newSet);
            if (subset.size() >= minimumSize) {
                sets.add(subset);
            }
        }
        return sets;
    }

    public static <TKey, TValue> void addToDeepMap(Map<TKey, Collection<TValue>> map, TKey key, TValue value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(value);
    }

    public static <TKey, TValue> void addAllToDeepMap(Map<TKey, Collection<TValue>> map, TKey key, Collection<TValue> values) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).addAll(values);
    }

    public static <T> Collection<T> getMaximums(Collection<? extends T> collection, Function<T, Double> valueFunction) {
        return getMinimums(collection, valueFunction.andThen(d -> -d));
    }

    public static <T> Collection<T> getMinimums(Collection<? extends T> collection, Function<T, Double> valueFunction) {
        if (collection.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<T> minimumCandidates = new ArrayList<>();
        Iterator<? extends T> iterator = collection.iterator();
        T first = iterator.next();
        minimumCandidates.add(first);
        double minimum = valueFunction.apply(first);
        while (iterator.hasNext()) {
            T candidate = iterator.next();
            double dimensionValue = valueFunction.apply(candidate);
            if (dimensionValue < minimum) {
                minimumCandidates.clear();
                minimumCandidates.add(candidate);
                minimum = dimensionValue;
            }
            else if (dimensionValue == minimum) {
                minimumCandidates.add(candidate);
            }
        }
        return minimumCandidates;
    }
}
