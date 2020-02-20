package com.Example.IRCTCWhereismyTrain.dialog;

import android.app.Dialog;
import android.content.Context;
import com.Example.IRCTCWhereismyTrain.R;

public class DataLoaderdialog extends Dialog {
    public DataLoaderdialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_view_load);
        setCancelable(false);
    }
}
