package at.spengergasse.vocago;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    public void repititionClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //Neues Fenster
        builder.setTitle(getString(R.string.wordRepitition)); //Titel des Fensters setzen



        //test

        FrameLayout layout = new FrameLayout(getApplicationContext()); //Neues FrameLayout erstellen um ein Padding für das Textfeld zu haben
        layout.setPadding(width / 15, 0, width / 15, 0); //Padding je nach Bildschirmgröße setzen

        LayoutInflater inflater;
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout outerLayout = (LinearLayout) inflater.inflate(R.layout.layout_repitition, null);

        FrameLayout inner1 = (FrameLayout) outerLayout.getChildAt(0);
        final EditText inner1txt= (EditText)inner1.getChildAt(1);
        inner1txt.setText(""+MainActivity.repArray.get(0));

        FrameLayout inner2 = (FrameLayout) outerLayout.getChildAt(1);
        final EditText inner2txt= (EditText)inner2.getChildAt(1);
        inner2txt.setText(""+MainActivity.repArray.get(1));

        FrameLayout inner3 = (FrameLayout) outerLayout.getChildAt(2);
        final EditText inner3txt= (EditText)inner3.getChildAt(1);
        inner3txt.setText(""+MainActivity.repArray.get(2));

        FrameLayout inner4 = (FrameLayout) outerLayout.getChildAt(3);
        final EditText inner4txt= (EditText)inner4.getChildAt(1);
        inner4txt.setText(""+MainActivity.repArray.get(3));

        layout.addView(outerLayout);
        builder.setView(layout);//Layout als Anzeige des Fensters setzen

        //OK Button listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean gueltig = true;
                try {
                    int repVal1 = Integer.parseInt(inner1txt.getText().toString());
                } catch (Exception exc) {
                    Toast.makeText(getApplicationContext(),"falsche eingabe 1-10",Toast.LENGTH_SHORT).show();
                    gueltig = false;
                }

                try {
                    int repVal2 = Integer.parseInt(inner2txt.getText().toString());
                    if(!(repVal2<20)) throw new Exception();
                } catch (Exception exc) {
                    Toast.makeText(getApplicationContext(),"falsche eingabe 11-20",Toast.LENGTH_SHORT).show();
                    gueltig = false;
                }

                try {
                    int repVal2 = Integer.parseInt(inner2txt.getText().toString());
                    if(!(repVal2<30)) throw new Exception();
                } catch (Exception exc) {
                    Toast.makeText(getApplicationContext(),"falsche eingabe 21-30",Toast.LENGTH_SHORT).show();
                    gueltig = false;
                }

                try {
                    int repVal2 = Integer.parseInt(inner2txt.getText().toString());
                    if(!(repVal2<31)) throw new Exception();
                } catch (Exception exc) {
                    Toast.makeText(getApplicationContext(),"falsche eingabe 31+",Toast.LENGTH_SHORT).show();
                    gueltig = false;
                }

                if(gueltig){
                    dialog.cancel(); //TODO? maybe weil geht ned wirklich
                }

            }
        });
        //Abbrechen Button listener
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); //Änderungen verwerfen
            }
        });
        builder.show(); //Fenster anzeigen
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void changeStatusBarColor(){
       getWindow().setStatusBarColor(0xFF435e70);
    }
}
