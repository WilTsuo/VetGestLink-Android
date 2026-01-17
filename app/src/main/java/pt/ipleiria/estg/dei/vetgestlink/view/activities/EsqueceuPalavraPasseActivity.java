package pt.ipleiria.estg.dei.vetgestlink.view.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;

public class EsqueceuPalavraPasseActivity extends AppCompatActivity {

    private EditText etEmail;
    private CardView mainCard, successCard;
    private TextView tvEmailDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueceu_palavra_pass);

        // 1. Inicializar as Views com os IDs EXATOS do seu XML
        etEmail = findViewById(R.id.editTextEmail); // ID corrigido
        Button btnSend = findViewById(R.id.buttonSendReset);
        Button btnBack = findViewById(R.id.buttonBackToLogin);

        // Elementos do Card de Sucesso
        mainCard = findViewById(R.id.mainCard);
        successCard = findViewById(R.id.successCard);
        tvEmailDisplay = findViewById(R.id.textViewEmailDisplay);
        Button btnBackSuccess = findViewById(R.id.buttonBackToLoginSuccess);
        Button btnResend = findViewById(R.id.buttonResendEmail);

        // 2. Configurar Listeners
        btnSend.setOnClickListener(v -> sendResetEmail());

        // Botões de voltar fecham a atividade
        View.OnClickListener backListener = v -> finish();
        btnBack.setOnClickListener(backListener);
        btnBackSuccess.setOnClickListener(backListener);

        // Botão de reenviar chama a função novamente
        btnResend.setOnClickListener(v -> sendResetEmail());
    }

    private void sendResetEmail() {
        // Verificação de segurança para evitar o crash
        if (etEmail == null) {
            Toast.makeText(this, "Erro interno: Campo de email não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Por favor, insira o email");
            return;
        }

        // Chamada à API via Singleton
        Singleton.getInstance(this).recuperarPalavraPasse(email, new Singleton.EsqueceuPassCallback() {
            @Override
            public void onSuccess(String message) {
                // Atualiza a UI para mostrar o card de sucesso
                if (mainCard != null) mainCard.setVisibility(View.GONE);
                if (successCard != null) successCard.setVisibility(View.VISIBLE);
                if (tvEmailDisplay != null) tvEmailDisplay.setText(email);

                Toast.makeText(EsqueceuPalavraPasseActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EsqueceuPalavraPasseActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
