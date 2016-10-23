package at.spengergasse.vocago;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static ArrayList<Unit> unitArray = new ArrayList<Unit>();
    static ArrayList<Integer> repArray = new ArrayList<>();
    NavigationView navigationView;
    int selectedUnitIndex = 0;
    int width; //Die Displaybreite
    int height; //Die Displayhöhe
    TextView textTop;
    TextView textBottom;
    Word currentWord; //Das aktuelle Wort
    boolean bothWords = true;
    boolean firstWordForeign = true;
    static boolean askForeignWord;
    static boolean askTranslationWord;
    Random rnd = new Random();
    static ArrayList<Word> lastWords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Setzt die Variablen width und height ja auf die Displaybreite und Displayhöhe des Handys
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        textTop = (TextView)findViewById(R.id.textViewTop);
        textBottom = (TextView)findViewById(R.id.textViewBottom);

        fillUnitArray();
        fillRepArray();
        fillWordBooleans();
        changeStatusBarColor();

    }

    @Override
    protected void onResume() {
        updateUnitList();
        super.onResume();
    }

    public void makeToast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    public void addCheckmark(){
        ImageView checkBox = (ImageView) findViewById(R.id.checkBox);
        if(currentWord!=null){
            int k = currentWord.getKnowledge();
            int coordinatesY = (int) findViewById(R.id.pointsZero).getY()+findViewById(R.id.pointsZero).getHeight()-checkBox.getHeight();
            if(k==0){
                checkBox.setX(findViewById(R.id.pointsZero).getX() + findViewById(R.id.pointsZero).getWidth() - checkBox.getWidth());
                checkBox.setY(coordinatesY);
            }
            else if(k==1){
                checkBox.setX(findViewById(R.id.pointsOne).getX() + findViewById(R.id.pointsOne).getWidth() - checkBox.getWidth());
                checkBox.setY(coordinatesY);
            }
            else if(k==2){
                checkBox.setX(findViewById(R.id.pointsTwo).getX() + findViewById(R.id.pointsTwo).getWidth() - checkBox.getWidth());
                checkBox.setY(coordinatesY);
            }
            else if(k==3){
                checkBox.setX(findViewById(R.id.pointsThree).getX() + findViewById(R.id.pointsThree).getWidth() - checkBox.getWidth());
                checkBox.setY(coordinatesY);
            }
            if(!(checkBox.getX()==0 && checkBox.getY()==0)){
                checkBox.setVisibility(View.VISIBLE);
            }
            checkBox.bringToFront();
            updateUnitList();
        }
        else{
            checkBox.setVisibility(View.INVISIBLE);
        }
    }

    //Fenster für das erstellen eines neuen Wortes
    public void addWordClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //Fenster wird erstellt
        builder.setTitle(getString(R.string.addWord)); //Titel wird gesetzt

        FrameLayout layout = new FrameLayout(getApplicationContext()); //Framelayout für das Padding der Elemente
        layout.setPadding(width / 18, width / 72, width / 18, 0); //Padding des FrameLayout

        LinearLayout linearLayout = new LinearLayout(getApplicationContext()); //LinearLayout zum anordnen der Elemente
        linearLayout.setOrientation(LinearLayout.VERTICAL); //Vertikale Orientierung für das Linear Layout
        linearLayout.setGravity(Gravity.CENTER); //Die Objekte werden zentriert

        final EditText inputForeign = new EditText(this); //Textfeld zum eingeben des Wortes
        inputForeign.setHint(getString(R.string.foreignWord)); //"Hintergrundschrift" des Textfeldes setzen
        linearLayout.addView(inputForeign); // Das Textfeld dem LinearLayout hinzufügen


        FrameLayout frameLayoutTrans = new FrameLayout(getApplicationContext()); //Neues Framelayout für den Abstand des Mittleren edittexts nach oben und unten
        frameLayoutTrans.setPadding(0,width/36,0,width/144); //Setzten des Abstandes

        final EditText inputTranslation = new EditText(this); //Textfeld zum eingeben der Übersetzung
        inputTranslation.setHint(getString(R.string.translation));
        frameLayoutTrans.addView(inputTranslation); //Textfeld dem FrameLayout hinzufügen
        linearLayout.addView(frameLayoutTrans); //FrameLayout dem LinearLayout hinzufügen

        List<CharSequence> unitNameList= new ArrayList<>(); //Liste mit den Namen der Units
        for(Unit u : unitArray) {
            unitNameList.add(u.getName()); //Liste wird befüllt
        }
        FrameLayout frameLayoutSpinner = new FrameLayout(getApplicationContext()); //Neues Framelayout für die ComboBox mit den Unitname
        frameLayoutSpinner.setPadding(width/(72/10), 0, width/(72/10), 0); //Abstand der Combobox nach links und rechts

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getApplicationContext(),R.layout.spinner_item_layout,unitNameList); //Adapter erstellen, um die Daten in die ComboBox zu übertragen und ein Layout für die Items zu setzen
        final Spinner inputUnit = new Spinner(this); //Neuen Spinner (ComboBox) erstellen
        inputUnit.setAdapter(adapter); //Adapter für den Spinner setzen
        inputUnit.setSelection(selectedUnitIndex); //Die Liste als source für den Spinner setzen
        frameLayoutSpinner.addView(inputUnit); //Spinner dem FrameLayout hinzufügen
        linearLayout.addView(frameLayoutSpinner);  //FrameLayout dem LinearLayout hinzufügen

        layout.addView(linearLayout); //LinearLayout dem äußersten FrameLayout hinzufügen

        builder.setView(layout); //äußerstes FrameLayout als Anzeige im Fenster setzen

        //Listener für dem "OK" Button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Word word = new Word(inputTranslation.getText().toString(),inputForeign.getText().toString(),0);
                boolean b = unitArray.get(inputUnit.getSelectedItemPosition()).addWord(word);
                if(b) makeToast("'" + word.getWordForeign() + "' " + getString(R.string.unitAddText));
                else makeToast(getString(R.string.addWordError));
                updateUnitList();
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

    public void nextClick(View view){
        int unitSize = unitArray.get(selectedUnitIndex).getWordArray().size();
        if(unitSize > 0){
           if(bothWords){
               currentWord = unitArray.get(selectedUnitIndex).getRandomWord();
               int index = 0;
               while(lastWords.contains(currentWord)){
                   currentWord = unitArray.get(selectedUnitIndex).getRandomWord();
                   index ++;
                   if(index > 999) break; //Sicherheitsmaßnahme gegen Endlosschleife
               }
               if(askForeignWord && askTranslationWord) firstWordForeign = rnd.nextBoolean();
               else if(askTranslationWord) firstWordForeign = true;
               else if(askForeignWord) firstWordForeign = false;
               if(firstWordForeign) textTop.setText(currentWord.getWordForeign());
               else textTop.setText(currentWord.getWordNative());
               textBottom.setText("");
               bothWords = false;
               addCheckmark();
               lastWords.add(currentWord);
               if(unitSize<=10){
                   if(lastWords.size() >= unitSize) lastWords.remove(0);
               }
               else if (unitSize<=20) {
                   if (lastWords.size() >= repArray.get(0)) lastWords.remove(0);
               }
               else if (unitSize<=30) {
                   if (lastWords.size() >= repArray.get(1)) lastWords.remove(0);
               }
               else{
                   if (lastWords.size() >= repArray.get(2)) lastWords.remove(0);
               }

           }
           else{
               if(firstWordForeign)  textBottom.setText(currentWord.getWordNative());
               else textBottom.setText(currentWord.getWordForeign());
               bothWords = true;
           }

        }
    }

    public void feedbackRedClick(View view){
        if(currentWord != null){
            currentWord.setKnowledge(0);
            addCheckmark();
        }
    }

    public void feedbackOrangeClick(View view){
        if(currentWord != null){
            currentWord.setKnowledge(1);
            addCheckmark();
        }
    }

    public void feedbackLimeClick(View view){
        if(currentWord != null){
            currentWord.setKnowledge(2);
            addCheckmark();
        }
    }

    public void feedbackGreenClick(View view){
        if(currentWord != null){
            currentWord.setKnowledge(3);
            addCheckmark();
        }
    }

    // Ladet die Units aus dem Ordner in das Array
    private void fillUnitArray(){
        try {
            FileInputStream fio = openFileInput("units.dat");
            ObjectInputStream ois = new ObjectInputStream(fio); //units.dat wird eingelesen und als unitArray gesetzt
            unitArray = (ArrayList<Unit>) ois.readObject();
        }
        catch(Exception exc){
            //Toast.makeText(getApplicationContext(),exc.getMessage(),Toast.LENGTH_SHORT).show();
        }

        if(unitArray.size()==0){
            addUnit(); //Wenn der Ordner leer war, wird eine neue Unit erzeugt
        }
       updateUnitList();
    }

    // Updatet die Liste im Navigation Drawer nach der ArrayList "unitArray"
    private void updateUnitList(){
        Menu menu = navigationView.getMenu(); //Das aktuelle Menü des Navigation Drawers wird geholt
        menu.removeGroup(R.id.unitMenu); //Alle Units werden aus dem Menü geschmissen
        for(Unit u:unitArray){
            menu.add(R.id.unitMenu,Menu.NONE,Menu.NONE,u.getName()); //Jede Unit, wird mit ihrem Namen in das Menü als Item eingefügt
        }
        menu.setGroupCheckable(R.id.unitMenu, true, true); //Das Menü wird als "checkable" gesetzt, man kann also eine Unit Auswählen und nicht nur draufklicken

        for(int i = 0; i < menu.size(); i ++){
            menu.getItem(i).setIcon(R.drawable.ic_description_24dp); //Den Items, also den Units wird ein Icon hinzugefügt
        }
        setSelectedUnit(); //Die aktuell ausgewählte Unit wird auch im Menü als ausgewählt gesetzt

        try {
            FileOutputStream fos = openFileOutput("units.dat",getApplicationContext().MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);   //Die Liste wird wieder abgespeichert und überschreibt die Alte
            oos.writeObject(unitArray);
            oos.close();
        }
        catch(Exception exc){
           // Toast.makeText(getApplicationContext(),exc.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    // Fügt eine neue Unit hinzu, nach dem Schema: "New Unit", "New Unit (1)", "New Unit (2)"...
    private void addUnit(){
        String newName = getString(R.string.newUnit); //Je nach ausgewählter Sprache, wird die neue Unit anders genannt
        boolean foundName = false;
        int i = 1;
        while (!foundName){
            foundName = true;
            for (Unit u:unitArray) {
                if((u.getName().equals(newName))){ //Schleifen um den nächsten Unitnamen herauszufinden
                    foundName = false;
                }
            }
            if(!foundName){
                newName = getString(R.string.newUnit) + " (" + i + ")";
                i++;
            }
        }
        Unit u = new Unit(newName); //Neue Unit mit dem zuvor automatisch erstellten Namen erzeugen
        unitArray.add(u); //Die Unit der ArrayList hinzufügen
        updateUnitList(); //Die Anzeige im Navigation Drawer updaten und die Liste speichern
        makeToast("'" + newName + "' " + getString(R.string.unitAddText));
    }

    //Setzt das Item im navigation Drawer auf 'selected'
    private void setSelectedUnit(){
        navigationView.getMenu().getItem(selectedUnitIndex).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent nextScreen = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(nextScreen);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.addUnit) {
            addUnit();
        }
        ////////////////
        //UNIT LÖSCHEN
        else if (id == R.id.deleteUnit) {
            if(unitArray.size()>1) {
                String name = unitArray.get(selectedUnitIndex).getName();
                unitArray.remove(selectedUnitIndex);
                if (selectedUnitIndex == unitArray.size()) {
                    selectedUnitIndex--;
                }
                textTop.setText("");
                textBottom.setText("");
                currentWord = null;
                addCheckmark();
                bothWords = true;
                updateUnitList();
                makeToast("'" + name + "' " + getString(R.string.unitDeleteText));
            }
            else{
                makeToast(getString(R.string.deleteuniterror));
            }
        }
        /////////////////
        //UNIT UMBENENNEN
        else if (id == R.id.renameUnit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //Neues Fenster
            builder.setTitle(getString(R.string.renameUnit)); //Titel des Fensters setzen

            FrameLayout layout = new FrameLayout(getApplicationContext()); //Neues FrameLayout erstellen um ein Padding für das Textfeld zu haben
            layout.setPadding(width/18, width/72, width/18, 0); //Padding je nach Bildschirmgröße setzen

            final EditText input = new EditText(this); //Neues Textfeld erstellen
            input.setText(unitArray.get(selectedUnitIndex).getName()); //Den Text auf den Namen der aktuellen Unit setzen
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(17)}); //Maximale Textlönge auf 17 Zeichen setzen

            layout.addView(input);//Textfeld dem layout hinzufügen

            builder.setView(layout);//Layout als Anzeige des Fensters setzen

            //OK Button listener
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    unitArray.get(selectedUnitIndex).setName(input.getText().toString()); //Änderungen übernehmen und alles updaten
                    updateUnitList();
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
        //////////////
        //Eine Unit wurde ausgewählt
        else{
            //Der index für die aktuell ausgewählte Unit wird geändert
            Menu menu = navigationView.getMenu();
            for(int i = 0; i < menu.size(); i ++){ //Alle Items des Menüs durchgehen
                if(menu.getItem(i).equals(item)){ //Wenn das ausgewählte Item gleich dem Item in der Liste ist
                    selectedUnitIndex = i; //wird die Indexvarialbe geändert
                    break;
                }
            }

            textTop.setText("");
            textBottom.setText("");
            currentWord = null;
            addCheckmark();
            bothWords = true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void changeStatusBarColor(){
        getWindow().setStatusBarColor(0xFF435e70);
    }

    public void fillRepArray(){
        try {
            FileInputStream fio = openFileInput("reps.dat");
            ObjectInputStream ois = new ObjectInputStream(fio);
            repArray = (ArrayList<Integer>) ois.readObject();
        }
        catch(Exception exc){
            try {
                repArray.add(5);
                repArray.add(15);
                repArray.add(25);

                FileOutputStream fos = openFileOutput("reps.dat",getApplicationContext().MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(repArray);
                oos.close();

                fillRepArray();
            }
            catch(Exception exc2){}
        }
    }

    public void fillWordBooleans(){
        try {
            FileInputStream fio = openFileInput("askForeign.dat");
            ObjectInputStream ois = new ObjectInputStream(fio);
            askForeignWord = (boolean) ois.readObject();

            fio = openFileInput("askTranslation.dat");
            ois = new ObjectInputStream(fio);
            askTranslationWord = (boolean) ois.readObject();
        }
        catch(Exception exc){
            try {
                askForeignWord = true;
                askTranslationWord = true;

                FileOutputStream fos = openFileOutput("askForeign.dat",getApplicationContext().MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(askForeignWord);
                oos.close();

                fos = openFileOutput("askTranslation.dat",getApplicationContext().MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(askTranslationWord);
                oos.close();

                fillWordBooleans();
            }
            catch(Exception exc2){

            }
        }
    }

}