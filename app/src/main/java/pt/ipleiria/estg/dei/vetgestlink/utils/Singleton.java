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

//Listeners
import pt.ipleiria.estg.dei.vetgestlink.listeners.AnimaisListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.AuthListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.FaturasListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.LembretesListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.MarcacoesListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.NotaListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.NotasListener;

//Modelos
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;
import pt.ipleiria.estg.dei.vetgestlink.models.Marcacao;
import pt.ipleiria.estg.dei.vetgestlink.models.Nota;
import pt.ipleiria.estg.dei.vetgestlink.models.MarcacaoDBHelper;


import pt.ipleiria.estg.dei.vetgestlink.models.NotaDBHelper;
import pt.ipleiria.estg.dei.vetgestlink.models.UserProfile;

public class Singleton {
    // region variaveis e constantes do singleton

    //Variaveis
    private ArrayList<Marcacao> marcacoes;
    private MarcacaoDBHelper marcacoesDB = null;


    //Constantes
    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_MAIN_URL = "main_url";
    private static final String DEFAULT_MAIN_URL = "http://172.22.21.220/backend/web/api";
    private static final String TAG = "VetGestLink";
    private static final String TAG_ANIMAIS = "Vetgetlink-AnimaisService";
    private static final String TAG_NOTAS = "Vetgetlink-NotasService";
    private static final String TAG_LOGIN = "Vetgetlink-AuthService";
    private static Singleton instance;
    //region - arrays e notadbhelper
    private ArrayList<Nota> notas;
    private NotaDBHelper notasDB=null;
    //endregion
    private final Context context;
    private RequestQueue requestQueue; //queue do volley
    private String mainUrl;
    //region - Listeners
    private AuthListener authListener;
    private NotasListener NotasListener;
    private NotaListener NotaListener;
    private AuthListener Authlistener;
    private AnimaisListener AnimaisListener;
    private FaturasListener FaturasListener;
    private MarcacoesListener MarcacoesListener;
    private LembretesListener LembretesListener;
    //endregion

    //region - Setters Listeners
    public void setNotasListener(NotasListener notasListener) {
        NotasListener = notasListener;
    }

    public void setAuthListener(AuthListener authListener) {
        this.authListener = authListener;
    }

    public void setNotaListener(NotaListener notaListener) {
        NotaListener = notaListener;
    }

    public void setAuthlistener(AuthListener authlistener) {
        Authlistener = authlistener;
    }

    public void setAnimaisListener(AnimaisListener animaisListener) {
        AnimaisListener = animaisListener;
    }

    public void setFaturasListener(FaturasListener faturasListener) {
        FaturasListener = faturasListener;
    }

    public void setMarcacoesListener(MarcacoesListener marcacoesListener) {
        MarcacoesListener = marcacoesListener;
    }

    public void setLembretesListener(LembretesListener lembretesListener) {
        LembretesListener = lembretesListener;
    }
    //endregion

    //region defenicao de Callbacks (message, login, notas ,userprofile)

    public interface MarcacoesCallback {
        void onSuccess(ArrayList<Marcacao> marcacoes);
        void onError(String error);
    }

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

    public interface ProfileCallback {
        void onSuccess(String nome, String email, String telefone, String moradaCompleta);
        void onError(String error);
    }
    // endregion

