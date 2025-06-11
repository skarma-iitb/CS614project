
/*0*/
import static a3.Memory.*;

public class out3 {
 public static void main(String[] args) {
    alloca(3);
    store(8,new TestTC01());
    store(0, ((TestTC01) load(8)).foo());
    store(5, ((TestTC01) load(8)).foo() ((TestTC01) load(8)).foo());
    System.out.println( ((int) load(0)));
    System.out.println( ((int) load(5)));
 }
}

class TestTC01 {
 public int foo(){
  alloca(6);
    store(1,1);
    store(3,4);
    store(7,2);
    store(2,new int [ ((int) load(3))]);
    store(4, ((int) load(1)) <=  ((int) load(3)));
    ((null) load(2))[ ((int) load(1))] =  ((int) load(1)); 
    ((null) load(2))[ ((int) load(7))] =  ((int) load(3));  while( ((boolean) load(4))){
    store(3, ((int) load(1)) +  ((int) load(3)));
    store(4, ((int) load(3)) <=  ((int) load(1)));
    }
    store(3, ((int) load(3)));
    store(6,2);
    store(3, ((int) load(6)));
    System.out.println( ((null) load(2)));
  return  ((int) load(3));
 }
}
