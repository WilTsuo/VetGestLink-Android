package pt.ipleiria.estg.dei.vetgestlink.listeners;

import android.content.Context;

public interface AuthListener {
    void onUpdateLogin(String token);
    void onUpdateSignin(Context context, String response);
}
