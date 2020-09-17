package com.example.encryptiondecryptionimagesproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.encryptiondecryptionimagesproject.Interface.Listener;
import com.example.encryptiondecryptionimagesproject.dialog.ProgressDialogController;

public class BaseActivity extends AppCompatActivity implements Listener {
    private ProgressDialogController mProgressDialogController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialogController = new ProgressDialogController(getSupportFragmentManager(),"please wait ...");
    }

    protected void setMessage(String msg) {
        mProgressDialogController.setMessageResource(msg);
    }


    @Override
    public void showProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mProgressDialogController.isProgressVisible()) {
                    mProgressDialogController.startProgress();
                }
            }});
    }

    @Override
    public void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialogController.finishProgress();
            }});
    }
}
