package com.example.yousangji.howru.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.yousangji.howru.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class main_container extends AppCompatActivity{
    private main_fr_home fr_mainhome;
    private main_fr_group fr_maingroup;
    private main_fr_noti fr_mainnoti;
    private main_fr_follow fr_follow;
    private main_fr_setting fr_setting;
    private main_fr_home_container fr_home_container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_fr_main);

        //fragment setting
        fr_mainhome=new main_fr_home();
        fr_mainnoti=new main_fr_noti();
        fr_maingroup=new main_fr_group();
        fr_follow=new main_fr_follow();
        fr_home_container=new main_fr_home_container();
        fr_setting=new main_fr_setting();
        initfragment();

        BottomBar bottomBar = (BottomBar)
                findViewById(R.id.bottomBar);
        Toolbar toolbar=(Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
                switch (tabId) {
                    case R.id.menu_home:
                        transaction.replace(R.id.contentContainer,fr_home_container)
                        .commit();
                        break;
                    case R.id.menu_noti:
                        transaction.replace(R.id.contentContainer,fr_mainnoti).commit();
                        break;
                    case R.id.menu_shot:
                        Intent toshot =new Intent(getApplicationContext(),broadcaster.class);
                        toshot.putExtra("userid","y0212@naver.com");
                        startActivity(toshot);
                        break;
                    case R.id.menu_follow :
                        transaction.replace(R.id.contentContainer,fr_follow).commit();
                        break;
                    case R.id.menu_setting :
                        transaction.replace(R.id.contentContainer,fr_setting).commit();
                        break;
                }
            }
        });

    }

    public void initfragment(){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentContainer,fr_mainhome);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
