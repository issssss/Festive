package com.example.sampleproject.voditelj;

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

//sve do sad napravljeno je premjesteno u VoditeljHome

public class VoditeljActivity extends AppCompatActivity {
    private final Fragment homeFragment = new VoditeljHome();
    private final Fragment licOrgFragment = new VoditeljLicOrg();
    private final Fragment licIzvFragment = new VoditeljLicIzv();
    private final Fragment infoFragment = new VoditeljInfo();
    private Fragment activeFragment = homeFragment;
    private final FragmentManager fm = getSupportFragmentManager();
    private AppBarConfiguration mAppBarConfiguration;
    private final Fragment propFragment = new PropusnicaFragment();
    private final Fragment postFragment = new PostavkeFragment();


    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_hamburger);

        //getSupportActionBar().setTitle("Moj profil");

        //pocetak bottom navbar navigacije
        BottomNavigationView footer = findViewById(R.id.meni_voditelj);
        footer.setOnNavigationItemSelectedListener(navListener);


        //getSupportFragmentManager().beginTransaction().replace(R.id.voditelj_fragment_container, new VoditeljHome()).commit();

        fm.beginTransaction().add(R.id.voditelj_fragment_container, homeFragment, "1").commit();
        fm.beginTransaction().add(R.id.voditelj_fragment_container, licOrgFragment, "2").hide(licOrgFragment).commit();
        fm.beginTransaction().add(R.id.voditelj_fragment_container, licIzvFragment, "3").hide(licIzvFragment).commit();
        fm.beginTransaction().add(R.id.voditelj_fragment_container, infoFragment, "4").hide(infoFragment).commit();

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
                            Intent intent = new Intent(VoditeljActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                    }
                    return false;
                }
            };



    //metoda za odabir gumba u footer-u
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //Fragment selectedFragment = null;

                    // TO DO: promijenji id gumba i java.class na kojeg vodi njegov pritisak
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            fm.beginTransaction().hide(activeFragment).show(homeFragment).commit();

                            activeFragment = homeFragment;
                            return true;
                        case R.id.nav_button2:
                            fm.beginTransaction().hide(activeFragment).show(licOrgFragment).commit();
                            activeFragment = licOrgFragment;
                            return true;
                        case R.id.nav_button3:
                            fm.beginTransaction().hide(activeFragment).show(licIzvFragment).commit();
                            activeFragment = licIzvFragment;
                            return true;
                        case R.id.nav_button4:
                            fm.beginTransaction().hide(activeFragment).show(infoFragment).commit();
                            activeFragment = infoFragment;
                            return true;
                    }
                    return false;

                }
            };

    @Override
    public void onBackPressed() {
       /* final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit Festive");
        builder.setMessage("Going back will end your session.");
        builder.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(VoditeljActivity.this, MainActivity.class);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        homeFragment.onActivityResult(requestCode, resultCode, data);
    }


}


