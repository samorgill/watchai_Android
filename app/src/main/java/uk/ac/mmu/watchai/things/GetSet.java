package uk.ac.mmu.watchai.things;

/**
 * Created by ssorg on 17/09/2016.
 */
public class GetSet {

    private static String mqttMsg;
    private static String mqttTopic;

    public void GetSet(String mm, String mt){
        this.mqttMsg = mm;
        this.mqttTopic = mt;
    }

    public static String getMqttMsg() {
        return mqttMsg;
    }

    public void setMqttMsg(String mqttMsg) {
        this.mqttMsg = mqttMsg;
    }

    public static String getMqttTopic() {
        return mqttTopic;
    }

    public void setMqttTopic(String mqttTopic) {
        this.mqttTopic = mqttTopic;
    }

}
