import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
    static AtomicInteger phase1Counter = new AtomicInteger();
    static AtomicInteger phase2Counter = new AtomicInteger();
    static AtomicInteger phase3Counter = new AtomicInteger();
    static AtomicInteger getCounter = new AtomicInteger();
}
