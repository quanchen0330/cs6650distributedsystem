import com.google.gson.Gson;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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

            try {
                liftRideDao.insert(newLiftRide, count);
                count++;
            }  catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            res.getWriter().write(newLiftRide.toString());
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
            String urlPath2= req.getPathInfo();
            String[] urlParts = urlPath2.split("/");
            int resortId = Integer.parseInt(urlParts[1]);
            String seasonId = urlParts[3];
            int day = Integer.parseInt(urlParts[5]);
            int skierId = Integer.parseInt(urlParts[7]);
            LiftGet liftGet = new LiftGet(resortId,seasonId,day,skierId);
            LiftRideDao liftRideDao = new LiftRideDao();
            try {
                res.getWriter().write(liftRideDao.select(liftGet).toString());
            } catch (SQLException e){

            }

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
