package com.mvctc.gw.bloodhoundapp;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by grant47710 on 11/13/2017.
 */

public class FileManager {
    String FILENAME = "filename";
    void FileWrite(String x, Context context){
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(x.getBytes());
            fos.close();
        }catch(Exception e){}
    }

    void FileRead(String x, Context context){
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            fis.read(x.getBytes());
            fis.close();
        }catch(Exception e){}
    }
}
