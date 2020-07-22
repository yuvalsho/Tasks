package com.example.myapplication;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class TaskList extends AppCompatActivity implements MyAdapter.OnTaskClick, NavigationView.OnNavigationItemSelectedListener
    {
        //in this activity we see the tasks of a chosen day
        int currDay;
        EditText newTask;
        RecyclerView taskList;
        MyAdapter adapter;
        Button add;
        ConstraintLayout taskLayout;
        TaskViewModel viewModel;

        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
            loadLocale();
            setContentView(R.layout.activity_task_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.task_view);
        navigationView.setNavigationItemSelectedListener(this);
        //getting the day from the intent and the tasks from the database
        Intent in=getIntent();
        currDay=in.getIntExtra("day",-1);
        viewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        viewModel.getDaily(currDay).observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable final List<Task> tasks) {
                adapter.setTasks(tasks);
            }
        });
        //setting the tasks int the activity
        taskLayout = findViewById(R.id.taskLayout);
        taskList = findViewById(R.id.taskList);
        adapter = new MyAdapter(getBaseContext(),this);
        taskList.setAdapter(adapter);
        taskList.setLayoutManager(new LinearLayoutManager(this));
        SetDay(currDay);
        //setting a button to add a new task
        add = findViewById(R.id.adding);
            add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation clicked= AnimationUtils.loadAnimation(TaskList.this,R.anim.alpha);
                add.startAnimation(clicked);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(TaskList.this);
                alertDialog.setTitle(R.string.adding);
                alertDialog.setMessage(R.string.insert);
                newTask = new EditText(TaskList.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                newTask.setLayoutParams(lp);
                alertDialog.setView(newTask);
                alertDialog.setIcon(R.mipmap.logo);

                alertDialog.setPositiveButton(R.string.add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String task = newTask.getText().toString();
                                if (!task.equals("")) {
                                    Task task1 = null;
                                    try {
                                        task1 = new Task(task,currDay,viewModel.getMax());
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    adapter.mData.add(task1);
                                    adapter.notifyDataSetChanged();
                                    viewModel.insert(task1);
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);
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
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
                public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                            0);
                }
                //a feature to change the order of the tasks by dragging up and down and updating the order in the database
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    int fromPosition = viewHolder.getAdapterPosition();
                    int toPosition = target.getAdapterPosition();

                    if (fromPosition < toPosition) {
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(adapter.mData, i, i + 1);

                            int order1 = adapter.mData.get(i).getOrder();
                            int order2 = adapter.mData.get(i + 1).getOrder();
                            adapter.mData.get(i).setOrder(order2);
                            adapter.mData.get(i + 1).setOrder(order1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(adapter.mData, i, i - 1);

                            int order1 = adapter.mData.get(i).getOrder();
                            int order2 = adapter.mData.get(i - 1).getOrder();
                            adapter.mData.get(i).setOrder(order2);
                            adapter.mData.get(i - 1).setOrder(order1);
                        }
                    }
                    adapter.notifyItemMoved(fromPosition, toPosition);
                    return true;
                }
                public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    super.clearView(recyclerView, viewHolder);
                    viewModel.updateAll(adapter.mData);
                }
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                }
            });
        ItemTouchHelper.Callback callback =
                new ItemTouchHelperCallback(adapter, viewModel, taskList);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(taskList);
        itemTouchHelper.attachToRecyclerView(taskList);
        }

        @Override
        public void onBackPressed () {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            Intent tasks = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(tasks);
        }
    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.task_list, menu);
        return true;
    }
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
                        Toast.makeText(TaskList.this, "השפה שונתה לעברית", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(TaskList.this, "the language was changed to english", Toast.LENGTH_LONG).show();
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
        public boolean onNavigationItemSelected (MenuItem item){
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
            contact.putExtra(Intent.EXTRA_SUBJECT, R.string.mailBody);
            contact.putExtra(Intent.EXTRA_TEXT, "");
            contact.setData(Uri.parse("mailto:yshoham36@gmail.com"));
            contact.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(contact);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

        @Override
        public void OnClick(final int position) {
            Intent task = new Intent(getApplicationContext(), TaskDescription.class);
            task.putExtra("task",adapter.mData.get(position).id);
            startActivity(task);
        }
        public void SetDay(int i) {
            NavigationView navigationView = findViewById(R.id.task_view);
            View hView = navigationView.getHeaderView(0);
            TextView day = hView.findViewById(R.id.header);
            switch (i) {
                case 0: {
                    day.setText(R.string.tasks);
                    setTitle(R.string.tasks);
                    break;
                }
                case 1: {
                    day.setText(R.string.first);
                    setTitle(R.string.first);
                    break;
                }
                case 2: {
                    day.setText(R.string.second);
                    setTitle(R.string.second);
                    break;
                }
                case 3: {
                    day.setText(R.string.third);
                    setTitle(R.string.third);
                    break;
                }
                case 4: {
                    day.setText(R.string.fourth);
                    setTitle(R.string.fourth);
                    break;
                }
                case 5: {
                    day.setText(R.string.fifth);
                    setTitle(R.string.fifth);
                    break;
                }
                case 6: {
                    day.setText(R.string.sixth);
                    setTitle(R.string.sixth);
                    break;
                }
                case 7: {
                    day.setText(R.string.seventh);
                    setTitle(R.string.seventh);
                    break;
                }
            }
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