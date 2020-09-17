package com.example.encryptiondecryptionimagesproject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.encryptiondecryptionimagesproject.AESENCRYPTION.AesEncryption;
import com.example.encryptiondecryptionimagesproject.Interface.Listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends BaseActivity {
    Unbinder unbinder;
    InputStream is;

    private static String FILE_NAME_ENC;
    private static String FILE_NAME_DEC = "decrypt"+ System.currentTimeMillis()+".jpg";


    int currentApiVersion = Build.VERSION.SDK_INT;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSIONS = 1234;
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private int choosenAction;
    boolean pictureChanged = false;

    File photoFile;
    File myDir;


    @BindView(R.id.image_stuff) ImageView imageStuff;
    @BindView(R.id.encrypt_button) Button encrypt_button;
    @BindView(R.id.decrypt_button) Button decrpyt_button;


    //hardcoded
    String key = "DjHLRPPvlNUmNBfo";
    String key_spec = "iuNSutndUyZNlFAk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);

        myDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/saved_images");
        myDir.mkdir();


        checkStoragePermission(MainActivity.this);

    }

    @OnClick(R.id.camera)
    public void takeShot(){
        photoFile = null;
        choosenAction = 0;
        boolean result = checkStoragePermission(MainActivity.this);
        if (result) photoCameraIntent();

    }


    @OnClick(R.id.gallery)
    public void getImage(){
        Log.i("YOO","kk");
        photoFile = null;
        choosenAction = 1;
        boolean result = checkStoragePermission(MainActivity.this);
        if (result) photoGalleryIntent();

    }


    @OnClick(R.id.remove)
    public void removeImage(){
        if(photoFile == null){
            Glide.with(this).load(R.drawable.ic_picture).into((ImageView) this.findViewById(R.id.image_stuff));

            return;
        }

        photoFile = null;
        Glide.with(this).load(R.drawable.ic_picture).into((ImageView) this.findViewById(R.id.image_stuff));

    }


    @OnClick(R.id.encrypt_button)
    public void encrpytPicture(){
        if(photoFile == null){
            Toast.makeText(this,"no picture to encrypt",Toast.LENGTH_SHORT).show();
        }else{
            showProgress();
            encryptionOfImage();
        }

    }

    @OnClick(R.id.decrypt_button)
    public void decryptPicture(){
        boolean result = checkStoragePermission(MainActivity.this);
        if (result) gettyIntent();

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
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
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

    private void gettyIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), 7);
    }

    private void photoGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), IMAGE_GALLERY_REQUEST);
    }

    private void photoCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAMERA_REQUEST);
    }

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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                if (data != null)
                    onSelectFromGalleryResult(data);
            } else if (requestCode == IMAGE_CAMERA_REQUEST) {
                if (data != null)
                    onCaptureImageResult(data);
            }else{
                decryptFiles(data);

            }
        }
    }

    private void onCaptureImageResult(Intent data) {

        if (data != null) {
            try {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                is  = new ByteArrayInputStream(bytes.toByteArray());
                photoFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                photoFile.createNewFile();
                fo = new FileOutputStream(photoFile);
                fo.write(bytes.toByteArray());
                fo.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Glide.with(this).load(photoFile).into((ImageView) this.findViewById(R.id.image_stuff));

    }

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                is  = new ByteArrayInputStream(bytes.toByteArray());
                photoFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                photoFile.createNewFile();
                fo = new FileOutputStream(photoFile);
                fo.write(bytes.toByteArray());
                fo.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Glide.with(this).load(photoFile).into((ImageView) this.findViewById(R.id.image_stuff));

    }

    private void encryptionOfImage(){
        FILE_NAME_ENC = "encrypt"+ System.currentTimeMillis();
        final File outputFileEnc = new File(myDir,FILE_NAME_ENC);
       // Log.i("YOO", outputFileEnc.toURI().toString());
        try {

            AesEncryption.encrypt_file(key,key_spec,is,new FileOutputStream(outputFileEnc));
            int SPLASH_TIME_OUT = 5500;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    photoFile = null;
                    Glide.with(MainActivity.this).load(R.drawable.ic_picture).into((ImageView) MainActivity.this.findViewById(R.id.image_stuff));
                }
            },SPLASH_TIME_OUT);

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void decryptFiles(Intent data){
        showProgress();
        Uri uri = data.getData();

        String[] split = getRealPathFromURI(uri).split("/");
       // Log.i("YOO",split.length+"");
        int SPLASH_TIME_OUT = 5500;
        Handler handler = new Handler();

        final File outputFileDec = new File(myDir,split[split.length-1]+".jpg");
        File encFile = new File(myDir,split[split.length-1]);
        try{
            AesEncryption.decrypt_file(key,key_spec,new FileInputStream(encFile),new FileOutputStream(outputFileDec));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    photoFile = null;
                    Glide.with(MainActivity.this).load(outputFileDec).into((ImageView) MainActivity.this.findViewById(R.id.image_stuff));

                }
            },SPLASH_TIME_OUT);



        } catch (FileNotFoundException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    Toast.makeText(getApplicationContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(MainActivity.this).load(outputFileDec).into((ImageView) MainActivity.this.findViewById(R.id.image_stuff));

                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        } catch (IOException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    Toast.makeText(getApplicationContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(MainActivity.this).load(outputFileDec).into((ImageView) MainActivity.this.findViewById(R.id.image_stuff));

                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    Toast.makeText(getApplicationContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(MainActivity.this).load(outputFileDec).into((ImageView) MainActivity.this.findViewById(R.id.image_stuff));

                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    Toast.makeText(getApplicationContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(MainActivity.this).load(outputFileDec).into((ImageView) MainActivity.this.findViewById(R.id.image_stuff));

                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    Toast.makeText(getApplicationContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(MainActivity.this).load(outputFileDec).into((ImageView) MainActivity.this.findViewById(R.id.image_stuff));

                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    Toast.makeText(getApplicationContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(MainActivity.this).load(outputFileDec).into((ImageView) MainActivity.this.findViewById(R.id.image_stuff));

                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
