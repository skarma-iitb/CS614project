package visitor;

import java.util.Objects;

public class LatticeValue {
    public enum State {
        T, CONSTANT, B
    }

    public static final LatticeValue T = new LatticeValue(null, State.T);
    public static final LatticeValue B = new LatticeValue(null, State.B);

    private final Integer constant;
    private final State state;

    private LatticeValue(Integer constant, State state) {
        this.constant = constant;
        this.state = state;
    }

    public static LatticeValue constant(int value) {
        return new LatticeValue(value, State.CONSTANT);
    }

    public boolean isConstant() {
        return state == State.CONSTANT;
    }

    public Integer getConstant() {
        return constant;
    }

    public State getState() {
        return state;
    }

    // Merge (meet) operator for two lattice values.
    public static LatticeValue merge(LatticeValue v1, LatticeValue v2) {
        if (v1.state == State.T)
            return v2;
        if (v2.state == State.T)
            return v1;
        if (v1.state == State.B || v2.state == State.B)
            return B;
        if (v1.state == State.CONSTANT && v2.state == State.CONSTANT) {
            if (Objects.equals(v1.constant, v2.constant))
                return v1;
            else
                return B;
        }
        return B;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LatticeValue))
            return false;
        LatticeValue other = (LatticeValue) o;
        return state == other.state && Objects.equals(constant, other.constant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, constant);
    }

    @Override
    public String toString() {
        if (state == State.T)
            return "T";
        if (state == State.B)
            return "B";
        return constant.toString();
    }
}