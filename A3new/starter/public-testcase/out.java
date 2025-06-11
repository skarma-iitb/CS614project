
/*1*/
import static a3.Memory.*;

class TC01 {
    public static void main(String[] args) {
        Object R0;
        alloca(3);
        R0 = 1;
        store(0, 2);
        store(1, 3);
        store(2, new TestTC01());
        R0 = ((TestTC01) load(2)).foo(((int) R0), ((int) load(0)), ((int) load(1)));
        System.out.println(((int) R0));
    }
}

class TestTC01 {
    public int foo(int i, int j, int l) {
        Object R0;
        alloca(2);
        store(4, 1);
        store(3, 2);
        R0 = ((int) load(4)) <= ((int) load(3));
        while (((boolean) R0)) {
            store(3, ((int) load(4)) + ((int) load(3)));
            R0 = ((int) load(3)) <= ((int) load(4));
        }
        store(3, ((int) load(3)));
        R0 = 2;
        store(3, ((int) R0));
        return ((int) load(3));
    }
}