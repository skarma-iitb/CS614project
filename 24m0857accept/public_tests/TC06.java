class TC06 {
    public static void main(String[] args) {

    }
}

class A {
    int x; // Instance variable of A

    public int foo(int y) {
        int a;
        int b;
        int c;

        int[] arr;
        int len;
        int index;
        B obj;
        boolean z;

        obj = new B(); // Create an instance of B
        len = 5;
        a = 100;
        arr = new int[len]; // Initialize an array of size 5
        a = len;
        b = len;
        x = 1;
        obj.x = a; // Assigning 'a' to obj's 'x'
        index = 0;
        len = a + b; // len = 5 + 5 = 10

        z = a != b; // z = false since a == b (both are 5)
        b = arr[index]; // b = arr[0] -> 0
        c = arr[x]; // c = arr[1] -> 2

        if (z) {
            a = y;
        } else {
            System.out.println(a);
        }

        return a;
    }
}

class B {
    int x; // Instance variable of B
}
