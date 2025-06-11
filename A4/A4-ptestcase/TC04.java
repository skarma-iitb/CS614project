class TC04 {

    // Main class: Just to call foo()
    public static void main(String[] args) {
        X o;
        int res;
        o = new X();
        res = o.foo(o);
        System.out.println(res);
    }
}

class X {
    public int foo(X obj) {
        A a;
        A y;
        A k;
        B x;
        int res;
        boolean t;
        t = false;
        a = new A();
        a.z = new B();
        // y = obj.zzz();
        // x = y.z;
        // res = x.bar();
        y = obj.zzz();
        k = obj.zzz();
        // y = X.zzz((X) obj);
        // k = X.zzz((X) obj);
        x = y.z;
        res = k.xx();
        System.out.println(res);
        // while (t) {
        // res = k.xx();
        // }
        return 1;
    }

    public A zzz() {
        return new A();
    }

    // public static A zzz(X a) {
    // return new A();
    // }

}

class A {
    B z;

    public int xx() {
        return 0;
    }
}

class B extends A {
    public int bar() {
        return 1;
    }
}

class C extends B {
    public int bar() {
        return 2;
    }
}

// output
// class TC04 {
// public static void main(String[] args) {
// X o;
// int res;
// o = new X();
// res = o.foo(o);
// System.out.println(res);
// }
// }

// class X {
// public int foo(X obj) {
// A a;
// A y;
// A k;
// B x;
// int res;
// boolean t;
// t = false;
// a = new A();
// a.z = new B();
// y = X.zzz((X) obj);
// k = X.zzz((X) obj);
// x = y.z;
// res = k.xx();
// return 1;
// }

// public A zzz() {
// return new A();
// }

// public static A zzz(X th) {
// return new A();
// }
// }

// class A {
// B z;

// public int xx() {
// return 0;
// }
// }

// class B extends A {
// public int bar() {
// return 1;
// }
// }

// class C extends B {
// public int bar() {
// return 2;
// }
// }
