package com.example.sampleproject.organizator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.sampleproject.*;
import com.example.sampleproject.authentication.MainActivity;
import com.example.sampleproject.izvodac.LicIzv;
import com.example.sampleproject.common.PostavkeFragment;
import com.example.sampleproject.common.PropusnicaFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

//sve do sad napravljeno je premjesteno u OrganizatorHome

public class OrganizatorActivity extends AppCompatActivity {
    private final Fragment homeFragment = new OrganizatorHome();
    private final Fragment licFestFragment = new LicOrg(); //todo promijeniti gdje ide
    private final Fragment licEventFragment = new OrganizatorDogadaji(); //todo promijeniti u OrganizatorDogadaji nakon sto se povezu dogadaji s organizatorima
    private final Fragment calFragment = new LicIzv();
    private Fragment activeFragment = homeFragment;

    private final FragmentManager fm = getSupportFragmentManager();

    DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;
    private final Fragment propFragment = new PropusnicaFragment();
    private final Fragment postFragment = new PostavkeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_hamburger);

        BottomNavigationView footer = findViewById(R.id.menu_organizator);
        footer.setOnNavigationItemSelectedListener(navListener);

        fm.beginTransaction().add(R.id.organizator_fragment_container, homeFragment, "1").commit();
        fm.beginTransaction().add(R.id.organizator_fragment_container, licFestFragment, "2").hide(licFestFragment).commit();
        fm.beginTransaction().add(R.id.organizator_fragment_container, licEventFragment, "3").hide(licEventFragment).commit();
        fm.beginTransaction().add(R.id.organizator_fragment_container, calFragment, "4").hide(calFragment).commit();

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
                            Intent intent = new Intent(OrganizatorActivity.this, MainActivity.class);
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

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            fm.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                            activeFragment = homeFragment;
                            return true;
                        case R.id.nav_festivals:
                            fm.beginTransaction().hide(activeFragment).show(licFestFragment).commit();
                            activeFragment = licFestFragment;
                            return true;
                        case R.id.nav_events:
                            fm.beginTransaction().hide(activeFragment).show(licEventFragment).commit();
                            activeFragment = licEventFragment;
                            return true;
                        case R.id.nav_calendar:
                            fm.beginTransaction().hide(activeFragment).show(calFragment).commit();
                            activeFragment = calFragment;
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
                Intent intent = new Intent(OrganizatorActivity.this, MainActivity.class);
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
