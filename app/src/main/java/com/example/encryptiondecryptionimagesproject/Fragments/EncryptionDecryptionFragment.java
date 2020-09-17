package com.example.encryptiondecryptionimagesproject.Fragments;

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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.encryptiondecryptionimagesproject.AESENCRYPTION.AesEncryption;
import com.example.encryptiondecryptionimagesproject.Interface.Listener;
import com.example.encryptiondecryptionimagesproject.MainActivity;
import com.example.encryptiondecryptionimagesproject.R;

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


public class EncryptionDecryptionFragment extends Fragment {
    Unbinder unbinder;
    private Listener mListener;
    InputStream is;
    File photoFile;
    Uri uploadUri;
    File outputFileDec;

    File myDir;

    private static String FILE_NAME_ENC;

    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSIONS = 1234;

    @BindView(R.id.image_picked)ImageView image_picked;

//    //hardcoded
//    String key = "DjHLRPPvlNUmNBfo";
//    String key_spec = "iuNSutndUyZNlFAk";

    public EncryptionDecryptionFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.encryptydecrypt_fragment, container, false);
        unbinder = ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            Bitmap bitmapimage = bundle.getParcelable("data");
            Log.i("YOO",bitmapimage+"");
            if(bundle.get("type").equals("camera")){
                onCaptureImageResult(bitmapimage);

            }else {
                uploadUri = Uri.parse(bundle.getString("uri"));
                onSelectFromGalleryResult(uploadUri);
            }
        }

        myDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/saved_images");
        myDir.mkdir();

    }

    @OnClick(R.id.remove_layout)
    public void removeImage(){
        if(photoFile == null ){
            Glide.with(this).load(R.drawable.ic_camera).into((ImageView) getActivity().findViewById(R.id.image_picked));

            return;
        }

        photoFile = null;

        Glide.with(this).load(R.drawable.ic_camera).into((ImageView) getActivity().findViewById(R.id.image_picked));

    }

    @OnClick(R.id.encryption_button)
    public void encrypytButton(){
        if(photoFile == null){
            Toast.makeText(getContext(),"no picture to encrypt",Toast.LENGTH_SHORT).show();
        }else{

            secretKeysPopUp("encrypt",null);

        }

    }

    @OnClick(R.id.decryption_button)
    public void decryptButton(){
        if (photoFile != null ){
            Toast.makeText(getContext(),"kindly remove the current picture and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        boolean result = checkStoragePermission(getActivity());
        if (result) gettyIntent();

    }

    private void onSelectFromGalleryResult(Uri uri) {
        if (uri != null) {
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
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
        Glide.with(this).load(photoFile).into((ImageView) getActivity().findViewById(R.id.image_picked));

    }


    private void onCaptureImageResult(Bitmap thumbnail) {

        if (thumbnail != null) {
            try {
              //  Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
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
        Glide.with(this).load(photoFile).into((ImageView) getActivity().findViewById(R.id.image_picked));

    }


    //encrypt module
    private void encryptionOfImage(String key, String key_spec){
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
                    mListener.hideProgress();
                    Toast.makeText(getContext(),"Encryption sucessfully saved at internal storage --> saved_imgaes",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(getContext()).load(R.drawable.ic_camera).into((ImageView) getActivity().findViewById(R.id.image_picked));
                }
            },SPLASH_TIME_OUT);

        } catch (NoSuchPaddingException e) {
            Toast.makeText(getContext(),"ERROR : file not found",Toast.LENGTH_SHORT).show();
            mListener.hideProgress();
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            Toast.makeText(getContext(),"ERROR : key invalid hint 16 characters",Toast.LENGTH_SHORT).show();
            mListener.hideProgress();
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(getContext(),"ERROR : key invalid hint 16 characters ",Toast.LENGTH_SHORT).show();
            mListener.hideProgress();
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            Toast.makeText(getContext(),"ERROR : key invalid hint 16 characters",Toast.LENGTH_SHORT).show();
            mListener.hideProgress();
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Toast.makeText(getContext(),"ERROR ",Toast.LENGTH_SHORT).show();
            mListener.hideProgress();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getContext(),"ERROR ",Toast.LENGTH_SHORT).show();
            mListener.hideProgress();
            e.printStackTrace();
        }




    }

    //Enter secret keys pop up
    private void secretKeysPopUp(final String type, final Intent data){
        final AlertDialog dialog;
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.secretkeylayout, null);


        final TextInputEditText secretkey1 = mView.findViewById(R.id.secretkey1_input);
        final TextInputEditText secretkey2 = mView.findViewById(R.id.secretkey2_input);

        Button proceed_button = mView.findViewById(R.id.proceed_button);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();

        proceed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(secretkey1.getText().toString().trim().isEmpty() || secretkey2.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(),"Fields must not be empty",Toast.LENGTH_SHORT).show();

                    return;
                }

                if(type.equals("encrypt")){
                    mListener.showProgress();
                    encryptionOfImage(secretkey1.getText().toString().trim(),secretkey2.getText().toString().trim());
                    dialog.cancel();
                }else if(type.equals("decrypt")){
                    decryptFiles(data,secretkey1.getText().toString().trim(),secretkey2.getText().toString().trim());
                    dialog.cancel();
                }
            }
        });




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
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSIONS);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
               secretKeysPopUp("decrypt",data);
               // decryptFiles(data);


        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    //decrypt module
    private void decryptFiles(Intent data, String key, String key_spec){
        mListener.showProgress();
        Uri uri = data.getData();

        String[] split = getRealPathFromURI(uri).split("/");
        // Log.i("YOO",split.length+"");
        int SPLASH_TIME_OUT = 5500;
        Handler handler = new Handler();

        outputFileDec = new File(myDir,split[split.length-1]+".jpg");
        File encFile = new File(myDir,split[split.length-1]);
        try{
            AesEncryption.decrypt_file(key,key_spec,new FileInputStream(encFile),new FileOutputStream(outputFileDec));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.hideProgress();
                    photoFile = null;
                    Glide.with(getContext()).load(outputFileDec).into((ImageView) getActivity().findViewById(R.id.image_picked));
                    Toast.makeText(getContext(),"Decryption sucessfully saved at internal storage --> saved_imgaes",Toast.LENGTH_SHORT).show();


                }
            },SPLASH_TIME_OUT);



        } catch (FileNotFoundException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.hideProgress();
                    Toast.makeText(getContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(getContext()).load(outputFileDec).into((ImageView) getActivity().findViewById(R.id.image_picked));

                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        } catch (IOException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.hideProgress();
                    Toast.makeText(getContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(getContext()).load(outputFileDec).into((ImageView) getActivity().findViewById(R.id.image_picked));


                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.hideProgress();
                    Toast.makeText(getContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(getContext()).load(outputFileDec).into((ImageView) getActivity().findViewById(R.id.image_picked));


                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.hideProgress();
                    Toast.makeText(getContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(getContext()).load(outputFileDec).into((ImageView) getActivity().findViewById(R.id.image_picked));


                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.hideProgress();
                    Toast.makeText(getContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(getContext()).load(outputFileDec).into((ImageView) getActivity().findViewById(R.id.image_picked));


                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.hideProgress();
                    Toast.makeText(getContext(),"UNAUTHORIZED ACCESS",Toast.LENGTH_SHORT).show();
                    photoFile = null;
                    Glide.with(getContext()).load(outputFileDec).into((ImageView) getActivity().findViewById(R.id.image_picked));


                }
            },SPLASH_TIME_OUT);
            e.printStackTrace();
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
