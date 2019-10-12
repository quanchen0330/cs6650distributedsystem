import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import com.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Sender implements Runnable {

    int start;
    int end;
    String name;
    CountDownLatch countDownLatch;
    Resorts resorts;
    Counter counter;
    CSVWriter csvWriter;
    List<Long> latencylist;
    Address address;


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

    public Counter getCounter() {
        return counter;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
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




    public Sender(int start, int end,String name, CountDownLatch countDownLatch, Counter counter, CSVWriter csvWriter, List<Long> latencylist, Address address,Resorts resorts){
        this.start = start;
        this.end = end;
        this.countDownLatch = countDownLatch;
        this.counter = counter;
        this.csvWriter = csvWriter;
        this.latencylist = latencylist;
        this.address = address;
        this.resorts =resorts;
    }

    @Override
    public void run(){
        try {
            send(start,end,counter,csvWriter,address,resorts);
            System.out.println(Thread.currentThread().getName()+"SENT");
            countDownLatch.countDown();
        } catch (Exception e){

        }
    }

    private void send(int start, int end, Counter counter, CSVWriter csvWriter,Address address,Resorts resorts){
        double numPosts = resorts.getNumPost();
        for(int i=0; i<numPosts; i++){
            try {
                sendPost(start,end,i,counter,csvWriter,address,resorts);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void sendPost(int start, int end,int index, Counter counter, CSVWriter csvWriter, Address address,Resorts resorts)throws IOException{
        long startTime = System.currentTimeMillis();

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            Random rd = new Random();
            int numLifts = resorts.getNumLifts();
            if(numLifts ==0)
                numLifts = 40;
            int skierRange = resorts.getSkierRange();
            int skierID = rd.nextInt(skierRange)+(index*skierRange);
            //int listID = rd.nextInt(numLifts);
            int timeforThread = rd.nextInt(end-start) + start;


            HttpPost request = new HttpPost("http://" + address.getIp()+address.getIp()+ "/skiers/" + skierID + "/seasons/2019/day/1/skier/" + skierID);
            request.setHeader("User-Agent", "Java client");

            HttpResponse response = client.execute(request);

            if(response.getStatusLine().getStatusCode()==200) {
                counter.requestSuccess();
            } else {
                counter.requestFail();
            }

            BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                   response.getEntity().getContent()));

            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = bufReader.readLine()) != null) {
                builder.append(line);
                builder.append(System.lineSeparator());
            }
            System.out.println(builder);

            long endTime = System.currentTimeMillis();

            long latency = endTime - startTime;
            this.latencylist.add(latency);

            String[] data = { timeforThread + "", "POST", latency + "", response.getStatusLine().getStatusCode() + "" };
            csvWriter.writeNext(data);


        }

    }
}
