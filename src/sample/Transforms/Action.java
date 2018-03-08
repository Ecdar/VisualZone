package sample.Transforms;

import java.util.ArrayList;

public class Action implements IAction {
    private ArrayList<IAction> delegates = new ArrayList<>();

    @Override
    public void invoke() {
        for (IAction action : delegates) {
            if (action != null) {
                action.invoke();
            }
        }
    }

    public void add(IAction action) {
        delegates.add(action);
    }

    public void remove(IAction action) {
        delegates.remove(action);
    }
}

