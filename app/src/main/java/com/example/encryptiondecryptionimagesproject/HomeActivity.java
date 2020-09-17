package com.example.encryptiondecryptionimagesproject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeActivity extends BaseActivity {
   Unbinder unbinder;
    Bitmap uploadThumbnail = null;
    Boolean upload = false;

    int currentApiVersion = Build.VERSION.SDK_INT;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSIONS = 1234;
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private int choosenAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);

        unbinder = ButterKnife.bind(this);

        checkStoragePermission(HomeActivity.this);

    }

    @OnClick(R.id.take_picture_card)
    public void passCameraPicture(){

        choosenAction = 0;
        boolean result = checkStoragePermission(HomeActivity.this);
        if (result) photoCameraIntent();




      //  startActivity(new Intent(HomeActivity.this,EncryptionDecryptionActivity.class));

    }

    @OnClick(R.id.upload_picture_card)
    public void passUploadPicture(){
        choosenAction = 1;
        boolean result = checkStoragePermission(HomeActivity.this);
        if (result) photoGalleryIntent();

    }

    private void photoCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAMERA_REQUEST);
    }

    private void photoGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), IMAGE_GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                upload = true;
                if (data != null){

                       // Toast.makeText(getApplicationContext(),"yp",Toast.LENGTH_SHORT);
                        Intent iny = new Intent(getApplicationContext(),EncryptionDecryptionActivity.class);
                        iny.putExtra("uri", data.getData()+"");
                        iny.putExtra("type","gallery");
                        startActivity(iny);

                }

            } else if (requestCode == IMAGE_CAMERA_REQUEST) {
                upload = false;
                if (data != null){

                    Intent intent = new Intent(this,EncryptionDecryptionActivity.class);
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    intent.putExtra("data",thumbnail);
                    intent.putExtra("type","camera");
                    startActivity(intent);
                    Log.i("YOO", thumbnail+"");
//                    Toast.makeText(getApplicationContext(),data.getData()+"",Toast.LENGTH_SHORT);
                    //onCaptureImageResult(data);

                }

            }else{
                //decryptFiles(data);

            }
        }
    }


    //    private void onCaptureImageResult(Intent data) {
//
//        if (data != null) {
//            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
////                is  = new ByteArrayInputStream(bytes.toByteArray());
////                photoFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
////                FileOutputStream fo;
////                photoFile.createNewFile();
////                fo = new FileOutputStream(photoFile);
////                fo.write(bytes.toByteArray());
////                fo.close();
//
//        }
////        Glide.with(this).load(photoFile).into((ImageView) this.findViewById(R.id.image_stuff));
//
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (choosenAction == 0) {
                        photoCameraIntent();

                    }

                    else photoGalleryIntent();
                } else {
                    //dialog.dismiss();
                }
                break;
        }
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkStoragePermission(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission Required");
            alertBuilder.setMessage("Permision to Read/Write to External storage is required");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSIONS);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSIONS);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
