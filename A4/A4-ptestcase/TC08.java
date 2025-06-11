class TC08 {
    public static void main(String[] args) {
        int x;
        x = 5;
        System.out.println(x);
    }
}

class X {
    A f;

    public int foo() {
        A a;
        B b;
        int x;
        a = new A();
        b = new B();
        a.d = b;
        x = a.foo();

        a = b.x;
        x = a.foo();
        return x;
    }
}

class A {
    B d;

    public int foo() {
        return 10;
    }
}

class B {
    A x;

    public int bar() {
        return 5;
    }
}

class C extends B {
}
