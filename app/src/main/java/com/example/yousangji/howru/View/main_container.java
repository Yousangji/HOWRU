package com.example.yousangji.howru.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.yousangji.howru.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class main_container extends AppCompatActivity{
    private main_fr_home fr_mainhome;
    //private main_noti fr_mainnoti;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_fr_main);

        //fragment setting
        fr_mainhome=new main_fr_home();
        //fr_mainnoti=new main_noti();

        initfragment();

        BottomBar bottomBar = (BottomBar)
                findViewById(R.id.bottomBar);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
                switch (tabId) {
                    case R.id.menu_home:
                        transaction.replace(R.id.contentContainer,fr_mainhome);
                        break;
                    case R.id.menu_noti:
                        break;
                    case R.id.menu_shot:
                        Intent toshot =new Intent(getApplicationContext(),broadcaster.class);
                        toshot.putExtra("userid","y0212@naver.com");
                        startActivity(toshot);
                        break;
                    case R.id.menu_follow :
                        break;
                    case R.id.menu_setting :
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
