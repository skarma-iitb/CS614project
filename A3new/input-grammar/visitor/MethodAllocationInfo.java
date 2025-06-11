package visitor;

public class MethodAllocationInfo {
    public int registersUsed; // Number of unique registers (e.g., "R0", "R1", etc.)
    public int spilledCount; // Number of spilled variables

    public MethodAllocationInfo(int registersUsed, int spilledCount) {
        this.registersUsed = registersUsed;
        this.spilledCount = spilledCount;
    }

    @Override
    public String toString() {
        return registersUsed + " " + spilledCount;
    }
}
