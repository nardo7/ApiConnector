package Connection;

import Utils.JsonConverter;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.HashMap;

public class Connection {

    private final String url;
    private String method;
    private HashMap<String, String> requestProp;
    private HttpsURLConnection connection=null;

    public Connection(String url, String method, HashMap<String,String> requestProp){

        this.url = url;
        this.method = method;
        this.requestProp = requestProp;


        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
        }catch (Exception e){
            System.out.print("error, the method is wrong "+e.getMessage()+ "... "+ e.getCause());
            return;
        }

        for (var key:requestProp.keySet())
            connection.setRequestProperty(key,requestProp.get(key));
    }

    public Connection(String url, String method){

        this.url = url;
        this.method = method;


        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
        }catch (Exception e){
            System.out.print("error, the method is wrong "+e.getMessage()+ "... "+ e.getCause());

        }
    }

    public void Httpscofigure(String method,HashMap<String,String> requestProp) throws IOException {
        this.requestProp=requestProp;
        connection.setRequestMethod(method);
        for (var key:requestProp.keySet())
            connection.setRequestProperty(key,requestProp.get(key));

    }

    private void PutOutMessage(HttpsURLConnection connection, HashMap<String,String> jsonMessage) throws IOException {
        if(jsonMessage!=null) {
            var json= JsonConverter.MapToJson(jsonMessage);
            var outw = new OutputStreamWriter(connection.getOutputStream());
            var outputdata = json.toString();
            outw.write(outputdata);
            outw.flush();
            outw.close();
        }
    }

    private void PutOutMessage(HttpsURLConnection connection,  String message) throws IOException {
        var outw = new OutputStreamWriter(connection.getOutputStream());
        outw.write(message);
        outw.flush();
        outw.close();
    }

    private String GetInputMessage(InputStream input) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        return response.toString();
    }

    public String Request(String message) throws IOException {

        try {
            connection.setDoOutput(true);
            PutOutMessage(connection, message);
            connection.connect();
            var input=connection.getInputStream();
            return GetInputMessage(input);

        } catch (Exception e) {
            var input=connection.getErrorStream();
            return GetInputMessage(input);

        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public String Request() throws IOException {
        HttpsURLConnection connection=null;

        try {
            connection.connect();
            var input=connection.getInputStream();
            return GetInputMessage(input);

        } catch (Exception e) {
            var input=connection.getErrorStream();

            return GetInputMessage(input);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public String Request(HashMap<String,String> message) throws IOException {

        try {
            connection.setDoOutput(true);
            PutOutMessage(connection, message);
            connection.connect();
            var input=connection.getInputStream();
            return GetInputMessage(input);

        } catch (Exception e) {
            var input=connection.getErrorStream();

            return GetInputMessage(input);
        } finally {
            if (connection != null)
                connection.disconnect();
        }

    }

}
