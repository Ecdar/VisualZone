package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.SingleClockConstraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;

import java.util.*;

public class Vertex {

    private final Map<Clock, Collection<Constraint>> constraints = new HashMap<>();
    private Map<Clock, Double> coordinates;

    public Vertex(Collection<Clock> dimensions) {
        dimensions.forEach(c -> constraints.put(c, new ArrayList<>()));
    }

    public Collection<Constraint> getConstraints(Clock key) {
        return constraints.get(key);
    }

    public void addConstraint(Clock key, Constraint value) {
        constraints.get(key).add(value);
    }

    public void addConstraints(Clock key, Collection<Constraint> values) {
        constraints.get(key).addAll(values);
    }

    public Double getCoordinate(Clock key) {
        if (coordinates == null) {
            calculateCoordinates();
        }
        return coordinates.get(key);
    }

    private void calculateCoordinates() {
        coordinates = new HashMap<>();
        //Find values from SingleClockConstraints
        //Todo handle degenerate cases
        constraints.entrySet().stream()
                .filter(e -> e.getValue() instanceof SingleClockConstraint)
                .forEach(e -> coordinates.put(e.getKey(), e.getValue().getnValue()));
        //Find values from TwoClockConstraints
        constraints.entrySet().stream()
                .filter(e -> e.getValue() instanceof TwoClockConstraint)
                .forEach(this::calculateTwoClockCoordinate);
    }

    private void calculateTwoClockCoordinate(Map.Entry<Clock, Constraint> entry) {
        Clock dim = entry.getKey();
        TwoClockConstraint constraint = (TwoClockConstraint) entry.getValue();
        if (dim == constraint.getClock1()) {
            double v = constraint.getnValue() + coordinates.get(constraint.getClock2());
            coordinates.put(dim, v);
        } else {
            double v = coordinates.get(constraint.getClock1()) - constraint.getnValue();
            coordinates.put(dim, v);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (!(obj instanceof Vertex)) {
            return false;
        }
        Vertex other = (Vertex)obj;
        if (constraints.size() != other.constraints.size()) {
            return false;
        }
        for (Clock key : constraints.keySet()) {
            if (constraints.get(key) != other.constraints.get(key)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        Map.Entry[] entryArray = new Map.Entry[constraints.size()];
        entryArray = constraints.entrySet().toArray(entryArray);
        return Objects.hash((Object[]) entryArray);
    }
}
