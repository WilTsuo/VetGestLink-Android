package pt.ipleiria.estg.dei.vetgestlink.api;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import pt.ipleiria.estg.dei.vetgestlink.model.UserProfile;

/**
 * Serviço de API para autenticação
 * Endpoint: POST /auth/login
 */
public class AuthApiService {

    private static final String TAG = "AuthApiService";

    private Context context;

    public AuthApiService(Context context) {
        this.context = context;
    }

    /**
     * Callback para login
     */
    public interface LoginCallback {
        void onSuccess(String token, UserProfile userProfile);
        void onError(String error);
    }

    /**
     * POST /auth/login
     * Autentica o utilizador na API
     */
    public void login(String username, String password, LoginCallback callback) {
        String url = ApiClient.getInstance(context).getBaseUrl() + "auth/login"; //agora  usar sempre este metodo para conneções a API

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            callback.onError("Erro ao preparar dados de login");
            return;
        }

        Log.d(TAG, "Enviando login para: " + url + " payload: " + jsonBody.toString());

        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            response -> {
                Log.d(TAG, "Resposta do login: " + response.toString());
                try {
                    boolean success = response.optBoolean("success", false);

                    if (success) {
                        String token = response.getString("token");

                        // Parse user profile
                        // resposta:
                        // auth_key
                        // user_id
                        // username
                        JSONObject userProfileObj = response.getJSONObject("userprofile");
                        UserProfile userProfile = new UserProfile();
                        userProfile.setId(userProfileObj.getInt("id"));
                        userProfile.setNomecompleto(userProfileObj.optString("nomecompleto", ""));
                        userProfile.setEmail(userProfileObj.optString("email", ""));
                        userProfile.setContacto(userProfileObj.optString("contacto", ""));

                        callback.onSuccess(token, userProfile);
                    } else {
                        String message = response.optString("message", "Login falhou");
                        callback.onError(message);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Erro ao parsear resposta de login", e);
                    callback.onError("Erro ao processar resposta do servidor");
                }
            },
            error -> {
                Log.e(TAG, "Erro no pedido de login", error);
                String errorMsg = "Erro ao fazer login";
                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    if (statusCode == 401) {
                        errorMsg = "Credenciais inválidas";
                    } else if (statusCode == 404) {
                        errorMsg = "Servidor não encontrado";
                        errorMsg = "URL:" + url;
                    } else {
                        errorMsg += " (Código: " + statusCode + ")";
                    }
                } else if (error.getMessage() != null) {
                    errorMsg = "Erro de conexão: " + error.getMessage();
                }
                callback.onError(errorMsg);
            }
        );

        ApiClient.getInstance(context).addToRequestQueue(request);
    }
}