    // region Construtor e Instanciação do Singleton
    private Singleton(Context context) {
        this.context = context.getApplicationContext();
        this.requestQueue = Volley.newRequestQueue(this.context); //inicializa a request queue do volley (no inicio logo como a stora disse)
        SharedPreferences prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.mainUrl = prefs.getString(KEY_MAIN_URL, DEFAULT_MAIN_URL);

        notas = new ArrayList<>();
        marcacoes = new ArrayList<>();
        notasDB = new NotaDBHelper(context);
        marcacoesDB = new MarcacaoDBHelper(context);
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

                            // Parse user profile CORRETAMENTE
                            UserProfile userProfile = new UserProfile();
                            JSONObject userObj = response.optJSONObject("user");
                            if (userObj != null) {
                                int userId = userObj.optInt("id", 0);
                                userProfile.setId(userId);
                                userProfile.setUsername(userObj.optString("username", ""));
                                userProfile.setEmail(userObj.optString("email", ""));

                                // Guardar nas SharedPreferences
                                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                prefs.edit().putInt("userprofile_id", userId).apply();
                            }

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
    public Nota getNota(int id){

        for(Nota n:notas)
            if(n.getId()==id)
                return n;
        return null;
    }
    //region- CRUD bd local
    public ArrayList<Nota> getNotasBD() {

        notas = notasDB.getAllNotasBD();
        return new ArrayList<>(notas);
    }

    public void adicionarNotaBD(Nota nota){
        notasDB.adicionarNotaBD(nota);
    }
    public void adicionarNotasBD(ArrayList<Nota> notas){
        notasDB.removerAllNotasBD();
        for(Nota l:notas) {
            adicionarNotaBD(l);
        }
    }
    public void removerNotaBD(int idnota){
        Nota n= getNota(idnota);
        if(n!=null) {
            notasDB.removerNotaBD(idnota);
        }
    }
    public void removerNotasBD(){
        notasDB.removerAllNotasBD();
    }
    //endregion

    //obter lista de notas de um animal, se nenhum animal for especificado retorna a lista completa realacionada a este user
    public void getNotas(String accessToken, Integer animalId, NotasCallback callback) {
        String url;

        if (animalId != null) {
            url = buildUrl("animal/"+animalId+"/notas?access-token=" + accessToken);
        } else {
            url = buildUrl("nota/all?access-token=" + accessToken);
        }

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<Nota> notas = NotaJsonParser.parserJsonNotas(response);
                    if (callback != null) {
                        callback.onSuccess(notas);

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
        String url = buildUrl("nota/create?access-token=" + accessToken);
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

    // endregion

    //region Gestao do userProfile e handler de informação associada

    public void getProfile(String token, ProfileCallback callback) {
        String url = buildUrl("profile?access-token=" + token);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject user = response.getJSONObject("user");
                        JSONObject profile = response.getJSONObject("profile");
                        JSONObject morada = response.getJSONObject("morada");

                        String nome = profile.getString("nomecompleto");
                        String email = user.getString("email");
                        String telefone = profile.getString("telemovel");
                        String moradaCompleta = morada.getString("rua") + ", " +
                                morada.getString("nporta") + "\n" +
                                morada.getString("cdpostal") + " " +
                                morada.getString("localidade");

                        if (callback != null) {
                            callback.onSuccess(nome, email, telefone, moradaCompleta);
                        }
                    } catch (JSONException e) {
                        if (callback != null) callback.onError("Erro ao processar dados do perfil");
                    }
                },
                error -> {
                    if (callback != null) callback.onError("Erro na rede: " + error.getMessage());
                }
        );
        addToRequestQueue(request);
    }

    //endregion

