package com.example.yousangji.howru.View;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.yousangji.howru.R;

/**
 * Created by YouSangJi on 2017-11-29.
 */

public class samplepager extends AppCompatActivity {

ViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.samplepager);

        viewPager=(ViewPager)findViewById(R.id.sampleviewpager);

    }
}
