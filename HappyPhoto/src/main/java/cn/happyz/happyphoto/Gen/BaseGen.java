package cn.happyz.happyphoto.Gen;

import cn.happyz.happyphoto.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import android.os.Environment;
import android.widget.TextView;

/**
 * Created by zcmzc on 13-11-18.
 */
public class BaseGen extends Activity {

    /**
     * 显示会员相册图片数据的模块调用来源类型 0:未设定 1:全部数据 2:分类数据 3:个人数据
     */
    protected static int USER_ALBUM_PIC_LIST_SHOW_MODULE = 0;

    /**
     * 检查会员是否登录
     * @param context
     */
    protected boolean UserCheckIsLogined(Context context){
        SharedPreferences sp = context.getSharedPreferences("USERINFO", MODE_PRIVATE);
        String userName = sp.getString("USERNAME","");
        TextView txtUserOp = (TextView) findViewById(R.id.titlebar_btnUserOp);
        if(userName.equals("")){ //not login
            return false;
        }else{ //已经登录
            return true;
        }
    }

    /**
     * 当前会员id
     * @param context
     * @return
     */
    protected int GetNowUserId(Context context){
        SharedPreferences sp = context.getSharedPreferences("USERINFO", MODE_PRIVATE);
        if(sp != null){
            int userId = sp.getInt("USERID",0);
            return userId;
        }else{
            return -1;
        }

    }

    /**
     * 检查SDCard是否装载
     * @return 逻辑值
     */
    protected boolean CheckExternalStorage(){
        String state= Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }
    protected void TakePictureFrom(){
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        //startActivityForResult(intent, REQUEST_CODE);
    }


}
