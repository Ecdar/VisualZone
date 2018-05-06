package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.Utility.LINQ;

import java.util.*;
import java.util.stream.Collectors;

public class Vertex {

    private final Map<Clock, Set<Constraint>> constraints;
    private final Map<Clock, Double> coordinates;
    private boolean degenerate;

    public Vertex(Collection<Clock> dimensions) {
        constraints = new HashMap<>();
        coordinates = new HashMap<>();
        dimensions.forEach(c -> constraints.put(c, new HashSet<>()));
    }

    private Vertex(Map<Clock, Set<Constraint>> constraints, Map<Clock, Double> coordinates, boolean degenerate) {
        this.constraints = constraints;
        this.coordinates = coordinates;
        this.degenerate = degenerate;
    }

    public PivotResult pivot(Clock clock) {
        Constraint removingConstraint = LINQ.first(constraints.get(clock));
        if (!removingConstraint.isLowerBoundOnDimension(clock)) {
            //Dimension wont be maximized by removing this constraint
            return null;
        }

        Vertex newVertex = new Vertex(constraints.keySet());
        Collection<TwoClockConstraint> twoClockConstraints = new ArrayList<>();
        for (Map.Entry<Clock, Set<Constraint>> entry : constraints.entrySet()) {
            if (entry.getKey() == clock) {
                continue;
            }
            if (entry.getValue().size() == 1) {
                Constraint c = LINQ.first(entry.getValue());
                if (c instanceof SingleClockConstraint) {
                    newVertex.addConstraint(entry.getKey(), c);
                    continue;
                }
                twoClockConstraints.add((TwoClockConstraint)c);
                continue;
            }
            //Todo handle degenerate case
        }

        return new PivotResult(this, newVertex, clock, twoClockConstraints);
    }

    public Collection<Constraint> getConstraints(Clock key) {
        return constraints.get(key);
    }

    public Set<Constraint> getAllConstraints() {
        return constraints.values().stream()
                .flatMap(col -> col.stream())
                .collect(Collectors.toSet());
    }

    public boolean knowsDimension(Clock dimension) {
        return !constraints.get(dimension).isEmpty();
    }

    public void addConstraint(Clock key, Constraint value) {
        constraints.get(key).add(value);
        if (constraints.get(key).size() > 1) {
            degenerate = true;
        }
    }

    public void addConstraints(Clock key, Collection<? extends Constraint> values) {
        constraints.get(key).addAll(values);
        if (constraints.get(key).size() > 1) {
            degenerate = true;
        }
    }

    public boolean isDegenerate() {
        return degenerate;
    }

    public Double getCoordinate(Clock key) {
        if (!coordinates.containsKey(key)) {
            coordinates.put(key, calculateCoordinate(key));
        }
        return coordinates.get(key);
    }

    private double calculateCoordinate(Clock dimension) {
        Constraint constraint = LINQ.first(constraints.get(dimension));
        return calculateCoordinate(dimension, constraint);
    }

    public double calculateCoordinate(Clock dimension, Constraint constraint) {
        if (constraint instanceof SingleClockConstraint) {
            return constraint.getnValue();
        }
        TwoClockConstraint tcc = (TwoClockConstraint)constraint;
        //Recursion beware
        Clock otherClock = tcc.getOtherClock(dimension);
        double otherValue = getCoordinate(otherClock);
        return tcc.getOtherValue(otherClock, otherValue);
    }

    public Collection<Clock> getKnownDimensions() {
        return constraints.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
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
            if (!other.constraints.containsKey(key)) {
                return false;
            }
            if (!constraints.get(key).equals(other.constraints.get(key))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        Set<Constraint> constraintSet = getAllConstraints();
        return constraintSet.hashCode();
    }

    @Override
    public String toString() {
        Set<Constraint> allConstraints = getAllConstraints();
        if (allConstraints.isEmpty()) {
            return "Empty Vertex";
        }
        return allConstraints.stream()
                .map(c -> c.toString())
                .reduce((s1, s2) -> s1 + ", " + s2).get();
    }

    public Vertex getClone() {
        return new Vertex(new HashMap<>(constraints), new HashMap<>(coordinates), degenerate);
    }

    public static class VertexComparator implements Comparator<Vertex> {

        private final List<Clock> dimensionOrder;

        public VertexComparator(Collection<Clock> dimensionOrder) {
            this.dimensionOrder = new ArrayList<>(dimensionOrder);
        }

        @Override
        public int compare(Vertex o1, Vertex o2) {
            if (o1 == null) {
                if (o2 == null) {
                    return 0;
                }
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            for (Clock clock : dimensionOrder) {
                Constraint c1 = LINQ.first(o1.getConstraints(clock));
                Constraint c2 = LINQ.first(o2.getConstraints(clock));
                if (c1 == null) {
                    if (c2 == null) {
                        return 0;
                    }
                    return -1;
                }
                if (c2 == null) {
                    return 1;
                }
                int dimComparison = Double.compare(c1.getnValue(), c2.getnValue());
                if (dimComparison != 0) {
                    return dimComparison;
                }
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }
    }
}
