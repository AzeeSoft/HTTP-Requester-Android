package com.azeesoft.libs.httprequester.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.azeesoft.libs.httprequester.R;
import com.rey.material.app.Dialog;
import com.rey.material.widget.TextView;

public class CustomProgressDialog extends Dialog
    {

        public CustomProgressDialog(Context context) {
            super(context);

            View v= LayoutInflater.from(context).inflate(R.layout.progress_dialog,null);
            setContentView(v);
            setCancelable(false);
        }

        public void setMessage(String s)
        {
            TextView tv=(TextView)findViewById(R.id.text);
//            tv.setTypeface(QT.getRoboticFont(getContext(),QT.light));
            tv.setText(s);
        }
    }