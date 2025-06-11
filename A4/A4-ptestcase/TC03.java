class TC03 {

    // Main class: Just to call foo()
    public static void main(String[] args) {
        A o;
        int res;
        o = new A();
        res = o.foo();
        System.out.println(res);
    }
}

class A {
    public int foo() {
        int a;
        int k;
        int l;
        B o1;
        k = 10;
        l = 10;
        o1 = new C(); // PTA of O1 = {O16)
        a = o1.bar(k, l); // PTA of O1 = {O16} --> Monomorphic call to C:bar
        return k;
    }
}

class B {
    int f;

    public int bar(int p1, int p2) {
        int t1;
        t1 = 1;
        System.out.println(f);
        return t1;
    }

    public B fun(int p1, int p2) {
        int t1;
        t1 = 1;
        System.out.println(f);
        return new B();
    }
}

class C extends B {
    int f;

    public int bar(int p2, int x) {
        C t1;
        int t2;
        t1 = this;
        t2 = t1.f;
        System.out.println(t2);
        return t2;
    }
}
