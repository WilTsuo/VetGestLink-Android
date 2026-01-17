package pt.ipleiria.estg.dei.vetgestlink.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import pt.ipleiria.estg.dei.vetgestlink.models.MqttHandler;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;

public class MqttNotificationService extends Service {

    private static final String TAG = "Vetgetlink-MqttDebug";
    private MqttHandler mqttHandler;

    private static final String CLIENT_ID = "VetGestLink_Android";
    private static final String CHANNEL_ID = "vetgestlink_channel";
    private static final String FOREGROUND_CHANNEL_ID = "vetgestlink_foreground";
    private static final int NOTIFICATION_ID = 1;

    // Removido acesso direto às SharedPreferences para URL
    // private static final String KEY_API_URL = "api_url"; <-- ISTO ESTAVA ERRADO

    private int userId = -1;
    private String brokerUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();

        // O User ID pode vir das prefs diretamente ou do Singleton (via UserProfile),
        // mas como o Singleton pode não ter o perfil carregado se a app foi morta,
        // manter a leitura do ID nas prefs é aceitável para persistência.
        SharedPreferences prefs = getSharedPreferences("VetGestLinkPrefs", Context.MODE_PRIVATE);
        // Nota: Certifique-se que guarda o "user_id" no LoginActivity ou Singleton ao fazer login
        userId = prefs.getInt("user_id", -1);

        // CORREÇÃO: Obter URL através do Singleton
        brokerUrl = Singleton.getInstance(getApplicationContext()).getMqttBrokerUrl();

        Log.d(TAG, "Serviço criado. UserID: " + userId + " | Broker: " + brokerUrl);
    }

    // O método convertApiUrlToMqttUrl foi removido pois agora está no Singleton

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();

        if (userId != -1 && userId != 0) {
            if (mqttHandler == null || !mqttHandler.isConnected()) {
                startMqtt();
            }
        } else {
            Log.e(TAG, "UserID inválido. O serviço não vai conectar.");
        }

        return START_STICKY;
    }

    private void startForegroundService() {
        Notification notification = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
                .setContentTitle("VetGestLink")
                .setContentText("Conectado a " + brokerUrl)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private void startMqtt() {
        mqttHandler = new MqttHandler();
        new Thread(() -> {
            // Recarrega URL caso tenha mudado entretanto
            brokerUrl = Singleton.getInstance(getApplicationContext()).getMqttBrokerUrl();

            String fullClientId = CLIENT_ID + "_" + userId + "_" + System.currentTimeMillis();
            Log.d(TAG, "A tentar conectar a: " + brokerUrl);

            mqttHandler.connect(brokerUrl, fullClientId);

            if (mqttHandler.isConnected()) {
                Log.d(TAG, "MQTT CONECTADO COM SUCESSO!");

                mqttHandler.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.e(TAG, "Conexão perdida: " + (cause != null ? cause.getMessage() : "Desconhecido"));
                        // Opcional: Tentar reconectar ou deixar o serviço reiniciar
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) {
                        String payload = message.toString();
                        Log.i(TAG, ">>> MENSAGEM RECEBIDA | Tópico: " + topic + " | Payload: " + payload);
                        handleMessage(topic, payload);
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {}
                });

                mqttHandler.subscribe("INSERT_" + userId + "_MARCACAO");
                mqttHandler.subscribe("UPDATE_" + userId + "_MARCACAO");
                mqttHandler.subscribe("DELETE_" + userId + "_MARCACAO");

            } else {
                Log.e(TAG, "FALHA AO CONECTAR MQTT. Verifique se o IP " + brokerUrl + " está acessível.");
            }
        }).start();
    }

    private void handleMessage(String topic, String payload) {
        try {
            if (!topic.contains("_" + userId + "_") && !topic.equals("test")) {
                return;
            }

            JSONObject json = new JSONObject(payload);
            String title = "VetGestLink";
            String content = "Nova atualização.";

            String data = json.optString("data", "");
            String hora = json.optString("horainicio", "");
            String infoExtra = (!data.isEmpty()) ? " (" + data + " " + hora + ")" : "";

            if (topic.contains("INSERT")) {
                title = "Nova Marcação";
                content = "Consulta agendada" + infoExtra;
            } else if (topic.contains("UPDATE")) {
                title = "Alteração na Agenda";
                content = "A sua consulta foi atualizada" + infoExtra;
            } else if (topic.contains("DELETE")) {
                title = "Marcação Cancelada";
                content = "Uma consulta foi removida.";
            }

            showNotification(title, content);

        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar JSON: " + e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void showNotification(String title, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "ERRO: Sem permissão de notificação!");
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            NotificationChannel channelAlerts = new NotificationChannel(
                    CHANNEL_ID, "Alertas VetGestLink", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channelAlerts);

            NotificationChannel channelService = new NotificationChannel(
                    FOREGROUND_CHANNEL_ID, "Serviço Ativo", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channelService);
        }
    }

    @Override
    public void onDestroy() {
        if (mqttHandler != null) mqttHandler.disconnect();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
