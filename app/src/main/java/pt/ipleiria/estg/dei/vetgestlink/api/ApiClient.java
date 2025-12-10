package pt.ipleiria.estg.dei.vetgestlink.api;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;

public class ApiClient {

    private static ApiClient instance;
    private RequestQueue requestQueue;
    public static Context context;

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

    // Helper to get current base URL from Singleton
    public String getBaseUrl() {
        return Singleton.getInstance(context).getMainUrl();
    }

    // Optional: build a full endpoint URL
    public String buildUrl(String endpoint) {
        // endpoint example: "/login" or "login"
        String base = getBaseUrl();
        if (endpoint.startsWith("/")) {
            return base + endpoint;
        } else {
            return base + "/" + endpoint;
        }
    }
}
