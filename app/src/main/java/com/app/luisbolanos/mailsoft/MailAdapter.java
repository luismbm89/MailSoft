package com.app.luisbolanos.mailsoft;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by luis.bolanos on 6/12/2017.
 */

public class MailAdapter  extends ArrayAdapter<Mail> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public MailAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    /**
     * Returns the view for a specific item on the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final Mail currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);
        final TextView tvDe = (TextView) row.findViewById(R.id.tvDe);
        final TextView tvAsunto = (TextView) row.findViewById(R.id.tvAsunto);
        final CheckBox Estado = (CheckBox) row.findViewById(R.id.checkBox);
        tvDe.setText(currentItem.getFrom());
        tvAsunto.setText(currentItem.getSubject());
        if(currentItem.getRead()==true){
        Estado.setChecked(true);}else{Estado.setChecked(false);}
        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String de=currentItem.getFrom();
                String asunto=currentItem.getSubject();
                String msg=de+" --> "+asunto;
                Toast.makeText(arg0.getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        return row;
    }

}