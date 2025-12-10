package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class LoginJsonParser {

    public static String parserJsonLogin(){
        return null;
    }

    public static boolean isConnectionInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm!=null){
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni!= null && ni.isConnected();
        }
        return false;
    }
}
