package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.services.MqttNotificationService;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.activities.LoginActivity;

public class DefinicoesFragment extends Fragment {

    private TextInputEditText etServerUrl;
    private SwitchMaterial switchNotifications;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    // KEY_API_URL removida pois o Singleton gere o URL com a chave "main_url"
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startNotificationService();
                } else {
                    switchNotifications.setChecked(false);
                    showToast("Permissão necessária para notificações");
                }
            });

    public DefinicoesFragment() { /* Required empty public constructor */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_definicoes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        etServerUrl = view.findViewById(R.id.etServerUrl);
        switchNotifications = view.findViewById(R.id.switchNotifications);

        // 1. Carregar URL diretamente do Singleton (Garante que é o mesmo do Login)
        String currentUrl = Singleton.getInstance(requireContext()).getMainUrl();
        etServerUrl.setText(currentUrl);

        // 2. Carregar estado das notificações
        boolean isNotifEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, false);
        switchNotifications.setChecked(isNotifEnabled);

        // Listener do Switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, isChecked).apply();
            if (isChecked) {
                checkPermissionAndStartService();
            } else {
                requireContext().stopService(new Intent(requireContext(), MqttNotificationService.class));
                showToast("Notificações Desativadas");
            }
        });

        // Listener Guardar Servidor
        setupClick(view, R.id.btnSaveServer, v -> {
            String newUrl = etServerUrl.getText().toString().trim();
            if (!newUrl.isEmpty()) {
                // Atualiza o Singleton (que guarda automaticamente nas SharedPreferences corretas)
                Singleton.getInstance(requireContext()).setMainUrl(newUrl);
                showToast("Configuração guardada com sucesso!");
            } else {
                etServerUrl.setError("O URL não pode estar vazio");
            }
        });

        // Listener Testar Conexão
        setupClick(view, R.id.btnTestConnection, v -> {
            String inputUrl = etServerUrl.getText().toString().trim();
            if (inputUrl.isEmpty()) {
                etServerUrl.setError("O URL não pode estar vazio");
                return;
            }

            // Atualiza o Singleton temporariamente para testar o novo URL inserido
            Singleton.getInstance(requireContext().getApplicationContext()).setMainUrl(inputUrl);

            // Usa o método do Singleton para verificar a saúde da API
            Singleton.getInstance(requireContext().getApplicationContext()).isApiResponding(responding -> {
                if (responding) {
                    showToast("Conexão estabelecida com sucesso!");
                } else {
                    showToast("Erro ao conectar: O servidor não respondeu.");
                }
            });
        });

        // Listener Logout
        setupClick(view, R.id.btnLogout, v -> performLogout());

        // Botões de Navegação
        setupClick(view, R.id.btnChangePassword, v -> showToast("Funcionalidade: Alterar Senha"));
        setupClick(view, R.id.btnPersonalData, v -> showToast("Funcionalidade: Dados Pessoais"));
        setupClick(view, R.id.btnReportProblem, v -> showToast("Funcionalidade: Reportar Problema"));
        setupClick(view, R.id.btnTerms, v -> showToast("Funcionalidade: Termos e Condições"));
        setupClick(view, R.id.btnPrivacyPolicy, v -> showToast("Funcionalidade: Política de Privacidade"));
    }

    private void performLogout() {
        requireContext().stopService(new Intent(requireContext(), MqttNotificationService.class));

        // Limpa as preferências, mas mantém o URL do servidor para o próximo login
        String currentUrl = Singleton.getInstance(requireContext()).getMainUrl();
        sharedPreferences.edit().clear().apply();

        // Restaura o URL no Singleton/Prefs após o clear
        Singleton.getInstance(requireContext()).setMainUrl(currentUrl);

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void setupClick(View parent, int id, View.OnClickListener listener) {
        View view = parent.findViewById(id);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    private void checkPermissionAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                startNotificationService();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            startNotificationService();
        }
    }

    private void startNotificationService() {
        requireContext().startService(new Intent(requireContext(), MqttNotificationService.class));
        showToast("Notificações Ativadas");
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
