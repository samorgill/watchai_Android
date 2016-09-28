
package uk.ac.mmu.watchai.register;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import uk.ac.mmu.babywatch.R;
import uk.ac.mmu.watchai.uk.ac.mmu.watchai.Main.MainActivity;
import uk.ac.mmu.watchai.things.MQTT;

/**
 * @author Samuel Orgill 15118305
 * NW5 Smartwatch Control of Environment
 * September 2016
 *
 * JmDNS Broadcast & Listener. Broadcasts service (username) and listens for
 * the Hubs service (IP address). Resolves service
 */

public class Registration extends AppCompatActivity{

    public static String sensorServerURL =
            "http://3-dot-projectbabywatch.appspot.com/";

    String SERVICE_TYPE = "MQTT";
    String SERVICE_NAME = "Android";
    int SERVICE_PORT = 8856;

    public final static String EXTRA_MESSAGE = "uk.ac.mmu.watchai.things";
    public static final String PREFS_NAME = "MyPrefsFile";

    TextView tv;
    TextView teVi;
    TableLayout tabLay;
    TableRow tabRow;

    private static String addIp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent2 = getIntent();
        final TextView textV = (TextView) findViewById(R.id.textView);

        jmdnsListen();
        Context context = this;
    }

    //On Click Submit
    public void onClickSubmit(View v){
        Intent intent = new Intent(this, MQTT.class);
        startActivity(intent);
    }

    /**
     * A method that broadcasts the username/topic
     * @param userName
     */
    public void broadcast(String userName){
        try {

            InetAddress inet = getInet();
            String SERVICE_NAME = userName;
            Log.i("IP Address: ", inet.toString());

            JmDNS jmdns;
            jmdns = JmDNS.create(inet, SERVICE_NAME);
            ServiceInfo mqttService = ServiceInfo.create(SERVICE_TYPE, SERVICE_NAME, SERVICE_PORT, 0, 0, "Android MQTT Broker Service");
            System.out.println("Pro Names: " + mqttService.getNiceTextString());
            jmdns.registerService(mqttService);
            System.out.println("Service: " + mqttService.toString());

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_MESSAGE, SERVICE_NAME);
            startActivity(intent);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method listens for services being broadcast.
     */

    public void jmdnsListen(){

        try {
            InetAddress inet = getInet();
            String hostname = "Android";
            JmDNS jmdns = JmDNS.create(inet, hostname);
            jmdns.addServiceListener("_MQTT._tcp.local.", new SampleListener());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * A listener to listen to services registered on the local network
     * Used to obtain the IP address of the Hub during registration.
     */


    class SampleListener implements ServiceListener {

        String brokerIP;
        boolean ready;
        JmDNS jmdns;

        final TextView tv = (TextView) findViewById(R.id.ipView);

        String SERVICE_TYPE = "MQTT";
        String SERVICE_NAME = "sample_jmdns_service";

        /**
         * Adds service once found
         * @param event
         */
        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added   : " + event.getName() + "." + event.getType());
            System.out.println("Event: " + event.getInfo());
            jmdns.requestServiceInfo(SERVICE_TYPE, event.getName());
        }

        /**
         * Triggered when a service is removed
         * @param event
         */
        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed : " + event.getName() + "." + event.getType());
        }

        /**
         * Confirmation service has been resolved
         * @param event
         */
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());
            String[] serviceUrl = event.getInfo().getURLs();
            brokerIP = "tc"+serviceUrl[0].substring(3);
            System.out.println("Broker IP: " + "tc"+serviceUrl[0].substring(3));
            String ipAddress = serviceUrl[0].substring(7, 19);
            System.out.println(ipAddress);
            ready=true;
            tv.setText(ipAddress);
            tabLay.addView(tabRow);
        }
    }

    /**
     * A method for users to register
     * @param v
     */

    public void clickReg(View v){

        TextView tv = (TextView) findViewById(R.id.userName);
        TextView em = (TextView) findViewById(R.id.email);
        EditText pw = (EditText) findViewById(R.id.pass);
        EditText pw2 = (EditText) findViewById(R.id.pass2);
        EditText phone = (EditText) findViewById(R.id.phoneNo);
        TextView ipAdd = (TextView) findViewById(R.id.ipView);
        String userName = tv.getText().toString();
        String email = em.getText().toString();
        String pass = pw.getText().toString();
        String pass2 = pw2.getText().toString();
        String phoneNo = phone.getText().toString();
        String ipAddress = ipAdd.getText().toString();

        if(!pass.equals(pass2)) {

            Context context = this;

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            final TextView noMatch = new TextView(context);
            noMatch.setText("  Oh noes! Your password doesn't match :(");
            noMatch.setGravity(50);

            alertDialogBuilder.setView(noMatch);

            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        } else {

            String fullURLStr = sensorServerURL +
                    "Register?&user2="+userName+"&pass2="+pass +"&email=" + email + "&phone=" +
                        phoneNo + "&hub=" + ipAddress;
            Log.i("Full URL: ", fullURLStr);

            String result = sendToServer(fullURLStr);

            broadcast(userName);
            setAddIp(ipAddress);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("ipadd", ipAddress);
            editor.putString("usrName", userName);
            editor.putString("pssWord", pass);
            editor.apply();
        }
    }

    /**
     * A method for posting data to the server
     * @param urlStr
     * @return
     */
    public String sendToServer (String urlStr) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line = "";
        String result = "";
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            //Iterates through response
            while ((line = rd.readLine()) != null) {
                result = result + line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * A method to get the device's IP address
     * @return Inet
     */
    public InetAddress getInet() {

        InetAddress inet = null;

        try {
            WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            String ipAddress = Formatter.formatIpAddress(ip);
            inet = InetAddress.getByName(ipAddress);
        }catch (Exception e){
            e.printStackTrace();
        }

        return inet;
    }

    /**
     * Method returns IP address
     * @param ipAdd
     * @return
     */
    public static String rtnIp(String ipAdd){
        String ipAddress = ipAdd;
        Log.i("IpAdd: ", ipAddress);
        return ipAddress;
    }

    /**
     * Persisting the Username, password and IP address of Hub for future use
     */

    public static String getAddIp() {
        return addIp;
    }

    public void setAddIp(String addIp) {
        this.addIp = addIp;
    }

    public void clickSearch(View v){
        jmdnsListen();
    }

}