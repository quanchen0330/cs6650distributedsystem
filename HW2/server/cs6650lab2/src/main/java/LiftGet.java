public class LiftGet {
    public int resort;
    public String season;
    public int day;
    public int skier;

    public int getResort() {
        return resort;
    }

    public void setResort(int resort) {
        this.resort = resort;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getSkier() {
        return skier;
    }

    public void setSkier(int skier) {
        this.skier = skier;
    }




        public LiftGet(int resort, String season, int day,int skier) {
            this.resort = resort;
            this.season = season;
            this.day = day;
            this.skier = skier;
        }

}
