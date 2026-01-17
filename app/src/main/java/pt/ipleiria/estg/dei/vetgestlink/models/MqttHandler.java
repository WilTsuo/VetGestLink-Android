package pt.ipleiria.estg.dei.vetgestlink.models;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttHandler {

    //Instância do cliente MQTT para aceder aos metodos de publicação e subscrição
    private MqttClient client;

    //Faz a conexão ao broker MQTT
    public void connect(String brokerUrl, String clientId) {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient(brokerUrl, clientId, persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            connectOptions.setAutomaticReconnect(true);
            client.connect(connectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //Desconecta do broker MQTT
    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //Publica uma mensagem num tópico específico (Não foi usado no serviço)
    //Mas pode ser útil futuramente se caso nossa aplicação queira enviar mensagens no MQTT para o webservice
    public void publish(String topic, String message) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                client.publish(topic, mqttMessage);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //Subscreve um tópico específico para receber mensagens
    public void subscribe(String topic) {
        try {
            if (client != null && client.isConnected()) {
                client.subscribe(topic);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void setCallback(MqttCallback callback) {
        if (client != null) {
            client.setCallback(callback);
        }
    }

    //Verifica se o cliente está conectado ao broker MQTT
    public boolean isConnected() {
        return client != null && client.isConnected();
    }
}
