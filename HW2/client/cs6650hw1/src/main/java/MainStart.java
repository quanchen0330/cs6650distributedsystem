import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import com.opencsv.CSVWriter;
public class MainStart {
    public static void main(String[] args) throws IOException,InterruptedException {
        Resorts resorts1 = new Resorts(Integer.valueOf(args[0]),Integer.valueOf(args[1]),Integer.valueOf(args[2]),Integer.valueOf(args[3]),4,0.1);
        Resorts resorts2 = new Resorts(Integer.valueOf(args[0]),Integer.valueOf(args[1]),Integer.valueOf(args[2]),Integer.valueOf(args[3]),1,0.8);
        Resorts resorts3 = new Resorts(Integer.valueOf(args[0]),Integer.valueOf(args[1]),Integer.valueOf(args[2]),Integer.valueOf(args[3]),4,0.1);
        Address address = new Address(args[4],Integer.valueOf(args[5]));

        CSVGenerator csvGenerator = new CSVGenerator();
        CSVWriter writer = csvGenerator.getWriter();


        List<Long> listOfLatency = new ArrayList<>();

        CountDownLatch latch1 = new CountDownLatch((resorts1.getNumThreads()/4)/10);
        CountDownLatch latch2 = new CountDownLatch(resorts1.getNumThreads()/10);
        CountDownLatch latch3 = new CountDownLatch(resorts1.getNumThreads()/4);

        ConcurrentLinkedQueue set1 = new ConcurrentLinkedQueue();
        ConcurrentLinkedQueue set2 = new ConcurrentLinkedQueue();
        ConcurrentLinkedQueue set3 = new ConcurrentLinkedQueue();


        System.out.println("phase1 start");
        long start = System.currentTimeMillis();

        ExecutorService executorService1 = Executors.newFixedThreadPool(resorts1.getThreadToGo());
        //phase1
        Sender sender1 = new Sender(1, 90,"phase1", latch1, Counter.phase1Counter, writer,set1,address,resorts1);
        for(int i =0; i<resorts1.getThreadToGo();i++){
            executorService1.execute(sender1);
        }
        latch1.await();

        System.out.println("phase2 start");

        ExecutorService executorService2 = Executors.newFixedThreadPool(resorts2.getThreadToGo());
        //phase2
        Sender sender2 = new Sender(91, 360,"phase2", latch2, Counter.phase2Counter, writer,set2,address,resorts2);
        for(int i =0; i<resorts2.getThreadToGo();i++){
            executorService2.execute(sender2);
        }
        latch2.await();

        System.out.println("phase3 start");

        ExecutorService executorService3 = Executors.newFixedThreadPool(resorts3.getThreadToGo());
        Sender sender3 = new Sender(361, 420, "phase3", latch3, Counter.phase3Counter, writer,set3,address,resorts3);
        for(int i=0; i<resorts3.getThreadToGo();i++){
            executorService3.execute(sender3);
        }
        latch3.await();

        executorService1.shutdown();
        executorService2.shutdown();
        executorService3.shutdown();
        //wait for threadpool2
        try {
            executorService2.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("All threads finished");
        long end = System.currentTimeMillis();


        listOfLatency.addAll(set1);
        listOfLatency.addAll(set2);
        listOfLatency.addAll(set3);

        int phase1Count = Counter.phase1Counter.get();
        int phase2Count = Counter.phase2Counter.get();
        int phase3Count = Counter.phase3Counter.get();

        double rate =  ((double) (phase1Count + phase2Count + phase3Count)/ (double) 400000);

        Collections.sort(listOfLatency);

        long sum = 0;

        for(long e : listOfLatency) sum += e;

        long mean = sum / listOfLatency.size();



        System.out.println("The total running time "+ (end-start)/1000 + " s");
        System.out.println("Now we have" + Integer.valueOf(args[0]) + "threads");

        System.out.println("Phase1 has totally" + phase1Count + "successful requests to server");
        System.out.println("phase2 has totally" + phase2Count + "successful requests to server");
        System.out.println("phase3 has totally" + phase3Count + "successful requests to server");
        System.out.println("The total successful response number is " + (phase1Count + phase2Count + phase3Count));


        System.out.println("The Overall throughput across all phases: " + String.format("%.01f",(double)((phase1Count + phase2Count + phase3Count)) /((end-start)/1000)));
        System.out.println("The Mean latency: " + mean + " ms");
        System.out.println("The Median latency: " + listOfLatency.get(listOfLatency.size()/2) + " ms");
        System.out.println("The 99th percentile latency: " + listOfLatency.get(listOfLatency.size() * 99 / 100) + " ms");
        System.out.println("The Max response time"+ Collections.max(listOfLatency)+"ms");
        System.out.println("the successful post rate is " + rate + " %");
    }
}