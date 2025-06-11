class TC9 {
    public static void main(String[] args) {
        int x;
        x = 5;
        System.out.println(x);
    }
}

class A {
    public int foo() {
        return 10;
    }

    public static int foo(A th) {
        return 10;
    }
}

class B extends A {
    public int bar() {
        B b;
        int x;
        b = new B();
        x = B.foo((B) b);
        return 10;
    }
}
