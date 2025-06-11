import static a3.Memory.*;

class TC05 {
    public static void main(String[] args) {
        Object R0;
        Object R1;
        Object R2;
        alloca(2);
        store(0, 1);
        store(1, 2);
        R2 = ((int) load(0)) + ((int) load(1));
        R1 = ((int) load(1)) * ((int) R2);
        R0 = ((int) R2) + ((int) R1);
        store(0, ((int) R1) / ((int) R0));
        R0 = new A();
    }
}

class A {
    public int foo(int a, int b, int c, int d) {
        Object R0;
        Object R1;
        Object R2;
        alloca(0);
        R2 = ((int) load(0)) <= ((int) load(1));
        if (((boolean) R2)) {
            R1 = 1;
        } else {
            R1 = 0;
        }
        return ((int) R1);
    }
}