class TC07 {
    public static void main(String[] args) {
        TestTC02 o;
        int res;
        o = new TestTC02();
        res = o.foo();
        System.out.println(res);
    }
}

class TestTC02 {
    public int foo() {
        int a;
        int b;
        int c;
        int d;
        boolean z;
        TestTC02 o;
        a = 1;
        b = 2;
        o = new TestTC02();
        c = a + b;
        d = c + a;
        d = o.bar(a, b, c);
        return d;
    }

    public int bar(int a, int b, int c) {
        a = b + c;
        return a;
    }
}

class A extends TestTC02 {
    public int bar(int a, int b, int c) {
        a = b + c;
        return a;
    }

    public int fun() {
        int a;
        int b;
        int c;
        int d;
        boolean z;
        TestTC02 o;
        a = 1;
        b = 2;
        o = new TestTC02();
        c = a + b;
        d = c + a;
        d = o.bar(a, b, c);
        return d;
    }
}
