package com.example.yousangji.howru.Controller;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by YouSangJi on 2017-11-28.
 */

public class adt_spinner extends ArrayAdapter<String> {

    public adt_spinner(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

    }

    @Override
    public int getCount() {


        int count = super.getCount();

        return count>0 ? count-1 : count ;

    }
}
