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

public class UnitWordlistActivity extends AppCompatActivity {
    int height;
    LinearLayout layout;

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

        layout = (LinearLayout) findViewById(R.id.wordListLayout);
        fillWordList();

    }

    private void backButton(){
        finish();
    }

    public void fillWordList(){
        for(final Word w : WordlistActivity.selectedUnit.getWordArray()) {
            LayoutInflater inflater;
            inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout innerLayout = (LinearLayout) inflater.inflate(R.layout.unitlistelement, null);
            innerLayout.setMinimumHeight(height / 10);
            TextView textView = (TextView) innerLayout.getChildAt(0);
            textView.setText(w.getWordForeign());
            layout.addView(innerLayout);

            innerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });
        }
    }

}
