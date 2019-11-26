package Auth;

import Connection.Connection;

import javax.json.Json;
import java.io.*;
import java.net.*;
import java.awt.Desktop;
import java.util.HashMap;


 /*INPUT MESSAGE
    {
        message: {success/unsuccess}
        code: {code/""}
        observations: ....
    }
    OUTPUT MESSAGE
    {
        message: {GET_CODE}
    }
    * */

/**
 * Class used to authenticate in a OAuth site ( Linkedin )
 * **/
public class Authenticator {

    private String client_id;

    private String secret_key;

    private String response_type;

    private String redirect_uri;

    private String state;

    private String scope;

    private String auth_uri;

    private String GET_CODE_MESSAGE = "GET_CODE";

    private String grant_type = "authorization_code";

    private String accessTokenUrlParams = "grant_type=authorization_code&" +
            "code=%s&" +
            "redirect_uri=%s&" +
            "client_id=%s&" +
            "client_secret=%s";


    public Authenticator(String client_id, String secret_key, String response_type, String redirect_uri,
                         String state, String scope, String auth_uri) {
        this.client_id = client_id;
        this.secret_key = secret_key;
        this.response_type = response_type;
        this.redirect_uri = redirect_uri;
        this.state = state;
        this.scope = scope;
        this.auth_uri = auth_uri;

    }

    /**
     * Method used to get authenticated in a OAuth Site**/
    public void Authenticate() {
        var code = GetAuthCode();
        if (code != null) {
            getAccessToken(code);
        }
    }

    /**
     * Returns, if possible, the code, which is required for an after access token requesting
     * **/
    public String GetAuthCode() {
        String urlstring = "https://www.linkedin.com/oauth/v2/authorization";

        var newState = RequestAuth(this.state);
        if (newState == null)
            return null;
        this.state = newState;
        String urlParameters = String.format("response_type=%s&client_id=%s&redirect_uri=%s&state=%s&scope=%s", response_type, client_id, redirect_uri, state, scope);

        try {
            URI url = new URI(urlstring + "?" + urlParameters);
            Desktop.getDesktop().browse(url);
            var code = getCodeFromRedirect();
            if (code == null) {
                System.out.print("timeout");
                return null;
            } else
                return code;

        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }


    }

    /**
     * Makes a request to the auth web server provided, so that the server can know and store the code provided from the OAuth
     * site (Linkedin)
     * Returns a new state which is used for requesting the code
     * **/
    private String RequestAuth(String state) {
        var bodyRequest = new HashMap<String, String>();
        bodyRequest.put("state", state);
        var requestProp = new HashMap<String, String>();
        requestProp.put("Content-Type", "application/json; utf-8");
        requestProp.put("Accept", "application/json");
        var connection = new Connection(auth_uri, "POST", requestProp);
        try {
            String response = connection.Request(bodyRequest);
            var json = Json.createReader(new StringReader(response)).readObject();
            String statusCode = json.getString("code");
            switch (statusCode) {
                case "success":
                    return json.getString("message");
                default:
                    System.out.print(json.getString("message"));
                    return null;
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }

    }



    /**
     * Makes a request to the server the provided code is stored in, and returns it if it's possible
     * **/
    private String getCodeFromRedirect() {

        var url = auth_uri + this.state;
        var connection = new Connection(url, "GET");
        try {
            Thread.sleep(1000);
            var requestProp = new HashMap<String, String>();
            var response = connection.Request();
            try {
                var json = Json.createReader(new StringReader(response)).readObject();
                if (json.getInt("status") == 404)
                    return null;
            } catch (Exception e) {
            }
            var waitingTime = 1000;
            while (response == null || response.isEmpty()) {
                if (waitingTime == 30000)
                    return null;
                Thread.sleep(1000);
                waitingTime += 1000;
                response = connection.Request();
            }
            return response;

        } catch (Exception e) {
            System.out.print(e.getMessage() + "... " + e.getCause());
            return null;
        }

    }

    /**
     * Makes a request to the OAuth site, to request the access token providing the given code
     * this method writes the code in a physical file
     * **/
    private void getAccessToken(String code) {
        var url = "https://www.linkedin.com/oauth/v2/accessToken";
        var urlParams = String.format(accessTokenUrlParams, code, redirect_uri, client_id, secret_key);
        var requestprop = new HashMap<String, String>();

        requestprop.put("Content-Type", "application/x-www-form-urlencoded");

        var connection = new Connection(url, "POST", requestprop);
        try {
            var response = connection.Request(urlParams);
            FileWriter fw = new FileWriter("C:\\Users\\aleja\\programming\\Applicatorr\\src\\com\\apply\\token.json");
            fw.write(response);
            fw.close();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }


}
