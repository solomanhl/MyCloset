package com.solomanhl.mycloset;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
//        requestExternalSdPemission();
        performCodeWithPermission(getString(R.string.request_permission),new MainActivity.PermissionCallback() {
            @Override
            public void hasPermission() {
                //执行打开相机相关代码
                // Permission Granted
                app.SD_Permission = true;
                showFragment();
            }
            @Override
            public void noPermission() {
                app.SD_Permission = false;
                showFragment();
            }
        },Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);

//        调用fragment放到获取sd权限之后
//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction().add(R.id.container, chageClothe).commit();
//        }
    }

    private void showFragment() {

        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction().add(R.id.container, chageClothe).commit();
//            getSupportFragmentManager().beginTransaction().add(R.id.container, homeFragment, "home").commit();
            getSupportFragmentManager().beginTransaction().add(R.id.container, homeFragment, "home").commitAllowingStateLoss();
        }
    }

    private void findView() {
        container = (FrameLayout) findViewById(R.id.container);
    }

    private void findFragment() {
        chageClothe = new ChangeClothe();
        homeFragment = new HomeFragment();
    }


//    private void requestExternalSdPemission() {
//        if (app.SDK_Version >= 23) {//6.0或以上版本要申请权限
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                //申请WRITE_EXTERNAL_STORAGE权限
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_EXTERNAL_STORAGE,
//                                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
//                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//            } else {//第二次打开app已经授权了，直接显示fragment
//                app.SD_Permission = true;
//                showFragment();
//            }
//        } else {//如果是6.0以下版本，具有sd权限
//            app.SD_Permission = true;
//            showFragment();
//        }
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        doNext(requestCode, grantResults);
//    }
//
//    private void doNext(int requestCode, int[] grantResults) {
//        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission Granted
//                app.SD_Permission = true;
//                showFragment();
//
//            } else {
//                // Permission Denied
//                app.SD_Permission = false;
//            }
//        }
//    }


    //**************** Android M Permission (Android 6.0权限控制代码封装)
    //新的权限封装办法，CameraAddMaskFragment里面用过了*****************************************************
    private int permissionRequestCode = 88;
    private PermissionCallback permissionRunnable ;
    public interface PermissionCallback{
        void hasPermission();
        void noPermission();
    }

    /**
     * Android M运行时权限请求封装
     * @param permissionDes 权限描述
     * @param runnable 请求权限回调
     * @param permissions 请求的权限（数组类型），直接从Manifest中读取相应的值，比如Manifest.permission.WRITE_CONTACTS
     */
    public void performCodeWithPermission(@NonNull String permissionDes, PermissionCallback runnable, @NonNull String... permissions){
        if(permissions == null || permissions.length == 0)return;
//        this.permissionrequestCode = requestCode;
        this.permissionRunnable = runnable;
        if((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || checkPermissionGranted(permissions)){
            if(permissionRunnable!=null){
                permissionRunnable.hasPermission();
                permissionRunnable = null;
            }
        }else{
            //permission has not been granted.
            requestPermission(permissionDes,permissionRequestCode,permissions);
        }

    }
    private boolean checkPermissionGranted(String[] permissions){
        boolean flag = true;
        for(String p:permissions){
            if(ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED){
                flag = false;
                break;
            }
        }
        return flag;
    }
    private void requestPermission(String permissionDes,final int requestCode,final String[] permissions){
        if(shouldShowRequestPermissionRationale(permissions)){
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.

//            Snackbar.make(getWindow().getDecorView(), requestName,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.common_ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            ActivityCompat.requestPermissions(BaseAppCompatActivity.this,
//                                    permissions,
//                                    requestCode);
//                        }
//                    })
//                    .show();
            //如果用户之前拒绝过此权限，再提示一次准备授权相关权限
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(permissionDes)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, permissions, requestCode);
                        }
                    }).show();

        }else{
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(MainActivity.this, permissions, requestCode);
        }
    }
    private boolean shouldShowRequestPermissionRationale(String[] permissions){
        boolean flag = false;
        for(String p:permissions){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,p)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == permissionRequestCode){
            if(verifyPermissions(grantResults)){
                if(permissionRunnable!=null) {
                    permissionRunnable.hasPermission();
                    permissionRunnable = null;
                }
            }else{
//                showToast("暂无权限执行相关操作！");
                if(permissionRunnable!=null) {
                    permissionRunnable.noPermission();
                    permissionRunnable = null;
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
    public boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    //********************** END Android M Permission ****************************************
}
