package mqtt.example;

import org.eclipse.paho.client.mqttv3.*;

public class MQTTExample {

    private static final String SERVER_URL = "tcp://iot.eclipse.org:1883";

    public static void main(String[] args) throws Exception{
        IMqttClient firstClient = new MqttClient(SERVER_URL,"firstClient");
        IMqttClient secondClient = new MqttClient(SERVER_URL,"secondClient");

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        firstClient.connect(options);
        secondClient.connect(options);

        Thread firstClientThread = new Thread(() -> {
            int messageCount = 1;
            while (true){
                try {
                    firstClient.publish("secondClient",new MqttMessage(("message : " + messageCount++).getBytes()));
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        Thread secondClientThread = new Thread(() -> {
            try {
                secondClient.subscribe("secondClient",(s, mqttMessage) -> {
                    System.out.println(new String(mqttMessage.getPayload()));
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });

        firstClientThread.start();
        secondClientThread.start();

        firstClientThread.join();
        secondClientThread.join();
    }
}
