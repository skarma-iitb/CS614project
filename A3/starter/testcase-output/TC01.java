// /*1*/
// import static a3.Memory.*;

// class TC01 {
//   public static void main(String[] args) {
//     Object R0;
//     alloca(0);
//     R0 = new TestTC01();
//     R0 = ((TestTC01) R0).foo();
//     System.out.println(((int) R0));
//   }
// }

// class TestTC01 {
//     public int foo() {
//         Object R0;
//         alloca(2);
//         store(1, 1); // a = 1; --> a was spilled to 1
//         store(0, 2); // b = 2; --> b was spilled to 0
//         R0 = ((int) load(1)) <= ((int) load(0)); // e = a <= b;
//         while(((boolean) R0)) {
//           store(0, ((int) load(1)) + ((int) load(0))); // b = a + b;
//           R0 = ((int) load(0)) <= ((int) load(1)); // b <= a;
//         }
//         store(0, ((int) load(0))); // b = b;
//         R0 = 2; // x = 2
//         store(0, ((int) R0));  // b = x;
//         return ((int) load(0)); // return b;
//     }
// }
/*0*/
// import static a3.Memory.*;

// class TC01 {
//   public static void main(String[] args) {
//     alloca(2);
//     store(0, new TestTC01());
//     store(1, ((TestTC01) load(0)).foo());
//     System.out.println(((int) load(1)));
//   }
// }

// class TestTC01 {
//   public int foo() {
//     alloca(4);
//     store(0, 1);
//     store(1, 2);
//     store(3, ((int) load(0)) <= ((int) load(1)));
//     while (((boolean) load(3))) {
//       store(1, ((int) load(0)) + ((int) load(1)));
//       store(3, ((int) load(1)) <= ((int) load(0)));
//     }
//     store(1, ((int) load(1)));
//     store(2, 2);
//     store(1, ((int) load(2)));
//     return ((int) load(1));
//   }
// }
/*0*/
import static a3.Memory.*;

class TC05 {
 public static void main(String[] args) {
    alloca(3);
 store(0,new TestTC01());
 store(1, ((TestTC01) load(0)).foo());
 store(2, ((TestTC01) load(0)).foo() ((TestTC01) load(0)).foo());
    System.out.println( ((int) load(1)));
    System.out.println( ((int) load(2)));
 }
}

class TestTC01 {
  public int foo() {
    alloca(6);
    store(0, 1);
    store(1, 4);
    store(3, 2);
    store(4, new int[((int) load(1))]);
    store(5, ((int) load(0)) <= ((int) load(1)));
    ((int) load(4))[((int) load(0))] = ((int) load(0));
    ((int) load(4))[((int) load(3))] = ((int) load(1));
    while (((boolean) load(5))) {
      store(1, ((int) load(0)) + ((int) load(1)));
      store(5, ((int) load(1)) <= ((int) load(0)));
    }
    store(1, ((int) load(1)));
    store(2, 2);
    store(1, ((int) load(2)));
    System.out.println(((int) load(4)));
    return ((int) load(1));
  }
}