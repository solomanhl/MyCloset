package com.solomanhl.mycloset;

import android.app.Application;
import android.os.Build;
import android.os.Environment;

/**
 * Created by solomanhl on 2015/12/23.
 */
public class App extends Application {
    public String SDpath;
    public String AppPath = "BeautifulCloset/";
    public int SDK_Version;
    public boolean SD_Permission;
    public String model, shangshen, xiashen;

    @Override
    public void onCreate() {
        super.onCreate();

        getSDK_Version();
        getSDpath();
        SD_Permission = false;
    }

    private String getSDpath() {
        SDpath = Environment.getExternalStorageDirectory() + "/";
        return SDpath;
    }

    private void getSDK_Version() {
        SDK_Version = Build.VERSION.SDK_INT;
    }
}

