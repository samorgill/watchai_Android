package uk.ac.mmu.watchai.things;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import uk.ac.mmu.babywatch.R;
import uk.ac.mmu.watchai.things.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;



/**
 * @author Samuel Orgill 15118305
 * NW5 Smartwatch Control of Environment
 * September 2016
 *
 * Class for displaying and starting recipes
 */

public class Recipes extends AppCompatActivity{

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

    public static String sensorServerURL =
            "http://3-dot-projectbabywatch.appspot.com/";

    MQTT mqttClass;
    public Button sleepBtn;
    public RadioButton radBtn;
    private Context mContext;
    GetSet getSet;
    private String top = "/Recipe";
    private String sleep = "sleep";
    private String wake = "wake";
    private String sooth = "sooth";
    private String ent = "entertain";
    private String emerg = "emergency";
    private String stop = "stop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe);

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Get ip address and username
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        ipAddy = settings.getString("ipadd", "");
        usrName = settings.getString("usrName", "");


        tabLay = (TableLayout) findViewById(R.id.tabLay);
        tabRow = (TableRow) findViewById(R.id.tabRow);
        sleepBtn = (Button) findViewById(R.id.sleepBtn);

        Intent intent2 = getIntent();

        getSet = new GetSet();
        mqttClass = new MQTT();
        mContext = this;
    }

    /**
     * Method to activate sleep recipe
     * @param v
     */
    public void clkSleep(View v) {
        sendMqtt(sleep);
    }

    /**
     * method to activate wake recipe
     * @param v
     */
    public void clkWake(View v){
        sendMqtt(wake);
    }

    /**
     * method to activate sooth recipe
     * @param v
     */
    public void clkSooth(View v){
        sendMqtt(sooth);
    }

    /**
     * Method to activate entertain recipe
     * @param v
     */
    public void clkEnt(View v){
        sendMqtt(ent);
    }

    /**
     * Method to activate emergency recipe
     * @param v
     */
    public void clkEmerg(View v){
        sendMqtt(emerg);
    }

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
