public class Resorts {
    int numThreads;
    int numSkiers;
    Integer numLifts;
    int numRuns;
    int divisor;
    double factor;

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public int getNumSkiers() {
        return numSkiers;
    }

    public void setNumSkiers(int numSkiers) {
        this.numSkiers = numSkiers;
    }

    public Integer getNumLifts() {
        return numLifts;
    }

    public void setNumLifts(Integer numLifts) {
        this.numLifts = numLifts;
    }

    public int getNumRuns() {
        return numRuns;
    }

    public void setNumRuns(int numRuns) {
        this.numRuns = numRuns;
    }

    public int getDivisor() {
        return divisor;
    }

    public void setDivisor(int divisor) {
        this.divisor = divisor;
    }

    public double getFacotr() {
        return factor;
    }

    public void setFacotr(double facotr) {
        this.factor = facotr;
    }


    public Resorts(int numThreads, int numSkiers, Integer numLifts, int numRuns, int divisor, double factor){
        this.numThreads = numThreads;
        this.numSkiers = numSkiers;
        this.numLifts = numLifts;
        this.numRuns = numRuns;
        this.divisor = divisor;
        this.factor = factor;
    }

    public double getNumPost(){
        return (int)(numRuns*factor)*(numSkiers/(numThreads/divisor));
    }

    public int getSkierRange(){
        return  numSkiers/(numThreads/divisor);
    }

    public int getThreadToGo(){
        return numThreads/divisor;
    }

}
