import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class LiftRideDao {
    final static private int PERINSERT = 6000;
    private static Connection conn;
    private static PreparedStatement preparedStatement;
    String insertQueryStatement = "INSERT INTO LiftRide (skierId, resortId, seasonId, dayId, curTime, liftId, vertical) " +
            "VALUES (?,?,?,?,?,?,?)";
    int skierId = 0;
    int resortId = 0;
    String seasonId = null;
    int dayId = 0;
    String curTime = null;
    int liftId = 0;
    int vertical = 0;

    public LiftRideDao() {
        try {

            conn = DBCPDataSource.getConnection();
            preparedStatement = null;
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            conn.setAutoCommit(false);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void insert(LiftRide lift, int count) throws ClassNotFoundException, SQLException, IOException {

        try {
            preparedStatement.setInt(1, lift.getSkierId());
            preparedStatement.setInt(2, lift.getResortId());
            preparedStatement.setString(3, lift.getSeasonId());
            preparedStatement.setInt(4, lift.getDayId());
            preparedStatement.setString(5, lift.getTime());
            preparedStatement.setInt(6, lift.getLiftID());
            preparedStatement.setInt(7, lift.getVertical());

            preparedStatement.addBatch();
            if (count > 0 && count % PERINSERT == 0) {
                preparedStatement.executeBatch();
                conn.commit();
                preparedStatement.clearBatch();
            }
            //REMAINING
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
        try {
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        }
    }

    public LiftRide select(LiftGet liftget) throws SQLException{
        String selectQueryStatement = "SELECT * FROM LiftRides.LiftRide WHERE skierId =? AND resortId = ? AND seasonId = ? AND dayId = ?";
        PreparedStatement ps = conn.prepareStatement(selectQueryStatement);

        ps.setInt(1, liftget.getSkier());
        ps.setInt(2,liftget.getResort());
        ps.setString(3,liftget.getSeason());
        ps.setInt(4,liftget.getDay());
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            skierId = rs.getInt("skierId");
            resortId = rs.getInt("resortId");
            seasonId = rs.getString("seasonId");
            dayId = rs.getInt("dayId");
            curTime = rs.getString("curTime");
            liftId = rs.getInt("liftId");
            vertical = rs.getInt("vertical");
        }
        return new LiftRide(skierId,resortId,seasonId,dayId,curTime,liftId,vertical);
    }
}