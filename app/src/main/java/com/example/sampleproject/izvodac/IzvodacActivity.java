package com.example.sampleproject.izvodac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.sampleproject.common.PostavkeFragment;
import com.example.sampleproject.common.PropusnicaFragment;
import com.example.sampleproject.R;
import com.example.sampleproject.authentication.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class IzvodacActivity extends AppCompatActivity {
    private final Fragment homeFragment = new IzvodacHome();
    private final Fragment specFragment = new IzvodacSpecijalizacije();
    private final Fragment licFragment = new IzvodacLicitacije();
    private final Fragment infoFragment = new IzvodacKalendar();
    private Fragment activeFragment = homeFragment;
    private final FragmentManager fm = getSupportFragmentManager();
    DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;
    private final Fragment propFragment = new PropusnicaFragment();
    private final Fragment postFragment = new PostavkeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izv_hamburger);

        BottomNavigationView footer = findViewById(R.id.footer);
        footer.setOnNavigationItemSelectedListener(navListener);

        fm.beginTransaction().add(R.id.fragment_container, homeFragment, "1").commit();
        fm.beginTransaction().add(R.id.fragment_container, specFragment, "2").hide(specFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, licFragment, "3").hide(licFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, infoFragment, "4").hide(infoFragment).commit();

        //Hamburger menu

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_propusnica,
                R.id.nav_postavke)
                .setDrawerLayout(drawer)
                .build();


        fm.beginTransaction().add(R.id.ham_containder, propFragment, "5").hide(propFragment).commit();
        fm.beginTransaction().add(R.id.ham_containder, postFragment, "6").hide(postFragment).commit();


        navigationView.setNavigationItemSelectedListener(navdrawListaner);


    }

    private NavigationView.OnNavigationItemSelectedListener navdrawListaner =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_propusnica:
                            fm.beginTransaction().hide(activeFragment).show(propFragment).commit();
                            activeFragment = propFragment;
                            drawer.closeDrawers();
                            return true;
                        case R.id.nav_postavke:
                            fm.beginTransaction().hide(activeFragment).show(postFragment).commit();
                            activeFragment = postFragment;
                            drawer.closeDrawers();
                            return true;
                        case R.id.sign_out:
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(IzvodacActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                    }
                    return false;
                }
            };

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    //todo napravit ispravan poredak
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            fm.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                            activeFragment = homeFragment;
                            return true;
                        case R.id.nav_job:
                            fm.beginTransaction().hide(activeFragment).show(licFragment).commit();
                            activeFragment = licFragment;
                            return true;
                        case R.id.nav_spec:
                            fm.beginTransaction().hide(activeFragment).show(specFragment).commit();
                            activeFragment = specFragment;
                            return true;
                        case R.id.nav_calendar:
                            fm.beginTransaction().hide(activeFragment).show(infoFragment).commit();
                            activeFragment = infoFragment;
                            return true;
                    }
                    return false;

                };
            };


    @Override
    public void onBackPressed() {
      /*  final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit SampleApp");
        builder.setMessage("Going back will end your session.");
        builder.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(IzvodacActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.show();
       */

    }

}
