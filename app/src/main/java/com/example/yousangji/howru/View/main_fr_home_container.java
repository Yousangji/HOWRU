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
        adapter.addFragment(new main_fr_group(), "follower");
        adapter.addFragment(new main_fr_home(), "home");
        adapter.addFragment(new main_fr_home(), "놀방");
        adapter.addFragment(new main_fr_home(), "술방");
        adapter.addFragment(new main_fr_home(), "먹방");
        viewPager.setAdapter(adapter);
    }

}
