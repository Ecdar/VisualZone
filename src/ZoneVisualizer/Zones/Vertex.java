package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.Utility.LINQ;

import java.util.*;
import java.util.stream.Collectors;

public class Vertex {

    private final Map<Clock, Set<Constraint>> constraints = new HashMap<>();
    private final Map<Clock, Double> coordinates = new HashMap<>();
    private boolean degenerate;

    public Vertex(Collection<Clock> dimensions) {
        dimensions.forEach(c -> constraints.put(c, new HashSet<>()));
    }

    public PivotResult pivot(Clock clock) {
        Constraint removingConstraint = LINQ.first(constraints.get(clock));
        if ((removingConstraint instanceof SingleClockConstraint &&
             removingConstraint.getInequality() == Inequality.LessThan) ||
            (removingConstraint instanceof TwoClockConstraint &&
             ((TwoClockConstraint)removingConstraint).getClock1() == clock)) {
            //Dimension wont be maximized by removing this constraint
            return null;
        }

        Vertex newVertex = new Vertex(constraints.keySet());
        List<Clock> missingDimensions = new ArrayList<>();
        Collection<VertexPotential> potentials = new ArrayList<>();
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
                TwoClockConstraint tcc = (TwoClockConstraint)c;
                Clock nonKey = tcc.getOtherClock(entry.getKey());
                if (nonKey == clock) {
                    //This TCC is likely to change dimension
                    potentials.add(new VertexPotential(tcc, entry.getKey(), nonKey));
                    missingDimensions.add(entry.getKey());
                    continue;
                }
                Constraint otherBound = LINQ.first(constraints.get(nonKey));
                if (otherBound instanceof SingleClockConstraint) {
                    //Trivial case where TCC meets SCC
                    newVertex.addConstraint(entry.getKey(), tcc);
                    continue;
                }
                if (entry.getKey() == tcc.getClock1()) {
                    throw new IllegalStateException("Two Clock Constraint was not expected to bound " + tcc.getClock1());
                }
                //This TCC might change dimension back to bounding it's clock1 if another TCC changes dimension to bound it's clock2
                potentials.add(new VertexPotential(tcc, nonKey, entry.getKey()));
                continue;
            }
            //Todo handle degenerate case
        }
        if (missingDimensions.isEmpty()) {
            missingDimensions.add(clock);
        }

        return new PivotResult(newVertex, missingDimensions, potentials);
    }

    public Collection<Constraint> getConstraints(Clock key) {
        return constraints.get(key);
    }

    public Set<Constraint> getAllConstraints() {
        return constraints.values().stream()
                .flatMap(col -> col.stream())
                .collect(Collectors.toSet());
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
