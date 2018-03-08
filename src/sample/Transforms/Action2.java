package sample.Transforms;

import java.util.ArrayList;

interface IAction2<T1, T2> {
    void invoke(T1 arg1, T2 arg2);
}

public class Action2<T1, T2> implements IAction2<T1, T2> {
    private ArrayList<IAction2<T1, T2>> delegates = new ArrayList<>();

    @Override
    public void invoke(T1 arg1, T2 arg2) {
        for (IAction2<T1, T2> action : delegates) {
            if (action != null) {
                action.invoke(arg1, arg2);
            }
        }
    }

    public void add(IAction2<T1, T2> action) {
        delegates.add(action);
    }

    public void remove(IAction2<T1, T2> action) {
        delegates.remove(action);
    }
}
