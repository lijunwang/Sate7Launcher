package com.sate7.sate7launcher.uitl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static boolean copyFiles(File srcFile, File destDir) {
        //do some check actions
        if (srcFile == null || destDir == null || !srcFile.exists()) {
            Log.e(TAG, "invalid arguments for movePreinstallApkFile()");
            Log.e(TAG, "move " + srcFile + " to " + destDir + " failed");
            return false;
        }

        //create new file
        try {
            destDir.createNewFile();
        } catch (Exception e) {
            Log.e(TAG, "create file faild! due to:" + e);
            return false;
        }

        //set permission
        try {
            Runtime.getRuntime().exec("chmod 644 " + destDir.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "chmod file faild! due to:" + e);
        }

        //do copy
        try {
            boolean ret = copyFile(srcFile, destDir);
            if (!ret) {
                Log.e(TAG, "copy file faild!");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "copy file faild! due to:" + e);
        }
        return true && destDir.exists();
    }

    public static boolean copyFile(File src, File des) {
        boolean result = true;

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {

            fis = new FileInputStream(src);
            fos = new FileOutputStream(des);
            //3.执行复制操作
            byte[] b = new byte[1024];
            int len;
            while ((len = fis.read(b)) != -1) {
                fos.write(b, 0, len);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = false;
        } finally {
            //4.关闭对应的流，先关输出流，后关输入流
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    result = false;
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    result = false;
                }
            }
        }
        return result;
    }

    public static void CopyDirectory(Context context) {
        //the source directory not exists
        File storeDir = new File("/system/media/pictures/");
        if (!storeDir.exists()) {
            Log.e(TAG, "/system/third_app is not exist");
            return;
        }

        //get the apk files in /system/third_app
        String apkDirFilesNames[] = storeDir.list();
        if (apkDirFilesNames == null) {
            Log.e(TAG, "apk file name is null");
            return;
        }
        String des = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/";
        //copy the apk files to /data/app
        boolean installSucc = false;
        for (int i = 0; i < apkDirFilesNames.length; i++) {
            File srcFileFile = new File("system/media/pictures/" + apkDirFilesNames[i]);
            Log.e(TAG, "srcFile=" + srcFileFile);
            File destFile = new File(des + apkDirFilesNames[i]);

            boolean installResult = copyFiles(srcFileFile, destFile);
            if (!installResult) {
                Log.d(TAG, "install failed");
                //return;
            }

        }
        scanDir(context,des);
    }

    public static  void scanDir(final Context context, final String path) {
        android.util.Log.i(TAG, "scanDir");
        android.util.Log.i(TAG, "scanDir---path:" + path);

        File file = new File(path);
        File[] fileItemFiles = file.listFiles();

        for (int i = 0; i < fileItemFiles.length; i++) {

            String pathItem = fileItemFiles[i].getParent() + File.separator + fileItemFiles[i].getName();

            if (fileItemFiles[i].isFile()) {
                scanFile(context, pathItem);
            }

            if (fileItemFiles[i].isDirectory()) {
                scanDir(context, pathItem);
            }
        }
    }
    public static final void scanFile(final Context context,final String path) {
        android.util.Log.i(TAG, "scanFile--:" + path);

        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(path)));
        context.sendBroadcast(scanIntent);
    }


}
