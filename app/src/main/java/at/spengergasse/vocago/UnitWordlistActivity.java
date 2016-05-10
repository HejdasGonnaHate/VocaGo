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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UnitWordlistActivity extends AppCompatActivity {
    LinearLayout layout;
    int width; //Die Displaybreite
    int height; //Die Displayhöhe
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unitwordlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(WordlistActivity.selectedUnit.getName());
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

        layout = (LinearLayout) findViewById(R.id.wordListLayout);
        fillWordList();

        changeStatusBarColor();
    }

    private void backButton(){
        finish();
    }

    public void fillWordList(){
        layout.removeAllViews();
        for(final Word w : WordlistActivity.selectedUnit.getWordArray()) {
            LayoutInflater inflater;
            inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout innerLayout = (LinearLayout) inflater.inflate(R.layout.unitlistelement, null);
            innerLayout.setMinimumHeight(height / 10);
            TextView textView = (TextView) innerLayout.getChildAt(0);
            textView.setText(w.getWordForeign()+" - "+ w.getWordNative());
            layout.addView(innerLayout);

            ImageView imageView = (ImageView)innerLayout.getChildAt(1);
            imageView.setImageResource(R.drawable.ic_mode_edit_24dp_grey);

            innerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wordClick(w);
                }
            });
        }
    }

    private void wordClick(final Word w){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this); //Fenster wird erstellt
        builder.setTitle(getString(R.string.editWord)); //Titel wird gesetzt

        FrameLayout layout = new FrameLayout(getApplicationContext()); //Framelayout für das Padding der Elemente
        layout.setPadding(width / 18, width / 72, width / 18, 0); //Padding des FrameLayout

        LinearLayout linearLayout = new LinearLayout(getApplicationContext()); //LinearLayout zum anordnen der Elemente
        linearLayout.setOrientation(LinearLayout.VERTICAL); //Vertikale Orientierung für das Linear Layout
        linearLayout.setGravity(Gravity.CENTER); //Die Objekte werden zentriert

        final EditText inputForeign = new EditText(this); //Textfeld zum eingeben des Wortes
        inputForeign.setText(w.getWordForeign());
        inputForeign.setHint(getString(R.string.foreignWord)); //"Hintergrundschrift" des Textfeldes setzen
        linearLayout.addView(inputForeign); // Das Textfeld dem LinearLayout hinzufügen


        FrameLayout frameLayoutTrans = new FrameLayout(getApplicationContext()); //Neues Framelayout für den Abstand des Mittleren edittexts nach oben und unten
        frameLayoutTrans.setPadding(0,width/36,0,width/144); //Setzten des Abstandes

        final EditText inputTranslation = new EditText(this); //Textfeld zum eingeben der Übersetzung
        inputTranslation.setText(w.getWordNative());
        inputTranslation.setHint(getString(R.string.translation));
        frameLayoutTrans.addView(inputTranslation); //Textfeld dem FrameLayout hinzufügen
        linearLayout.addView(frameLayoutTrans); //FrameLayout dem LinearLayout hinzufügen

        List<CharSequence> unitNameList= new ArrayList<>(); //Liste mit den Namen der Units
        for(Unit u : MainActivity.unitArray) {
            unitNameList.add(u.getName()); //Liste wird befüllt
        }
        FrameLayout frameLayoutSpinner = new FrameLayout(getApplicationContext()); //Neues Framelayout für die ComboBox mit den Unitname
        frameLayoutSpinner.setPadding(width/(72/10), 0, width/(72/10), 0); //Abstand der Combobox nach links und rechts

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getApplicationContext(),R.layout.spinner_item_layout,unitNameList); //Adapter erstellen, um die Daten in die ComboBox zu übertragen und ein Layout für die Items zu setzen
        final Spinner inputUnit = new Spinner(this); //Neuen Spinner (ComboBox) erstellen
        inputUnit.setAdapter(adapter); //Adapter für den Spinner setzen
        int index = 0;
        for(Unit u : MainActivity.unitArray){
            if(u.equals(WordlistActivity.selectedUnit)) break;
            index ++;
        }
        inputUnit.setSelection(index); //Die Liste als source für den Spinner setzen //TODO
        frameLayoutSpinner.addView(inputUnit); //Spinner dem FrameLayout hinzufügen
        linearLayout.addView(frameLayoutSpinner);  //FrameLayout dem LinearLayout hinzufügen

        FrameLayout frameLayoutButton = new FrameLayout(getApplicationContext());
        frameLayoutButton.setPadding(width/(72/10), 0, width/(72/10), 0); //Abstand der Combobox nach links und rechts;
        Button b = new Button(getApplicationContext());

        b.setText(getString(R.string.delete));
        frameLayoutButton.addView(b);
        linearLayout.addView(frameLayoutButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;
                for (Word word : WordlistActivity.selectedUnit.getWordArray()) {
                    if (word.equals(w)) break;
                    index++;
                }
                WordlistActivity.selectedUnit.removeWord(index);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "'" + w.getWordForeign() + "' " + getString(R.string.unitDeleteText), Toast.LENGTH_SHORT).show();
                try {
                    FileOutputStream fos = openFileOutput("units.dat",getApplicationContext().MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);   //Die Liste wird wieder abgespeichert und überschreibt die Alte
                    oos.writeObject(MainActivity.unitArray);
                    oos.close();
                }
                catch(Exception exc){
                    // Toast.makeText(getApplicationContext(),exc.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        layout.addView(linearLayout); //LinearLayout dem äußersten FrameLayout hinzufügen

        builder.setView(layout); //äußerstes FrameLayout als Anzeige im Fenster setzen

        //Listener für dem "OK" Button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean b1 = false;
                for (char c : inputForeign.getText().toString().toCharArray()) {
                    if (c != ' ') b1 = true;
                }
                boolean b2 = false;
                for (char c : inputTranslation.getText().toString().toCharArray()) {
                    if (c != ' ') b2 = true;
                }
                if (b1 && b2) {
                    w.setWordForeign(inputForeign.getText().toString());
                    w.setWordNative(inputTranslation.getText().toString());
                    int index = 0;
                    for (Word word : WordlistActivity.selectedUnit.getWordArray()) {
                        if (word.equals(w)) break;
                        index++;
                    }
                    WordlistActivity.selectedUnit.removeWord(index);
                    index = inputUnit.getSelectedItemPosition();
                    MainActivity.unitArray.get(index).addWord(w);

                    fillWordList();

                    try {
                        FileOutputStream fos = openFileOutput("units.dat",getApplicationContext().MODE_PRIVATE);
                        ObjectOutputStream oos = new ObjectOutputStream(fos);   //Die Liste wird wieder abgespeichert und überschreibt die Alte
                        oos.writeObject(MainActivity.unitArray);
                        oos.close();
                    }
                    catch(Exception exc){
                        // Toast.makeText(getApplicationContext(),exc.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.addWordError), Toast.LENGTH_SHORT).show();
            }
        });

        //Listener für den "Cancel" Button
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); //Fenster schließen
            }
        });
        builder.show(); //Fenster anzeigen
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void changeStatusBarColor(){
        getWindow().setStatusBarColor(0xFF435e70);
    }

}
