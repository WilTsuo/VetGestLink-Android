package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.Manifest;
import android.app.AlertDialog;
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

import com.google.android.material.button.MaterialButton;


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

    // Constantes removidas. Agora usamos o Singleton.getUrlFrontend() para usar as imagens do glide,
    // e as SharedPreferences do Singleton para guardar o URL.

    private static final String PREFS_NAME = "VetGestLinkPrefs";
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

    // Construtor vazio obrigatório
    public DefinicoesFragment() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_definicoes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        etServerUrl = view.findViewById(R.id.etServidorUrl);
        switchNotifications = view.findViewById(R.id.switchNotificacao);

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
        setupClick(view, R.id.btnSalvarServer, v -> {
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
        setupClick(view, R.id.btnTestarConexao, v -> {
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

        // Botão de Alterar Palavra-Passe
        setupClick(view, R.id.btnMudarPalavraPass, v -> abrirDialogNovaPassword());

        // Botão Dados Pessoais
        setupClick(view, R.id.btnDadosPessoais, v -> {
            String baseUrl = Singleton.getInstance(requireContext()).getUrlFrontend();
            String url = baseUrl + "/site/login";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
            startActivity(browserIntent);
        });

        // Botão Reportar Problema - Abre link externo
        setupClick(view, R.id.btnReportarProblema, v -> {
            String baseUrl = Singleton.getInstance(requireContext()).getUrlFrontend();
            String url = baseUrl + "/site/contact";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
            startActivity(browserIntent);
        });

        // Botão Termos e Condições - Abre link externo
        setupClick(view, R.id.btnInformacao, v -> {
            String baseUrl = Singleton.getInstance(requireContext()).getUrlFrontend();
            String url = baseUrl + "/site/information";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
            startActivity(browserIntent);
        });

        // Listener Logout
        setupClick(view, R.id.btnLogout, v -> performLogout());
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

    // Verifica permissão e inicia o serviço de notificações MQTT
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

    private void abrirDialogNovaPassword() {
        // Verifica se o contexto é nulo
        if (getContext() == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_esqueceu_pass, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // Referências do Dialog
        final TextInputEditText etPalavraPasseAtual = dialogView.findViewById(R.id.etPalavraPasseAtual);
        final TextInputEditText etNovaPalavraPasse = dialogView.findViewById(R.id.etNovaPalavraPasse);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelar);
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btnGuardar);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            // Obter o token de acesso
            String token = Singleton.getInstance(requireContext()).getAccessToken();

            // Verifica se o token é nulo
            if (token == null) return;

            // Capturar valores
            String palavraPasseAtual = etPalavraPasseAtual.getText().toString();
            String palavraPasseNova = etNovaPalavraPasse.getText().toString();

            Singleton.getInstance(requireContext()).atualizarPalavraPasse(
                    token,
                    palavraPasseAtual,
                    palavraPasseNova,
                    new Singleton.atualizarPalavraPasseCallback() {
                        @Override
                        public void onSuccess(String message) {
                            if (!isAdded()) return;
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            // Efetuar logout após alteração de palavra-passe
                            // Porque muda o token de acesso quando e mudado a palavra-passe
                            performLogout();
                        }

                        @Override
                        public void onError(String error) {
                            if (!isAdded()) return;
                            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });
        dialog.show();
    }
}
