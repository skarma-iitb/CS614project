package visitor;

public class ConstantInfo {
    private boolean isConstant;
    private int value;

    public ConstantInfo(boolean isConstant, int value) {
        this.isConstant = isConstant;
        this.value = value;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public int getValue() {
        return value;
    }

    public void makeNonConstant() {
        isConstant = false;
    }
}