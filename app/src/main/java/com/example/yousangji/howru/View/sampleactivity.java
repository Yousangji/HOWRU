package com.example.yousangji.howru.View;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yousangji.howru.R;

/**
 * Created by YouSangJi on 2017-11-29.
 */

public class sampleactivity extends Fragment {
    samplepager verticalpager;
    MyAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.samplepager,container,false);
        mAdapter=new MyAdapter(getChildFragmentManager(),getContext());
        verticalpager=(samplepager)view.findViewById(R.id.sampleviewpager);
        verticalpager.setAdapter(mAdapter);

        return view;
    }

    public static class MyAdapter extends FragmentStatePagerAdapter {
        Context context;
        public MyAdapter(FragmentManager fragmentManager, Context c) {
            super(fragmentManager);
            context=c;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show image
                    return samplefragment.newInstance(context);
                case 1: // Fragment # 1 - This will show image
                    return samplefragment.newInstance(context);
                default:// Fragment # 2-9 - Will show list
                    return samplefragment.newInstance(context);
            }
        }




    }



}
