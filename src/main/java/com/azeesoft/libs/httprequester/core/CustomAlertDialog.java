package com.azeesoft.libs.httprequester.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.azeesoft.libs.httprequester.R;
import com.rey.material.app.Dialog;

/**
 * Created by azizt on 8/22/2015.
 */
public class CustomAlertDialog extends Dialog {
    TextView info;
    public CustomAlertDialog(Context context, String msg) {
        super(context);
        View conview= LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout,null);
        View buttons=conview.findViewById(R.id.buttons);
        buttons.setVisibility(View.GONE);

        setContentView(conview);
        info=(TextView)findViewById(R.id.info);
        info.setTypeface(QT.getRoboticFont(context));
        info.setText(msg);
    }

    public void closeOnPositive()
    {
        positiveActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void closeOnNegative()
    {
        negativeActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
