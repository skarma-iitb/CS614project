package visitor;

import java.util.Objects;

public class LatticeValue {
    public enum State {
        top, CONSTANT, bottom
    }

    public static final LatticeValue top = new LatticeValue(null, State.top);
    public static final LatticeValue bottom = new LatticeValue(null, State.bottom);

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
        if (v1.state == State.top)
            return v2;
        if (v2.state == State.top)
            return v1;
        if (v1.state == State.bottom || v2.state == State.bottom)
            return bottom;
        if (v1.state == State.CONSTANT && v2.state == State.CONSTANT) {
            if (Objects.equals(v1.constant, v2.constant))
                return v1;
            else
                return bottom;
        }
        return bottom;
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
        if (state == State.top)
            return "top";
        if (state == State.bottom)
            return "bottom";
        return constant.toString();
    }
}