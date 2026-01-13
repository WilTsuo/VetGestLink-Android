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

    // Constantes das SharedPreferences (Devem ser iguais às do LoginActivity)
    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_API_URL = "api_url";
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

        // 1. Carregar URL salvo ou usar padrão
        String savedUrl = sharedPreferences.getString(KEY_API_URL, "http://172.22.21.220");
        etServerUrl.setText(savedUrl);

        // 2. Carregar estado das notificações
        boolean isNotifEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, false);
        switchNotifications.setChecked(isNotifEnabled);

        // Listener do Switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Guardar preferência
            sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, isChecked).apply();

            if (isChecked) {
                checkPermissionAndStartService();
            } else {
                requireContext().stopService(new Intent(requireContext(), MqttNotificationService.class));
                showToast("Notificações Desativadas");
            }
        });

        // Listener Guardar Servidor (Lógica Real)
        setupClick(view, R.id.btnSaveServer, v -> {
            String newUrl = etServerUrl.getText().toString().trim();
            if (!newUrl.isEmpty()) {
                // Guardar nas SharedPreferences
                sharedPreferences.edit().putString(KEY_API_URL, newUrl).apply();

                // Atualizar Singleton para uso imediato na app
                Singleton.getInstance(requireContext()).setMainUrl(newUrl);

                showToast("Configuração guardada com sucesso!");
            } else {
                etServerUrl.setError("O URL não pode estar vazio");
            }
        });

        // Listener Testar Conexão
        setupClick(view, R.id.btnTestConnection, v -> {
            // Aqui poderia chamar um método do Singleton para fazer um ping à API
            showToast("A testar conexão com: " + etServerUrl.getText());
        });

        // Listener Logout (Lógica Real)
        setupClick(view, R.id.btnLogout, v -> performLogout());

        // Botões de Navegação (Mantêm-se como Toasts até criar os Fragments de destino)
        setupClick(view, R.id.btnChangePassword, v -> showToast("Funcionalidade: Alterar Senha"));
        setupClick(view, R.id.btnPersonalData, v -> showToast("Funcionalidade: Dados Pessoais"));
        setupClick(view, R.id.btnReportProblem, v -> showToast("Funcionalidade: Reportar Problema"));
        setupClick(view, R.id.btnTerms, v -> showToast("Funcionalidade: Termos e Condições"));
        setupClick(view, R.id.btnPrivacyPolicy, v -> showToast("Funcionalidade: Política de Privacidade"));
    }

    private void performLogout() {
        // 1. Parar serviço de notificações
        requireContext().stopService(new Intent(requireContext(), MqttNotificationService.class));

        // 2. Limpar SharedPreferences (Token, UserID, etc.)
        // Nota: Pode querer manter o URL do servidor (KEY_API_URL), então removemos chaves específicas ou limpamos tudo
        sharedPreferences.edit().clear().apply();

        // Se quiser manter o URL após logout, re-salve-o:
        // String currentUrl = etServerUrl.getText().toString();
        // sharedPreferences.edit().putString(KEY_API_URL, currentUrl).apply();

        // 3. Navegar para LoginActivity e limpar a stack de navegação
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
