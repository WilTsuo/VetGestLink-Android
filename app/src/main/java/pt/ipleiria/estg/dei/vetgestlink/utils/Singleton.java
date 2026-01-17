package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Listeners
import pt.ipleiria.estg.dei.vetgestlink.listeners.AnimaisListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.AuthListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.FaturasListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.LembretesListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.MarcacoesListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.NotaListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.NotasListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.MetodosPagamentoListener;

// Modelos
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;
import pt.ipleiria.estg.dei.vetgestlink.models.Marcacao;
import pt.ipleiria.estg.dei.vetgestlink.models.Nota;
import pt.ipleiria.estg.dei.vetgestlink.models.MetodoPagamento;
import pt.ipleiria.estg.dei.vetgestlink.models.UserProfile;
import pt.ipleiria.estg.dei.vetgestlink.models.Fatura;
import pt.ipleiria.estg.dei.vetgestlink.models.Lembrete;

// DBHelpers
import pt.ipleiria.estg.dei.vetgestlink.models.dbhelpers.FaturaDBHelper;
import pt.ipleiria.estg.dei.vetgestlink.models.dbhelpers.LembreteDBHelper;
import pt.ipleiria.estg.dei.vetgestlink.models.dbhelpers.NotaDBHelper;
import pt.ipleiria.estg.dei.vetgestlink.models.dbhelpers.AnimalDBHelper;
import pt.ipleiria.estg.dei.vetgestlink.models.dbhelpers.MarcacaoDBHelper;

public class Singleton {
    //region Listas em Memória (Cache)
    private ArrayList<Marcacao> marcacoes;
    private ArrayList<Nota> notas;
    private ArrayList<Fatura> faturas;
    private ArrayList<Lembrete> lembretes;
    private ArrayList<Animal> animais;
    // endregion

    //region Helpers de Base de Dados (SQLite)
    private LembreteDBHelper lembretesDB = null;
    private AnimalDBHelper animaisDB = null;
    private NotaDBHelper notasDB = null;
    private MarcacaoDBHelper marcacoesDB = null;
    private FaturaDBHelper faturasDB = null;
    // endregion

    //region Constantes de Preferências e API
    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_MAIN_URL = "main_url";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_API_AVAILABLE = "api_available";
    private static final String DEFAULT_MAIN_URL = "http://172.22.21.220/backend/web/api";
    // endregion

    //region Tags para o Volley
    private static final String TAG = "VetGestLink";
    private static final String TAG_ANIMAIS = "Vetgetlink-AnimaisService";
    private static final String TAG_NOTAS = "Vetgetlink-NotasService";
    private static final String TAG_LOGIN = "Vetgetlink-AuthService";
    private static final String TAG_ESQUECEU_PASS = "Vetgetlink-EsqueceuPassService";
    private static final String TAG_HEALTH = "Vetgetlink-HealthService";
    private static final String TAG_PROFILE = "Vetgetlink-ProfileService";
    private static final String TAG_LEMBRETES = "Vetgetlink-LembretesService";
    private static final String TAG_MARCACOES = "Vetgetlink-MarcacoesService";
    private static final String TAG_FATURAS = "Vetgetlink-FaturasService";
    private static final String TAG_METODOS_PAGAMENTO = "Vetgetlink-MetodosPagamentoService";
    private static Singleton instance;
    private final Context context;
    private RequestQueue requestQueue;
    private String mainUrl;
    private SharedPreferences sharedPreferences;
    // endregion

    //region Listeners da UI
    private AuthListener authListener;
    private NotasListener NotasListener;
    private NotaListener NotaListener;
    private AuthListener Authlistener;
    private AnimaisListener AnimaisListener;
    private FaturasListener FaturasListener;
    private MarcacoesListener MarcacoesListener;
    private LembretesListener LembretesListener;
    private MetodosPagamentoListener MetodosPagamentoListener;
    //endregion

