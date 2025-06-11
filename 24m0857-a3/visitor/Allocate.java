package visitor;

public class Allocate {
    public boolean spilled;
    public String register; // assigned register (e.g., "R0")
    public int spillIndex; // if spilled, a memory index

    public Allocate(String register) {
        this.spilled = false;
        this.register = register;
    }

    public Allocate(int spillIndex) {
        this.spilled = true;
        this.spillIndex = spillIndex;
    }

    @Override
    public String toString() {
        if (spilled) {
            return "" + spillIndex + "";
        } else {
            return register;
        }
    }
}