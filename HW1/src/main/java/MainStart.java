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

        List<Long> perLatency1 = new ArrayList< >();
        List<Long> perLatency2 = new ArrayList< >();
        List<Long> perLatency3 = new ArrayList< >();
        List<Long> listOfLatency = new ArrayList<>();

        Counter counter1 = new Counter(0,0);
        Counter counter2 = new Counter(0,0);
        Counter counter3 = new Counter(0,0);

        CountDownLatch latch1 = new CountDownLatch((resorts1.getNumThreads()/4)/10);
        CountDownLatch latch2 = new CountDownLatch(resorts1.getNumThreads()/10);
        CountDownLatch latch3 = new CountDownLatch(resorts1.getNumThreads()/4);


        System.out.println("phase1 start");
        long start = System.currentTimeMillis();

        ExecutorService executorService1 = Executors.newFixedThreadPool(resorts1.getThreadToGo());
        //phase1
        Sender sender1 = new Sender(1, 90,"phase1", latch1, counter1, writer,perLatency1,address,resorts1);
        for(int i =0; i<resorts1.getThreadToGo();i++){
            executorService1.execute(sender1);
        }
        latch1.await();

        System.out.println("phase2 start");

        ExecutorService executorService2 = Executors.newFixedThreadPool(resorts2.getThreadToGo());
        //phase2
        Sender sender2 = new Sender(91, 360,"phase2", latch2, counter2, writer,perLatency2,address,resorts2);
        for(int i =0; i<resorts2.getThreadToGo();i++){
            executorService2.execute(sender2);
        }
        latch2.await();

        System.out.println("phase3 start");

        ExecutorService executorService3 = Executors.newFixedThreadPool(resorts3.getThreadToGo());
        Sender sender3 = new Sender(361, 420, "phase3", latch3, counter3, writer,perLatency3,address,resorts3);
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

        System.out.println("All done");
        long end = System.currentTimeMillis();


        listOfLatency.addAll(sender1.getLatencylist());
        listOfLatency.addAll(sender2.getLatencylist());
        listOfLatency.addAll(sender3.getLatencylist());

        Collections.sort(listOfLatency);

        long sum = 0;

        for(long e : listOfLatency) sum += e;

        long mean = sum / listOfLatency.size();


        int scount1 = counter1.getSuccessReq();
        int scount2 = counter2.getSuccessReq();
        int scount3 = counter3.getSuccessReq();
        int fcount1 = counter1.getFailReq();
        int fcount2 = counter2.getFailReq();
        int fcount3 = counter3.getFailReq();
        int stotalcount = scount1+scount2+scount3;
        int ftotalcount = fcount1+fcount2+fcount3;

        System.out.println("Phase1 has " + scount1 + "successful requests");
        System.out.println("phase2 has " + scount2 + "successful requests");
        System.out.println("phase3 has " + scount3 + "successful requests");
        System.out.println("The total successful response number is " + stotalcount);
        System.out.println("Phase1 has " + fcount1 + "unsuccessful requests");
        System.out.println("phase2 has " + fcount2 + "unsuccessful requests");
        System.out.println("phase3 has " + fcount3 + "unsuccessful requests");
        System.out.println("The total unsuccessful requests number is " + ftotalcount);
        System.out.println("The total run time for all phases finished is "+ (end-start)/1000 + " seconds");
        System.out.println("Overall throughput across all phases: " + String.format("%.01f",(double)(stotalcount) /((end-start)/1000)));
        System.out.println("Mean latency: " + mean + " ms");
        System.out.println("Median latency: " + listOfLatency.get(listOfLatency.size()/2) + " ms");
        System.out.println("99th percentile latency: " + listOfLatency.get(listOfLatency.size() * 99 / 100) + " ms");
        System.out.println("max response time"+ Collections.max(listOfLatency)+"ms");
    }
}