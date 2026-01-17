package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import pt.ipleiria.estg.dei.vetgestlink.models.UserProfile;

public class LoginJsonParser {

    private static final String TAG = "LoginJsonParser";

    /**
     * Extrai o token de autenticação da resposta JSON.
     */
    public static String parseToken(JSONObject response) {
        return response.optString("token", null);
    }

    /**
     * Extrai os dados do utilizador (UserProfile) da resposta JSON.
     */
    public static UserProfile parseUserProfile(JSONObject response) {
        try {
            JSONObject userJson = response.optJSONObject("user");
            if (userJson != null) {
                int id = userJson.optInt("id", -1);
                String email = userJson.optString("email", "");
                String username = userJson.optString("username", "");

                if (id > 0) {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setId(id);
                    userProfile.setUsername(username);
                    userProfile.setEmail(email);
                    return userProfile;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar UserProfile: " + e.getMessage());
        }
        return null;
    }
}
