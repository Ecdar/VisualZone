package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Constraint;

public abstract class VertexPotential {

    public abstract double getKeyDimensionValue();

    public abstract Constraint getPotentialConstraint();

    public abstract Vertex getPotentialVertex();
}
