package at.spengergasse.vocago;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

public class SettingsActivity extends AppCompatActivity{
    int height;
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

        LinearLayout layout = (LinearLayout)findViewById(R.id.settingslist);
        layout.setMinimumHeight(height/10);

        changeStatusBarColor();
    }

    private void backButton(){
        finish();
    }

    public void wordlistClick(View view){
        Intent nextScreen = new Intent(getApplicationContext(),WordlistActivity.class);
        startActivity(nextScreen);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void changeStatusBarColor(){
       getWindow().setStatusBarColor(0x5482a1);
    }
}
