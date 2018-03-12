package sample.Constraints;

public abstract class Constraint {

    protected Inequality inequality;
    protected double nValue;

    public Constraint(Inequality inequality, double nValue) {
        this.inequality = inequality;
        this.nValue = nValue;
    }

    public abstract boolean holds();

    public Inequality getInequality() {
        return inequality;
    }

    public double getnValue() {
        return nValue;
    }
}