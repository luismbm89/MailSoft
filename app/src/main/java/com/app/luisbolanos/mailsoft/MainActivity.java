package com.app.luisbolanos.mailsoft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;
import static java.util.Collections.addAll;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MobileServiceClient mClient;
    private MobileServiceTable<Mail> mToDoTable;
    private ProgressBar mProgressBar;
    private MailAdapter mAdapter;
    private String email,de,from,para,mensaje;
    private int indice;
    private  Button btn;
    private  ListView listViewToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=findViewById(R.id.button3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewMessage.class);
                startActivity(intent);

            }
        });
btn.setVisibility(View.INVISIBLE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);
        SharedPreferences prefs =
                getSharedPreferences("Configuracion",Context.MODE_PRIVATE);
        email = prefs.getString("email", "");
        if(email.equals("")){
            Intent intent = new Intent(getApplicationContext(), Configuracion.class);
            startActivity(intent);
            Context context = getApplicationContext();
            CharSequence text = "Debes registrar tu nombre y un correo electrónico";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else{
        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://isw-1313.azurewebsites.net",
                    this).withFilter(new ProgressFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });
            mToDoTable = mClient.getTable(Mail.class);
            mAdapter = new MailAdapter(this, R.layout.listview);
            listViewToDo= (ListView) findViewById(R.id.listViewToDo);
            listViewToDo.setAdapter(mAdapter);
            refreshItemsFromTable();
            listViewToDo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                /*    Mail omail =(Mail)listViewToDo.getItemAtPosition(i);
                    String  from=omail.getFrom();
                    checkItem(omail);
                    String para=omail.getTo();
                    String asunto=omail.getSubject();
                    String de=omail.getFrom();
                    String mensaje=omail.getMessage();
                    String msg=de+" --> "+asunto;
                    // Toast.makeText(arg0.getContext(), msg, Toast.LENGTH_LONG).show();
                    // TODO Auto-generated method stub
                    Intent intent=new Intent(view.getContext(),Message.class);
                    intent.putExtra("de", de);
                    intent.putExtra("para", para);
                    intent.putExtra("asunto", asunto);
                    intent.putExtra("mensaje", mensaje);
                    view.getContext().startActivity(intent);*/
                    indice=i;
                    btn.setVisibility(View.VISIBLE);
                }
            });

    }catch(Exception ex){}
        }
    }
    public void Responder(View v){
        Mail omail =(Mail)listViewToDo.getItemAtPosition(indice);
        String  from=omail.getFrom();
        checkItem(omail);
        String para=omail.getTo();
        String asunto=omail.getSubject();
        String de=omail.getFrom();
        String mensaje=omail.getMessage();
        String msg=de+" --> "+asunto;
        Intent intent=new Intent(this,Message.class);
        intent.putExtra("de", de);
        intent.putExtra("para", para);
        intent.putExtra("asunto", asunto);
        intent.putExtra("mensaje", mensaje);
        this.startActivity(intent);
    }
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<Mail> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (Mail item : results) {
                               // if(!item.getRead()){
                                mAdapter.add(item);}
                         //   }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "refreshItemsFromTable");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }
    /**
     * Refresh the list with the items in the Mobile Service Table
     */
    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }
    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }
    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
    private List<Mail> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        /* final List<Mail> v=mToDoTable.where().field("read").
                eq(val(true)).execute().get();*/
      final  List<Mail> f=mToDoTable.where().field("read").
               eq(val(false)).execute().get();
        List<Mail> t = new ArrayList<Mail>() { { addAll(f); /*addAll(v);*/ } };
        return t;
    }
    private class ProgressFilter implements ServiceFilter {

            @Override
            public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

                final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    }
                });

                ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

                Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                    @Override
                    public void onFailure(Throwable e) {
                        resultFuture.setException(e);
                    }

                    @Override
                    public void onSuccess(ServiceFilterResponse response) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                            }
                        });

                        resultFuture.set(response);
                    }
                });

                return resultFuture;
            }
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
    public void checkItem(final Mail item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        if (item.getRead()){
            item.setRead(false);}
        else{
            item.setRead(true);
        }

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    checkItemInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (item.getRead()) {
                                mAdapter.remove(item);
                            }
                            refreshItemsFromTable();
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);

    }

    public void checkItemInTable(Mail item) throws ExecutionException, InterruptedException {
        mToDoTable.update(item).get();
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
            Intent intent = new Intent(getApplicationContext(), Configuracion.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        /*int id = item.getItemId();

        if (id == R.id.SPAM) {
            // Handle the camera action
        } else if (id == R.id.BE) {
            Intent intent = new Intent(getApplicationContext(), Inbox.class);
            startActivity(intent);

        } else if (id == R.id.BS) {
            Intent intent = new Intent(getApplicationContext(), Outbox.class);
            startActivity(intent);

        } else if (id == R.id.CS) {

        }else if (id == R.id.IS) {


        } else if (id == R.id.NM) {
            Intent intent = new Intent(getApplicationContext(), NewMessage.class);
            startActivity(intent);

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
