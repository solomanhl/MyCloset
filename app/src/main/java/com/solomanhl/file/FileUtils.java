/**
 * 文件操作
 *
 * @author 贺亮
 */
package com.solomanhl.file;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public String SDPATH;

    private int FILESIZE = 4 * 1024;

    public FileUtils() {
        // 得到当前外部存储设备的目录( /SDCARD )
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }

    public String getSDPATH() {
        return SDPATH;
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    public File createFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     * @return
     */
    public File createSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        // dir.mkdir(); //单级目录
        dir.mkdirs(); // 多级目录
        return dir;
    }

    public File createDir(String dirName) {
        File dir = new File(dirName);
        // dir.mkdir(); //单级目录
        boolean b = false;
        b = dir.mkdirs(); // 多级目录
        return dir;
    }

    /**
     * 在SD卡上删除目录
     *
     * @param fileName
     * @return
     */
    public void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    //递归调用
                    this.deleteFile(files[i].toString());
                    this.delFolder(files[i].toString());
                }
            }
            file.delete();
        } else {
            System.out.println(fileName + "所删除的文件不存在！" + '\n');
        }
    }

    /**
     * 删除文件夹
     *
     * @param folderPath String sd文件夹路径及名称 如mnt/sdcard/ordering/ct
     * @return
     */
    public void delFolder(String folderPath) {
        try {
            deleteFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹
            System.out.println("删除文件夹" + filePath + "操作成功");

        } catch (Exception e) {
            System.out.println("删除文件夹操作出错");
            e.printStackTrace();

        }
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileName
     * @return 1：存在 0：不存在
     */
    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param path
     * @param fileName
     * @param input
     * @return
     */
    public File write2SDFromInput(String path, String fileName,
                                  InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            createSDDir(path);// 建立文件夹
            file = createSDFile(path + fileName);// 建文件
            output = new FileOutputStream(file);
            byte[] buffer = new byte[FILESIZE];
            int count;// count为实际读取的字节数
            while ((count = input.read(buffer)) != -1) { // 直到读完
                output.write(buffer, 0, count);
            }

            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public File writeFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            createDir(path);// 建立文件夹
            file = createFile(path + fileName);// 建文件
            output = new FileOutputStream(file);
            byte[] buffer = new byte[FILESIZE];
            int count;// count为实际读取的字节数
            while ((count = input.read(buffer)) != -1) { // 直到读完
                output.write(buffer, 0, count);
            }

            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public String readFile(String filePath) {
        //String rtn = "";
        StringBuilder rtnb = new StringBuilder();

        try {
            FileReader fr = new FileReader(filePath);// 创建FileReader对象，用来读取字符流
            BufferedReader br = new BufferedReader(fr); // 缓冲指定文件的输入
            // FileWriter fw = new
            // FileWriter("f:/jackie.txt");//创建FileWriter对象，用来写入字符流
            // BufferedWriter bw = new BufferedWriter(fw); //将缓冲对文件的输出
            String myreadline; // 定义一个String类型的变量,用来每次读取一行
            while (br.ready()) {
                myreadline = br.readLine();// 读取一行
                //rtn += myreadline;//使用+=效率不高，特别是换行多的时候
                rtnb.append(myreadline);

                // bw.write(myreadline); //写入文件
                // bw.newLine();
                //System.out.println(myreadline);//在屏幕上输出
            }
            // bw.flush(); //刷新该流的缓冲
            // bw.close();
            br.close();
            // fw.close();
            br.close();
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return rtnb.toString();
    }

    public File[] getFiles(String path) {
        // TODO Auto-generated method stub
        int num = 0;
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }
        for (int j = 0; j < files.length; j++) {
            String name = files[j].getName();
            if (files[j].isDirectory()) {
                String dirPath = files[j].toString().toLowerCase();
                System.out.println(dirPath);
                getFiles(dirPath + "/");
            } else if (files[j].isFile() & name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".bmp") || name.endsWith(".gif") || name.endsWith(".jpeg")) {
                System.out.println("FileName===" + files[j].getName());
                num++;
            }
        }
        return files;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }
}
