package plentybase.plugin.hue.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plentybase.plugin.hue.bean.Response;
import plentybase.plugin.hue.context.Context;

import javax.swing.*;
import java.io.*;
import java.net.*;


public class RequestHelper {

    private static Logger logger = LogManager.getLogger("PlentyMarkets");
    private int responseCode;


    /**
     * doPostRequest
     * doPostRequest is a method that does a POST request by using HttpURLConnection
     *
     * @param stringUrl URL
     * @param postData  data that is send in the POST request
     * @return Response object that has a responseCode and a JSON response.
     */

    public Response doPOSTRequest(String stringUrl, String postData) {

        URL url = null;
        try {
            StringBuilder bodyData = new StringBuilder();
            bodyData.append(postData);
            byte[] postDataBytes = bodyData.toString().getBytes("UTF-8");


            url = new URL(stringUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // buildconnection
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c;
                 (c = in.read()) >= 0; ) {
                sb.append((char) c);
            }
            String response = sb.toString();

            return new Response(response, con.getResponseCode());


        } catch (MalformedURLException e) {
            Context.getLogger().error(e.toString());
        } catch (UnsupportedEncodingException e) {
            Context.getLogger().error(e.toString());
        } catch (ProtocolException e) {
            Context.getLogger().error(e.toString());
        } catch (SocketTimeoutException ex){
        } catch (IOException e) {
            Context.getLogger().error(e.toString());
        }
        return null;
    }

    /**
     * doGetRequest
     * doGetRequest is a method that does a GET request by using HttpURLConnection
     *
     * @param stringUrl URL
     * @return Response object that has a reponseCode and a JSON response.
     */

    public Response doGETRequest(String stringUrl) {

        try {
            URL url = new URL(stringUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // buildconnection
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); // get JSONresponse
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) { // build response
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return new Response(content.toString(), con.getResponseCode());

        } catch (MalformedURLException e) {
            Context.getLogger().error(e.toString());
        } catch (IOException e) {
            Context.getLogger().error(e.toString());
        }
        return null;
    }

    /**
     * doPutRequest
     * doPUTRequest is a method that does a PUT request by using HttpURLConnection.
     *
     * @param stringUrl URL
     * @param JsonBody  Body that is send through the request
     * @return Response object that has a reponseCode and a JSON response.
     */

    public Response doPUTRequest(String stringUrl, String JsonBody) {

        try {
            URL url = new URL(stringUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // buildconnection
            con.setRequestMethod("PUT");
            con.setDoOutput(true);

            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
            osw.write(JsonBody);

            osw.close();
            con.getInputStream();
            responseCode = con.getResponseCode();
            logger.info("Response Code, lightOn --> " + responseCode + "massage : " + con.getResponseMessage());
            return new Response("", responseCode);
        } catch (IOException e) {
            Context.getLogger().error(e.toString());;
        }
        return null;
    }
}