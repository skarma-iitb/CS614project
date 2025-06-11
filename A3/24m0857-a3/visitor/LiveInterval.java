
package visitor;

public class LiveInterval {
    int start;
    int end;

    public LiveInterval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public void updateEnd(int newEnd) {
        this.end = newEnd;
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }
}
