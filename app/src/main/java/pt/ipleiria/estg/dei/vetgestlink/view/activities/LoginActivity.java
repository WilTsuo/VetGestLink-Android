package pt.ipleiria.estg.dei.vetgestlink.view.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log; // Importante para debug
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.UserProfile;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginDebug"; // Tag para filtrar no Logcat
    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_USERNAME = "saved_username";
    private static final String KEY_PASSWORD = "saved_password";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private CheckBox cbRememberMe;
    private TextView tvForgotPassword;
    private MaterialButton btnLogin;
    private TextView tvSignUp;
    private ImageView btnChangeUrl;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Verifica login automático
        if (sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)) {
            // DEBUG: Verificar se temos o ID guardado antes de entrar automaticamente
            int savedId = sharedPreferences.getInt(KEY_USER_ID, -1);
            Log.d(TAG, "Login Automático. ID guardado: " + savedId);

            // Se o ID for inválido (-1 ou 0), forçamos o utilizador a fazer login novamente
            if (savedId <= 0) {
                Log.w(TAG, "ID inválido no login automático. A forçar novo login.");
                clearCredentials(); // Limpa para obrigar a meter a senha
            } else {
                String username = sharedPreferences.getString(KEY_USERNAME, "");
                goToMainActivity(username);
                return;
            }
        }

        setContentView(R.layout.activity_login);
        Singleton.getInstance(getApplicationContext());

        initializeViews();
        loadSavedCredentials();
        setupListeners();
    }

    private void initializeViews() {
        btnChangeUrl = findViewById(R.id.btnChangeUrl);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvSignUp.setOnClickListener(v -> {
            String url = Singleton.getInstance(getApplicationContext()).getMainUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        btnChangeUrl.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            android.view.LayoutInflater inflater = getLayoutInflater();
            android.view.View dialogView = inflater.inflate(R.layout.dialog_change_url, null);
            android.widget.EditText etMainUrl = dialogView.findViewById(R.id.etMainUrl);
            etMainUrl.setText(Singleton.getInstance(getApplicationContext()).getMainUrl());

            builder.setView(dialogView)
                    .setTitle("Configurar URL")
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String url = etMainUrl.getText().toString().trim();
                        if (!url.isEmpty()) {
                            Singleton.getInstance(getApplicationContext()).setMainUrl(url);
                            Toast.makeText(LoginActivity.this, "URL guardada", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .create().show();
        });
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        boolean rememberMe = cbRememberMe.isChecked();

        if (!validateInputs(username, password)) return;

        btnLogin.setEnabled(false);
        btnLogin.setText("Entrando...");

        Singleton.getInstance(getApplicationContext()).login(username, password, new Singleton.LoginCallback() {
            @Override
            public void onSuccess(String token, UserProfile userProfile) {
                Log.d(TAG, "Login OK. ID recebido: " + userProfile.getId());

                // BLOQUEIO DE SEGURANÇA: Se o ID for 0, não deixa entrar
                if (userProfile.getId() <= 0) {
                    runOnUiThread(() -> {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Entrar");
                        Toast.makeText(LoginActivity.this, "Erro: ID de utilizador inválido (0). Contacte o suporte.", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                // Se chegou aqui, o ID é válido (ex: 9)
                saveSessionData(token, userProfile.getId());

                if (rememberMe) {
                    saveCredentials(username, password);
                } else {
                    clearCredentials();
                }

                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Bem-vindo " + userProfile.getUsername(), Toast.LENGTH_SHORT).show();
                    goToMainActivity(username);
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Erro Login: " + error);
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Entrar");
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void saveSessionData(String token, int userId) {
        Log.d(TAG, "A guardar sessão -> Token: [Oculto], UserID: " + userId);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    private void goToMainActivity(String username) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user_username", username);
        startActivity(intent);
        finish();
    }

    private boolean validateInputs(String username, String password) {
        if (username.isEmpty()) {
            etUsername.setError("Nome de utilizador é obrigatório");
            etUsername.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Senha é obrigatória");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void loadSavedCredentials() {
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
        if (rememberMe) {
            String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            etUsername.setText(savedUsername);
            etPassword.setText(savedPassword);
            cbRememberMe.setChecked(true);
        }
    }

    private void saveCredentials(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_REMEMBER_ME, true);
        editor.apply();
    }

    private void clearCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.putBoolean(KEY_REMEMBER_ME, false);
        editor.apply();
    }
}
