package sample.Transforms;

import java.util.ArrayList;

interface IAction1<T> {
    void invoke(T arg);
}

public class Action1<T> implements IAction1<T> {
    private ArrayList<IAction1<T>> delegates = new ArrayList<>();

    @Override
    public void invoke(T arg) {
        for (IAction1<T> action : delegates) {
            if (action != null) {
                action.invoke(arg);
            }
        }
    }

    public void add(IAction1<T> action) {
        delegates.add(action);
    }

    public void remove(IAction1<T> action) {
        delegates.remove(action);
    }
}
