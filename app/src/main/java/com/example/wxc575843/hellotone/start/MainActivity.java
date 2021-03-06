package com.example.wxc575843.hellotone.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.wxc575843.hellotone.Community.Community;
import com.example.wxc575843.hellotone.Community.CommunityTest;
import com.example.wxc575843.hellotone.Culture.Culture;
import com.example.wxc575843.hellotone.Practice.Practice;
import com.example.wxc575843.hellotone.R;
import com.example.wxc575843.hellotone.Settings.SettingActivity;
import com.example.wxc575843.hellotone.utils.SharePreferenceUtils;

import java.io.File;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Practice practice = new Practice();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_layout,practice).commit();

        CreateFiles();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_practice) {
            // Handle the camera action
            Practice practice = new Practice();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, practice).commit();
        } else if (id == R.id.nav_culture) {
            Culture culture = new Culture();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,culture).commit();

        } else if (id == R.id.nav_community) {
//            Intent intent = new Intent(MainActivity.this, CommunityTest.class);
//            startActivity(intent);
            Community community = new Community();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,community).commit();

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            SharePreferenceUtils.putBoolean(MainActivity.this,"loginState",false);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_send) {
            Toast.makeText(MainActivity.this,"wait to develop", Toast.LENGTH_SHORT).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void CreateFiles() {
        String user = SharePreferenceUtils.getString(MainActivity.this, "email", null);
        String path = SharePreferenceUtils.getString(MainActivity.this, "AppFilePath",null);
        String voiceDir = path+"/"+user;
        File destDir = new File(voiceDir);
        if (!destDir.exists()){
            destDir.mkdirs();

            Log.d("userFile",voiceDir);
        }
        SharePreferenceUtils.putString(MainActivity.this, "VoiceFilePath", voiceDir);
    }
}
