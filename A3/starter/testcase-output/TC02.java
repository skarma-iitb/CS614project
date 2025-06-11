// /*2*/
// import static a3.Memory.*;

// class TC02 {
// public static void main(String[] args) {
// Object R0;
// Object R1;
// alloca(0);
// R0 = new TestTC02();
// R0 = ((TestTC02) R0).foo();
// System.out.println(((int) R0));
// }
// }

// class TestTC02 {
// public int foo() {
// Object R0;
// Object R1;
// alloca(1);
// R0 = 5; // R0 = a
// R1 = 6; // R1 = b
// R1 = ((int) R0) + ((int) R1); // R0 = a, R1 = c
// store(0, ((int) R1) + ((int) R0)); // SPILL D to 0
// R0 = ((int) R0) - ((int) R1); // R0 = e, R1 = c
// R0 = ((int) load(0)) - ((int) R0); // R0 = t, R1 = c
// return ((int) R0);
// }
// }
/* 2 */
// import static a3.Memory.*;

// class TC02 {
// public static void main(String[] args) {
// Object R0;
// Object R1;
// alloca(0);
// R0 = new TestTC02();
// // R1 = ((TestTC02) R0).foo();
// // System.out.println(((int) R1));
// // }
// // }

// // class TestTC02 {
// // public int foo() {
// // Object R0;
// // Object R1;
// // alloca(1);
// // R0 = 5;
// // R1 = 6;
// // R1 = ((int) R0) + ((int) R1);
// // store(0, ((int) R1) + ((int) R0));
// // R0 = ((int) R0) - ((int) R1);
// // R1 = ((int) load(0)) - ((int) R0);
// // return ((int) R1);
// // }
// // }
// /*2*/
// import static a3.Memory.*;

// class TC02 {
//     public static void main(String[] args) {
//         Object R0;
//         Object R1;
//         alloca(0);
//         R0 = new TestTC02();
//         R1 = ((TestTC02) R0).foo();
//         System.out.println(((int) R1));
//     }
// }

// class TestTC02 {
//     public int foo() {
//         Object R0;
//         Object R1;
//         alloca(1);
//         R0 = 5;
//         R1 = 6;
//         R1 = ((int) R0) + ((int) R1);
//         store(0, ((int) R1) + ((int) R0));
//         R0 = ((int) R0) - ((int) R1);
//         R1 = ((int) load(0)) - ((int) R0);
//         return ((int) R1);
//     }
// }
/*0*/
import static a3.Memory.*;

class TC02 {
    public static void main(String[] args) {
        alloca(2);
        store(0, new TestTC01());
        store(1, ((TestTC01) load(0)).foo());
        System.out.println(((int) load(1)));
    }
}

class TestTC01 {
    public int foo() {
        alloca(4);
        store(0, 1);
        store(1, 2);
        store(3, ((int) load(0)) <= ((int) load(1)));
        while (((boolean) load(3))) {
            store(1, ((int) load(0)) + ((int) load(1)));
            store(3, ((int) load(1)) <= ((int) load(0)));
        }
        store(1, ((int) load(1)));
        store(2, 2);
        store(1, ((int) load(2)));
        return ((int) load(1));
    }
}