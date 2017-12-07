package com.app.luisbolanos.mailsoft;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class Message extends AppCompatActivity {

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
}
