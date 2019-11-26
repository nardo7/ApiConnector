package com.apply;

import Auth.Authenticator;
import Utils.JsonConverter;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Main {

    static String[] urlParameterKeys={""};

    public static void main(String[] args){
	// write your code here
        JsonParser jr=null;
        try {

            jr=Json.createParser(new FileReader("C:\\Users\\aleja\\programming\\Applicatorr\\src\\com\\apply\\config.json"));
        }catch (Exception e){
            System.out.print(e.getMessage());
            return;
        }
        var map=JsonConverter.JsonToMap(jr);
        Authenticator auth=null;
        try {
            var state=map.get("state");
            auth= new Authenticator(map.get("client_id"),map.get("secret_key"),map.get("response_type"),map.get("redirect_uri"),
                    state, map.get("scope"),map.get("auth_uri"));
        }catch (NullPointerException e){
            System.out.print(e.getMessage());
            return;
        }
        auth.Authenticate();
    }
}
