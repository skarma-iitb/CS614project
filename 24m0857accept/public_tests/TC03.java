class TC03 {
    public static void main(String[] args) {
        TestTC03 o;
        int res;
        o = new TestTC03();
        res = o.foo();
        System.out.println(res);
    }
}

class TestTC03 {
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
        while (z) {
            if (z) {
                c = a + b;
                d = c + a;
            }
            // } else {
            // c = a - b;
            // d = c * a;
            // }
            // c = a + b;
        }
        return d;
    }
}
