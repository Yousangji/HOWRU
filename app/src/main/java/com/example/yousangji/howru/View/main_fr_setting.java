package com.example.yousangji.howru.View;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yousangji.howru.Controller.api_sign;
import com.example.yousangji.howru.Model.api_url;
import com.example.yousangji.howru.Model.obj_serverresponse;
import com.example.yousangji.howru.R;
import com.example.yousangji.howru.Util.util_sharedpref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by YouSangJi on 2017-11-02.
 */

public class main_fr_setting extends Fragment {
    final String TAG="[main_fr_setting]";
    EditText edit_nickname;
    EditText edit_message;
    ImageButton btn_prof_img;
    Button btn_sub_prof;
    ImageView img_profile;
    TextView txt_name;

    util_sharedpref prefutil;
    String username;
    String nickname;
    String message;
    String userid;
    String str_profileurl=null;
    Uri uri_profile;
    String str_photopath;
    Bitmap bm_profile;
    boolean imgflag=false;

    private static final int REQUEST_Select_photo = 0;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int CROP = 2;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.lay_fr_setting,container,false);

        edit_nickname=(EditText)rootview.findViewById(R.id.input_nickname);
        edit_message=(EditText)rootview.findViewById(R.id.input_message);
        btn_prof_img=(ImageButton) rootview.findViewById(R.id.btn_prof_img);
        btn_sub_prof=(Button)rootview.findViewById(R.id.btn_sub_prof);
        img_profile=(ImageView)rootview.findViewById(R.id.main_backdrop);
        txt_name=(TextView)rootview.findViewById(R.id.txt_setting_name);

        //sharedpreference
        //shared
        util_sharedpref.createInstance(getApplicationContext());
        prefutil=util_sharedpref.getInstance();
        userid=prefutil.getString("userid");
        nickname=prefutil.getString("nickname");
        message=prefutil.getString("usermsg");
        username=prefutil.getString("username");
        str_profileurl=prefutil.getString("profileurl");

        btn_prof_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //사진촬영으로 불러오기
                DialogInterface.OnClickListener CameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TakePhotoAction();
                    }
                };
                //갤러리에서 불러오기
                DialogInterface.OnClickListener GalleryListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectfromGallery();
                    }
                };
                //취소
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(getActivity())
                        .setNegativeButton("Gallery",GalleryListener )
                        .setNeutralButton("Cancel", cancelListener)
                        .setPositiveButton("Camera", CameraListener)
                        .show();
            }
        });
        btn_sub_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickname=edit_nickname.getText().toString();
                message=edit_message.getText().toString();


                if(imgflag){
                    retro_putwithimg(str_photopath,nickname,message,userid);
                }else {
                    retro_put(nickname, message, userid, str_profileurl);
                }
            }
        });
        return rootview;
    }

    //카메라로 사진찍기
    private void TakePhotoAction() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //지정한 경로에 파일생성
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //파일 생성시 에러
                ex.printStackTrace();
            }
            //파일 생성시만 진행
            if (photoFile != null) {
                uri_profile = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri_profile);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // 갤러리에서 사진불러오기
    public void SelectfromGallery() {
        Intent selectfromGalleryintent = new Intent(Intent.ACTION_PICK);
        selectfromGalleryintent.setType("image/*");
        startActivityForResult(selectfromGalleryintent, REQUEST_Select_photo);
    }

    //크롭하기
    private void cropImage(Uri photoURI) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 200);
        cropIntent.putExtra("outputY", 200);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, CROP);
    }

    //Save the Full_size Photo
    private File createImageFile() throws IOException {
        //create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        //save a file : path for use with Action_view intents
        str_photopath = image.getAbsolutePath();
        return image;
    }


    public void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String foler_name = "/"+folder+"/";
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            str_photopath=string_path+file_name;
        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }

    private void galleryAddpic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f=new File(str_photopath);
        Uri contentUri=Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_Select_photo:
                uri_profile = data.getData();

            case REQUEST_TAKE_PHOTO:
                cropImage(uri_profile);

                break;

            case CROP:
                if (resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();

                if (extras != null) {
                    bm_profile= extras.getParcelable("data");
                    //현재시간 구하기
                    SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault());
                    Date date = new Date();
                    String strDate = dateFormat.format(date);
                    saveBitmaptoJpeg(bm_profile,"bultalk",strDate);
                    galleryAddpic();

                    imgflag=true;
                }
                break;
        }
    }

    //http sign_up 통신
    public void retro_put(String un,String message,String userid,String profurl){
        Log.d("mytag",TAG+ un + message + userid);
        api_sign.getRetrofit(getApplicationContext()).put(un,message,userid,profurl).enqueue(new Callback<obj_serverresponse>() {
            @Override
            public void onResponse(Call<obj_serverresponse> call, Response<obj_serverresponse> response) {
                Log.d("mytag","[main_fr_setting] "+response.message());

                obj_serverresponse serverres=response.body();
                try {
                    JSONObject userobj = new JSONObject(serverres.getData());
                    prefutil = util_sharedpref.getInstance();
                    prefutil.putString("nickname", userobj.getString("nickname"));
                    prefutil.putString("profileurl", userobj.getString("profileurl"));
                    prefutil.putString("usermsg", userobj.getString("usermsg"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), serverres.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<obj_serverresponse> call, Throwable t) {
                Log.d("mytag","[main_fr_setting] put failure");
                Toast.makeText(getActivity(), "네트워크 오류, 다시 실행해주세요", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

    }

    public void retro_putwithimg(String pp,String un,String message,String userid){
        Log.d("mytag","[main_fr_setting]pp : "+pp);
        File file=new File(pp);
        Log.d("mytag","[main_fr_setting]pp : "+file.getName());
        MultipartBody.Part filepart=MultipartBody.Part.createFormData("file",file.getName(), RequestBody.create(MediaType.parse("image/*"),file));
        RequestBody req_body_name = RequestBody.create(MediaType.parse("text/plain"), un);
        RequestBody req_body_usermsg= RequestBody.create(MediaType.parse("text/plain"), message);
        RequestBody req_body_id = RequestBody.create(MediaType.parse("text/plain"), userid);
        api_sign.getRetrofit(getApplicationContext()).putwithimg(filepart,req_body_name,req_body_usermsg,req_body_id).enqueue(new Callback<obj_serverresponse>() {
            @Override
            public void onResponse(Call<obj_serverresponse> call, Response<obj_serverresponse> response) {
                Log.d("mytag","[main_fr_setting] "+response.message());

                obj_serverresponse serverres=response.body();
                try {
                    JSONObject userobj = new JSONObject(serverres.getData());
                    prefutil = util_sharedpref.getInstance();
                    prefutil.putString("nickname", userobj.getString("nickname"));
                    prefutil.putString("profileurl", userobj.getString("profileurl"));
                    prefutil.putString("usermsg", userobj.getString("usermsg"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), serverres.getMessage(), Toast.LENGTH_SHORT).show();            }

            @Override
            public void onFailure(Call<obj_serverresponse> call, Throwable t) {
            t.printStackTrace();
                Toast.makeText(getContext(), "네트워크 오류, 다시 실행해주세요", Toast.LENGTH_SHORT).show();
                Log.d("mytag","[main_fr_setting] putwithimg failure");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        edit_nickname.setText(nickname);
        edit_message.setText(message);
        txt_name.setText(username);
        if(imgflag) {
            img_profile.setImageBitmap(bm_profile);
        }else {
            Glide
                    .with(getApplicationContext())
                    .load(api_url.API_BASE_URL + "users/profile/" + str_profileurl)
                    .error(R.drawable.person)
                    .into(img_profile);
        }
    }
}
