public class Test5 {
    public static void main(String[] args) {

    }
}

class A {
    int x;

    public int bar(int x) {
        int y;
        y = x;
        return y;
    }
}

class B extends A {
    public int foo() {
        int y;
        y = x;
        return y;
    }
}
