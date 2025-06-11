class TC08 {
    public static void main(String[] args) {
        // Exec e;
        // e = new Exec();
        // e.exec();
    }
}

class Exec {
    public int exec() {
        // int obj1;
        Exec obj2;
        int obj3;
        int obj4;
        Odd1 obj1;
        obj2 = new Exec();
        // obj2 = new Even1();
        // obj3 = new Odd2();
        // obj4 = new Even2();
        obj3 = obj2.foo(1);
        // obj2.foo(2); // obj2.foo = [Odd1.foo, Even1.foo, Odd2.foo, Even2.foo]
        // obj3.foo(3); // obj3.foo = [Odd2.foo, Even2.foo]
        // obj4.foo(4); // obj4.foo = [Odd2.foo, Even2.foo]
        return obj3;
    }

    public int foo(int p) {
        return p;
    }
}

class Odd1 {
    public int foo(int p) {
        return p;
    }
}

class Even1 extends Odd1 {
    public int foo(int p, int q) {
        return p;
    }
}

class Odd2 extends Even1 {
    public int foo(int p) {
        return p;
    }
}

class Even2 extends Odd2 {
    public int foo(int p) {
        return p;
    }
}
