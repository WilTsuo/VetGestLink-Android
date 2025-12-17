package pt.ipleiria.estg.dei.vetgestlink.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthJsonParser {
    public static String parserJsonLogin(JSONObject response)
    {
        String token = null;

        try
        {
            token = response.getString("token");
        }
        catch(JSONException e)
        {
            throw new RuntimeException(e);
        }

        return token;
    }

    public static String parserJsonSignIn(String response)
    {
        String token = null;

        try
        {
            JSONObject loginJson = new JSONObject(response);

            token = loginJson.getString("token");
        }
        catch(JSONException e)
        {
            throw new RuntimeException(e);
        }

        return token;
    }

}
