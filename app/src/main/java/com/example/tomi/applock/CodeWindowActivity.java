package com.example.tomi.applock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Tomi on 2014.12.08..
 */
public class CodeWindowActivity extends Activity {

    private Button codeButton;
    private Button cancelButton;
    private EditText codeText;
    private String code = new String();
    private String currentApp = new String();
    private ArrayList<String> listedApps = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopService(new Intent(AppLockerService.TAG));
        setContentView(R.layout.code_layout);
        ;
        codeButton = (Button) findViewById(R.id.enterCode);
        cancelButton = (Button) findViewById(R.id.cancelCode);
        codeText = (EditText) findViewById(R.id.editCodeText);
        code = GetCode();
        loadArray(this, listedApps);
        HandlerWinIntent();
        Toast.makeText(this, code, Toast.LENGTH_SHORT).show();

        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("WW", "1");
                //processStopService(HandlerWin.TAG);
                // stopService(new Intent(HandlerWin.TAG));
                Log.w("WW", "2");
                /*Intent lockIntent = new Intent(getBaseContext(), currentApp);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                CodeActivity.this.startActivity();*/

                Log.w("WW","3");
                PackageManager pmi = getPackageManager();
                Intent intent = null;
                Log.w("WW","4");
                intent = pmi.getLaunchIntentForPackage("com.estoty.game2048");
                if (intent != null){
                    Log.w("WW","5");
                    startActivity(intent);
                }

                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();

            }
        });

    }

    public String HandlerWinIntent()
    {
        String selectedApp = new String();
        Intent intent = getIntent();



        selectedApp = intent.getStringExtra("foundApp");
        return selectedApp;
    }

    private void processStopService(final String tag) {
        Intent intent = new Intent(getBaseContext(), AppLockerService.class);
        intent.addCategory(tag);
        stopService(intent);
    }

    public String GetCode()
    {
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream fIn = openFileInput ( "codeFile.txt" ) ;
            InputStreamReader isr = new InputStreamReader ( fIn ) ;
            BufferedReader buffreader = new BufferedReader ( isr ) ;

            String readString = buffreader.readLine ( ) ;
            while ( readString != null ) {
                datax.append(readString);
                readString = buffreader.readLine ( ) ;
            }

            isr.close ( ) ;
        } catch ( IOException ioe ) {
            ioe.printStackTrace ( ) ;
        }
        return datax.toString() ;
    }

    public void saveArray(ArrayList<String> selectedApps)
    {

        String PrefFileName = "MyPrefFile";
        SharedPreferences sp = getSharedPreferences(PrefFileName, 0);

        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Status_size", selectedApps.size()); /* sKey is an array */

        for(int i=0;i<selectedApps.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, selectedApps.get(i));
        }

        mEdit1.apply();
    }

    public void loadArray(Context mContext, ArrayList<String> packageNames)
    {
        SharedPreferences settings = getSharedPreferences("MyPrefFile", 0);
        packageNames.clear();
        int size = settings.getInt("Status_size", 0);

        for(int i=0;i<size;i++)
        {
            packageNames.add(settings.getString("Status_" + i, null));
        }
    }

}
