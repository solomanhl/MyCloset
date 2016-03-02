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
    public String model, shangyi, kuzi, qunzi;
    public String[] type = {"shangyi", "qunzi", "kuzi"};
    public int type_posi;

    //衣服的初始大小和位置，变化后保存最新的
    public float shangyi_info[] = {240, 320, 120, 160,0};//上衣的初始位置 宽 高 左上角x y,旋转cox
    public float kuzi_info[] = {240, 320, 120, 600,0};//裤子的初始位置 宽 高 左上角x y,旋转cox
    public float qunzi_info[] = {240, 320, 120, 1000,0};//裙子的初始位置 宽 高 左上角x y,旋转cox

    @Override
    public void onCreate() {
        super.onCreate();

        getSDK_Version();
        getSDpath();
        SD_Permission = false;
        type_posi = 0;
    }

    private String getSDpath() {
        SDpath = Environment.getExternalStorageDirectory() + "/";
        return SDpath;
    }

    private void getSDK_Version() {
        SDK_Version = Build.VERSION.SDK_INT;
    }
}

