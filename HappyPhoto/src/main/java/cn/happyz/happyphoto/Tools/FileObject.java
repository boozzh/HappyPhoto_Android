package cn.happyz.happyphoto.Tools;

import android.os.Environment;

import java.io.File;

/**
 * Created by zcmzc on 13-12-13.
 */
public class FileObject {

    /**
     *
     * @param filePath
     * @param fileName
     * @return
     */
    public static File Create(String filePath,String fileName){
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(filePath+"/"+fileName);
            return file;
        } else {
            return null;
        }

    }
}
