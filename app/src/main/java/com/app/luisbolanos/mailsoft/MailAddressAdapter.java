package com.app.luisbolanos.mailsoft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by luis.bolanos on 7/12/2017.
 */

public class MailAddressAdapter extends ArrayAdapter<EmailAddress> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public MailAddressAdapter(Context context, int layoutResourceId) {
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

        final EmailAddress currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);
        final TextView tvDe = (TextView) row.findViewById(R.id.tvDe);
        final TextView tvAsunto = (TextView) row.findViewById(R.id.tvAsunto);
        tvDe.setText(currentItem.getEmail());
        tvAsunto.setText(currentItem.getId());
        return row;
    }

}