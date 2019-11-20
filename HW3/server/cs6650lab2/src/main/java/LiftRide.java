import java.sql.Timestamp;

public class LiftRide {
    private int skierId;
    private int resortId;
    private String seasonId;
    private int dayId;
    private String curTime;
    private int liftId;
    private int vertical;

    public LiftRide(int skierId, int resortId, String seasonId, int dayId, String curTime, int liftId, int vertical) {
        this.skierId = skierId;
        this.resortId = resortId;
        this.seasonId = seasonId;
        this.dayId = dayId;
        this.curTime = curTime;
        this.liftId = liftId;
        this.vertical = vertical;
    }

    public int getSkierId() {
        return skierId;
    }

    public int getDayId() {
        return dayId;
    }

    public int getResortId() {

        return resortId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public String getTime() {

        return curTime;
    }

    public int getLiftID() {
        return liftId;
    }

    public int getVertical() {
        return vertical;
    }

    @Override
    public String toString() {
        return "SkierId is: "+this.skierId+"//ResortId is: "+ this.resortId+"//SeasonId is: "+this.seasonId+"//DayId is: "+this.dayId+"//Time is :" + this.curTime
                + "//LiftId is: " + this.liftId +"//Vertical is: " + this.vertical + "*";
    }
}