package ZoneVisualizer.Utility;

public class BackedUpValue<T, T2> {
    private T value;
    private T2 backupValue;

    public BackedUpValue(T value, T2 backupValue) {
        this.value = value;
        this.backupValue = backupValue;
    }

    public boolean isNull() {
        return value == null;
    }

    public T getValue() {
        return value;
    }

    public T2 getBackupValue() {
        return backupValue;
    }
}
