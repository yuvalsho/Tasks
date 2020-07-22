package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setTitle(R.string.slogan);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.task_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                finish();
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,R.string.close, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.language) {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle(R.string.changeLang);
            @SuppressLint("InflateParams")
            final View changeLang=getLayoutInflater().inflate(R.layout.changelang,null);
            builder.setView(changeLang);
            AlertDialog dialog = builder.create();
            dialog.show();
            ImageView eng=changeLang.findViewById(R.id.eng);
            ImageView heb=changeLang.findViewById(R.id.heb);
            //choosing hebrew as the default language
            heb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeLang("iw");
                    Toast.makeText(MainActivity.this, "השפה שונתה לעברית", Toast.LENGTH_LONG).show();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            });
            //choosing english as the default language
            eng.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeLang("en");
                    Toast.makeText(MainActivity.this, "the language was changed to english", Toast.LENGTH_LONG).show();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.taskList) {
            Intent tasks=new Intent(getApplicationContext(),TaskList.class);
            tasks.putExtra("day",0);
            startActivity(tasks);
        } else if (id == R.id.sunday) {
            Intent day=new Intent(getApplicationContext(),TaskList.class);
            day.putExtra("day",1);
            startActivity(day);
        } else if (id == R.id.monday) {
            Intent day=new Intent(getApplicationContext(),TaskList.class);
            day.putExtra("day",2);
            startActivity(day);
        } else if (id == R.id.tuesday) {
            Intent day=new Intent(getApplicationContext(),TaskList.class);
            day.putExtra("day",3);
            startActivity(day);
        } else if (id == R.id.wednesday) {
            Intent day=new Intent(getApplicationContext(),TaskList.class);
            day.putExtra("day",4);
            startActivity(day);
        } else if (id == R.id.thursday) {
            Intent day=new Intent(getApplicationContext(),TaskList.class);
            day.putExtra("day",5);
            startActivity(day);
        } else if (id == R.id.friday) {
            Intent day=new Intent(getApplicationContext(),TaskList.class);
            day.putExtra("day",6);
            startActivity(day);
        }
        else if (id== R.id.saturday){
            Intent day=new Intent(getApplicationContext(),TaskList.class);
            day.putExtra("day",7);
            startActivity(day);
        }
        else if (id==R.id.contact){
            Intent contact = new Intent(Intent.ACTION_SENDTO);
            contact.putExtra(Intent.EXTRA_SUBJECT, R.string.mailBody);
            contact.putExtra(Intent.EXTRA_TEXT, "");
            contact.setData(Uri.parse("mailto:admin@company.com"));
            contact.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(contact);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }
    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
    }
    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.apply();
    }
}
