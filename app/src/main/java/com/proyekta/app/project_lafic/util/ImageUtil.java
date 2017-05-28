package com.proyekta.app.project_lafic.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by WINDOWS 10 on 28/05/2017.
 */

public class ImageUtil {

    public static File ImageResizer(File fileInput){

        Bitmap b = BitmapFactory.decodeFile(fileInput.getAbsolutePath());

        if (b != null){
            int origWidth = b.getWidth();
            int origHeight = b.getHeight();

            final int destWidth = 640;

            if(origWidth > destWidth){
                double destHeight = (double)origHeight / ( (double)origWidth / destWidth ) ;

                Bitmap b2 = Bitmap.createScaledBitmap(b, destWidth, (int)Math.round(destHeight), false);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();

                b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream);

                File f = new File(fileInput.getPath()+"_resize.jpg");

                try {
                    f.createNewFile();

                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(outStream.toByteArray());

                    fo.close();
                } catch (IOException e){
                    Log.getStackTraceString(e);
                }

                return f;
            }
        }

        return null;
    }
}
