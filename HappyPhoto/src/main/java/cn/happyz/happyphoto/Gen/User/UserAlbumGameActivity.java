package cn.happyz.happyphoto.Gen.User;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.R;

/**
 * Created by zcmzc on 14-1-6.
 */
public class UserAlbumGameActivity extends BaseGen {

    private UserAlbumListSideDrawer userAlbumListSideDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_album_game);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        userAlbumListSideDrawer = new UserAlbumListSideDrawer(this);
        userAlbumListSideDrawer.setLeftBehindContentView(R.layout.user_album_list_side_menu);
        findViewById(R.id.leftBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAlbumListSideDrawer.toggleLeftDrawer();
            }
        });

    }
}
