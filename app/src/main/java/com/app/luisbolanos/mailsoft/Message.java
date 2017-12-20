package com.app.luisbolanos.mailsoft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Message extends AppCompatActivity {

    private ArrayAdapter<Mail> mAdapter1;
    private MobileServiceTable<Mail> mToDoTable1;
    private MobileServiceClient mClient;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        String txtmsgPara;
        String txtmsgDe;
        String txtmsgAsunto;
        String txtmsgMensaje;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                txtmsgDe= null;
                txtmsgPara= null;
                txtmsgAsunto= null;
                txtmsgMensaje= null;
            } else {
                txtmsgPara= extras.getString("de");
                txtmsgAsunto= extras.getString("asunto");
                txtmsgMensaje= extras.getString("mensaje");
            }
        } else {
            txtmsgPara= (String) savedInstanceState.getSerializable("de");
            txtmsgAsunto= (String) savedInstanceState.getSerializable("asunto");
            txtmsgMensaje= (String) savedInstanceState.getSerializable("mensaje");
        }
        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://isw-1313.azurewebsites.net",
                    this).withFilter(new Message.ProgressFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });}catch(Exception ex){}
        SharedPreferences prefs =
                getSharedPreferences("Configuracion", Context.MODE_PRIVATE);
        String email = prefs.getString("email", "");
        EditText txtemail=(EditText)findViewById(R.id.txtmsgDe);
        txtemail.setText(email);
        txtemail.setEnabled(false);
        TextView txtPara=(TextView)findViewById(R.id.txtMsgPara);
        TextView txtAsunto=(TextView)findViewById(R.id.txtmsgAsunto);
        TextView txtMensaje=(TextView)findViewById(R.id.txtMsgBody);
        txtPara.setText(txtmsgPara);
        txtAsunto.setText("RE:"+  txtmsgAsunto);
        txtMensaje.setText(txtmsgMensaje);
        TextView txtRespuesta=(TextView)findViewById(R.id.editText);
        txtRespuesta.requestFocus();
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
    public void Send(View view) {
        if (mClient == null) {
            return;
        }
        mToDoTable1 = mClient.getTable(Mail.class);
        mAdapter1= new MailAdapter(this, R.layout.listview);
        // Create a new item
        final Mail item = new Mail();
        EditText from =(EditText)findViewById(R.id.txtmsgDe);
        EditText to =(EditText)findViewById(R.id.txtMsgPara);
        EditText subject =(EditText)findViewById(R.id.txtmsgAsunto);
        EditText msg =(EditText)findViewById(R.id.txtMsgBody);
        EditText msg1 =(EditText)findViewById(R.id.editText);
        item.setMessage(msg1.getText().toString()+" / "+msg.getText().toString());
        item.setSubject(subject.getText().toString());
        item.setTo(to.getText().toString());
        item.setFrom(from.getText().toString());

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Mail entity = SendMail(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //if(!entity.isComplete()){
                            mAdapter1.add(entity);
                            // }
                        }
                    });
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);
    }
    public Mail SendMail(Mail item) throws ExecutionException, InterruptedException {
        Mail entity = mToDoTable1.insert(item).get();
        return entity;
    }
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
}
