public class A {
    private String message = "Hello, World!";

    public A() {
        this.message = "Hello, World!";
    }

    private void printMessage() {
        System.out.println(message);
    }

    public static void main(String[] args) {
        A hello = new A();
        hello.printMessage();
    }
}
