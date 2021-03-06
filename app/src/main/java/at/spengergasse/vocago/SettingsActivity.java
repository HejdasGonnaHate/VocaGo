package at.spengergasse.vocago;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity{
    final private int REQUEST_CODE_ASK_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 11;
    final private int REQUEST_CODE_ASK_PERMISSIONS_READ_EXTERNAL_STORAGE = 12;
    int height;
    int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton();
            }
        });

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        findViewById(R.id.wordList).setMinimumHeight(height/10);;
        findViewById(R.id.repitition).setMinimumHeight(height/10);
        findViewById(R.id.askWords).setMinimumHeight(height/10);
        findViewById(R.id.importUnit).setMinimumHeight(height/10);
        findViewById(R.id.exportUnit).setMinimumHeight(height/10);

        changeStatusBarColor();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    exportUnitClickExecute();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "WRITE_EXTERNAL_STORAGE denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    importUnitClickExecute();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "READ_EXTERNAL_STORAGE denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void backButton(){
        finish();
    }

    public void wordlistClick(View view){
        Intent nextScreen = new Intent(getApplicationContext(),WordlistActivity.class);
        startActivity(nextScreen);
    }

    public void evaluationClick(View view){
        //TODO
        Toast.makeText(getApplicationContext(),"This feature has not been implemented yet :/",Toast.LENGTH_LONG).show();
    }


    public void repetitionClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //Neues Fenster
        builder.setTitle(getString(R.string.wordRepitition)); //Titel des Fensters setzen

        FrameLayout layout = new FrameLayout(getApplicationContext()); //Neues FrameLayout erstellen um ein Padding für das Textfeld zu haben
        layout.setPadding(width / 15, 0, width / 15, 0); //Padding je nach Bildschirmgröße setzen

        LayoutInflater inflater;
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout outerLayout = (LinearLayout) inflater.inflate(R.layout.layout_repitition, null);

        FrameLayout inner2 = (FrameLayout) outerLayout.getChildAt(0);
        final EditText inner2txt= (EditText)inner2.getChildAt(1);
        inner2txt.setText(""+MainActivity.repArray.get(0));

        FrameLayout inner3 = (FrameLayout) outerLayout.getChildAt(1);
        final EditText inner3txt= (EditText)inner3.getChildAt(1);
        inner3txt.setText("" + MainActivity.repArray.get(1));

        FrameLayout inner4 = (FrameLayout) outerLayout.getChildAt(2);
        final EditText inner4txt= (EditText)inner4.getChildAt(1);
        inner4txt.setText("" + MainActivity.repArray.get(2));

        layout.addView(outerLayout);
        builder.setView(layout);//Layout als Anzeige des Fensters setzen

        builder.setPositiveButton("OK", null);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setNeutralButton("?",null);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        boolean gueltig = true;
                        int repVal0 = 5;
                        int repVal1 = 15;
                        int repVal2 = 25;

                        try {
                            repVal0 = Integer.parseInt(inner2txt.getText().toString());
                            if (repVal0 > 10 || repVal0 < 0) throw new Exception();
                        } catch (Exception exc) {
                            Toast.makeText(getApplicationContext(), getString(R.string.repError1), Toast.LENGTH_SHORT).show();
                            gueltig = false;
                        }

                        try {
                            repVal1 = Integer.parseInt(inner3txt.getText().toString());
                            if (repVal1 > 20 || repVal1 < 0) throw new Exception();
                        } catch (Exception exc) {
                            Toast.makeText(getApplicationContext(), getString(R.string.repError2), Toast.LENGTH_SHORT).show();
                            gueltig = false;
                        }

                        try {
                            repVal2 = Integer.parseInt(inner4txt.getText().toString());
                            if (repVal2 > 30 || repVal2 < 0) throw new Exception();
                        } catch (Exception exc) {
                            Toast.makeText(getApplicationContext(), getString(R.string.repError3), Toast.LENGTH_SHORT).show();
                            gueltig = false;
                        }

                        if (gueltig) {
                            mAlertDialog.cancel();
                            MainActivity.repArray.set(0, repVal0);
                            MainActivity.repArray.set(1, repVal1);
                            MainActivity.repArray.set(2, repVal2);

                            MainActivity.lastWords.clear();
                            saveRepArray();
                        }
                    }
                });

                Button b2 = mAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this); //Neues Fenster
                        builder.setTitle(getString(R.string.help));

                        FrameLayout layout = new FrameLayout(getApplicationContext()); //Neues FrameLayout erstellen um ein Padding für das Textfeld zu haben
                        layout.setPadding(width / 15, width/72, width / 15, 0); //Padding je nach Bildschirmgröße setzen

                        TextView textView = new TextView(getApplicationContext());
                        textView.setTextColor(0xFF737373);
                        textView.setText(getString(R.string.repHelpText));
                        layout.addView(textView);

                        builder.setPositiveButton("OK", null);
                        builder.setView(layout);
                        builder.show();
                    }
                });
            }
        });
        mAlertDialog.show();
    }

    public void askWordsClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //Neues Fenster
        builder.setTitle(getString(R.string.askedWords)); //Titel des Fensters setzen

        FrameLayout layout = new FrameLayout(getApplicationContext()); //Neues FrameLayout erstellen um ein Padding für das Textfeld zu haben
        layout.setPadding(width / 15, 0, width / 15, 0); //Padding je nach Bildschirmgröße setzen

        LayoutInflater inflater;
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout outerLayout = (LinearLayout) inflater.inflate(R.layout.layout_askwords, null);

        FrameLayout inner1 = (FrameLayout) outerLayout.getChildAt(0);
        final CheckBox checkBoxTrans = (CheckBox) inner1.getChildAt(1);
        checkBoxTrans.setChecked(MainActivity.askTranslationWord);
        inner1.setMinimumHeight(width / 8);

        FrameLayout inner2 = (FrameLayout) outerLayout.getChildAt(1);
        final CheckBox checkBoxForeign = (CheckBox) inner2.getChildAt(1);
        checkBoxForeign.setChecked(MainActivity.askForeignWord);
        inner2.setMinimumHeight(width / 8);

        layout.addView(outerLayout);
        builder.setView(layout);//Layout als Anzeige des Fensters setzen

        builder.setPositiveButton("OK", null);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setNeutralButton("?", null);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        boolean gueltig = true;
                        if(!checkBoxTrans.isChecked() && !checkBoxForeign.isChecked()) gueltig = false;
                        if (gueltig) {
                            MainActivity.askTranslationWord = checkBoxTrans.isChecked();
                            MainActivity.askForeignWord = checkBoxForeign.isChecked();
                            saveAskWords();
                            mAlertDialog.cancel();
                        }
                        else
                            Toast.makeText(getApplicationContext(), getString(R.string.askError),Toast.LENGTH_SHORT).show();
                    }
                });

                Button b2 = mAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this); //Neues Fenster
                        builder.setTitle(getString(R.string.help));

                        FrameLayout layout = new FrameLayout(getApplicationContext()); //Neues FrameLayout erstellen um ein Padding für das Textfeld zu haben
                        layout.setPadding(width / 15, width / 72, width / 15, 0); //Padding je nach Bildschirmgröße setzen

                        TextView textView = new TextView(getApplicationContext());
                        textView.setTextColor(0xFF737373);
                        textView.setText(getString(R.string.askHelpText));
                        layout.addView(textView);

                        builder.setPositiveButton("OK", null);
                        builder.setView(layout);
                        builder.show();
                    }
                });
            }
        });
        mAlertDialog.show();
    }

    public void importUnitClick(View view){
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS_READ_EXTERNAL_STORAGE);
            } else {
                importUnitClickExecute();
            }
        } else {
            importUnitClickExecute();
        }
    }

    public void importUnitClickExecute(){
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String[] names = dir.list(
                new FilenameFilter()
                {
                    public boolean accept(File dir, String name)
                    {
                        return name.endsWith(".vocago");
                    }
                });
        for(String fileName:names){
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Unit unit = (Unit)ois.readObject();
                MainActivity.unitArray.add(unit);
                Toast.makeText(getApplicationContext(),unit.getName()+" wurde importiert!",Toast.LENGTH_SHORT).show();
            }
            catch (Exception exc){
                //TODO
            }
        }
    }

    public void exportUnitClick(View view){
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
            } else {
                exportUnitClickExecute();
            }
        } else {
            exportUnitClickExecute();
        }
    }

    public void exportUnitClickExecute(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.exportUnit));

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setPadding(width/15, 0, width/15, 0);
        layout.setOrientation(LinearLayout.VERTICAL); //Vertikale Orientierung für das Linear Layout
        layout.setGravity(Gravity.CENTER); //Die Objekte werden zentriert

        List<CharSequence> unitNameList= new ArrayList<>();
        for(Unit u : MainActivity.unitArray) {
            unitNameList.add(u.getName());
        }
        FrameLayout frameLayoutSpinner = new FrameLayout(getApplicationContext());
        frameLayoutSpinner.setPadding(width/(72/10), 0, width/(72/10), 0);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getApplicationContext(),R.layout.spinner_item_layout,unitNameList);
        final Spinner inputUnit = new Spinner(this);
        inputUnit.setAdapter(adapter);
        frameLayoutSpinner.addView(inputUnit);
        layout.addView(frameLayoutSpinner);

        builder.setView(layout);

        builder.setPositiveButton("OK", null);
        builder.setNegativeButton(getString(R.string.cancel), null);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        try {
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), MainActivity.unitArray.get(inputUnit.getSelectedItemPosition()).getName()+".vocago");
                            FileOutputStream fos = new FileOutputStream(file);
                            ObjectOutputStream oos = new ObjectOutputStream(fos);
                            oos.writeObject(MainActivity.unitArray.get(inputUnit.getSelectedItemPosition()));
                            oos.close();
                            Toast.makeText(getApplicationContext(),MainActivity.unitArray.get(inputUnit.getSelectedItemPosition()).getName()+" "+getString(R.string.exportSuccess),Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        mAlertDialog.show();
    }

    private void saveRepArray(){
        try{
            FileOutputStream fos = openFileOutput("reps.dat",getApplicationContext().MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(MainActivity.repArray);
            oos.close();
        }
        catch(Exception exc){
            //TODO
        }
    }

    private void saveAskWords(){
        try {
            FileOutputStream fos = openFileOutput("askForeign.dat", getApplicationContext().MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(MainActivity.askForeignWord);
            oos.close();

            fos = openFileOutput("askTranslation.dat", getApplicationContext().MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(MainActivity.askTranslationWord);
            oos.close();
        }
        catch(Exception exc){}
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void changeStatusBarColor(){
        getWindow().setStatusBarColor(0xFF435e70);
    }
}
