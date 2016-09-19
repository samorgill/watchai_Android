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

/**
 * Created by ssorg on 17/09/2016.
 */
public class Monitor extends AppCompatActivity {

    String TAG = "Sensor MainActivity";
    LinearLayout l1;
    Switch sw;
    TextView label1;
    TableLayout tabLay;
    TableRow tabRow;
    TextView tv;
    private String usrName, ipAddy;
    int textSize = 20;
    int top = 65;
    int left = 60;
    int bottom = 5;
    int right = 0;
    GetSet getSet;

    private static String mqttMsg;
    private static String mqttTopic;

    //Home
   /*public static String sensorServerURL =
            "http://192.168.0.19/Server/DataToServer";*/

    // WebServer
    public static String sensorServerURL =
            "http://3-dot-projectbabywatch.appspot.com/";

    //192 is my IPv4 address on the laptop used by localhost. This will need to change for Pi.
    // Can only be accessed via same wifi network. "Server" is the program name in Eclipse
    //"http://10.0.2.2:8080/SensorServer/sensorToDB";

    MQTT mqttClass;
    public Switch switchTog;
    public RadioButton radBtn;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);

        // Ignore this code if not sending location to server
        // IMPORTANT: Strict mode only here to allow networking in main thread.
        // Ideally create an AsyncTask
        // Need to remove this after testing initial solution and use AsyncTask
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

        Intent intent2 = getIntent();

        mqttClass = new MQTT();
        mContext = this;

        getSet = new GetSet();
        //Log.i("Username works?: ", usrName);

        getAll(mContext);
    }


    String sendToServer (String urlStr) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line = "";
        String result = "";
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            // Issue the GET to send the data
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            // read the result to process response and ensure data sent
            while ((line = rd.readLine()) != null) {
                result = result + line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // send back data from server
        return result;
    }

    JSONArray getFromServer(String urlStr){
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line = "";
        String result = "";
        String stat ="";

        JSONObject jObject = new JSONObject();
        JSONObject jOb = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONArray jAr = new JSONArray();
        JSONArray jArr = new JSONArray();
        ArrayList<Object> aList = new ArrayList<Object>();

        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // Issue the GET to send the data
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            // read the result to process response and ensure data sent

            StringBuilder sb = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            result = sb.toString();

            jArray = new JSONArray(result);
            jObject = new JSONObject();
            for(int i = 0; i < jArray.length(); i++){
                jObject = jArray.getJSONObject(i);
                jAr.put(jObject.get("propertyMap"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            for (int j = 0; j < jAr.length(); j++) {
                jOb = jAr.getJSONObject(j);
                jArr.put(jOb);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jArr;
    }

    void getAll(final Context mContext){
        JSONObject jObject = new JSONObject();

        String fullURLStr = sensorServerURL + "GetMonitors?user=" + usrName;
        Log.i("GetAll url", fullURLStr);
        JSONArray jArray = getFromServer(fullURLStr);

        String type;

        try {
            for (int i = 0; i < jArray.length(); i++) {
                jObject = jArray.getJSONObject(i);

                if(jObject.get("state").equals("On")){

                    type = jObject.get("type").toString();

                    sw = new Switch(this);
                    sw.setChecked(true);
                    sw.setText(type);
                    int col = this.getResources().getColor(R.color.text);
                    int checkCol = this.getResources().getColor(R.color.switch_col);
                    sw.setTextColor(col);
                    sw.setHighlightColor(checkCol);
                    sw.setTextSize(textSize);
                    sw.setPadding(left, top, right, bottom);

                    final String st = "Off";
                    final String ty = type;
                    sw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getSet.setMqttTopic(usrName+"/"+ty);
                            getSet.setMqttMsg(st);
                            sendSensorData(st, ty);
                            tabLay.removeAllViews();

                            getAll(mContext);
                        }
                    });


                    tabRow = new TableRow(this);

                    tabRow.addView(sw);
                    tabLay.addView(tabRow);

                }else if(jObject.get("state").equals("Off")){
                    type = jObject.get("type").toString();

                    sw = new Switch(this);
                    sw.setChecked(false);
                    sw.setText(type);
                    int col = this.getResources().getColor(R.color.text_low);
                    sw.setTextColor(col);
                    sw.setTextSize(textSize);
                    sw.setPadding(left, top, right, bottom);

                    final String st = "On";
                    final String ty = type;
                    sw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            getSet.setMqttTopic(usrName+"/"+ty);
                            getSet.setMqttMsg(st);
                            sendSensorData(st, ty);

                            tabLay.removeAllViews();
                            sw.setChecked(true);
                            getAll(mContext);
                        }
                    });

                    tabRow = new TableRow(this);

                    tabRow.addView(sw);
                    tabLay.addView(tabRow);
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void sendSensorData(String st, String ty) {
        // build up URL to send sensor data to server

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");

        mqttClass.msgClick(mContext);

        String fullURLStr = sensorServerURL +
                "Monitor?&user="+usrName+"&state="+st+"&type="+ty;
        Log.i(TAG, "Retrieving sensor data from "+fullURLStr);

        // send it using utility method
        String result = sendToServer(fullURLStr);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
