package cn.happyz.happyphoto.Tools;

import android.content.Context;
import android.util.DisplayMetrics;

import java.security.MessageDigest;

import cn.happyz.happyphoto.DataProvider.Activity.Activity;
import cn.happyz.happyphoto.Gen.BaseGen;

/**
 * Created by zcmzc on 13-11-18.
 */
public class FormatObject {

    public static String MD5(String content)
    {
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
        }catch(Exception e)
        {
            e.printStackTrace();
            return "";
        }

        char[] charArray = content.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for(int i = 0; i < charArray.length; i++)
        {
            byteArray[i] = (byte)charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for( int i = 0; i < md5Bytes.length; i++)
        {
            int val = ((int)md5Bytes[i])&0xff;
            if(val < 16)
            {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 将dip值转换为px值
     * @param context
     * @param dipValue
     * @return
     */
    public static int DipToPx(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为dip值
     * @param context
     * @param pxValue
     * @return
     */
    public static int PxToDip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    public static int GetDisplayWidth(BaseGen activity){
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;// 屏幕的宽dp
        return width;
    }
}
