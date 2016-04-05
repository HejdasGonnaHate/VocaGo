package at.spengergasse.vocago;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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

        //Setzt die Variablen width und height ja auf die Displaybreite und Displayhöhe des Handys
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        fillUnitArray();

    }


    public void addWordClick(View view){

    }

    // Ladet die Units aus dem Ordner in das Array
    private void fillUnitArray(){
        try {
            FileInputStream fio = openFileInput("units.dat");
            ObjectInputStream ois = new ObjectInputStream(fio);
            unitArray = (ArrayList<Unit>) ois.readObject();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(),exc.getMessage(),Toast.LENGTH_SHORT).show();
        }

        if(unitArray.size()==0){
            addUnit(); //Wenn der Ordner leer war, wird eine neue Unit erzeugt
        }
       updateUnitList();
    }

    // Updatet die Liste im Navigation Drawer nach der ArrayList "unitArray"
    private void updateUnitList(){
        Menu menu = navigationView.getMenu();
        menu.removeGroup(R.id.unitMenu);
        for(Unit u:unitArray){
            menu.add(R.id.unitMenu,Menu.NONE,Menu.NONE,u.getName());
        }
        menu.setGroupCheckable(R.id.unitMenu, true, true);

        for(int i = 0; i < menu.size(); i ++){
            menu.getItem(i).setIcon(R.drawable.ic_description_24dp);
        }
        setSelectedUnit();

        try {
            FileOutputStream fos = openFileOutput("units.dat",getApplicationContext().MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(unitArray);
            oos.close();
        }
        catch(Exception exc){
           // Toast.makeText(getApplicationContext(),exc.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    // Fügt eine neue Unit hinzu, nach dem Schema: "New Unit", "New Unit (1)", "New Unit (2)"...
    private void addUnit(){
        String newName = getString(R.string.newUnit);
        boolean foundName = false;
        int i = 1;
        while (!foundName){
            foundName = true;
            for (Unit u:unitArray) {
                if((u.getName().equals(newName))){
                    foundName = false;
                }
            }
            if(!foundName){
                newName = getString(R.string.newUnit) + " (" + i + ")";
                i++;
            }
        }
        Unit u = new Unit(newName);
        unitArray.add(u);
        updateUnitList();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent nextScreen = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(nextScreen);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.addUnit) {
            addUnit();
        }
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
        else if (id == R.id.renameUnit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.renameUnit));

            FrameLayout layout = new FrameLayout(getApplicationContext());
            layout.setPadding(28800/width,7200/width,28800/width,0);

            final EditText input = new EditText(this);
            input.setText(unitArray.get(selectedUnitIndex).getName());
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(17)});

            layout.addView(input);

            builder.setView(layout);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    unitArray.get(selectedUnitIndex).setName(input.getText().toString());
                    updateUnitList();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else{
            Menu menu = navigationView.getMenu();
            for(int i = 0; i < menu.size(); i ++){
                if(menu.getItem(i).equals(item)){
                    selectedUnitIndex = i;
                    break;
                }
            }
            setSelectedUnit();
            //Toast.makeText(getApplicationContext(),""+selectedUnitIndex,Toast.LENGTH_SHORT).show();

            //TODO update unit, neue unit wurde ausgewählt
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
