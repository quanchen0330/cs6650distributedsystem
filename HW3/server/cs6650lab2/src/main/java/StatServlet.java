import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class StatServlet extends javax.servlet.http.HttpServlet {
    private final static int TIME_OUT = 10000;
    protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res) throws javax.servlet.ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        String urlPath = req.getPathInfo();
        if (!validate(urlPath)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            res.setStatus(HttpServletResponse.SC_OK);
            int min = 0;
            int max = TIME_OUT;
            int total = 0;
            int size = 1;
            String[] path = urlPath.split("/");
            int N = Integer.parseInt(path[path.length - 1]);

            Jedis jedis = new Jedis("localhost");
            List<String> list = jedis.lrange("latency", 0, -1);
            size = list.size();
            List<String> rangeList = jedis.lrange("latency", size - N, -1);

            if (rangeList != null) System.out.println("size is " + rangeList.size());

            for (String s : rangeList) {
                int num = Integer.parseInt(s);
                min = Math.min(min, num);
                max = Math.max(max, num);
                total += num;
            }

            PrintWriter out = res.getWriter();
            out.println("<p>Request URI: " + req.getRequestURI() + "</p >");
            out.println("<p>Mean latency for " + N + "request is <strong>" + (total / N) + " ms</strong></p >");
            out.println("<p>Max latency for " + N + "request is <strong>" + max + " ms</strong></p >");
        }

    }

    protected void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res) throws javax.servlet.ServletException, IOException {
    }

    private boolean validate(String urlPath) {
        // /statistics/N
        String[] urlParts = urlPath.split("/");
        if (urlParts.length < 2) {
            return false;
        }
        try {

        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

}
