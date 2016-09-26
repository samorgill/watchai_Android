package uk.ac.mmu.watchai.uk.ac.mmu.watchai.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import uk.ac.mmu.babywatch.R;
import uk.ac.mmu.watchai.register.Registration;
import uk.ac.mmu.watchai.things.Monitor;
import uk.ac.mmu.watchai.things.Music;
import uk.ac.mmu.watchai.things.Recipes;
import uk.ac.mmu.watchai.things.Things;
import uk.ac.mmu.watchai.things.MQTT;


public class MainActivity extends AppCompatActivity {

    private static TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.welcome);

        // If there is no stored username then it automatically takes the user to register.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String usrName = settings.getString("usrName", "");
        String ipAdd = settings.getString("ipadd", "");
        if(!usrName.equals("")){
            tv.setText("Welcome " + usrName + " " + ipAdd);
        }else{
            Intent intent = new Intent(this, Registration.class);
            startActivity(intent);
        }

    }

    /**
     * Logs out of the account and erases the username/ password shared preferences
     * @param v
     */
    public void onClickLog(View v){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        /*editor.putString("ipadd", "");
        editor.putString("usrName", "");
        editor.putString("pssWord", "");*/
        editor.clear();
        editor.apply();

        Context context = this;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        final TextView noMatch = new TextView(context);
        noMatch.setText("Logged out");
        noMatch.setGravity(50);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(noMatch);


        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goReg();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();





    }

    public void onClickReg(View v){
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    public void goReg(){
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    public void mqtt(View v){
        Intent intent = new Intent(this, MQTT.class);
        startActivity(intent);
    }

    public void clickThings(View v){
        Intent intent = new Intent(this, Things.class);
        startActivity(intent);
    }

    public void clickMonitor(View v){
        Intent intent = new Intent(this, Monitor.class);
        startActivity(intent);
    }


    public void clickRecipes(View v){
        Intent intent = new Intent(this, Recipes.class);
        startActivity(intent);
    }


    public void clickMusic(View v){
        Intent intent = new Intent(this, Music.class);
        startActivity(intent);
    }


}
