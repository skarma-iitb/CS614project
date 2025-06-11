public class Test6 {
    public static void main(String[] args) {
    }
}

class A {
    int x;

    public void foo() {
        x = 10;
    }
}

class B extends A {
    int y;

    public void bar() {
        x = 20;
        y = 10;
    }

}
