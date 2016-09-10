package uk.ac.mmu.watchai.things;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;


import uk.ac.mmu.babywatch.R;
import uk.ac.mmu.watchai.register.Registration;

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
        btn = (Button) findViewById(R.id.listenBtn);

       /* SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");
        tv.setText(ipAddy + " " + usrName);
*/

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");





    }

/*

    public static void mqttMsg(String message){

        TextView tv;
        Button btn;


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");




        TextView tv2 = null;
        Button btn2;

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");




        TextView tv2 = null;
        Button btn2;

        final String mqttTopic = DB_Utils.getMqttTopic();
        final String mqttMessage = DB_Utils.getMqttMsg();

        topic        =  mqttTopic;
        content      = "Hello from Android";
        content2      = mqttMessage;
        qos             = 1;
        broker       = "tcp://" + ipAddy + ":1883";
        clientId     = "Android";


        Log.i("User: ", topic);
        Log.i("Broker: ", broker);

        MemoryPersistence persistence = new MemoryPersistence();
        final MqttAndroidClient mqttClient = new MqttAndroidClient(this.getApplicationContext(),broker, clientId, persistence);
        final TextView finalTv = tv2;
        mqttClient.setCallback(new MqttCallback() {
            public void messageArrived(String topic, MqttMessage msg)
                    throws Exception {
                System.out.println("Recived:" + topic);
                System.out.println("Recived now:" + new String(msg.getPayload()));

                finalTv.setText("Recived now:" + new String(msg.getPayload()));


            }

            public void deliveryComplete(IMqttDeliveryToken arg0) {
                System.out.println("Delivary complete");
            }

            public void connectionLost(Throwable arg0) {
                // TODO Auto-generated method stub
            }
        });




        //mqttClient.connect( connOpts);


        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            // connOpts.setUserName("samo");
            // connOpts.setPassword(new char[]{'a', 't', 'h', 'c', 'l', 'i', 'a', 't', 'h', '8'});
            // mqttClient.connect(connOpts);


            mqttClient.connect(connOpts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connection Success!");
                    try {
                        MqttMessage message = new MqttMessage(content2.getBytes());
                        message.setQos(qos);
                        System.out.println("Publish message: " + message);
                        Log.i("Tipppic: ", topic);
                        mqttClient.subscribe(topic, qos);
                        mqttClient.publish(topic, message);
                    */
/*mqttClient.disconnect();
                    System.exit(0);*//*

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
*/


    // This is duplication of above, but by pressing the button it sends the info.


    public void msgClick(Context mContext){


        TextView tv;
        Button btn;

        Context con = mContext;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(con);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");

        String mes = DB_Utils.getMqttMsg();
        String top = DB_Utils.getMqttTopic();

        topic        = top;
        content      = mes;
        qos             = 1;
        broker       = "tcp://" + ipAddy + ":1883";
        clientId     = "Android";

        Log.i("User: ", topic);
        Log.i("Broker: ", broker);

        MemoryPersistence persistence = new MemoryPersistence();
        final MqttAndroidClient mqttClient = new MqttAndroidClient(con.getApplicationContext(),broker, clientId, persistence);
        mqttClient.setCallback(new MqttCallback() {
            public void messageArrived(String topic, MqttMessage msg)
                    throws Exception {
                System.out.println("Recived:" + topic);
                System.out.println("Recived now:" + new String(msg.getPayload()));

                Log.i("Recived now:", new String(msg.getPayload()));


            }

            public void deliveryComplete(IMqttDeliveryToken arg0) {
                System.out.println("Delivary complete");
            }

            public void connectionLost(Throwable arg0) {
                // TODO Auto-generated method stub
            }
        });




        //mqttClient.connect( connOpts);


        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
           // connOpts.setUserName("samo");
           // connOpts.setPassword(new char[]{'a', 't', 'h', 'c', 'l', 'i', 'a', 't', 'h', '8'});
            // mqttClient.connect(connOpts);


            mqttClient.connect(connOpts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connection Success!");
                    try {
                        MqttMessage message = new MqttMessage(content.getBytes());
                        message.setQos(qos);
                        System.out.println("Publish message: " + message);
                        Log.i("Tipppic: ", topic);
                        mqttClient.subscribe(topic, qos);
                        mqttClient.publish(topic, message);

                    /*mqttClient.disconnect();
                    System.exit(0);
*/
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
