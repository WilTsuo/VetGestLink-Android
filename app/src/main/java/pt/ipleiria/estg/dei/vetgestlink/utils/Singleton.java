package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.vetgestlink.model.Animal;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;
import pt.ipleiria.estg.dei.vetgestlink.model.NotaMerda;
import pt.ipleiria.estg.dei.vetgestlink.model.UserProfile;

public class Singleton {
    // region variaveis e constantes do singleton
    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_MAIN_URL = "main_url";
    private static final String DEFAULT_MAIN_URL = "http://172.22.21.220/backend/web/api";
    private static final String TAG = "VetGestLink";
    private static final String TAG_LOGIN = "AuthService";
    private static Singleton instance;
    private final Context context;
    private RequestQueue requestQueue; //queue do volley
    private String mainUrl;
    //endregion

    //region defenicao de Callbacks (message, login, notas ,userprofile)
    public interface MessageCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface LoginCallback {
        void onSuccess(String token, UserProfile userProfile);
        void onError(String error);
    }
    public interface NotasCallback {
        void onSuccess(List<Nota> notas);
        void onError(String error);
    }
    public interface UserProfileCallback {
        void onSuccess(UserProfile userProfile, List<Animal> animais);
        void onError(String error);
    }
    // endregion

    // region Construtor e Instanciação do Singleton
    private Singleton(Context context) {
        this.context = context.getApplicationContext();
        this.requestQueue = Volley.newRequestQueue(this.context); //inicializa a request queue do volley (no inicio logo como a stora disse)
        SharedPreferences prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.mainUrl = prefs.getString(KEY_MAIN_URL, DEFAULT_MAIN_URL);
    }

    //ponto de acceco ao singleton para n haver asneiras (chamamos, enviamos o context e ele devolve a instancia do singleton)
    public static synchronized Singleton getInstance(Context context) {
        if (instance == null) {
            instance = new Singleton(context);
        }
        return instance;
    }
    // endregion

    // region Métodos do Volley Request Queue
    //inicializa a request queue do volley se n estiver inicializada e devolve-a
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    // adiciona requests à queue do volley de forma simplificada (tipo em vez de estar sempre a chamar getRequestQueue().add(req) chamamos so isto)
    // addToRequestQueue(req): adiciona com a TAG padrão (útil para cancelar em bloco)
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    // addToRequestQueue(req, tag): permite tag customizada para poder cancelar por grupos e
    // manipular dados em blocos mais facilmente, ou só para indentificar mais facilmente blocos de pedidos na queue (debugging)
    public <T> void addToRequestQueue(Request<T> req, Object tag) {
        req.setTag(tag != null ? tag : TAG);
        getRequestQueue().add(req);
    }

    // cancela pedidos pendentes com a tag especificada (útil para cancelar grupos de pedidos relacionados como mencionado la atraz) bom para debugging também
    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
    // endregion

    // region Gestão da URL da API
    //devolve o url principal guardado
    public String getMainUrl() {
        return mainUrl;
    }

