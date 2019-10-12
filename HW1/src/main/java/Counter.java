public class Counter {
    int successReq;
    int failReq;

    public int getSuccessReq() {
        return successReq;
    }

    public void setSuccessReq(int successReq) {
        this.successReq = successReq;
    }

    public int getFailReq() {
        return failReq;
    }

    public void setFailReq(int failReq) {
        this.failReq = failReq;
    }


    public Counter(int successReq, int failReq){
        this.successReq = successReq;
        this.failReq = failReq;
    }

    synchronized public void requestSuccess(){
        try {
            this.successReq++;
        } catch (Exception e){

        }
    }

    synchronized public void requestFail(){
        try{
            this.failReq++;
        } catch (Exception e){

        }
    }
}
