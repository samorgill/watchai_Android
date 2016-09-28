package uk.ac.mmu.watchai.things;

/**
 * @author Samuel Orgill 15118305
 * NW5 Smartwatch Control of Environment
 * September 2016
 *
 * Getters and setters for MQTT messages and topics
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
