package com.example.yousangji.howru.Controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yousangji.howru.Model.api_url;
import com.example.yousangji.howru.Model.obj_serverresponse;
import com.example.yousangji.howru.Model.obj_user;
import com.example.yousangji.howru.R;
import com.example.yousangji.howru.Util.util_sharedpref;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by YouSangJi on 2017-10-31.
 */

public class adt_recy_follow extends RecyclerView.Adapter<adt_recy_follow.ViewHolder> {
        List<obj_user> list_users;
        Context mcontext;
        String userid;
        String followee;
        String nickname;
        util_sharedpref prefutil;

    public adt_recy_follow(Context c,String uid,String followe){
        this.mcontext=c;
        list_users= new ArrayList<obj_user>();
        util_sharedpref.createInstance(getApplicationContext());
        prefutil=util_sharedpref.getInstance();
        this.userid=prefutil.getString("userid");
        this.followee=prefutil.getString("followee");
        this.nickname=prefutil.getString("nickname");
        Log.d("mytag","[adt_follow] followee"+followe);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        ViewHolder viewHolder=null;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.itm_listfriend,parent,false);
        viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Glide
                .with(mcontext)
                .load(api_url.API_BASE_URL+"users/profile/"+list_users.get(position).getProfileurl()).
        bitmapTransform(new CropCircleTransformation(mcontext)).into(holder.img_follow_prof);


            holder.txtv_follow_name.setText(list_users.get(position).getUsername());
            holder.txtv_follow_nick.setText(list_users.get(position).getEmailaddr());
            holder.btn_follow_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("mytag", "[adt_recy_follow]" + userid + list_users.get(position).getUserid());
                    api_follow.getRetrofit(mcontext).post(userid, list_users.get(position).getUserid(), nickname + "님이 " + list_users.get(position).getNickname() + "님을 팔로우했습니다.").enqueue(new Callback<obj_serverresponse>() {
                        @Override
                        public void onResponse(Call<obj_serverresponse> call, Response<obj_serverresponse> response) {

                            obj_serverresponse serverresobj = response.body();
                            if (serverresobj.getStatus().equals("OK")) {
                                Toast.makeText(mcontext, serverresobj.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<obj_serverresponse> call, Throwable t) {
                            Log.d("mytag", "[adt_recy_follow]post follow error");
                        }
                    });


                }
            });

    }

    @Override
    public int getItemCount() {
        return this.list_users.size();
    }

    public void putlist(List<obj_user> listuser){
        list_users=listuser;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img_follow_prof;
        private TextView txtv_follow_name;
        private TextView txtv_follow_nick;
        private Button btn_follow_add;

        public ViewHolder (View view){
            super(view);
            img_follow_prof=(ImageView)view.findViewById(R.id.inviteitm_img);
            txtv_follow_name=(TextView)view.findViewById(R.id.inviteitm_name);
            txtv_follow_nick=(TextView)view.findViewById(R.id.invite_msg);
            btn_follow_add=(Button)view.findViewById(R.id.btn_follow_add);

        }
    }
}