    //region Listener para estado da API
    public interface ApiStateChangeListener {
        void onApiStateChanged(boolean available);
    }
    private final List<ApiStateChangeListener> apiStateListeners = new ArrayList<>();
    // endregion

    //region Setters para Listeners
    public void setNotasListener(NotasListener notasListener) { NotasListener = notasListener; }
    public void setAuthListener(AuthListener authListener) { this.authListener = authListener; }
    public void setNotaListener(NotaListener notaListener) { NotaListener = notaListener; }
    public void setAuthlistener(AuthListener authlistener) { Authlistener = authlistener; }
    public void setAnimaisListener(AnimaisListener animaisListener) { AnimaisListener = animaisListener; }
    public void setFaturasListener(FaturasListener faturasListener) { FaturasListener = faturasListener; }
    public void setMarcacoesListener(MarcacoesListener marcacoesListener) { MarcacoesListener = marcacoesListener; }
    public void setLembretesListener(LembretesListener lembretesListener) { LembretesListener = lembretesListener; }
    public void setMetodosPagamentoListener(MetodosPagamentoListener metodosPagamentoListener) { MetodosPagamentoListener = metodosPagamentoListener; }

    public void addApiStateChangeListener(ApiStateChangeListener listener) {
        if (listener != null && !apiStateListeners.contains(listener)) apiStateListeners.add(listener);
    }
    public void removeApiStateChangeListener(ApiStateChangeListener listener) { apiStateListeners.remove(listener); }
    // endregion

    //region Callbacks para comunicação assíncrona
    public interface MarcacoesCallback { void onSuccess(ArrayList<Marcacao> marcacoes); void onError(String error); }
    public interface LembretesCallback { void onSuccess(List<Lembrete> lembretes); void onError(String error); }
    public interface MessageCallback { void onSuccess(String message); void onError(String error); }
    public interface AtualizarLembreteCallback { void onSuccess(String message); void onError(String error); }
    public interface CriarLembreteCallback { void onSuccess(String message); void onError(String error); }
    public interface EliminarLembreteCallback { void onSuccess(String message); void onError(String error); }
    public interface CriarNotaCallback { void onSuccess(String message); void onError(String error); }
    public interface AtualizarNotaCallback { void onSuccess(String message); void onError(String error); }
    public interface EliminarNotaCallback { void onSuccess(String message); void onError(String error); }
    public interface FaturasCallback { void onSuccess(ArrayList<Fatura> faturas); void onError(String error); }
    public interface MetodosPagamentoCallback { void onSuccess(ArrayList<MetodoPagamento> metodos); void onError(String error); }
    public interface LoginCallback { void onSuccess(String token, UserProfile userProfile); void onError(String error); }
    public interface EsqueceuPassCallback { void onSuccess(String message); void onError(String error); }
    public interface NotasCallback { void onSuccess(List<Nota> notas); void onError(String error); }
    public interface AnimaisCallback { void onSuccess(List<Animal> animais); void onError(String error); }
    public interface ProfileCallback { void onSuccess(String nome, String email, String telefone, String moradaCompleta); void onError(String error); }
    public interface ProfileUpdateCallback { void onSuccess(String message); void onError(String error); }
    public interface ApiHealthCallback { void onResult(boolean responding); }
    public interface atualizarPalavraPasseCallback { void onSuccess(String message); void onError(String error); }
    // endregion

    // region Construtor e Instanciação
    private Singleton(Context context) {
        this.context = context.getApplicationContext();
        this.sharedPreferences = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.mainUrl = sharedPreferences.getString(KEY_MAIN_URL, DEFAULT_MAIN_URL);
        this.requestQueue = Volley.newRequestQueue(this.context);

        // Inicializa DBHelpers
        notasDB = new NotaDBHelper(this.context);
        marcacoesDB = new MarcacaoDBHelper(this.context);
        animaisDB = new AnimalDBHelper(this.context);
        lembretesDB = new LembreteDBHelper(this.context);
        faturasDB = new FaturaDBHelper(this.context);

        // Inicializa Listas em Memória
        notas = new ArrayList<>();
        marcacoes = new ArrayList<>();
        animais = new ArrayList<>();
        lembretes = new ArrayList<>();
        faturas = new ArrayList<>();
    }

