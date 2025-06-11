class TC05 {
    public static void main(String[] args) {
        TestTC05 o;
        int res;
        o = new TestTC05();
        res = o.foo();
        System.out.println(res);
    }
}

class TestTC05 {
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
                c = b;
            } else {
                c = a - b;
                d = c * a;
            }

        }
        return d;
    }
}
