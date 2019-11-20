public class PostBody {
        String time;
        int liftId;
        int vertical;
        Long curTime;


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

        public int getVertical() {
            return vertical;
        }

        public void setVertical(int vertical) {
            this.vertical = vertical;
        }

        public Long getCurTime() {
            return curTime;
        }

        public void setCurTime(Long curTime) {
            this.curTime = curTime;
        }

        public PostBody(String time, int liftId, int vertical, Long curTime) {
            this.time = time;
            this.liftId = liftId;
            this.vertical = vertical;
            this.curTime = curTime;
        }

}
