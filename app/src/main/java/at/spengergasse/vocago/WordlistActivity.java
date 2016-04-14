package at.spengergasse.vocago;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WordlistActivity extends AppCompatActivity {
    int height;
    LinearLayout layout;
    public static Unit selectedUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordlist);
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

        layout = (LinearLayout) findViewById(R.id.unitListLayout);
        fillUnitList();
    }

    private void backButton(){
        finish();
    }

    private void fillUnitList(){
        for(final Unit u : MainActivity.unitArray) {
            LayoutInflater inflater;
            inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout innerLayout = (LinearLayout) inflater.inflate(R.layout.unitlistelement, null);
            innerLayout.setMinimumHeight(height / 10);
            TextView textView = (TextView) innerLayout.getChildAt(0);
            textView.setText(u.getName());
            layout.addView(innerLayout);

            innerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedUnit = u;
                    Intent nextScreen = new Intent(getApplicationContext(),UnitWordlistActivity.class);
                    startActivity(nextScreen);
                }
            });
        }
    }

}
