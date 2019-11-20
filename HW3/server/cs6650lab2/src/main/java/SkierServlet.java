import com.google.gson.Gson;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SkierServlet extends javax.servlet.http.HttpServlet {

    private int count = 0;

    protected void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res) throws javax.servlet.ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();
        PrintWriter out = res.getWriter();
        req.setCharacterEncoding("UTF-8");
        LiftRideDao liftRideDao = new LiftRideDao();

        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("Parameters Missing");
            return;
        }

        if (!validate(urlPath)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            res.setStatus(HttpServletResponse.SC_OK);
            LiftRide newLiftRide = generate(req);

            DataSource pool = (DataSource) req.getServletContext().getAttribute("my-pool");

            try (Connection conn = pool.getConnection()) {
                // PreparedStatements can be more efficient and project against injections.
                PreparedStatement voteStmt = conn.prepareStatement(
                        "INSERT INTO LiftRide (skierId, resortId, seasonId, dayId, curTime, liftId, vertical) VALUES (?,?,?,?,?,?,?)");
                voteStmt.setInt(1, newLiftRide.getSkierId());
                voteStmt.setInt(2, newLiftRide.getResortId());
                voteStmt.setString(3, newLiftRide.getSeasonId());
                voteStmt.setInt(4, newLiftRide.getDayId());
                voteStmt.setString(5, newLiftRide.getTime());
                voteStmt.setInt(6, newLiftRide.getLiftID());
                voteStmt.setInt(7, newLiftRide.getVertical());
                // Finally, execute the statement. If it fails, an error will be thrown.
                voteStmt.execute();

            } catch (SQLException ex) {
                // If something goes wrong, handle the error in this section. This might involve retrying or
                // adjusting parameters depending on the situation.
                // [START_EXCLUDE]
                //LOGGER.log(Level.WARNING, "Error while attempting to submit vote.", ex);
                res.setStatus(500);
                res.getWriter().write("Unable to successfully insert! Please check the application "
                        + "logs for more details.");
                // [END_EXCLUDE]
            }




//            try {
//                liftRideDao.insert(newLiftRide, count);
//                count++;
//            }  catch (SQLException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            res.getWriter().write(newLiftRide.toString());
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res) throws javax.servlet.ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        if (urlPath == null || urlPath.isEmpty()) {
            PrintWriter out = res.getWriter();
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing parameters");
            return;
        }

        if (!validate(urlPath)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            res.setStatus(HttpServletResponse.SC_OK);
//            String urlPath2= req.getPathInfo();
//            String[] urlParts = urlPath2.split("/");
//            int resortId = Integer.parseInt(urlParts[1]);
//            String seasonId = urlParts[3];
//            int day = Integer.parseInt(urlParts[5]);
//            int skierId = Integer.parseInt(urlParts[7]);
//            LiftGet liftGet = new LiftGet(resortId,seasonId,day,skierId);
//            LiftRideDao liftRideDao = new LiftRideDao();

            int skierId = 0;
            int resortId = 0;
            String seasonId = null;
            int dayId = 0;
            String curTime = null;
            int liftId = 0;
            int vertical = 0;

            DataSource pool = (DataSource) req.getServletContext().getAttribute("my-pool");
            String[] urlParts = urlPath.split("/");

            String selectQueryStatement = "SELECT * FROM LiftRides.LiftRide WHERE skierId =?"
                    +" AND resortId = ?"
                    +" AND seasonId = ?"
                    + " AND dayId = ?";

            try (Connection conn = pool.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(selectQueryStatement);
                ps.setInt(1, Integer.parseInt(urlParts[1]));
                ps.setInt(2, Integer.parseInt(urlParts[3]));
                ps.setString(3, urlParts[5]);
                ps.setInt(4, Integer.parseInt(urlParts[7]));

                ResultSet resultSet = ps.executeQuery();

                while (resultSet.next()) {
                    skierId = resultSet.getInt("skierId");
                    resortId = resultSet.getInt("resortId");
                    seasonId = resultSet.getString("seasonId");
                    dayId = resultSet.getInt("dayId");
                    curTime = resultSet.getString("curTime");
                    liftId = resultSet.getInt("liftId");
                    vertical = resultSet.getInt("vertical");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            LiftRide liftride = new LiftRide(skierId,resortId,seasonId,dayId,curTime,liftId,vertical);

            res.getWriter().write(liftride.toString());



        }


    }

    private LiftRide generate(javax.servlet.http.HttpServletRequest req){
        String urlPath = req.getPathInfo();
        String[] urlParts = urlPath.split("/");
        int resortId = Integer.parseInt(urlParts[1]);
        String seasonId = urlParts[3];
        int day = Integer.parseInt(urlParts[5]);
        int skierId = Integer.parseInt(urlParts[7]);


        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = req.getReader();
            line = reader.readLine();

        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String data = sb.toString();
        try {
            LiftRideBody body = new Gson().fromJson(data, LiftRideBody.class);
            String time = body.time;
            int liftId = body.liftId;
            int vertical = body.vertical;

            return new LiftRide(skierId, resortId, seasonId, day, time, liftId, vertical);
        }catch (Exception e){

        }
        return null;
    }

    private boolean validate(String urlPath) {
        // /{resortId}/seasons/{seasonId}/days/{dayId}/skiers/{skierId}
        String[] urlParts = urlPath.split("/");
        if (urlParts.length < 8) {
            return false;
        }
        try {
            int resorts = Integer.parseInt(urlParts[1]);
            int seasonId = Integer.parseInt(urlParts[3]);
            int day = Integer.parseInt(urlParts[5]);
            int skier = Integer.parseInt(urlParts[7]);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }
}
