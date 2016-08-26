package com.tripping.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddTripActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mSearchRoutes;
    private Button mClearForm;

    private TextView mStartingPnt;
    private TextView mDesitnationPnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mClearForm = (Button) findViewById(R.id.btn_clear);
        mSearchRoutes = (Button) findViewById(R.id.btn_srch_route);

        mStartingPnt = (EditText) findViewById(R.id.starting_pnt_txt);
        mDesitnationPnt = (EditText) findViewById(R.id.destination_pnt_txt);

        mClearForm.setOnClickListener(this);
        mSearchRoutes.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_srch_route:
                startActivity(new Intent(this,TripRouteSelectActivity.class));
                break;
            case R.id.btn_clear:
                mDesitnationPnt.setText("");
                mStartingPnt.setText("");
                break;
        }
    }
}
