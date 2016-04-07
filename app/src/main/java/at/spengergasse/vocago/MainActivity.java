package at.spengergasse.vocago;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<Unit> unitArray = new ArrayList<Unit>();
    NavigationView navigationView;
    int selectedUnitIndex = 0;
    int width; //Die Displaybreite
    int height; //Die Displayhöhe

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

        Button nextButton = (Button) findViewById(R.id.nextButton);

        //Setzt die Variablen width und height ja auf die Displaybreite und Displayhöhe des Handys
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        fillUnitArray();
        nextButton.performClick();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Fenster für das erstellen eines neuen Wortes
    public void addWordClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //Fenster wird erstellt
        builder.setTitle(getString(R.string.addWord)); //Titel wird gesetzt

        FrameLayout layout = new FrameLayout(getApplicationContext()); //Framelayout für das Padding der Elemente
        layout.setPadding(28800 / width, 7200 / width, 28800 / width, 0); //Padding des FrameLayout

        LinearLayout linearLayout = new LinearLayout(getApplicationContext()); //LinearLayout zum anordnen der Elemente
        linearLayout.setOrientation(LinearLayout.VERTICAL); //Vertikale Orientierung für das Linear Layout
        linearLayout.setGravity(Gravity.CENTER); //Die Objekte werden zentriert

        final EditText inputForeign = new EditText(this); //Textfeld zum eingeben des Wortes
        inputForeign.setHint(getString(R.string.foreignWord)); //"Hintergrundschrift" des Textfeldes setzen
        linearLayout.addView(inputForeign); // Das Textfeld dem LinearLayout hinzufügen


        FrameLayout frameLayoutTrans = new FrameLayout(getApplicationContext()); //Neues Framelayout für den Abstand des Mittleren edittexts nach oben und unten
        frameLayoutTrans.setPadding(0,14400/width,0,3600/width); //Setzten des Abstandes

        final EditText inputTranslation = new EditText(this); //Textfeld zum eingeben der Übersetzung
        inputTranslation.setHint(getString(R.string.translation));
        frameLayoutTrans.addView(inputTranslation); //Textfeld dem FrameLayout hinzufügen
        linearLayout.addView(frameLayoutTrans); //FrameLayout dem LinearLayout hinzufügen

        List<CharSequence> unitNameList= new ArrayList<>(); //Liste mit den Namen der Units
        for(Unit u : unitArray) {
            unitNameList.add(u.getName()); //Liste wird befüllt
        }
        FrameLayout frameLayoutSpinner = new FrameLayout(getApplicationContext()); //Neues Framelayout für die ComboBox mit den Unitname
        frameLayoutSpinner.setPadding(72000 / width, 0, 72000 / width, 0); //Abstand der Combobox nach links und rechts

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
                //TODO
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
        TextView textTop = (TextView)findViewById(R.id.textViewTop);
        TextView textBottom = (TextView)findViewById(R.id.textViewBottom);
    }

    public void evaluationClick(View view){

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
                unitArray.remove(selectedUnitIndex);
                if (selectedUnitIndex == unitArray.size()) {
                    selectedUnitIndex--;
                }
                updateUnitList();
            }
            else{
                Toast.makeText(getApplicationContext(),R.string.deleteuniterror,Toast.LENGTH_SHORT).show();
            }
        }
        /////////////////
        //UNIT UMBENENNEN
        else if (id == R.id.renameUnit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //Neues Fenster
            builder.setTitle(getString(R.string.renameUnit)); //Titel des Fensters setzen

            FrameLayout layout = new FrameLayout(getApplicationContext()); //Neues FrameLayout erstellen um ein Padding für das Textfeld zu haben
            layout.setPadding(28800/width,7200/width,28800/width,0); //Padding je nach Bildschirmgröße setzen

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

            //TODO update unit, neue unit wurde ausgewählt
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
