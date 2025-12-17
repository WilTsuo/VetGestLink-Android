package pt.ipleiria.estg.dei.vetgestlink.Listeners;

import android.content.Context;

public interface AuthListener {
    void onUpdateLogin(String token);
    void onUpdateSignin(Context context, String response);
}
