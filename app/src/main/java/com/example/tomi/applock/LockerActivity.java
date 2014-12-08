package com.example.tomi.applock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LockerActivity extends Activity {
    private ArrayList<String> appName = new ArrayList<String>();
    private ArrayList<String> packName = new ArrayList<String>();
    private ArrayList<CheckBox> checkList = new ArrayList<CheckBox>();
    private ArrayList<String> selectedApps = new ArrayList<String>();
    private ListView Apps;
    private CheckBox Check;
    private Button okButton;
    private Button codeButton;
    private Button exitButton;
    private String code = new String();
    private boolean codeSetted = false;
    private EditText editText;
    private Context context = LockerActivity.this;
    private AppListAdapter adapter;



    protected void onCreate(Bundle savedInstanceState) {
        code = "0000";
        SaveCode(code);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_layout);
        buildView(R.layout.app_list);
        buildView(R.layout.activity_locker);
        Apps = (ListView) findViewById(R.id.AppList);
        Check = (CheckBox) findViewById(R.id.CheckBox);
        okButton = (Button) findViewById(R.id.OK);
        codeButton = (Button) findViewById(R.id.ChangeCode);
        exitButton = (Button) findViewById(R.id.Exit);
        getPackages();
        okButtonListener(codeSetted);
        codeButtonListener();
        exitButtonListener();
        adapter = new AppListAdapter(context, android.R.layout.simple_list_item_1, appName, checkList, packName);
        Apps.setAdapter(adapter);
    }




    private void buildView(int resource) //This function build the view from the xml-s
    {
        RelativeLayout my_root = (RelativeLayout) findViewById(R.id.root_Layout);
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(resource, null);
        RelativeLayout A = new RelativeLayout(this);
        A.addView(v);
        my_root.addView(A);
    }
    private void getPackages() //This function gets the installed packages
    {
        PackageManager packageManager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = packageManager.queryIntentActivities(mainIntent, 0);
        Collections.sort(appList, new ResolveInfo.DisplayNameComparator(packageManager));
        List<PackageInfo> packs = packageManager.getInstalledPackages(0);
        for(int i=0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            ApplicationInfo a = p.applicationInfo;
            String name = packageManager.getApplicationLabel(a).toString();
            // skip system apps if they shall not be included
            if((a.flags & ApplicationInfo.FLAG_SYSTEM) == 1 || name.matches("AppLockV4"))
            {
                continue;
            }
            packName.add(p.packageName);
            appName.add(name);
            checkList.add(new CheckBox(getBaseContext()));
        }
    }
    private void okButtonListener(final boolean codeSetted) //This function set the ok buttons events
    {
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if(codeSetted == false)
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LockerActivity.this, 4);
                    alertDialogBuilder.setTitle(R.string.dialog_title);
                    alertDialogBuilder.setMessage(R.string.dialog_message);
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();*/

                saveArray();
                startService(new Intent(AppLockerService.TAG));
                finish();

                //    }
            }
        });
    }
    private void codeButtonListener() //This function set the code buttons events
    {
        codeButton.setOnClickListener(new View.OnClickListener() {////http://stackoverflow.com/questions/24969380/android-edittext-nullpointerexception
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(LockerActivity.this, 4);
                // Get the layout inflater
                LayoutInflater inflater = LockerActivity.this.getLayoutInflater();
                final View layout = inflater.inflate(R.layout.input_window, null);
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(layout);
                builder.setTitle("Kód megadása");
                builder.setPositiveButton("Beállít", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) layout.findViewById(R.id.editCodeText);
                        String emptyText = "";
                        emptyText = editText.getText().toString().trim();
                        if (emptyText.isEmpty() || emptyText.length() == 0 || emptyText.equals("") || emptyText == null) {
                            Toast.makeText(context, "Nincs érték!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (editText.getText().toString().length() == 4) {
                                code = editText.getText().toString();
                                SaveCode(code);
                                //codeSetted = true;
                                Toast.makeText(context, "Érték beállítva!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Nem megfelelő a hossz!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                Dialog editTextDialog = builder.create();
                editTextDialog.show();


            }
        });
    }
    private void exitButtonListener() //This function set the exit buttons events
    {
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LockerActivity.this, AppLockerService.class);
                LockerActivity.this.stopService(i);

                System.exit(0);
                //finish();
            }
        });
    }
    private void CurrentAppFinder()
    {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        // activityOnTop = ar.topActivity.getClassName();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.locker, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean saveArray()
    {
        selectedApps = adapter.getSelectedApps();

        String PrefFileName = "MyPrefFile";
        SharedPreferences sp = getSharedPreferences(PrefFileName, 0);

        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Status_size", selectedApps.size()); /* sKey is an array */

        for(int i=0;i<selectedApps.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, selectedApps.get(i));
        }

        return mEdit1.commit();
    }

    public void SaveCode(String codeS)
    {
        String filename = "codeFile.txt";
        String string = codeS;
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}

