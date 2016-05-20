package at.spengergasse.vocago;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class SettingsActivity extends AppCompatActivity{
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
        findViewById(R.id.evaluationList).setMinimumHeight(height/10);;
        findViewById(R.id.repitition).setMinimumHeight(height/10);

        changeStatusBarColor();
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

        //test

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
        inner3txt.setText(""+MainActivity.repArray.get(1));

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

    private void saveRepArray(){
        try{
            FileOutputStream fos = openFileOutput("reps.dat",getApplicationContext().MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(MainActivity.repArray);
            oos.close();
        }
        catch(Exception exc){}
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void changeStatusBarColor(){
       getWindow().setStatusBarColor(0xFF435e70);
    }
}
