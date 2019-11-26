package Utils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import java.util.HashMap;

public class JsonConverter {

    public static JsonObject MapToJson(HashMap<String,String> map){
        var builder = Json.createObjectBuilder();
        for (var item:map.keySet()
             ) {
            builder.add(item,map.get(item));
        }
        return builder.build();
    }

    public static HashMap<String,String> JsonToMap(JsonParser parser){

        var result=new HashMap<String,String>();
        var factory = Json.createParserFactory(null);

        if (!parser.hasNext() && parser.next() != JsonParser.Event.START_ARRAY) {

            return null;
        }

        // looping over object attributes
        while (parser.hasNext()) {

            var event = parser.next();

            // starting object
            if (event == JsonParser.Event.START_OBJECT) {

                while (parser.hasNext()) {

                    event = parser.next();

                    if (event == JsonParser.Event.KEY_NAME) {

                        var key = parser.getString();
                        parser.next();
                        var value=parser.getString();
                        result.put(key,value);

                        System.out.printf("Key: %s and value: %s were read and stored\n",key, value);
                    }
                }
            }
        }
        return result;
    }
}
