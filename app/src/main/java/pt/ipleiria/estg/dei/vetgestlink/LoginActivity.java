package pt.ipleiria.estg.dei.vetgestlink;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private CheckBox cbRememberMe;
    private TextView tvForgotPassword;
    private MaterialButton btnLogin;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar views
        initializeViews();

        // Configurar listeners
        setupListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void setupListeners() {
        // Click no botão de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        // Click em "Esqueci a senha"
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navegar para tela de recuperação de senha
                Toast.makeText(LoginActivity.this, "Recuperar senha", Toast.LENGTH_SHORT).show();
            }
        });

        // Click em "Solicite acesso"
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navegar para tela de registro
                Toast.makeText(LoginActivity.this, "Solicitar acesso", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        boolean rememberMe = cbRememberMe.isChecked();

        // Validar campos
        if (!validateInputs(email, password)) {
            return;
        }

        // TODO: Implementar lógica de autenticação
        // Por enquanto, apenas mostra uma mensagem
        Toast.makeText(
                this,
                "Login: " + email + ", Lembrar: " + rememberMe,
                Toast.LENGTH_SHORT
        ).show();

        // Aqui você pode:
        // 1. Fazer chamada à API
        // 2. Salvar credenciais se "Lembrar-me" estiver marcado
        // 3. Navegar para a tela principal após autenticação bem-sucedida
    }

    private boolean validateInputs(String email, String password) {
        // Validar email
        if (email.isEmpty()) {
            etEmail.setError("Email é obrigatório");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email inválido");
            etEmail.requestFocus();
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
}