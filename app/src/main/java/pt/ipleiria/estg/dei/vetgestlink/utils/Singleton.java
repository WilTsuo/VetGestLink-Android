package pt.ipleiria.estg.dei.vetgestlink.utils;

import static pt.ipleiria.estg.dei.vetgestlink.utils.LoginJsonParser.isConnectionInternet;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.Listeners.AnimaisListener;
import pt.ipleiria.estg.dei.vetgestlink.Listeners.AuthListener;
import pt.ipleiria.estg.dei.vetgestlink.Listeners.FaturasListener;
import pt.ipleiria.estg.dei.vetgestlink.Listeners.LembretesListener;
import pt.ipleiria.estg.dei.vetgestlink.Listeners.MarcacoesListener;
import pt.ipleiria.estg.dei.vetgestlink.Listeners.NotaListener;
import pt.ipleiria.estg.dei.vetgestlink.Listeners.NotasListener;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;
import pt.ipleiria.estg.dei.vetgestlink.model.NotaDBHelper;

public class Singleton {

    private static Singleton instance;
    private ArrayList<Nota> notas;
    private NotaDBHelper notasDB=null;

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_MAIN_URL = "main_url";
    private static final String DEFAULT_MAIN_URL = "http://172.22.21.220/backend/web/api";
    private static String IP = "172.22.21.220";

    private RequestQueue requestQueue;
    //region - API endpoints
    public static String mURLAPILogin = "http://" + IP + "/backend/web/api/auth/login";
    public static String mURLAPINota = "http://" + IP + "/backend/web/api/nota?access-token=hnPtsND0o3Fe3ytvU9YcEDAkpsYkDPY2";//hardcoded for now
    //endregion
    private String mainUrl;
    private static RequestQueue volleyQueue = null;
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

    public static synchronized Singleton getInstance(Context context) {
        if (instance == null) {
            instance = new Singleton(context);
        }
        return instance;
    }

    private Singleton(Context context) {
        notas = new ArrayList<>();
        notasDB = new NotaDBHelper(context);

    }

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

    //region - auth API
    public void loginAPI(Context context, String username, String password)
    {
        if(! isConnectionInternet(context))
        {
            //nao tem ligacao a internet
            Toast.makeText(context, "Sem Internet", Toast.LENGTH_SHORT).show();
        }
        else
        {
            JSONObject authJson = new JSONObject();
            try
            {
                authJson.put("username", username);
                authJson.put("password", password);

            } catch (JSONException e)
            {
                throw new RuntimeException(e);
            }

            JsonObjectRequest reqInsert = new JsonObjectRequest(Request.Method.POST, mURLAPILogin,authJson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    //handle success call to API

                    String token = AuthJsonParser.parserJsonLogin(response);

                    //atualizar a vista
                    if(authListener != null)
                        authListener.onUpdateLogin(token);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //something went wrong
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            volleyQueue.add(reqInsert);
        }
    }

    //endregion

    //region - Notas API

    public void getAllNotasAPI(final Context context)
    {
        if(!isConnectionInternet(context))
        {
            //nao tem ligacao a internet
            Toast.makeText(context, "Sem Internet", Toast.LENGTH_SHORT).show();
        }
        else
        {
            JsonArrayRequest reqSelect = new JsonArrayRequest(Request.Method.GET, mURLAPINota, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    //handle success call to API

                    notas = NotaJsonParser.parserJsonNotas(response);

                    //atualizar a vista
                    if (NotasListener != null)
                    {
                        NotasListener.onRefreshListaNotas(notas);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            volleyQueue.add(reqSelect);
        }
    }

    public Nota getProduto(int id)
    {
        for (Nota produto: notas)
        {
            if(produto.getId() == id)
                return produto;
        }

        return null;
    }

    public ArrayList<Nota> getProdutos()
    {
        return new ArrayList<>(notas);
    }

    //endregion
}
