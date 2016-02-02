package com.solomanhl.mycloset;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.solomanhl.mycloset.changeClotheFragment.ChangeClothe;
import com.solomanhl.mycloset.changeClotheFragment.HomeFragment;

public class MainActivity extends AppCompatActivity {
    final private int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 123;
    private Bundle savedInstanceState;
    private App app;
    private FrameLayout container;
    private ChangeClothe chageClothe;
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = (App) getApplicationContext(); // 获得全局变量
        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main);

        findView();
        findFragment();
        requestExternalSdPemission();

//        调用fragment放到获取sd权限之后
//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction().add(R.id.container, chageClothe).commit();
//        }
    }

    private void showFragment() {

        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction().add(R.id.container, chageClothe).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.container, homeFragment, "home").commit();
        }
    }

    private void findView() {
        container = (FrameLayout) findViewById(R.id.container);
    }

    private void findFragment() {
        chageClothe = new ChangeClothe();
        homeFragment = new HomeFragment();
    }


    private void requestExternalSdPemission() {
        if (app.SDK_Version >= 23) {//6.0或以上版本要申请权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            } else {//第二次打开app已经授权了，直接显示fragment
                app.SD_Permission = true;
                showFragment();
            }
        } else {//如果是6.0以下版本，具有sd权限
            app.SD_Permission = true;
            showFragment();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                app.SD_Permission = true;
                showFragment();

            } else {
                // Permission Denied
                app.SD_Permission = false;
            }
        }
    }
}
