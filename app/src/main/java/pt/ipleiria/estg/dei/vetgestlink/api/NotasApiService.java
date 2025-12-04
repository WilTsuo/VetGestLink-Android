package pt.ipleiria.estg.dei.vetgestlink.api;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;

/**
 * Serviço de API para gerenciar Notas
 * Endpoints: GET /nota, POST /nota, PUT /nota/{id}, DELETE /nota/{id}
 */
public class NotasApiService {

    private Context context;

    public NotasApiService(Context context) {
        this.context = context;
    }

    /**
     * Callback para operações de lista de notas
     */
    public interface NotasCallback {
        void onSuccess(List<Nota> notas);
        void onError(String error);
    }

    /**
     * Callback para operações individuais (criar, atualizar, deletar)
     */
    public interface NotaOperationCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    /**
     * GET /nota?access-token={token}&animal_id={id}
     * Listar notas dos animais do utilizador
     */
    public void getNotas(String accessToken, Integer animalId) {
        String url = ApiClient.getInstance(context).getBaseUrl() + "nota?access-token=" + accessToken;

        if (animalId != null) {
            url += "&animal_id=" + animalId;
        }

        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                List<Nota> notas = parseNotas(response);
                if (notasCallback != null) {
                    notasCallback.onSuccess(notas);
                }
            },
            error -> {
                String errorMsg = "Erro ao carregar notas";
                if (error.networkResponse != null) {
                    errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                }
                if (notasCallback != null) {
                    notasCallback.onError(errorMsg);
                }
            }
        );

        ApiClient.getInstance(context).addToRequestQueue(request);
    }

    private NotasCallback notasCallback;

    public void setNotasCallback(NotasCallback callback) {
        this.notasCallback = callback;
    }

    /**
     * POST /nota?access-token={token}
     * Criar nova nota
     */
    public void criarNota(String accessToken, int animalId, String nota, NotaOperationCallback callback) {
        String url = ApiClient.getInstance(context).getBaseUrl() + "nota?access-token=" + accessToken;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("animais_id", animalId);
            jsonBody.put("nota", nota);
        } catch (JSONException e) {
            callback.onError("Erro ao preparar dados");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            response -> {
                try {
                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");
                    if (success) {
                        callback.onSuccess(message);
                    } else {
                        callback.onError(message);
                    }
                } catch (JSONException e) {
                    callback.onError("Erro ao processar resposta");
                }
            },
            error -> {
                String errorMsg = "Erro ao criar nota";
                if (error.networkResponse != null) {
                    errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                }
                callback.onError(errorMsg);
            }
        );

        ApiClient.getInstance(context).addToRequestQueue(request);
    }

    /**
     * PUT /nota/{id}?access-token={token}
     * Atualizar nota existente
     */
    public void atualizarNota(String accessToken, int notaId, String nota, NotaOperationCallback callback) {
        String url = ApiClient.getInstance(context).getBaseUrl() + "nota/" + notaId +"access-token=" + accessToken;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nota", nota);
        } catch (JSONException e) {
            callback.onError("Erro ao preparar dados");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.PUT,
            url,
            jsonBody,
            response -> {
                try {
                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");
                    if (success) {
                        callback.onSuccess(message);
                    } else {
                        callback.onError(message);
                    }
                } catch (JSONException e) {
                    callback.onError("Erro ao processar resposta");
                }
            },
            error -> {
                String errorMsg = "Erro ao atualizar nota";
                if (error.networkResponse != null) {
                    errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                }
                callback.onError(errorMsg);
            }
        );

        ApiClient.getInstance(context).addToRequestQueue(request);
    }

    /**
     * DELETE /nota/{id}?access-token={token}
     * Deletar nota (soft delete)
     */
    public void deletarNota(String accessToken, int notaId, NotaOperationCallback callback) {
        String url = ApiClient.getInstance(context).getBaseUrl() + "nota/" + notaId + "?access-token=" + accessToken;

        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.DELETE,
            url,
            null,
            response -> {
                try {
                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");
                    if (success) {
                        callback.onSuccess(message);
                    } else {
                        callback.onError(message);
                    }
                } catch (JSONException e) {
                    callback.onError("Erro ao processar resposta");
                }
            },
            error -> {
                String errorMsg = "Erro ao deletar nota";
                if (error.networkResponse != null) {
                    errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                }
                callback.onError(errorMsg);
            }
        );

        ApiClient.getInstance(context).addToRequestQueue(request);
    }

    /**
     * Parser de JSON para lista de Notas
     */
    private List<Nota> parseNotas(JSONArray jsonArray) {
        List<Nota> notas = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Nota nota = new Nota();
                nota.setId(obj.getInt("id"));
                nota.setNota(obj.getString("nota"));
                nota.setCreatedAt(obj.optString("created_at", ""));
                nota.setUpdatedAt(obj.optString("updated_at", ""));
                nota.setUserprofilesId(obj.optInt("userprofiles_id", 0));
                nota.setAnimaisId(obj.optInt("animais_id", 0));

                // Campo adicional que pode vir da API
                if (obj.has("animal_nome")) {
                    nota.setNomeAnimal(obj.getString("animal_nome"));
                }

                notas.add(nota);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return notas;
    }
}

