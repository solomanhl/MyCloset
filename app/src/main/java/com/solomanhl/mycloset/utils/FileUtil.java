package com.solomanhl.mycloset.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.StatFs;

import com.solomanhl.mycloset.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {
	private App app;
	private static String tag = FileUtil.class.getSimpleName();
//    public static final String savePath = "/mnt/sdcard/MyCloset/";
	public static final String savePath = Environment.getExternalStorageDirectory() + "/MyCloset/";

	public static String readLocalTxtContent(String path){
		LogUtil.i(tag, "readLocalTxtContent() -- the path is:"+path);
		String content = "";
		boolean exist = fielExist(path);
		LogUtil.i(tag, "readLocalTxtContent() -- the file exist is:"+exist);
		if(!exist){
			return content;
		}
		File file = new File(path);
		try {
			content = getFileContent(file);
		} catch (FileNotFoundException e) {
			LogUtil.e(e);
		} catch (IOException e) {
			LogUtil.e(e);
		}
		return content;
	}
	
	public static boolean checkSdcard(){
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 *
	 * @param path
	 * @return
	 */
	public static boolean fielExist(String path){
		if(path == null || "".equals(path.trim())){
			return false;
		}
		File file = new File(path);
		return file.exists();
	}
	
	public static String getFileContent(File file)throws FileNotFoundException,IOException {
		LogUtil.i(tag, "getFileContent() -- ");
		if(file !=  null && file.exists()){
			InputStream ins = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(ins,"gb2312");
			BufferedReader br = new BufferedReader(reader);
			StringBuilder sb = new StringBuilder();
			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				sb.append(line);
			}
			br.close();
			reader.close();
			ins.close();
			return sb.toString();
		}
		return null;
	}

    public static String getFileContent(InputStream ins)throws IOException{
        if(ins != null){
            InputStreamReader reader = new InputStreamReader(ins,"utf-8");
            BufferedReader br = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            for (String line = br.readLine(); line != null; line = br
                    .readLine()) {
                sb.append(line);
            }
            br.close();
            reader.close();
            ins.close();
            return sb.toString();
        }
        return null;
    }


    /**
     *
     *
     * @param type
     *            0: 1:
     * @param sizeMb
     *
     * @return
     * @throws RuntimeException
     */
    public static boolean isAvaiableSpace(int type, int sizeMb)
            throws RuntimeException {
        boolean ishasSpace = false;
//        if (type < 0 || type > 1) {
//            //
//            throw new RuntimeException(
//                    MessageData.MESSAGEDATA_LMERROR_ARGUMENT_NOTLEGAL);
//        }

        String sdcard = null;
        if (type == 0) {
            if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
//                sdcard = Environment.getExternalStorageDirectory().getPath();
                sdcard = "/mnt/sdcard";
            } else {
//                throw new RuntimeException(
//                        MessageData.MESSAGEDATA_LMERROR_SDCARD_SIZENOTENOUGH);
            }
        } else if (type == 1) {
            sdcard = "/data";
        }
        StatFs statFs = new StatFs(sdcard);
        long blockSize = statFs.getBlockSize();
        long blocks = statFs.getAvailableBlocks();
        long availableSpare = (blocks * blockSize) / (1024 * 1024);
        LogUtil.e("Error", "availableSpare = " + availableSpare+"; sdcard path:"+sdcard);
        if (availableSpare > sizeMb) {
            ishasSpace = true;
        }
        return ishasSpace;
    }
    
    private static void saveLocalData(Context context,int type,String str,Object value){
		if(context == null||value == null)
			return;
		SharedPreferences sh = context.getSharedPreferences("ClotheProperties",
				Context.MODE_WORLD_WRITEABLE);
		Editor edit = sh.edit();
		if(str == null || "".equals(str.trim())){
			LogUtil.e(tag,"save the data to local failed,the str is null ");
			return;
		}
		LogUtil.i(tag, "saveLocalData() -- str:"+str+"; value :"+value);
		switch(type){
		case 0:
			//
			edit.putString(str, String.valueOf(value));
			break;
		case 1:
			// boolean
			edit.putBoolean(str, Boolean.valueOf(String.valueOf(value)));
			break;
		case 2:
			// int
			edit.putInt(str, Integer.valueOf(String.valueOf(value)));
			break;
		case 3:
			// long
			edit.putLong(str, Long.valueOf(String.valueOf(value)));
			break;
		case 4:
			// float
			edit.putFloat(str, Float.valueOf(String.valueOf(value)));
			break;
		}
		edit.commit();
	}
    
    private static Object getLocalData(Context context,String str,Object defValue,int type){
		if(context == null||str==null||"".equals(str))
			return null;
		SharedPreferences sh = context.getSharedPreferences("ClotheProperties",
				Context.MODE_WORLD_WRITEABLE);
		Object obj = null;
		LogUtil.i(tag, "getLocalData() -- str is:"+str);
		switch(type){
		case 0:
			obj = sh.getString(str, String.valueOf(defValue));
			break;
		case 1:
			obj = sh.getBoolean(str, Boolean.valueOf(String.valueOf(defValue)));
			break;
		case 2:
			obj = sh.getInt(str, Integer.valueOf(String.valueOf(defValue)));
			break;
		case 3:
			obj = sh.getLong(str, Long.valueOf(String.valueOf(defValue)));
			break;
		case 4:
			obj = sh.getFloat(str, Float.valueOf(String.valueOf(defValue)));
			break;
		}
		return obj;
	}
    
    public static void saveLocalDataInteger(Context context,String str,int value){
		saveLocalData(context,2,str,value);
	}
	public static void saveLocalDataStr(Context context,String str,String value){
		saveLocalData(context,0,str,value);
	}
	public static void saveLocalDataBoolean(Context context,String str,Boolean value){
		saveLocalData(context,1,str,value);
	}
	public static void saveLocalDataLong(Context context,String str,long value){
		saveLocalData(context,3,str,value);
	}
	public static void saveLocalDataFloat(Context context,String str,float value){
		saveLocalData(context,4,str,value);
	}
	
	public static int getLocalDataInteger(Context context,String str,int defValue){
		Object obj = getLocalData(context,str,defValue,2);
		if(obj == null)
		{
			return defValue;
		}
		return Integer.valueOf(String.valueOf(obj));
	}
	public static String getLocalDataString(Context context,String str,String defValue){
		Object obj = getLocalData(context,str,defValue,0);
		if(obj == null)
		{
			return defValue;
		}
		return String.valueOf(obj);
	}
	public static boolean getLocalDataBoolean(Context context,String str,boolean defValue){
		Object obj = getLocalData(context,str,defValue,1);
		if(obj == null)
		{
			return defValue;
		}
		return Boolean.valueOf(String.valueOf(obj));
	}
	public static float getLocalDataFloat(Context context,String str,float defValue){
		Object obj = getLocalData(context,str,defValue,4);
		if(obj == null)
		{
			return defValue;
		}
		return Float.valueOf(String.valueOf(obj));
	}
	public static long getLocalDataLong(Context context,String str,long defValue){
		Object obj = getLocalData(context,str,defValue,3);
		if(obj == null)
		{
			return defValue;
		}
		return Long.valueOf(String.valueOf(obj));
	}
	
	public static String getImagePath(String imagePath){
		return savePath+imagePath+".jpg";
//		return savePath+imagePath+".png";
	}

	public static String getImagePath(String imagePath, String type){
		if (type.toLowerCase().equals("jpg"))
			return savePath+imagePath+".jpg";
		else if (type.toLowerCase().equals("png"))
			return savePath+imagePath+".png";
		else
			return savePath+imagePath+".jpg";
	}

}
