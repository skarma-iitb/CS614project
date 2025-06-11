class TC04 {
    public static void main(String[] args) {
        TestTC04 o;
        int res;
        o = new TestTC04();
        res = o.foo();
        System.out.println(res);
    }
}

class TestTC04 {
    public int foo() {
        int a;
        int b;
        int c;
        int d;
        boolean z;
        a = 5;
        b = 6;
        z = true;
        d = 7;

        if (z) {
            c = a + b;
            d = c + a;
        } else {
            c = a - b;
            d = c * a;
        }

        return d;
    }
}
