public class LiftRideBody {
        public String time;
        public int liftId;
        public int vertical;

    public int getVertical() {
        return vertical;
    }

    public void setVertical(int vertical) {
        this.vertical = vertical;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLiftId() {
        return liftId;
    }

    public void setLiftId(int liftId) {
        this.liftId = liftId;
    }


    public LiftRideBody(String time, int liftId) {
            this.time = time;
            this.liftId = liftId;
    }

}
