class TC1 {
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
        B o1;
        B o2;
        B o3;
        boolean a;
        int b;
        B thh;
        a = true;
        b = 10;
        o1 = new B();
        o1.f2 = new B();
        o2 = o1.f2;
        thh = (B) o2;
        a = B.bar(thh, b);
        if (a) {
            o3 = new C();
        } else {
            o3 = new B();
        }
        a = o3.bar(b);
        return b;
    }
}

class B {
    int f1;
    B f2;

    public boolean bar(int p1) {
        this.f1 = p1;
        this.f2 = new B();
        System.out.println(p1);
        return true;
    }

    public static boolean bar(B th, int p1) {
        th.f1 = p1;
        th.f2 = new B();
        System.out.println(p1);
        return true;
    }
}

class C extends B {
    public boolean bar(int p2) {
        B t1;
        int t2;
        t1 = this;
        t2 = t1.f1;
        System.out.println(t2);
        return true;
    }
}