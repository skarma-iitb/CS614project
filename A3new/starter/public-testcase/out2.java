
/*0*/
// import static a3.Memory.*;

// class out2 {
//     public static void main(String[] args) {
//         alloca(2);
//         store(7, new TestTC02());
//         store(0, ((TestTC02) load(7)).foo());
//         System.out.println(((int) load(0)));
//     }
// }

// class TestTC02 {
//     public int foo() {
//         alloca(6);
//         store(1, 5);
//         store(2, 6);
//         store(3, ((int) load(1)) + ((int) load(2)));
//         store(4, ((int) load(3)) + ((int) load(1)));
//         store(6, ((int) load(1)) - ((int) load(3)));
//         store(5, ((int) load(4)) - ((int) load(6)));
//         return ((int) load(5));
//     }
// }
/*1*/
import static a3.Memory.*;

class out2 {
    public static void main(String[] args) {
        Object R0;
        alloca(3);
        R0 = 1;
        store(0, 2);
        store(1, 3);
        store(2, new TestTC01());
        R0 = ((TestTC01) load(2)).foo(((int) R0), ((int) load(0)), ((int) load(1)));
        System.out.println(((int) R0));
    }
}

class TestTC01 {
    public int foo(int i, int j, int l) {
        Object R0;
        alloca(2);
        store(4, 1);
        store(3, 2);
        R0 = ((int) load(4)) <= ((int) load(3));
        while (((boolean) R0)) {
            store(3, ((int) load(4)) + ((int) load(3)));
            R0 = ((int) load(3)) <= ((int) load(4));
        }
        store(3, ((int) load(3)));
        R0 = 2;
        store(3, ((int) R0));
        return ((int) load(3));
    }
}