package com.example.yousangji.howru.Model;

import com.seu.magicfilter.utils.MagicFilterType;

/**
 * Created by YouSangJi on 2017-11-13.
 */

public class obj_filter_thumb {
    int imgurl;
    String title_filter;
    MagicFilterType magicFilterType;

    public obj_filter_thumb(int i,String filter_name,MagicFilterType type){
        this.imgurl=i;
        this.title_filter=filter_name;
        this.magicFilterType=type;

    }
    public void setImgurl(int imgurl) {
        this.imgurl = imgurl;
    }

    public void setTitle_filter(String title_filter) {
        this.title_filter = title_filter;
    }

    public void setMagicFilterType(MagicFilterType magicFilterType) {
        this.magicFilterType = magicFilterType;
    }

    public int getImgurl() {
        return imgurl;
    }

    public String getTitle_filter() {
        return title_filter;
    }

    public MagicFilterType getMagicFilterType() {
        return magicFilterType;
    }
}
