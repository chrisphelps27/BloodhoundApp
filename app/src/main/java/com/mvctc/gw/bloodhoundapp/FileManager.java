package com.mvctc.gw.bloodhoundapp;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import static android.provider.Telephony.Mms.Part.FILENAME;

/**
 * Created by grant47710 on 11/13/2017.
 * Edited by mcintosh47303 on 11/14/2017.
 */

public class FileManager {
    static String FILENAME = "bloodhoundData";
    /**
     * Created by grant47710 on 11/13/2017.
     */
    public static void FileWrite(int Eco, int Loc, int ID, Context context) {
        String x = "" + Eco + Loc + ID;
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            Log.d("Bloodhound", "" + x.getBytes());
            fos.write(x.getBytes());
            fos.close();
        } catch (Exception e) {
        }
    }
    /**
     * @deprecated
     * @param context
     */
    public static void FileRead(Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            String fileContent = "";
            int x;
            while ((x = fis.read()) != -1) {
                x = x & 0xFF;
                fileContent += (char)x;
            }
            Log.d("Bloodhound", fileContent);
            fis.close();
        } catch (Exception e) {
        }
    }
    public static Integer[] GetData(Context context) {
        Integer[] data = new Integer[3];
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            String fileContent = "";
            String dataTemp = "";
            for (int x = 0; x < 3; x++){
                dataTemp += (char)(fis.read() & 0xFF);
            }
            data[0] = Integer.parseInt(dataTemp);
            dataTemp = "";
            for (int x = 0; x < 3; x++){
                dataTemp += (char)(fis.read() & 0xFF);
            }
            data[1] = Integer.parseInt(dataTemp);
            dataTemp = "";
            for (int x = 0; x < 6; x++){
                dataTemp += (char)(fis.read() & 0xFF);
            }
            data[2] = Integer.parseInt(dataTemp);
            Log.d("Bloodhound", "" + data[0]);
            Log.d("Bloodhound", "" + data[1]);
            Log.d("Bloodhound", "" + data[2]);
            fis.close();
            return data;
        } catch (Exception e) {
        }
        return data;
    }
}