    public static synchronized Singleton getInstance(Context context) {
        if (instance == null) {
            instance = new Singleton(context);
        }
        return instance;
    }
    // endregion

    // region Volley e Utilitários de Rede

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, Object tag) {
        req.setTag(tag != null ? tag : TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    public String getMainUrl() { return mainUrl; }

    public void setMainUrl(String url) {
        this.mainUrl = url;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_MAIN_URL, url).apply();
    }

    public String buildUrl(String endpoint) {
        String base = getMainUrl();
        return endpoint.startsWith("/") ? base + endpoint : base + "/" + endpoint;
    }

    // Adicione este método na região "Volley e Utilitários de Rede" do Singleton.java

    /**
     * Converte a URL da API (HTTP) para a URL do Broker MQTT (TCP).
     * Ex: http://192.168.1.10/backend/web/api -> tcp://192.168.1.10:1883
     */
    public String getMqttBrokerUrl() {
        String url = getMainUrl(); // Obtém a URL centralizada (ex: http://192.168.1.10/backend/web/api)

        // Remove a parte do caminho da API para ficar apenas com o domínio/IP
        if (url.contains("/backend/web/api")) {
            url = url.replace("/backend/web/api", "");
        }

        // Remove barras finais
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        // Substitui protocolo
        url = url.replace("http://", "tcp://").replace("https://", "tcp://");

        // Adiciona porta se não existir
        if (!url.contains(":1883")) {
            url += ":1883";
        }

        return url;
    }

    /**
     * NOVO MÉTODO: Converte a URL da API (backend) para a URL do Frontend (imagens/site) tava a dar pau aqui, ta solved.
     * Ex: http://192.168.1.10/backend/web/api -> http://192.168.1.10/frontend/web
     */
    public String getUrlFrontend() {
        if (mainUrl != null && mainUrl.contains("/backend/web/api")) {
            return mainUrl.replace("/backend/web/api", "/frontend/web");
        }
        // Fallback caso a URL não siga o padrão esperado, retorna a base
        return mainUrl;
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    // endregion

    // region Autenticação (LoginActivity)
    public void login(String username, String password, LoginCallback callback) {
        String url = buildUrl("auth/login");
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            callback.onError("Erro ao criar JSON de login");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    boolean success = response.optBoolean("success", false);
                    if (success) {
                        String token = LoginJsonParser.parseToken(response);
                        UserProfile userProfile = LoginJsonParser.parseUserProfile(response);

                        if (token != null && userProfile != null) {
                            callback.onSuccess(token, userProfile);
                        } else {
                            callback.onError("Erro ao processar dados do utilizador.");
                        }
                    } else {
                        callback.onError(response.optString("message", "Credenciais inválidas"));
                    }
                },
                error -> {
                    String err = (error.networkResponse != null) ? "Erro " + error.networkResponse.statusCode : "Erro de conexão";
                    callback.onError(err);
                }
        );
        addToRequestQueue(request,TAG_LOGIN);
    }
    // endregion

    // region Esqueceu a Palavra-Passe(EsqueceuPalavraPassActivity)
    public void recuperarPalavraPasse(String email, final EsqueceuPassCallback callback) {
        String url = buildUrl("auth/forgot");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
        } catch (JSONException e) {
            if (callback != null) callback.onError("Erro ao processar o email.");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    // A API retorna { "success": true/false, "message": "..." }
                    boolean success = response.optBoolean("success", false);
                    String message = response.optString("message", "Operação realizada.");

                    if (success) {
                        if (callback != null) callback.onSuccess(message);
                    } else {
                        if (callback != null) callback.onError(message);
                    }
                },
                error -> {
                    String errorMsg = "Erro de conexão ou email não encontrado.";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        // Tenta ler a mensagem de erro do servidor se existir
                        try {
                            String jsonString = new String(error.networkResponse.data);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            errorMsg = jsonObject.optString("message", errorMsg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (callback != null) callback.onError(errorMsg);
                }
        );
        addToRequestQueue(request, TAG_ESQUECEU_PASS);
    }
    //endregion

    // region Notas
    public void getNotas(String accessToken, Integer animalId, NotasCallback callback) {
        // Verificação Offline Imediata
        if (!isNetworkAvailable(context)) {
            ArrayList<Nota> local = getNotasBD();
            if (animalId != null) {
                ArrayList<Nota> filtradas = new ArrayList<>();
                for (Nota n : local) {
                    filtradas.add(n);
                }
                if (callback != null) callback.onSuccess(filtradas);
            } else {
                if (callback != null) callback.onSuccess(local);
            }
            return;
        }

        String url = (animalId != null) ?
                buildUrl("animal/" + animalId + "/notas?access-token=" + accessToken) :
                buildUrl("nota/all?access-token=" + accessToken);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<Nota> notasList = NotaJsonParser.parseNotas(response);
                    if (animalId == null) {
                        adicionarNotasBD(notasList);
                        this.notas = notasList;
                    } else {
                        for (Nota n : notasList) adicionarNotaBD(n);
                        this.notas = notasList;
                    }
                    if (callback != null) callback.onSuccess(notasList);
                },
                error -> {
                    ArrayList<Nota> local = getNotasBD();
                    if (callback != null) callback.onSuccess(local);
                }
        );
        addToRequestQueue(request, TAG_NOTAS);
    }

    public void criarNota(String accessToken, int animalId, String nota, CriarNotaCallback callback) {
        String url = buildUrl("nota/create?access-token=" + accessToken);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("animais_id", animalId);
            jsonBody.put("nota", nota);
        } catch (JSONException e) {
            if (callback != null) callback.onError("Erro ao preparar dados");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    boolean success = response.optBoolean("success", false);
                    String message = response.optString("message", "Operação concluída");
                    if (success && callback != null) callback.onSuccess(message);
                    else if (callback != null) callback.onError(message);
                },
                error -> { if (callback != null) callback.onError("Erro ao criar nota"); }
        );
        addToRequestQueue(request, TAG_NOTAS);
    }

    public void atualizarNota(String accessToken, int notaId, String nota, AtualizarNotaCallback callback) {
        String url = buildUrl("nota/" + notaId + "?access-token=" + accessToken);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nota", nota);
        } catch (JSONException e) { return; }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    if (response.optBoolean("success", false) && callback != null)
                        callback.onSuccess(response.optString("message"));
                    else if (callback != null)
                        callback.onError(response.optString("message"));
                },
                error -> { if (callback != null) callback.onError("Erro ao atualizar nota"); }
        );
        addToRequestQueue(request, TAG_NOTAS);
    }

    public void deletarNota(String accessToken, int notaId, EliminarNotaCallback callback) {
        String url = buildUrl("nota/" + notaId + "?access-token=" + accessToken);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    if (response.optBoolean("success", false) && callback != null) {
                        notasDB.removerNotaBD(notaId);
                        callback.onSuccess(response.optString("message"));
                    } else if (callback != null)
                        callback.onError(response.optString("message"));
                },
                error -> { if (callback != null) callback.onError("Erro ao eliminar nota"); }
        );
        addToRequestQueue(request, TAG_NOTAS);
    }

    // Métodos BD Notas
    public ArrayList<Nota> getNotasByAnimalNome(String animalNome) { return notasDB.getNotasByAnimalNome(animalNome); }
    public ArrayList<Nota> getNotasBD() { notas = notasDB.getAllNotasBD(); return new ArrayList<>(notas); }
    public void adicionarNotaBD(Nota nota) { notasDB.adicionarNotaBD(nota); }
    public void adicionarNotasBD(ArrayList<Nota> lista) {
        notasDB.removerAllNotasBD();
        for (Nota n : lista) adicionarNotaBD(n);
    }

    // endregion

    // region Perfil de Utilizador
    public void getPerfil(String token, ProfileCallback callback) {
        String url = buildUrl("profile?access-token=" + token);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    PerfilJsonParser.ProfileDetails details = PerfilJsonParser.parseProfile(response);
                    if (details != null && callback != null) {
                        callback.onSuccess(details.nome, details.email, details.telefone, details.moradaCompleta);
                    } else if (callback != null) {
                        callback.onError("Erro ao processar perfil");
                    }
                },
                error -> { if (callback != null) callback.onError("Erro na rede"); }
        );
        addToRequestQueue(request, TAG_PROFILE);
    }

    public void atualizarPerfil(String accessToken, String nome, String email, String telefone,
                                String rua, String porta, String postal, String localidade,
                                ProfileUpdateCallback callback) {
        String url = buildUrl("profile/update?access-token=" + accessToken);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nomecompleto", nome);
            jsonBody.put("email", email);
            jsonBody.put("telemovel", telefone);
            JSONObject jsonMorada = new JSONObject();
            jsonMorada.put("rua", rua);
            jsonMorada.put("nporta", porta);
            jsonMorada.put("cdpostal", postal);
            jsonMorada.put("localidade", localidade);
            jsonBody.put("morada", jsonMorada);
        } catch (JSONException e) { return; }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    if (callback != null) callback.onSuccess(response.optString("message", "Perfil atualizado!"));
                },
                error -> { if (callback != null) callback.onError("Erro ao atualizar perfil"); }
        );
        addToRequestQueue(request, TAG_PROFILE);
    }

    public void atualizarPalavraPasse(String accessToken, String atual, String nova, atualizarPalavraPasseCallback callback) {
        String url = buildUrl("profile/password?access-token=" + accessToken);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("current_password", atual);
            jsonBody.put("new_password", nova);
        } catch (JSONException e) { return; }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    boolean success = response.optBoolean("success");
                    String msg = response.optString("message");
                    if (success) callback.onSuccess(msg);
                    else callback.onError(msg);
                },
                error -> callback.onError("Erro ao alterar palavra-passe")
        );
        addToRequestQueue(request, TAG_PROFILE);
    }

    // endregion

    // region Animais
    public void getAnimais(String accessToken, AnimaisCallback callback) {
        if (!isNetworkAvailable(context)) {
            ArrayList<Animal> local = getAnimaisBD();
            this.animais = local;
            if (callback != null) callback.onSuccess(local);
            return;
        }

        String url = buildUrl("animal/all?access-token=" + accessToken);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Animal> lista = AnimalJsonParser.parseAnimais(response);
                    adicionarAnimaisBD(new ArrayList<>(lista));
                    this.animais = new ArrayList<>(lista);
                    if (callback != null) callback.onSuccess(lista);
                },
                error -> {
                    ArrayList<Animal> local = getAnimaisBD();
                    this.animais = local;
                    if (callback != null) callback.onSuccess(local);
                }
        );
        addToRequestQueue(request, TAG_ANIMAIS);
    }

    public void getNomesAnimais(String accessToken, AnimaisCallback callback) {
        String url = buildUrl("animal/nomes?access-token=" + accessToken);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Animal> lista = AnimalJsonParser.parseAnimaisNome(response);
                    if (callback != null) callback.onSuccess(lista);
                },
                error -> { if (callback != null) callback.onError("Erro ao carregar nomes"); }
        );
        addToRequestQueue(request, TAG_ANIMAIS);
    }

    // Métodos BD Animais
    public ArrayList<Animal> getAnimaisBD() { animais = animaisDB.getAllAnimaisBD(); return new ArrayList<>(animais); }
    public void adicionarAnimalBD(Animal animal) { animaisDB.adicionarAnimalBD(animal); }
    public void adicionarAnimaisBD(ArrayList<Animal> lista) {
        animaisDB.removerAllAnimaisBD();
        for (Animal a : lista) adicionarAnimalBD(a);
    }

    // endregion

    // region Marcações
    public void getMarcacoes(String accessToken, final MarcacoesCallback callback) {
        if (!isNetworkAvailable(context)) {
            ArrayList<Marcacao> local = getMarcacoesBD();
            this.marcacoes = local;
            if (callback != null) callback.onSuccess(local);
            if (MarcacoesListener != null) MarcacoesListener.onRefreshListaMarcacoes(local);
            return;
        }

        String url = buildUrl("marcacao/all?access-token=" + accessToken);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<Marcacao> lista = MarcacaoJsonParser.parseMarcacoes(response);
                    adicionarMarcacoesBD(lista);
                    this.marcacoes = lista;
                    if (callback != null) callback.onSuccess(lista);
                    if (MarcacoesListener != null) MarcacoesListener.onRefreshListaMarcacoes(lista);
                },
                error -> {
                    ArrayList<Marcacao> local = getMarcacoesBD();
                    this.marcacoes = local;
                    if (callback != null) callback.onSuccess(local);
                }
        );
        addToRequestQueue(request, TAG_MARCACOES);
    }

    public void getMarcacaoDetalhesAPI(int marcacaoId, Context context) {
        String token = getAccessToken();
        String url = buildUrl("marcacao/view/" + marcacaoId + "?access-token=" + token);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Marcacao marcacao = MarcacaoJsonParser.parseMarcacao(response);
                    if (marcacao != null && MarcacoesListener != null) {
                        MarcacoesListener.onMarcacaoDetalhesLoaded(marcacao);
                    }
                },
                error -> Toast.makeText(context, "Modo Offline: Conecte-se a API para obter informações detalhadas", Toast.LENGTH_LONG).show()
        );
        addToRequestQueue(request, TAG_MARCACOES);
    }

    // Métodos BD Marcações
    public ArrayList<Marcacao> getMarcacoesBD() { marcacoes = marcacoesDB.getAllMarcacoesBD(); return new ArrayList<>(marcacoes); }
    public void adicionarMarcacoesBD(ArrayList<Marcacao> lista) {
        marcacoesDB.removerAllMarcacoesBD();
        for (Marcacao m : lista) marcacoesDB.adicionarMarcacaoBD(m);
    }

    // endregion

    // region Lembretes
    public void getLembretes(String accessToken, LembretesCallback callback) {
        if (!isNetworkAvailable(context)) {
            ArrayList<Lembrete> local = getLembretesBD();
            this.lembretes = local;
            if (callback != null) callback.onSuccess(local);
            return;
        }

        String url = buildUrl("lembrete/all?access-token=" + accessToken);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Lembrete> lista = LembreteJsonParser.parseLembretes(response);
                    adicionarLembretesBD(lista);
                    this.lembretes = new ArrayList<>(lista);
                    if (callback != null) callback.onSuccess(lista);
                },
                error -> {
                    ArrayList<Lembrete> local = getLembretesBD();
                    this.lembretes = local;
                    if (callback != null) callback.onSuccess(local);
                }
        );
        addToRequestQueue(request, TAG_LEMBRETES);
    }

    public void criarLembrete(String accessToken, String descricao, CriarLembreteCallback callback) {
        String url = buildUrl("lembrete/create?access-token=" + accessToken);
        JSONObject jsonBody = new JSONObject();
        try { jsonBody.put("descricao", descricao); } catch (JSONException e) { return; }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    if (response.optBoolean("success", false) && callback != null)
                        callback.onSuccess(response.optString("message"));
                    else if (callback != null) callback.onError(response.optString("message"));
                },
                error -> { if (callback != null) callback.onError("Erro ao criar lembrete"); }
        );
        addToRequestQueue(request, TAG_LEMBRETES);
    }

    public void atualizarLembrete(String accessToken, int id, String descricao, CriarLembreteCallback callback) {
        String url = buildUrl("lembrete/update/" + id + "?access-token=" + accessToken);
        JSONObject jsonBody = new JSONObject();
        try { jsonBody.put("descricao", descricao); } catch (JSONException e) { return; }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    if (response.optBoolean("success", false) && callback != null)
                        callback.onSuccess(response.optString("message"));
                    else if (callback != null) callback.onError(response.optString("message"));
                },
                error -> { if (callback != null) callback.onError("Erro ao atualizar lembrete"); }
        );
        addToRequestQueue(request, TAG_LEMBRETES);
    }

    public void deletarLembrete(String accessToken, int id, EliminarLembreteCallback callback) {
        String url = buildUrl("lembrete/delete/" + id + "?access-token=" + accessToken);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    if (response.optBoolean("success", false) && callback != null) {
                        lembretesDB.removerLembreteBD(id); // Remove localmente
                        callback.onSuccess(response.optString("message"));
                    } else if (callback != null) callback.onError(response.optString("message"));
                },
                error -> { if (callback != null) callback.onError("Erro ao eliminar lembrete"); }
        );
        addToRequestQueue(request, TAG_LEMBRETES);
    }

    // Métodos BD Lembretes
    public ArrayList<Lembrete> getLembretesBD() { lembretes = lembretesDB.getAllLembretesBD(); return new ArrayList<>(lembretes); }
    public void adicionarLembreteBD(Lembrete l) { lembretesDB.adicionarLembreteBD(l); }
    public void adicionarLembretesBD(List<Lembrete> lista) {
        lembretesDB.removerAllLembretesBD();
        for (Lembrete l : lista) adicionarLembreteBD(l);
    }

    // endregion

    // region Faturas
    public void getFaturas(String accessToken, final FaturasCallback callback) {
        if (!isNetworkAvailable(context)) {
            ArrayList<Fatura> local = getFaturasBD();
            this.faturas = local;
            if (callback != null) callback.onSuccess(local);
            if (FaturasListener != null) FaturasListener.onRefreshListaFaturas(local);
            return;
        }

        String url = buildUrl("fatura/all?access-token=" + accessToken);
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<Fatura> lista = FaturaJsonParser.parseFaturas(response);
                    adicionarFaturasBD(lista);
                    this.faturas = lista;
                    if (callback != null) callback.onSuccess(lista);
                    if (FaturasListener != null) FaturasListener.onRefreshListaFaturas(lista);
                },
                error -> {
                    ArrayList<Fatura> local = getFaturasBD();
                    this.faturas = local;
                    if (callback != null) callback.onSuccess(local);
                    if (FaturasListener != null) FaturasListener.onRefreshListaFaturas(local);
                }
        );
        addToRequestQueue(req, TAG_FATURAS);
    }

    public void getFaturaDetalhesAPI(int faturaId, Context context) {
        String token = getAccessToken();
        String url = buildUrl("fatura/view/" + faturaId + "?access-token=" + token);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Fatura faturaDetalhada = FaturaJsonParser.parseFaturaDetalhes(response);
                    if (faturaDetalhada != null) {
                        Fatura existente = getFatura(faturaId);
                        if (existente != null) {
                            existente.setLinhas(faturaDetalhada.getLinhas());
                            existente.setClienteNome(faturaDetalhada.getClienteNome());
                            existente.setClienteNif(faturaDetalhada.getClienteNif());
                            if (FaturasListener != null) FaturasListener.onFaturaDetalhesLoaded(existente);
                        } else {
                            if (FaturasListener != null) FaturasListener.onFaturaDetalhesLoaded(faturaDetalhada);
                        }
                    } else {
                        Toast.makeText(context, "Erro ao processar detalhes", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Modo Offline: Conecte-se a API para obter informações detalhadas", Toast.LENGTH_LONG).show()
        );
        addToRequestQueue(request, TAG_FATURAS);
    }

    public void pagarFatura(int faturaId, int metodoId, Context context, final MessageCallback callback) {
        String token = getAccessToken();
        String url = buildUrl("fatura/pay/" + faturaId + "?access-token=" + token);
        JSONObject body = new JSONObject();
        try { body.put("metodospagamentos_id", metodoId); } catch (JSONException e) { return; }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, body,
                response -> {
                    if (response.optBoolean("success", false)) {
                        if (callback != null) callback.onSuccess(response.optString("message"));
                        getFaturas(token, null); // Atualiza lista
                    } else {
                        if (callback != null) callback.onError(response.optString("message"));
                    }
                },
                error -> { if (callback != null) callback.onError("Erro ao pagar fatura"); }
        );
        addToRequestQueue(request, TAG_FATURAS);
    }

    public Fatura getFatura(int id) {
        if (faturas != null) {
            for (Fatura f : faturas) if (f.getId() == id) return f;
        }
        return null;
    }

    // Métodos BD Faturas
    public ArrayList<Fatura> getFaturasBD() { faturas = faturasDB.getAllFaturasBD(); return new ArrayList<>(faturas); }
    public void adicionarFaturaBD(Fatura f) { faturasDB.adicionarFaturaBD(f); }
    public void adicionarFaturasBD(ArrayList<Fatura> lista) {
        faturasDB.removerAllFaturasBD();
        for (Fatura f : lista) adicionarFaturaBD(f);
    }

    // endregion

    // region Métodos de Pagamento
    public void getMetodosPagamento(String token, final MetodosPagamentoCallback callback) {
        String url = buildUrl("fatura/paymentmethods?access-token=" + token);
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<MetodoPagamento> metodos = MetodoPagamentoJsonParser.parseMetodosPagamento(response);
                    if (callback != null) callback.onSuccess(metodos);
                    if (MetodosPagamentoListener != null) MetodosPagamentoListener.onRefreshMetodosPagamento(metodos);
                },
                error -> { if (callback != null) callback.onError("Erro ao obter métodos de pagamento"); }
        );
        addToRequestQueue(req, TAG_METODOS_PAGAMENTO);
    }

    // endregion

    // region Verificação de Estado da API (Health Check)
    public void isApiResponding(final ApiHealthCallback callback) {
        String url = buildUrl("health");
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    setApiAvailable(true);
                    if (callback != null) callback.onResult(true);
                },
                error -> {
                    setApiAvailable(false);
                    if (callback != null) callback.onResult(false);
                });
        request.setRetryPolicy(new DefaultRetryPolicy(200, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(request, TAG_HEALTH);
    }

    private void setApiAvailable(boolean available) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean wasAvailable = prefs.getBoolean(KEY_API_AVAILABLE, true);
        prefs.edit().putBoolean(KEY_API_AVAILABLE, available).apply();
        if (wasAvailable != available) notifyApiStateChange(available);
    }

    private void notifyApiStateChange(boolean available) {
        for (ApiStateChangeListener listener : apiStateListeners) {
            if (listener != null) listener.onApiStateChanged(available);
        }
    }

    public boolean getApiAvailable() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_API_AVAILABLE, true);
    }

    public void updateApiState(Context context, final ApiHealthCallback callback) {
        if (!isNetworkAvailable(context)) {
            setApiAvailable(false);
            if (callback != null) callback.onResult(false);
            return;
        }
        isApiResponding(callback);
    }

    public void quickCheckApiState(Context context, final ApiHealthCallback callback) {
        if (!isNetworkAvailable(context)) {
            setApiAvailable(false);
            if (callback != null) callback.onResult(false);
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastCheck = prefs.getLong("last_api_check", 0);
        long now = System.currentTimeMillis();
        if (now - lastCheck < 3000) {
            if (callback != null) callback.onResult(prefs.getBoolean(KEY_API_AVAILABLE, true));
            return;
        }
        prefs.edit().putLong("last_api_check", now).apply();
        isApiResponding(callback);
    }

    private boolean isNetworkAvailable(Context context) {
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        android.net.Network network = cm.getActiveNetwork();
        if (network == null) return false;
        android.net.NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        return caps != null && (caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) ||
                caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    // endregion
}
