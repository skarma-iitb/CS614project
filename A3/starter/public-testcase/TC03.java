/*3*/
class TC03 {
    public static void main(String[] args) {
        int a;
        int b;
        int c;
        int d;
        int e;
        int ans;
        A obj;
        a = 1;
        b = 2;
        c = a + b;
        d = b * c;
        e = c + d;
        a = d / e;
        obj = new A();
        ans = obj.foo(a, b, c, d);

    }

}

class A {
    public int foo(int a, int b, int c, int d) {
        int f;
        boolean s;
        s = a <= b;
        if (s) {
            f = 1;
        } else {
            f = 0;
        }
        return f;

    }
}
