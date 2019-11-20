import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import com.opencsv.CSVWriter;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Sender implements Runnable {

    int start;
    int end;
    String name;
    CountDownLatch countDownLatch;
    Resorts resorts;
    AtomicInteger count;
    CSVWriter csvWriter;
    List<Long> latencylist;
    Address address;
    ConcurrentLinkedQueue set;
    RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(10000).setConnectionRequestTimeout(10000).setSocketTimeout(10000).setStaleConnectionCheckEnabled(true).build();
    Gson gson = new Gson();


    public Resorts getResorts(){
        return this.resorts;
    }
    public void setResorts(Resorts resorts){
        this.resorts = resorts;
    }
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public CSVWriter getCsvWriter() {
        return csvWriter;
    }

    public void setCsvWriter(CSVWriter csvWriter) {
        this.csvWriter = csvWriter;
    }

    public List<Long> getLatencylist() {
        return latencylist;
    }

    public void setLatencylist(List<Long> latencylist) {
        this.latencylist = latencylist;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public void setCount(AtomicInteger count) {
        this.count = count;
    }

    public Sender(int start, int end, String name, CountDownLatch countDownLatch, AtomicInteger count, CSVWriter csvWriter, ConcurrentLinkedQueue set, Address address, Resorts resorts){
        this.start = start;
        this.end = end;
        this.countDownLatch = countDownLatch;
        this.count = count;
        this.csvWriter = csvWriter;
        this.set = set;
        this.address = address;
        this.resorts =resorts;
    }

    @Override
    public void run(){
        try {
            send(start,end,count,csvWriter,address,resorts);
            System.out.println(Thread.currentThread().getName()+"SENT");

            countDownLatch.countDown();
        } catch (Exception e){

        }
    }

    private void send(int start, int end, AtomicInteger count, CSVWriter csvWriter,Address address,Resorts resorts){
        double numPosts = resorts.getNumPost();
        for(int i=0; i<numPosts; i++){
            try {
                sendPost(start,end,i,count,csvWriter,address,resorts);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendGet(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            HttpResponse response = client.execute(get);
            int internResponseStatus = response.getStatusLine().getStatusCode();

            if(internResponseStatus == 200) {
                Counter.getCounter.getAndIncrement();
            }

        } finally {
            get.abort();
        }
    }

    private void sendPost(int start, int end,int index, AtomicInteger count, CSVWriter csvWriter, Address address,Resorts resorts)throws IOException{
        long startTime = System.currentTimeMillis();

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            int numLifts = resorts.getNumLifts();
            if(numLifts == 0) numLifts = 40;
            int skierRange = resorts.getSkierRange();
            int skierID =  ThreadLocalRandom.current().nextInt(skierRange) + (index * skierRange);
            int resortId = ThreadLocalRandom.current().nextInt(40);
            int day = ThreadLocalRandom.current().nextInt(30);
            int seasonId = ThreadLocalRandom.current().nextInt(10);
            int timeforThread = ThreadLocalRandom.current().nextInt(end-start) + start;

            //String url = "http://localhost:8080/skiers/" + resortId + "/seasons/" + seasonId + "/day/" + day + "/skier/" + skierID;
            String url = "http://"+address.getIp()+":" + address.getPort() + "/assignment1_war/skiers/" + resortId + "/seasons/" + seasonId + "/day/" + day + "/skier/" + skierID;
            HttpPost request = new HttpPost(url);
            request.setHeader("User-Agent", "Java client");


            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("HH:mm:ss a");
            String time = sdf.format(new Date());
            int liftId = ThreadLocalRandom.current().nextInt(40);
            int vertical = liftId * 10;

            PostBody postBody = new PostBody(time, liftId, vertical, System.currentTimeMillis());
            if (this.name.equals("phase3")) {
                sendGet(url);
            }

            StringEntity postingString = new StringEntity(gson.toJson(postBody));
            request.setHeader("User-Agent", "Java client");
            request.setEntity(postingString);
            request.setConfig(config);

            HttpResponse response = client.execute(request);

            if(response.getStatusLine().getStatusCode() == 200) {
                count.getAndIncrement();
            }

            HttpEntity entity =  response.getEntity();

            String result= EntityUtils.toString(entity, "UTF-8");

            System.out.println("Response from server: " + result);

            long endTime = System.currentTimeMillis();

            long latency = endTime - startTime;
            this.set.add(latency);

            String[] data = { timeforThread + "", "POST", latency + "", response.getStatusLine().getStatusCode() + "" };

            csvWriter.writeNext(data);


        }

    }
}
