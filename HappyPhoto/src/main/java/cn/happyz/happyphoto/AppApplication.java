package cn.happyz.happyphoto;

import android.app.Application;

import cn.happyz.happyphoto.DataProvider.Activity.Activity;
import cn.happyz.happyphoto.DataProvider.User.UserAlbum;

/**
 * Created by homezc on 14-2-6.
 */
public class AppApplication extends Application {

    private int DisplayWidth;
    private UserAlbum NowSelectUserAlbum;
    private Activity NowSelectActivity;

    public UserAlbum getNowSelectUserAlbum() {
        return NowSelectUserAlbum;
    }

    public void setNowSelectUserAlbum(UserAlbum nowSelectUserAlbum) {
        NowSelectUserAlbum = nowSelectUserAlbum;
    }

    public Activity getNowSelectActivity() {
        return NowSelectActivity;
    }

    public void setNowSelectActivity(Activity nowSelectActivity) {
        NowSelectActivity = nowSelectActivity;
    }

    /**
     * 返回屏幕宽度
     * @return
     */
    public int getDisplayWidth() {
        return DisplayWidth;
    }

    /**
     * 设置屏幕宽度
     * @param displayWidth
     */
    public void setDisplayWidth(int displayWidth) {
        DisplayWidth = displayWidth;
    }


}
