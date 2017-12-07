package com.app.luisbolanos.mailsoft;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                txtmsgDe= extras.getString("de");
                txtmsgPara= extras.getString("para");
                txtmsgAsunto= extras.getString("asunto");
                txtmsgMensaje= extras.getString("mensaje");
            }
        } else {
            txtmsgDe= (String) savedInstanceState.getSerializable("de");
            txtmsgPara= (String) savedInstanceState.getSerializable("para");
            txtmsgAsunto= (String) savedInstanceState.getSerializable("asunto");
            txtmsgMensaje= (String) savedInstanceState.getSerializable("mensaje");
        }
        TextView txtDe=(TextView)findViewById(R.id.txtmsgDe);
        TextView txtPara=(TextView)findViewById(R.id.txtMsgPara);
        TextView txtAsunto=(TextView)findViewById(R.id.txtmsgAsunto);
        TextView txtMensaje=(TextView)findViewById(R.id.txtMsgBody);
        txtDe.setText(txtmsgDe);
        txtPara.setText(txtmsgPara);
        txtAsunto.setText("RE:"+  txtmsgAsunto);
        txtMensaje.setText(txtmsgMensaje);
        TextView txtRespuesta=(TextView)findViewById(R.id.editText);
        txtRespuesta.requestFocus();
    }
}
