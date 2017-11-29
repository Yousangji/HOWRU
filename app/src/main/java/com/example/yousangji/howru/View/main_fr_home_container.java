package com.example.yousangji.howru.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yousangji.howru.Controller.adt_frpager_main;
import com.example.yousangji.howru.R;

/**
 * Created by YouSangJi on 2017-11-02.
 */

public class main_fr_home_container extends Fragment{
    Fragment fr_1;
    Fragment fr_2;
    Fragment fr_3;
    Fragment fr_4;
    Fragment fr_5;
    Fragment fr_6;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.lay_tabcontainer,container, false);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);

        tabs.setupWithViewPager(viewPager);

        return view;
    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        adt_frpager_main adapter = new adt_frpager_main(getChildFragmentManager());


        fr_1=main_fr_home.newInstance("수다방");
        fr_2=main_fr_home.newInstance("먹방");
        fr_3=main_fr_home.newInstance("액팅방");
        fr_4=main_fr_home.newInstance("공방");
        fr_5=main_fr_home.newInstance("놀방");
        fr_6=main_fr_home.newInstance("기타");


        adapter.addFragment(fr_1, "수다방");
        adapter.addFragment(fr_2, "먹방");
        adapter.addFragment(fr_3, "액팅방");
        adapter.addFragment(fr_4, "공방");
        adapter.addFragment(fr_5, "놀방");
        adapter.addFragment(fr_6, "기타");
        viewPager.setAdapter(adapter);
    }

}
