package uk.ac.mmu.watchai.things;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


import uk.ac.mmu.babywatch.R;

/**
 * Created by ssorg on 27/08/2016.
 */
public class MQTT extends AppCompatActivity {





    TextView tv, tv2;
    Button btn;

    String topic;
    String content;
    int qos;
    String broker;

    //MQTT client id to use for the device. "" will generate a client id automatically
    String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqtt);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        tv = (TextView) findViewById(R.id.textView);
       // btn = (Button) findViewById(R.id.listenBtn);

       /* SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");
        tv.setText(ipAddy + " " + usrName);
*/

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");

    }



    public void msgClick(Context mContext){
        Context con = mContext;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(con);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");

            String mes = GetSet.getMqttMsg();
            String top = GetSet.getMqttTopic();

        topic        = top;
        content      = mes;
        qos             = 1;
        broker       = "tcp://" + ipAddy + ":1883";
        clientId     = "Watchai_Android";

        Log.i("User: ", topic);
        Log.i("Broker: ", broker);

        MemoryPersistence persistence = new MemoryPersistence();
        final MqttAndroidClient mqttClient = new MqttAndroidClient(con.getApplicationContext(),broker, clientId, persistence);
        mqttClient.setCallback(new MqttCallback() {
            public void messageArrived(String topic, MqttMessage msg)
                    throws Exception {
                Log.i("Recieved message:", new String(msg.getPayload()));
            }

            public void deliveryComplete(IMqttDeliveryToken arg0) {
                System.out.println("Delivary complete");
            }

            public void connectionLost(Throwable arg0) {
                // TODO Auto-generated method stub
            }
        });

        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            mqttClient.connect(connOpts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connection Success!");
                    try {
                        MqttMessage message = new MqttMessage(content.getBytes());
                        message.setQos(qos);
                        System.out.println("Publish message: " + message);
                        mqttClient.subscribe(topic, qos);
                        mqttClient.publish(topic, message);
                        mqttClient.disconnect();
                    } catch (MqttException ex) {
                }
            }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Connection Failure!");
                }
            });
        } catch (MqttException ex) {

        }

    }
}
