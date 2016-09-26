package uk.ac.mmu.watchai.things;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
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



public class Things extends AppCompatActivity {

    String TAG = "Sensor MainActivity";
    LinearLayout l1;
    Switch sw;
    TextView label1;
    TableLayout tabLay;
    TableRow tabRow;
    TextView tv;
    private String usrName, ipAddy;

    ActionBar ab;
    int textSize = 20;
    int top = 65;
    int left = 60;
    int bottom = 5;
    int right = 0;
    private static String mqttMsg;
    private static String mqttTopic;

    // WebServer
    public static String sensorServerURL =
            "http://3-dot-projectbabywatch.appspot.com/";

    MQTT mqttClass;
    public Switch switchTog;
    public RadioButton radBtn;
    private Context mContext;
    GetSet getSet;
    Switch tc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.things);
       // ab.setTitle("Things");

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //checks the state when the app is launced

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        ipAddy = settings.getString("ipadd", "");
        usrName = settings.getString("usrName", "");

        //LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout2);
      //  tc = (Switch) findViewById(R.color.text);
        tabLay = (TableLayout) findViewById(R.id.tabLay);
        tabRow = (TableRow) findViewById(R.id.tabRow);

        Intent intent2 = getIntent();

        mqttClass = new MQTT();
        mContext = this;

        getSet = new GetSet();
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
        //String urlStr = "http://projectbabywatch.appspot.com/GetAllDoors";
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
                // System.out.println(jOb.get("doorName") + " is " + jOb.get("state"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return jArr;
    }

    /**
     * Get's all things from the database, creates switches based on their state and then sets
     * them as click. When clicked an MQTT topic/message is generated and sent. The datatstore
     * is then updated.
     * @param mContext
     */

    void getAll(final Context mContext){
        JSONObject jObject = new JSONObject();

        String fullURLStr = sensorServerURL + "GetAllThings?user3=" + usrName;
        Log.i("GetAll url", fullURLStr);
        JSONArray jArray = getFromServer(fullURLStr);

        String thing, state, serial, type, zone, room;

        try {
            for (int i = 0; i < jArray.length(); i++) {
                jObject = jArray.getJSONObject(i);

                if(jObject.get("state").equals("Locked") || jObject.get("state").equals("On")){

                    thing = jObject.get("thing").toString();
                    serial = jObject.get("serial").toString();
                    type = jObject.get("type").toString();
                    zone = jObject.get("zone").toString();
                    room = jObject.get("room").toString();

                    sw = new Switch(this);
                    sw.setChecked(true);
                    sw.setText(thing);
                    int col = this.getResources().getColor(R.color.text);
                    int checkCol = this.getResources().getColor(R.color.switch_col);
                    sw.setTextColor(col);
                    sw.setHighlightColor(checkCol);
                    sw.setTextSize(textSize);
                    sw.setPadding(left, top, right, bottom);

                    final String th = thing;
                    final String st = "Unlocked";
                    final String sl = serial;
                    final String ty = type;
                    final String zo = zone;
                    final String ro = room;
                    sw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getSet.setMqttTopic(usrName+"/"+ty+"/"+zo+"/"+ro+"/"+th);
                            getSet.setMqttMsg(st);
                            sendSensorData(th, st, sl, ty, zo, ro);
                            tabLay.removeAllViews();
                            getAll(mContext);
                        }
                    });

                    tabRow = new TableRow(this);
                    tabRow.addView(sw);
                    tabLay.addView(tabRow);

                }else if(jObject.get("state").equals("Unlocked") || jObject.get("state").equals("Off")){

                    thing = jObject.get("thing").toString();
                    serial = jObject.get("serial").toString();
                    type = jObject.get("type").toString();
                    zone = jObject.get("zone").toString();
                    room = jObject.get("room").toString();

                    sw = new Switch(this);
                    sw.setChecked(false);
                    sw.setText(thing);

                    int col = this.getResources().getColor(R.color.text_low);

                    sw.setTextColor(col);
                    sw.setTextSize(textSize);
                    sw.setPadding(left, top, right, bottom);


                    final String th = thing;
                    final String st = "Locked";
                    final String sl = serial;
                    final String ty = type;
                    final String zo = zone;
                    final String ro = room;
                    sw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            getSet.setMqttTopic(usrName+"/"+ty+"/"+zo+"/"+ro+"/"+th);
                            getSet.setMqttMsg(st);
                            sendSensorData(th, st, sl, ty, zo, ro);

                            tabLay.removeAllViews();
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

    void sendSensorData(String th, String st, String sl, String ty, String zo, String ro) {
        // build up URL to send sensor data to server

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");


        mqttClass.msgClick(mContext);


        String fullURLStr = sensorServerURL +
                "UpdateThing?&thing3="+th+"&state="+st+"&user4="+usrName + "&serial2=" + sl
                + "&type2=" + ty +"&zone2=" +zo + "&room2=" + ro;
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

