package com.ciprian.inventoryapp.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


//I get source code from this "https://github.com/CoderzHeaven/StoreImageSqliteAndroid/blob/e1e91654ba7d627419cebf2aea1490b927f6e4f2/app/src/main/java/com/coderzheaven/storeimage/Utils.java"


public class DbBitmapUtility {

    //convert to original photo
    public static Bitmap getImage(byte[] image){
        BitmapFactory.Options option2 = new BitmapFactory.Options();
        option2.inPreferredConfig = Bitmap.Config.RGB_565;
        // added for reducing the memory
        option2.inDither = false;
        option2.inPurgeable = true;
        return BitmapFactory.decodeByteArray(image,0,image.length,option2);
    }
    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}
