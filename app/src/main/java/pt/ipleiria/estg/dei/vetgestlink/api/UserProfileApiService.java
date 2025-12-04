package pt.ipleiria.estg.dei.vetgestlink.api;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import pt.ipleiria.estg.dei.vetgestlink.model.Animal;
import pt.ipleiria.estg.dei.vetgestlink.model.UserProfile;

/**
 * Serviço de API para gerenciar UserProfile
 * Endpoint: GET /userprofile?access-token={token}
 */
public class UserProfileApiService {

    private Context context;

    public UserProfileApiService(Context context) {
        this.context = context;
    }

    /**
     * Callback para perfil do utilizador
     */
    public interface UserProfileCallback {
        void onSuccess(UserProfile userProfile, List<Animal> animais);
        void onError(String error);
    }

    /**
     * GET /userprofile?access-token={token}
     * Ver perfil completo (inclui moradas e animais)
     */
    public void getUserProfile(String accessToken, UserProfileCallback callback) {
        String url = ApiClient.getInstance(context).buildUrl("userprofile?access-token=" + accessToken);

        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    UserProfile userProfile = parseUserProfile(response);
                    List<Animal> animais = parseAnimais(response.optJSONArray("animais"));
                    callback.onSuccess(userProfile, animais);
                } catch (JSONException e) {
                    callback.onError("Erro ao processar dados do perfil");
                }
            },
            error -> {
                String errorMsg = "Erro ao carregar perfil";
                if (error.networkResponse != null) {
                    errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                }
                callback.onError(errorMsg);
            }
        );

        ApiClient.getInstance(context).addToRequestQueue(request);
    }

    /**
     * Parser de JSON para UserProfile
     */
    private UserProfile parseUserProfile(JSONObject obj) throws JSONException {
        UserProfile profile = new UserProfile();
        profile.setId(obj.getInt("id"));
        profile.setNomecompleto(obj.optString("nomecompleto", ""));
        profile.setEmail(obj.optString("email", ""));
        profile.setContacto(obj.optString("contacto", ""));
        profile.setFotoUrl(obj.optString("foto_url", ""));
        return profile;
    }

    /**
     * Parser de JSON para lista de Animais
     */
    private List<Animal> parseAnimais(JSONArray jsonArray) {
        List<Animal> animais = new ArrayList<>();

        if (jsonArray == null) {
            return animais;
        }

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Animal animal = new Animal();
                animal.setId(obj.getInt("id"));
                animal.setNome(obj.getString("nome"));
                animal.setDtanascimento(obj.optString("dtanascimento", ""));
                animal.setPeso((float) obj.optDouble("peso", 0.0));
                animal.setMicroship(obj.optBoolean("microship", false));
                animal.setSexo(obj.optString("sexo", ""));
                animal.setEspeciesId(obj.optInt("especies_id", 0));
                animal.setEspecieNome(obj.optString("especie_nome", ""));
                animal.setRacasId(obj.optInt("racas_id", 0));
                animal.setRacaNome(obj.optString("raca_nome", ""));
                animal.setEliminado(obj.optBoolean("eliminado", false));

                // Adicionar apenas animais não eliminados
                if (!animal.isEliminado()) {
                    animais.add(animal);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return animais;
    }
}

