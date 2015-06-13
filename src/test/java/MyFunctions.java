import me.evthoriz.utils.*;

/**
 * Created by evtHoriz on 15/6/4.
 */
public class MyFunctions {


    @Timeit(count = 2, timeout = 3000)
    public void timeout1() {
        while (true);
    }

    @Timeit(count = 1, timeout = 1000)
    public void timeout0() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Timeit(count = 1, timeout = 5000)
    public void timeok1() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Timeit(count = 2, timeout = 7000)
    public void timeok2() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            TimeitCore.setMutithread(true);
            TimeitCore.start(MyFunctions.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