    //guarda o novo url no SharedPreferences
    public void setMainUrl(String url) {
        this.mainUrl = url;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_MAIN_URL, url).apply();
    }

    //construtor simples de endpoints
    public String buildUrl(String endpoint) {
        // endpoint example: "/auth/login" or "auth/login" FUNFA BEM OS DOIS
        String base = getMainUrl();
        if (endpoint.startsWith("/")) {
            return base + endpoint;
        } else {
            return base + "/" + endpoint;
        }
    }
    // endregion

    // region Gestao do Login e Auth Handler
    public void login(String username, String password, LoginCallback callback) {
        String url = buildUrl("auth/login");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            callback.onError("Erro ao preparar dados de login");
            return;
        }

        Log.d(TAG_LOGIN, "Enviando login para: " + url + " payload: " + jsonBody);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Log.d(TAG_LOGIN, "Resposta do login: " + response);
                    try {
                        boolean success = response.optBoolean("success", false);

                        if (success) {
                            String token = response.optString("token", "");

                            // Parse user profile
                            // 1) { "user": { "id", "username", "email" } }

                            UserProfile userProfile = new UserProfile();
                            JSONObject userObj = response.optJSONObject("user");
                            if (userObj != null) {
                                userProfile.setId(userObj.optInt("id", 0));
                                userProfile.setUsername(userObj.optString("username", ""));
                                userProfile.setEmail(userObj.optString("email", ""));
                            }
                            // Parse user profile antigo (se tivermos de usar por algum santo motivo)
                            //else {
                                //JSONObject userProfileObj = response.optJSONObject("userprofile");
                                //if (userProfileObj != null) {
                                    //userProfile.setId(userProfileObj.optInt("id", 0));
                                    //userProfile.setNomecompleto(userProfileObj.optString("nomecompleto", ""));
                                    //userProfile.setEmail(userProfileObj.optString("email", ""));
                                    //userProfile.setContacto(userProfileObj.optString("contacto", ""));
                                    //userProfile.setFotoUrl(userProfileObj.optString("foto_url", ""));
                                //}
                            //}

                            callback.onSuccess(token, userProfile);
                        } else {
                            String message = response.optString("message", "Login falhou");
                            callback.onError(message);
                        }
                    } catch (Exception e) {
                        Log.e(TAG_LOGIN, "Erro ao parsear resposta de login", e);
                        callback.onError("Erro ao processar resposta do servidor");
                    }

                    try {
                        boolean success = response.optBoolean("success", false);

                        if (success) {
                            String token = response.getString("token");

                            // Parse user profile
                            // resposta:
                            // auth_key
                            // user_id
                            // username
                            JSONObject userProfileObj = response.getJSONObject("user");
                            UserProfile userProfile = new UserProfile();
                            userProfile.setId(userProfileObj.getInt("id")); //guarda user_id
                            userProfile.setUsername(userProfileObj.optString("username", "ERRO!!!!!!!"));   // guarda username
                            userProfile.setEmail(userProfileObj.optString("email", "ERROO!!!!!!!"));        // guarda email

                            callback.onSuccess(token, userProfile);
                        } else {
                            String message = response.optString("message", "Login falhou");
                            callback.onError(message);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG_LOGIN, "Erro ao parsear resposta de login", e);
                        callback.onError("Erro ao processar resposta do servidor");
                    }
                },
                error -> {
                    Log.e(TAG_LOGIN, "Erro no pedido de login", error);
                    String errorMsg = "Erro ao fazer login";
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        if (statusCode == 401) {
                            errorMsg = "Credenciais inválidas";
                        } else if (statusCode == 404) {
                            errorMsg = "Servidor não encontrado";
                        } else {
                            errorMsg += " (Código: " + statusCode + ")";
                        }
                    } else if (error.getMessage() != null) {
                        errorMsg = "Erro de conexão: " + error.getMessage();
                    }
                    callback.onError(errorMsg);
                }
        );

        addToRequestQueue(request);
    }
    // endregion

    //region CRUD Notas e funcoes relacionadas
    //obter lista de notas de um animal, se nenhum animal for especificado retorna a lista completa realacionada a este user
    public void getNotas(String accessToken, Integer animalId, NotasCallback callback) {
        String url;

        if (animalId != null) {
            url = buildUrl("nota/?access-token=" + accessToken + "&animal_id=" + animalId);
        } else {
            url = buildUrl("nota/all?access-token=" + accessToken);
        }

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<NotaMerda> notas = parseNotas(response);
                    if (callback != null) {
                        //callback.onSuccess(notas);
                        //TODO TIRAR O NOTA MERDA E USAR O NOTA AQUI
                    }
                },
                error -> {
                    String errorMsg = url;
                    if (error.networkResponse != null) {
                        errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                    }
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
        );
        addToRequestQueue(request);
    }
    //criar nota
    public void criarNota(String accessToken, int animalId, String nota, MessageCallback callback) {
        String url = buildUrl("nota?access-token=" + accessToken);
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

        addToRequestQueue(request);
    }

    //update de notas
    public void atualizarNota(String accessToken, int notaId, String nota, MessageCallback callback) {
        String url = buildUrl("nota/" + notaId + "?access-token=" + accessToken);

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

        addToRequestQueue(request);
    }

    //delete de notas atravez de softdelete
    public void deletarNota(String accessToken, int notaId, MessageCallback callback) {
        String url = buildUrl("nota/" + notaId + "?access-token=" + accessToken);

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

        addToRequestQueue(request);
    }

    //parse de notas a partir de um JSONArray (devolvido pelo get notas)
    private List<NotaMerda> parseNotas(JSONArray jsonArray) {
        List<NotaMerda> notas = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                NotaMerda nota = new NotaMerda();
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
            Log.e(TAG, "Erro ao parsear notas", e);
        }

        return notas;
    }
    // endregion

    //region Gestao do userProfile e handler de informação associada
    public void getUserProfile(String accessToken, UserProfileCallback callback) {
        String url = buildUrl("userprofile?access-token=" + accessToken);

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

        addToRequestQueue(request);
    }

    private UserProfile parseUserProfile(JSONObject obj) throws JSONException {
        UserProfile profile = new UserProfile();
        profile.setId(obj.getInt("id"));
        profile.setNomecompleto(obj.optString("nomecompleto", ""));
        profile.setEmail(obj.optString("email", ""));
        profile.setContacto(obj.optString("contacto", ""));
        profile.setFotoUrl(obj.optString("foto_url", ""));
        return profile;
    }

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
    //endregion
}
