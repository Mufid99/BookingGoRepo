import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * This class is responsible for making the get request
 * @author Mufid Alkhaddour
 */

public class HttpsRequest {

    public static boolean FORCE_HTTPS = false;

    private static final String DEFAULT_AGENT = "Mozilla/5.0";

    /**
     * calls the sendGET_method
     * @param url the api url
     * @param agent the agent to be used
     * @return the JSON
     */
    public static StringBuffer sendGET(String url, String agent) {
        try {
            return sendGET_(url, agent);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * calls the sendGET_method
     * @param url the api url
     * @return the JSON
     */
    public static StringBuffer sendGET(String url) {
        try {
            return sendGET_(url, "");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * makes the get request
     * @param url the api url
     * @param agent_ the agent to be used
     * @return
     * @throws IOException
     */
    private static StringBuffer sendGET_(String url, String agent_) throws IOException {
        String fullurl = "";
        if (url.substring(0, 7).equals("http://") || url.substring(0, 8).equals("https://") || url.substring(0, 7).equals("file://")) {
            fullurl = url;
        } else if (FORCE_HTTPS) {
            fullurl = "https://" + url;
        } else {
            fullurl = "http://" + url;
        }
        URL obj = new URL(fullurl);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setConnectTimeout(2000); // timeout after 2 seconds
        con.setRequestMethod("GET");
        String agent = DEFAULT_AGENT;
        if (agent_ != "") {
            agent = agent_;
        }
        con.setRequestProperty("User-Agent", agent);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        //System.out.println(fullurl + ", " + agent);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            return response;
        } else {
            if (responseCode == 400){
                System.out.println("Bad Request");
            }
            else if(responseCode == 500){
                System.out.println("Internal Server Error");
            }
            return null;
        }
    }
}