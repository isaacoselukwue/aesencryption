package com.example.encryptiondecryptionimagesproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;


import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.encryptiondecryptionimagesproject.Fragments.EncryptionDecryptionFragment;

public class EncryptionDecryptionActivity extends BaseActivity {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;


    ConstraintLayout home_layout;
    ConstraintLayout upload_layout;
    ConstraintLayout camera_layout;


    ImageView upload_icon;
    ImageView camera_icon;

    String uri;


    View cameraView;
    View galleryView;

    String cameraTransitionName;
    String galleryTransitionName;

    String getType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption_decryption);


        if(getIntent() != null){
            Log.i("YO1O",(Bitmap)getIntent().getParcelableExtra("data")+"");
            getType = getIntent().getExtras().getString("type");
            uri = getIntent().getExtras().getString("uri");
            Log.i("YOO1",uri+"uri");
            Toast.makeText(getApplicationContext(), "ypo",Toast.LENGTH_SHORT);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);



        drawerLayout.addDrawerListener(drawerToggle);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();


        Bundle bundle=new Bundle();
        bundle.putParcelable("data",(Bitmap)getIntent().getParcelableExtra("data"));
        bundle.putString("type",getType);
        bundle.putString("uri",uri);
        Fragment frag = new EncryptionDecryptionFragment();

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.frame_layout, frag);
//                ft.addToBackStack(null);
        ft.commit();





        navigationView = (NavigationView)findViewById(R.id.side_nav);
        View header = navigationView.getHeaderView(0);
        home_layout = (ConstraintLayout)header.findViewById(R.id.home_layout);
        upload_layout = (ConstraintLayout)header.findViewById(R.id.upload_layout);
        camera_layout = (ConstraintLayout)header.findViewById(R.id.camera_layout);

        upload_icon = (ImageView)header.findViewById(R.id.upload_icon);
        camera_icon = (ImageView)header.findViewById(R.id.camera_icon);

        home_layout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"home",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(),HomeActivity.class);
                Pair<View, String> p1 = Pair.create((View)upload_icon, upload_icon.getTransitionName()+"");
                Pair<View, String> p2 = Pair.create((View)camera_icon, camera_icon.getTransitionName()+"");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(EncryptionDecryptionActivity.this, p1, p2);
                startActivity(intent, options.toBundle());
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });



    }


}
