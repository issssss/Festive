package com.example.sampleproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class LicitacijaPrijavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licitacija_prijava);

        getSupportActionBar().setTitle("Moj Profil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIncomingContent();
    }

    private void getIncomingContent(){
        if(getIntent().hasExtra("Example Item")){

            Intent intent = getIntent();
            LicItem exampleItem = intent.getParcelableExtra("Example Item");

            int imageRes = exampleItem.getImageResource();
            String line1 = exampleItem.getText1();
            String line2 = exampleItem.getText2();

            setContent(imageRes, line1, line2);

        }

    }

    private void setContent(int imageRes, String line1, String line2){
        ImageView imageView = findViewById(R.id.image_activity2);
        imageView.setImageResource(imageRes);

        TextView textView1 = findViewById(R.id.text1_activity2);
        textView1.setText(line1);

        TextView textView2 = findViewById(R.id.text2_activity2);
        textView2.setText(line2);
    }
}
