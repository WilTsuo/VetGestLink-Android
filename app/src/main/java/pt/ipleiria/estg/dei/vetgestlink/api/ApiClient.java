package pt.ipleiria.estg.dei.vetgestlink.api;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Cliente HTTP centralizado usando Volley
 */
public class ApiClient {

    private static ApiClient instance;
    private RequestQueue requestQueue;
    private static Context context;

    // URL base da API - WAMP Server
    // IMPORTANTE:
    // - Para EMULADOR Android: use 10.0.2.2 (representa o localhost da sua máquina)
    // - Para DISPOSITIVO FÍSICO: substitua por seu IP local (ex: 192.168.1.100)
    public static final String BASE_URL = "http://10.0.2.2/2_ano_1_semestre/Projeto/vetgestlink/backend/web/api/";

    private ApiClient(Context ctx) {
        context = ctx.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

