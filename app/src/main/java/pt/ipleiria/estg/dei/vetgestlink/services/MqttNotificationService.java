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

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.MqttHandler;

public class MqttNotificationService extends Service {

    private static final String TAG = "MqttDebug"; // Tag para filtrar no Logcat
    private MqttHandler mqttHandler;

    private static final String CLIENT_ID = "VetGestLink_Android";
    private static final String CHANNEL_ID = "vetgestlink_channel";
    private static final String FOREGROUND_CHANNEL_ID = "vetgestlink_foreground";
    private static final int NOTIFICATION_ID = 1;

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_API_URL = "api_url"; // Chave usada no DefinicoesFragment

    private int userId = -1;
    private String brokerUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userId = prefs.getInt(KEY_USER_ID, -1);

        // 1. Lógica para obter o URL dinâmico das definições
        String savedApiUrl = prefs.getString(KEY_API_URL, "http://172.22.21.220");
        brokerUrl = convertApiUrlToMqttUrl(savedApiUrl);

        Log.d(TAG, "Serviço criado. UserID: " + userId + " | Broker: " + brokerUrl);
    }

    // Converte http://192.168.1.10 para tcp://192.168.1.10:1883
    private String convertApiUrlToMqttUrl(String apiUrl) {
        String url = apiUrl.replace("http://", "tcp://").replace("https://", "tcp://");
        // Remove barras no final se houver
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        // Adiciona a porta se não estiver especificada (assumindo 1883 padrão)
        if (!url.contains(":1883")) {
            url += ":1883";
        }
        return url;
    }

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
            String fullClientId = CLIENT_ID + "_" + userId + "_" + System.currentTimeMillis();
            Log.d(TAG, "A tentar conectar a: " + brokerUrl);

            mqttHandler.connect(brokerUrl, fullClientId);

            if (mqttHandler.isConnected()) {
                Log.d(TAG, "MQTT CONECTADO COM SUCESSO!");

                mqttHandler.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.e(TAG, "Conexão perdida: " + (cause != null ? cause.getMessage() : "Desconhecido"));
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

                // 2. Subscrever TUDO (#) para debug - REMOVER DEPOIS
                mqttHandler.subscribe("#");

                // 3. Subscrever tópicos específicos do utilizador
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
            // Filtro de segurança: Se a mensagem não for para este user, ignora
            // (Útil porque estamos a ouvir # para debug)
            if (!topic.contains("_" + userId + "_") && !topic.equals("test")) {
                // Se quiser ver tudo no log, comente o return abaixo
                // return;
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
