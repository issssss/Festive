package com.example.sampleproject.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sampleproject.R;

public class OdabirActivity extends AppCompatActivity {
    Button vodButton;
    Button orgButton;
    Button izvButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odabira);
       // Toolbar toolbar = findViewById(R.id.toolbar);

       // setSupportActionBar(toolbar);

        vodButton=findViewById(R.id.button7);
        orgButton=findViewById(R.id.button8);
        izvButton=findViewById(R.id.button5);

        vodButton.setOnClickListener(new View.OnClickListener() {
            String acc = "voditelj";
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OdabirActivity.this, RegistracijaActivity.class);
                intent.putExtra("accountType", acc);
                startActivity(intent);
                finish();
            }
        });

        orgButton.setOnClickListener(new View.OnClickListener() {
            String acc = "organizator";
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OdabirActivity.this, RegistracijaActivity.class);
                intent.putExtra("accountType", acc);
                startActivity(intent);
                finish();
            }
        });

        izvButton.setOnClickListener(new View.OnClickListener() {
            String acc = "izvodac";
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OdabirActivity.this, RegistracijaActivity.class);
                intent.putExtra("accountType", acc);
                startActivity(intent);
                finish();
            }
        });



    }
}
