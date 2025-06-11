
class TC07 {
    public static void main(String[] args) {
        A o;
        int res;
        o = new A();
        res = o.foo();
        System.out.println(res);
    }
}

class A {
    int b;
    B b1;

    public int foo() {
        B o1;
        B o2;
        int d;
        d = 4;
        o1 = new B();
        o2 = new B();
        o1 = o1.bar();
        o1.b2 = o2;
        o1 = o1.bar();
        return d;
    }

}

class B {
    int c;
    B b2;

    public B bar() {
        int a;
        a = 1;
        c = 2;
        a = a + c;
        c = a + c;
        c = c + c;
        return b2;
    }
}
