package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class TaskDescription extends AppCompatActivity
        implements SimpleAdapter.OnTaskClick,NavigationView.OnNavigationItemSelectedListener {

    //in this activity you can see and edit the details of a task

    TextView description;
    Button add;
    SimpleAdapter adapter;
    Task current;
    TaskViewModel viewModel;
    int originalDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_task_description);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.task_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        Intent intent=getIntent();
        int id=intent.getIntExtra("task",-1);
        //connecting the view model to the database
        viewModel= ViewModelProviders.of(this).get(TaskViewModel.class);
        try {
            current=viewModel.get(id);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //setting the properties of the task in the activity
        originalDay=current.getDayOfWeek();
        description=findViewById(R.id.description);
        if (current.getDescription().isEmpty()){
            current.setDescription(getResources().getString(R.string.enter));
        }
        description.setText(current.getDescription());
        View HeaderView=navigationView.getHeaderView(0);
        TextView header=HeaderView.findViewById(R.id.taskHeader);
        header.setText(current.getTask());
        setTitle(current.getTask());
        List subList=current.subTask;
        RecyclerView subTasks=findViewById(R.id.subTasks);
        adapter=new SimpleAdapter(this,subList,this);
        subTasks.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        subTasks.setLayoutManager(mLayoutManager);
        //setting a spinner to chose a new day for the task
        final Spinner day=findViewById(R.id.day);
        String[] days=new String[8];
        days[0]=getString(R.string.tasks);
        days[1]=getString(R.string.first);
        days[2]=getString(R.string.second);
        days[3]=getString(R.string.third);
        days[4]=getString(R.string.fourth);
        days[5]=getString(R.string.fifth);
        days[6]=getString(R.string.sixth);
        days[7]=getString(R.string.seventh);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                R.layout.spinner_item,days);
        day.setAdapter(spinnerArrayAdapter);
        day.setSelection(current.getDayOfWeek());
        day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    current.setDayOfWeek(position);
                    viewModel.update(current);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //editing the description
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(TaskDescription.this);
                alertDialog.setTitle(R.string.description);
                final EditText details = new EditText(TaskDescription.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                details.setLayoutParams(lp);
                details.setText(current.getDescription());
                alertDialog.setView(details);
                alertDialog.setIcon(R.mipmap.logo);

                alertDialog.setPositiveButton(R.string.add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                current.setDescription(details.getText().toString());
                                description.setText(details.getText().toString());
                                viewModel.update(current);
                            }
                        });
                alertDialog.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
        //setting a button to add a new sub task
        add=findViewById(R.id.addSub);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation clicked= AnimationUtils.loadAnimation(TaskDescription.this,R.anim.alpha);
                add.startAnimation(clicked);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(TaskDescription.this);
                alertDialog.setTitle(R.string.subadding);
                alertDialog.setMessage(R.string.subinsert);
                final EditText sub = new EditText(TaskDescription.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                sub.setLayoutParams(lp);
                alertDialog.setView(sub);
                alertDialog.setIcon(R.mipmap.logo);

                alertDialog.setPositiveButton(R.string.add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String task = sub.getText().toString();
                                if (!task.equals("")) {
                                    current.subTask.add(task);
                                    adapter.notifyDataSetChanged();
                                    viewModel.update(current);
                                }
                            }
                        });
                alertDialog.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
        //setting a button to edit the name of the task
        final Button rename=findViewById(R.id.rename);
        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation clicked= AnimationUtils.loadAnimation(TaskDescription.this,R.anim.alpha);
                rename.startAnimation(clicked);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(TaskDescription.this);
                alertDialog.setTitle(R.string.taskEdit);
                alertDialog.setMessage(R.string.taskEdit);
                final EditText sub = new EditText(TaskDescription.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                sub.setLayoutParams(lp);
                sub.setText(current.getTask());
                alertDialog.setView(sub);
                alertDialog.setIcon(R.mipmap.logo);

                alertDialog.setPositiveButton(R.string.edit,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String task = sub.getText().toString();
                                if (!task.equals("")) {
                                    current.setTask(task);
                                    setTitle(task);
                                    adapter.notifyDataSetChanged();
                                    viewModel.update(current);
                                }
                            }
                        });
                alertDialog.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
    ItemTouchHelper.Callback callback=new SimpleItemTouchHelper(adapter,viewModel,subTasks,current);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(subTasks);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        0);
            }
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(adapter.data, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(adapter.data, i, i - 1);
                    }
                }
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewModel.update(current);
            }
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }
        });
        itemTouchHelper.attachToRecyclerView(subTasks);
    }

    @Override
    //editing a subt task by clicking it
    public void OnClick(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TaskDescription.this);
        alertDialog.setTitle(R.string.subEditing);
        alertDialog.setMessage(R.string.subEdit);
        final EditText sub = new EditText(TaskDescription.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        sub.setLayoutParams(lp);
        sub.setText(current.subTask.get(position));
        alertDialog.setView(sub);
        alertDialog.setIcon(R.mipmap.logo);

        alertDialog.setPositiveButton(R.string.edit,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String task = sub.getText().toString();
                        if (!task.equals("")) {
                            current.subTask.remove(position);
                            current.subTask.add(position,task);
                            adapter.notifyDataSetChanged();
                            viewModel.update(current);
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent tasks = new Intent(getApplicationContext(), TaskList.class);
            tasks.putExtra("day",originalDay);
            startActivity(tasks);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_description, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
            heb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeLang("iw");
                    Toast.makeText(TaskDescription.this, "השפה שונתה לעברית", Toast.LENGTH_LONG).show();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            });
            eng.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeLang("en");
                    Toast.makeText(TaskDescription.this, "the language was changed to english", Toast.LENGTH_LONG).show();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            });        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.taskList) {
                Intent tasks = new Intent(getApplicationContext(), TaskList.class);
                tasks.putExtra("day",0);
                startActivity(tasks);
            } else if (id == R.id.sunday) {
                Intent day = new Intent(getApplicationContext(), TaskList.class);
                day.putExtra("day", 1);
                startActivity(day);
            } else if (id == R.id.monday) {
                Intent day = new Intent(getApplicationContext(), TaskList.class);
                day.putExtra("day", 2);
                startActivity(day);
            } else if (id == R.id.tuesday) {
                Intent day = new Intent(getApplicationContext(), TaskList.class);
                day.putExtra("day", 3);
                startActivity(day);
            } else if (id == R.id.wednesday) {
                Intent day = new Intent(getApplicationContext(), TaskList.class);
                day.putExtra("day", 4);
                startActivity(day);
            } else if (id == R.id.thursday) {
                Intent day = new Intent(getApplicationContext(), TaskList.class);
                day.putExtra("day", 5);
                startActivity(day);
            } else if (id == R.id.friday) {
                Intent day = new Intent(getApplicationContext(), TaskList.class);
                day.putExtra("day", 6);
                startActivity(day);
            } else if (id == R.id.saturday) {
                Intent day = new Intent(getApplicationContext(), TaskList.class);
                day.putExtra("day", 7);
                startActivity(day);
            }
            else if (id==R.id.contact){
                Intent contact = new Intent(Intent.ACTION_SENDTO);
                contact.putExtra(Intent.EXTRA_SUBJECT,R.string.mailBody);
                contact.putExtra(Intent.EXTRA_TEXT, "");
                contact.setData(Uri.parse("mailto:yshoham36@gmail.com"));
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
