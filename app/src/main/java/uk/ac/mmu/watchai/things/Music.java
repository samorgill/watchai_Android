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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import uk.ac.mmu.babywatch.R;

/**
 * Created by ssorg on 17/09/2016.
 */
public class Music extends AppCompatActivity{

    String TAG = "Sensor MainActivity";
    LinearLayout l1;
    Switch sw;
    TextView label1;
    TableLayout tabLay;
    TableRow tabRow;
    TextView tv;
    private String usrName, ipAddy;
    private static String mqttMsg;
    private static String mqttTopic;

    // WebServer
    public static String sensorServerURL =
            "http://3-dot-projectbabywatch.appspot.com/";

    MQTT mqttClass;
    public Button sleepBtn;
    public RadioButton radBtn;
    private Context mContext;
    GetSet getSet;
    private String top = "/Music";
    private String sleep = "sleep";
    private String genius = "genius";
    private String party = "party";
    private String rhyme = "rhyme";
    private String stop = "stop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music);

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //checks the state when the app is launced

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        ipAddy = settings.getString("ipadd", "");
        usrName = settings.getString("usrName", "");

       // LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout2);
        tabLay = (TableLayout) findViewById(R.id.tabLay);
        tabRow = (TableRow) findViewById(R.id.tabRow);
        sleepBtn = (Button) findViewById(R.id.sleepBtn);

        Intent intent2 = getIntent();

        getSet = new GetSet();
        mqttClass = new MQTT();
        mContext = this;


    }

    /**
     * Method to play genius music
     * @param v
     */
    public void clkGenius(View v) {
        sendMqtt(genius);
    }

    /**
     * method to play sleep music
     * @param v
     */
    public void clkSleep(View v){
        sendMqtt(sleep);
    }

    /**
     * method to play party music
     * @param v
     */
    public void clkParty(View v){
        sendMqtt(party);
    }

    /**
     * Method to play nursery rhyme
     * @param v
     */
    public void clkRhyme(View v){
        sendMqtt(rhyme);
    }

    /**
     * Method to stop all music
     * @param v
     */
    public void clkStop(View v){
        sendMqtt(stop);
    }

    /**
     * Method to send the MQTT message
     * @param msg
     */
    public void sendMqtt(String msg){
        getSet.setMqttTopic(usrName+top);
        getSet.setMqttMsg(msg);
        mqttClass.msgClick(mContext);
    }

}
