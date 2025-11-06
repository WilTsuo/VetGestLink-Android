package pt.ipleiria.estg.dei.vetgestlink.view.activities;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import pt.ipleiria.estg.dei.vetgestlink.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button buttonSendReset;
    private Button buttonBackToLogin;
    private CardView mainCard;
    private CardView successCard;
    private TextView textViewEmailDisplay;
    private Button buttonBackToLoginSuccess;
    private Button buttonResendEmail;

    private String userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Inicializar views
        initViews();

        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        // Main form views
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSendReset = findViewById(R.id.buttonSendReset);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);
        mainCard = findViewById(R.id.mainCard);

        // Success views
        successCard = findViewById(R.id.successCard);
        textViewEmailDisplay = findViewById(R.id.textViewEmailDisplay);
        buttonBackToLoginSuccess = findViewById(R.id.buttonBackToLoginSuccess);
        buttonResendEmail = findViewById(R.id.buttonResendEmail);
    }

    private void setupListeners() {
        // Botão enviar link de recuperação
        buttonSendReset.setOnClickListener(v -> sendResetEmail());

        // Botão voltar ao login (formulário)
        buttonBackToLogin.setOnClickListener(v -> finish());

        // Botão voltar ao login (sucesso)
        buttonBackToLoginSuccess.setOnClickListener(v -> finish());

        // Botão reenviar email
        buttonResendEmail.setOnClickListener(v -> resendEmail());
    }

    private void sendResetEmail() {
        // Validar email
        userEmail = editTextEmail.getText().toString().trim();

        if (userEmail.isEmpty()) {
            editTextEmail.setError("Por favor, insira o seu e-mail");
            editTextEmail.requestFocus();
            return;
        }

        if (!isValidEmail(userEmail)) {
            editTextEmail.setError("Por favor, insira um e-mail válido");
            editTextEmail.requestFocus();
            return;
        }

        // Desabilitar botão e mostrar loading
        buttonSendReset.setEnabled(false);
        buttonSendReset.setText("Enviando...");

        // Simular chamada à API (substituir com chamada real)
        new Handler().postDelayed(() -> {
            // Aqui você faria a chamada real à API
            // POST /auth/forgot-password com { email: userEmail }

            // Após sucesso da API:
            showSuccessState();

            // Resetar botão
            buttonSendReset.setEnabled(true);
            buttonSendReset.setText("Enviar Link de Recuperação");
        }, 1500);
    }

    private void resendEmail() {
        // Desabilitar botão temporariamente
        buttonResendEmail.setEnabled(false);
        buttonResendEmail.setText("Reenviando...");

        // Simular reenvio
        new Handler().postDelayed(() -> {
            // Chamar API novamente
            // POST /auth/forgot-password com { email: userEmail }

            // Mostrar feedback
            // Toast.makeText(this, "E-mail reenviado!", Toast.LENGTH_SHORT).show();

            // Resetar botão
            buttonResendEmail.setEnabled(true);
            buttonResendEmail.setText("Reenviar E-mail");
        }, 1500);
    }

    private void showSuccessState() {
        // Esconder formulário principal
        mainCard.setVisibility(View.GONE);

        // Mostrar card de sucesso
        successCard.setVisibility(View.VISIBLE);

        // Mostrar email do usuário
        textViewEmailDisplay.setText(userEmail);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        // Voltar ao login
        super.onBackPressed();
        finish();
    }
}