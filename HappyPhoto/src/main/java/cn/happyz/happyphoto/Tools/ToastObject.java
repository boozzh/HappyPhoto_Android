package cn.happyz.happyphoto.Tools;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by zcmzc on 13-12-21.
 */
public class ToastObject {

    public static void Show(Context context,String message){
        Toast toast = Toast.makeText(context,message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.TOP, 12, 40);
        toast.show();
    }
}
