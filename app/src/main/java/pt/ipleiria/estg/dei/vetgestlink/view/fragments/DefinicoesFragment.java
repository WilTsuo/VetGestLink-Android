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

        switchNotifications = view.findViewById(R.id.switchNotifications);

        // 1. Carregar URL
        String savedUrl = sharedPreferences.getString(KEY_API_URL, "http://172.22.21.220");
        etServerUrl.setText(savedUrl);

        // 2. Carregar estado do Switch de Notificações
        boolean isNotifEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, false);
        switchNotifications.setChecked(isNotifEnabled);

        // 3. Listener do Switch
        switchNotifications.setOnCheckedChangeListener((v, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, isChecked).apply();
            if (isChecked) {
                checkPermissionAndStartService();
            } else {
                requireContext().stopService(new Intent(requireContext(), MqttNotificationService.class));
                showToast("Notificações Desativadas");
            }
        });

        // 4. Listeners dos Botões
        setupClick(view, R.id.btnSaveServer, v -> {
            String newUrl = etServerUrl.getText().toString().trim();
            if (!newUrl.isEmpty()) {
                sharedPreferences.edit().putString(KEY_API_URL, newUrl).apply();
                Singleton.getInstance(requireContext()).setMainUrl(newUrl);
                showToast("Configuração guardada!");
            } else {
                etServerUrl.setError("URL inválido");
            }
        });

        setupClick(view, R.id.btnTestConnection, v -> showToast("A testar conexão..."));
        setupClick(view, R.id.btnLogout, v -> performLogout());

        // Botões de Navegação
        setupClick(view, R.id.btnChangePassword, v -> showToast("Alterar Senha"));
        setupClick(view, R.id.btnPersonalData, v -> showToast("Dados Pessoais"));
        setupClick(view, R.id.btnReportProblem, v -> showToast("Reportar Problema"));
        setupClick(view, R.id.btnTerms, v -> showToast("Termos e Condições"));
        setupClick(view, R.id.btnPrivacyPolicy, v -> showToast("Política de Privacidade"));
    }

    private void performLogout() {
        requireContext().stopService(new Intent(requireContext(), MqttNotificationService.class));
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void setupClick(View parent, int id, View.OnClickListener listener) {
        View view = parent.findViewById(id);
        if (view != null) view.setOnClickListener(listener);
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
