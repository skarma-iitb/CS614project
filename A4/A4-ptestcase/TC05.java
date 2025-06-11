class TC05 {
    public static void main(String[] args) {
        Exec e;
        int a;
        e = new Exec();
        a = e.exec();
    }
}

class Exec {
    public int exec() {
        int x;
        Odd1 obj1;
        Odd1 obj2;
        Odd2 obj3;
        Odd2 obj4;

        obj1 = new Odd1();
        obj2 = new Even1();
        obj3 = new Odd2();
        obj4 = new Even2();

        // x = obj1.foo(1); // obj1.foo = [Odd1.foo, Even1.foo, Odd2.foo, Even2.foo]
        // x = obj2.foo(2); // obj2.foo = [Odd1.foo, Even1.foo, Odd2.foo, Even2.foo]
        x = obj3.foo(3); // obj3.foo = [Odd2.foo, Even2.foo]
        x = obj4.foo(4); // obj4.foo = [Odd2.foo, Even2.foo]
        return 0;
    }
}

class Odd1 {
    public int foo(int p) {
        return 10;
    }
}

class Even1 extends Odd1 {
    public int foo(int p) {
        return 10;
    }
}

class Odd2 extends Even1 {
    // public int foo(int p) {
    // return 10;
    // }
}

class Even2 extends Odd2 {
    public int foo(int p) {
        return 10;
    }
}
