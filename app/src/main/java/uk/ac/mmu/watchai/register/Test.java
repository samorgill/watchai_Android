package uk.ac.mmu.watchai.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import uk.ac.mmu.babywatch.R;


/**
 * @author Samuel Orgill 15118305
 * NW5 Smartwatch Control of Environment
 * September 2016
 *
 * Class to test methods.
 *
 */
public class Test extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        Intent intent = getIntent();
    }
}

