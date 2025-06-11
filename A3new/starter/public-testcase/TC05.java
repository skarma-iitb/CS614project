/*0*/
class TC05 {
    public static void main(String[] args) {
        TestTC01 o;
        int res;
        int w;
        // w = new int[1]; // Initialize the array with a size of 10
        o = new TestTC01();
        res = o.foo();
        w = o.foo();
        System.out.println(res);
        System.out.println(w);
    }
}

class TestTC01 {
    public int foo() {
        int a;
        int b;
        int x;
        int m;
        int[] q;
        boolean e;

        a = 1;
        b = 4;
        m = 2;
        q = new int[b];
        e = a <= b;
        q[a] = a;
        q[m] = b;
        while (e) {
            b = a + b;
            e = b <= a;
        }
        b = b;
        x = 2;
        b = x;
        System.out.println(q);
        return b;
    }

}