    //region - Gestao de Animais e handler de informação associada
    private List<Animal> parseAnimais(JSONArray jsonArray) {
        List<Animal> animais = new ArrayList<>();
        if (jsonArray == null) return animais;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (obj.optBoolean("ativo", true)) {
                    Animal animal = new Animal();
                    animal.setId(obj.optInt("id", 0)); // Mapeia o ID
                    animal.setNome(obj.optString("nome", ""));
                    animal.setEspecie(obj.optString("especie", ""));
                    animal.setRaca(obj.optString("raca", ""));
                    animal.setIdade(obj.optInt("idade", 0));
                    animal.setPeso(obj.optDouble("peso", 0.0));
                    animal.setSexo(obj.optString("sexo", ""));
                    animal.setMicrochip(obj.optInt("microchip", 0));
                    animal.setFotoUrl(obj.optString("foto_url", ""));
                    animal.setDtanascimento(obj.optString("datanascimento", ""));

                    animais.add(animal);
                }
            }
        } catch (JSONException e) {
            Log.e("Singleton", "Erro no parseAnimais: " + e.getMessage());
        }
        return animais;
    }

    private List<Animal> parseAnimaisNome(JSONArray jsonArray) {
        List<Animal> animais = new ArrayList<>();
        if (jsonArray == null) return animais;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Animal animal = new Animal();
                animal.setId(obj.optInt("id", 0)); // Mapeia o ID necessário para o NotasFragment
                animal.setNome(obj.optString("nome", ""));
                animais.add(animal);
            }
        } catch (JSONException e) {
            Log.e("Singleton", "Erro no parseAnimaisNome: " + e.getMessage());
        }
        return animais;
    }

    public interface AnimaisCallback {
        void onSuccess(List<Animal> animais);
        void onError(String error);
    }

    public void getAnimais(String accessToken, AnimaisCallback callback) {
        String url = buildUrl("animal/all?access-token=" + accessToken);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<Animal> animais = parseAnimais(response);
                    if (callback != null) {
                        callback.onSuccess(animais);
                    }
                },
                error -> {
                    String errorMsg = "Erro ao carregar animais";
                    if (error.networkResponse != null) {
                        errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                    } else if (error.getMessage() != null) {
                        errorMsg += ": " + error.getMessage();
                    }
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
        );
        addToRequestQueue(request);
    }


    public void getNomesAnimais(String accessToken, AnimaisCallback callback) {
        String url = buildUrl("animal/nomes?access-token=" + accessToken);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<Animal> animais = parseAnimaisNome(response);
                    if (callback != null) {
                        callback.onSuccess(animais);
                    }
                },
                error -> {
                    String errorMsg = "Erro ao carregar nomes dos animais";
                    if (error.networkResponse != null) {
                        errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                    } else if (error.getMessage() != null) {
                        errorMsg += ": " + error.getMessage();
                    }
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
        );
        addToRequestQueue(request);
    }
    //endregion

    //region - Gestao de Marcacoes e handler de informação associada
    public ArrayList<Marcacao> getMarcacoesLocal() {
        return marcacoes;
    }
    public void getMarcacoesAPI(String accessToken, final MarcacoesCallback callback) {
        String url = buildUrl("marcacao/all?access-token=" + accessToken);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        ArrayList<Marcacao> lista = MarcacaoJsonParser.parserJsonMarcacoes(response.toString());

                        // Guarda localmente para uso offline
                        adicionarMarcacoesBD(lista);
                        this.marcacoes = lista;

                        if (callback != null) callback.onSuccess(lista);
                        if (MarcacoesListener != null) MarcacoesListener.onRefreshListaMarcacoes(lista);

                    } catch (Exception e) {
                        if (callback != null) callback.onError("Erro ao processar dados");
                    }
                },
                error -> {
                    // Se falhar (offline), tenta carregar da BD local
                    ArrayList<Marcacao> localData = getMarcacoesBD();
                    if (callback != null) {
                        if (!localData.isEmpty()) {
                            callback.onSuccess(localData);
                        } else {
                            callback.onError("Sem ligação e sem dados locais");
                        }
                    }
                });
        addToRequestQueue(request);
    }
    public ArrayList<Marcacao> getMarcacoesBD() {
        marcacoes = marcacoesDB.getAllMarcacoesBD();
        return new ArrayList<>(this.marcacoes);
    }
    public void adicionarMarcacoesBD(ArrayList<Marcacao> lista) {
        marcacoesDB.removerAllMarcacoesBD();
        for (Marcacao m : lista) {
            marcacoesDB.adicionarMarcacaoBD(m);
        }
    }
    //endregion

    // region - Gestão de Lembretes
    public interface LembretesCallback {
        void onSuccess(List<pt.ipleiria.estg.dei.vetgestlink.models.Lembrete> lembretes);
        void onError(String error);
    }

    private List<pt.ipleiria.estg.dei.vetgestlink.models.Lembrete> parseLembretes(JSONArray jsonArray) {
        List<pt.ipleiria.estg.dei.vetgestlink.models.Lembrete> lista = new ArrayList<>();
        if (jsonArray == null) return lista;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                int id = obj.optInt("id", 0);
                String descricao = obj.optString("descricao", "");
                String createdAt = obj.optString("created_at", "");
                String updatedAt = obj.optString("updated_at", "");
                int userprofilesId = obj.optInt("userprofiles_id", 0);

                pt.ipleiria.estg.dei.vetgestlink.models.Lembrete l =
                        new pt.ipleiria.estg.dei.vetgestlink.models.Lembrete(id, descricao, createdAt, updatedAt, userprofilesId);

                lista.add(l);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Erro no parseLembretes: " + e.getMessage());
        }
        return lista;
    }

    public void getLembretes(String accessToken, LembretesCallback callback) {
        String url = buildUrl("lembrete/all?access-token=" + accessToken);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<pt.ipleiria.estg.dei.vetgestlink.models.Lembrete> lista = parseLembretes(response);
                    if (callback != null) callback.onSuccess(lista);
                },
                error -> {
                    String errorMsg = "Erro ao carregar lembretes";
                    if (error.networkResponse != null) {
                        errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                    } else if (error.getMessage() != null) {
                        errorMsg += ": " + error.getMessage();
                    }
                    if (callback != null) callback.onError(errorMsg);
                }
        );
        addToRequestQueue(request);
    }

    public void deletarLembrete(String accessToken, int lembreteId, MessageCallback callback) {
        String url = buildUrl("lembrete/delete/" + lembreteId + "?access-token=" + accessToken);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    try {
                        boolean success = response.optBoolean("success", false);
                        String message = response.optString("message", "");
                        if (success) {
                            if (callback != null) callback.onSuccess(message);
                        } else {
                            if (callback != null) callback.onError(message);
                        }
                    } catch (Exception e) {
                        if (callback != null) callback.onError("Erro ao processar resposta");
                    }
                },
                error -> {
                    String errorMsg = "Erro ao eliminar lembrete";
                    if (error.networkResponse != null) {
                        errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                    } else if (error.getMessage() != null) {
                        errorMsg += ": " + error.getMessage();
                    }
                    if (callback != null) callback.onError(errorMsg);
                }
        );

        addToRequestQueue(request);
    }

    public void atualizarLembrete(String accessToken, int lembreteId, String descricao, MessageCallback callback) {
        String url = buildUrl("lembrete/update/" + lembreteId + "?access-token=" + accessToken);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("descricao", descricao);
        } catch (JSONException e) {
            if (callback != null) callback.onError("Erro ao preparar dados");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonBody,
                response -> {
                    try {
                        boolean success = response.optBoolean("success", false);
                        String message = response.optString("message", "");
                        if (success) {
                            if (callback != null) callback.onSuccess(message);
                        } else {
                            if (callback != null) callback.onError(message);
                        }
                    } catch (Exception e) {
                        if (callback != null) callback.onError("Erro ao processar resposta");
                    }
                },
                error -> {
                    String errorMsg = "Erro ao atualizar lembrete";
                    if (error.networkResponse != null) {
                        errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                    } else if (error.getMessage() != null) {
                        errorMsg += ": " + error.getMessage();
                    }
                    if (callback != null) callback.onError(errorMsg);
                }
        );

        addToRequestQueue(request);
    }

    public void criarLembrete(String accessToken, String descricao, MessageCallback callback) {
        String url = buildUrl("lembrete/create?access-token=" + accessToken);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("descricao", descricao);
        } catch (JSONException e) {
            if (callback != null) callback.onError("Erro ao preparar dados");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try {
                        boolean success = response.optBoolean("success", false);
                        String message = response.optString("message", "");
                        if (success) {
                            if (callback != null) callback.onSuccess(message);
                        } else {
                            if (callback != null) callback.onError(message);
                        }
                    } catch (Exception e) {
                        if (callback != null) callback.onError("Erro ao processar resposta");
                    }
                },
                error -> {
                    String errorMsg = "Erro ao criar lembrete";
                    if (error.networkResponse != null) {
                        errorMsg += " (Código: " + error.networkResponse.statusCode + ")";
                    } else if (error.getMessage() != null) {
                        errorMsg += ": " + error.getMessage();
                    }
                    if (callback != null) callback.onError(errorMsg);
                }
        );

        addToRequestQueue(request);
    }

    // endregion

}
