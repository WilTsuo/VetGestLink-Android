package pt.ipleiria.estg.dei.vetgestlink.view.activities;

import static pt.ipleiria.estg.dei.vetgestlink.api.ApiClient.context;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.api.ApiClient;
import pt.ipleiria.estg.dei.vetgestlink.api.AuthApiService;
import pt.ipleiria.estg.dei.vetgestlink.model.UserProfile;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;

/**
 * View - Activity de Login
 * Responsável pela interface de autenticação do utilizador
 */
public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_USERNAME = "saved_username";
    private static final String KEY_PASSWORD = "saved_password";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    // Views
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private CheckBox cbRememberMe;
    private TextView tvForgotPassword;
    private MaterialButton btnLogin;
    private TextView tvSignUp;
    private MaterialButton btnChangeUrl;

    // API Service
    private AuthApiService authApiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Singleton.getInstance(getApplicationContext());

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Inicializar serviço de autenticação
        authApiService = new AuthApiService(this);

        // Inicializar views
        initializeViews();

        // Carregar credenciais salvas (se houver)
        loadSavedCredentials();

        // Configurar listeners
        setupListeners();
    }

    /**
     * Inicializa todas as views da tela
     */
    private void initializeViews() {
        btnChangeUrl = findViewById(R.id.btnChangeUrl);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    /**
     * Configura os listeners dos elementos da UI
     */
    private void setupListeners() {
        // Botão de login
        btnLogin.setOnClickListener(v -> performLogin());

        // Link "Esqueci a senha"
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Link "Solicite acesso"
        tvSignUp.setOnClickListener(v -> {
            String url = ApiClient.getInstance(context).getBaseUrl(); //geturl
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        btnChangeUrl.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(LoginActivity.this);

            // Inflate custom view
            android.view.LayoutInflater inflater = getLayoutInflater();
            android.view.View dialogView = inflater.inflate(R.layout.dialog_change_url, null);
            android.widget.EditText etMainUrl = dialogView.findViewById(R.id.etMainUrl);

            // Pre-fill current value
            etMainUrl.setText(Singleton.getInstance(getApplicationContext()).getMainUrl());

            builder.setView(dialogView)
                    .setTitle("Configurar URL")
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String url = ApiClient.getInstance(context).getBaseUrl(); //geturl
                        if (!url.isEmpty()) {
                            Singleton.getInstance(getApplicationContext()).setMainUrl(url);
                            Toast.makeText(LoginActivity.this, "URL guardada", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });

    }

    /**
     * Executa o processo de login
     */
    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        boolean rememberMe = cbRememberMe.isChecked();

        // Validar campos
        if (!validateInputs(username, password)) {
            return;
        }

        // Desabilitar botão e mostrar loading
        btnLogin.setEnabled(false);
        btnLogin.setText("Entrando...");

        // Chamar API de login
        authApiService.login(username, password, new AuthApiService.LoginCallback() {
            @Override
            public void onSuccess(String token, UserProfile userProfile) {
                // Salvar token
                saveAccessToken(token);

                // Salvar credenciais se "Lembrar-me" estiver marcado
                if (rememberMe) {
                    saveCredentials(username, password);
                } else {
                    clearCredentials();
                }

                // Mostrar mensagem de sucesso e navegar para MainActivity na UI thread
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this,
                            "Bem-vindo, " + userProfile.getNomecompleto() + "!",
                            Toast.LENGTH_SHORT).show();

                    // Navegar para a MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user_username", username);
                    startActivity(intent);

                    // Finalizar LoginActivity
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                // Reabilitar botão na UI thread
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText(getString(R.string.login_button));

                    // Mostrar erro
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Salva o token de acesso no SharedPreferences
     */
    private void saveAccessToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
    }

    /**
     * Valida os campos de entrada
     */
    private boolean validateInputs(String username, String password) {
        // Validar username
        if (username.isEmpty()) {
            etUsername.setError("Nome de utilizador é obrigatório");
            etUsername.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            etUsername.setError("Nome de utilizador deve ter no mínimo 3 caracteres");
            etUsername.requestFocus();
            return false;
        }

        // Validar senha
        if (password.isEmpty()) {
            etPassword.setError("Senha é obrigatória");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Senha deve ter no mínimo 6 caracteres");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Carrega credenciais salvas do SharedPreferences
     */
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

    /**
     * Salva credenciais no SharedPreferences
     */
    private void saveCredentials(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_REMEMBER_ME, true);
        editor.apply();
    }

    /**
     * Limpa credenciais do SharedPreferences
     */
    private void clearCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.putBoolean(KEY_REMEMBER_ME, false);
        editor.apply();
    }
}
