package cn.happyz.happyphoto.Gen.User;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

import cn.happyz.happyphoto.DataProvider.User.UserAlbumCollections;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumData;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumDataOperateType;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumListAdapter;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.Plugins.PullToRefresh.PullToRefreshView;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ToastObject;

/**
 * Created by zcmzc on 13-12-22.
 */
public class UserAlbumListOfMineActivity extends BaseGen implements PullToRefreshView.OnHeaderRefreshListener,PullToRefreshView.OnFooterRefreshListener {

    private ImageButton btnBack;

    PullToRefreshView pullToRefreshView;
    public static UserAlbumCollections userAlbumCollectionsOfMine;
    GridView gvOfMine;
    int PageSize = 18;
    int PageIndex = 1;
    //private int userLogin = 3;
    UserAlbumListAdapter userAlbumListAdapterOfMine;

    /**
     * 点击的照片在数组中的索引位置
     */
    public static int ImagePositionsOfMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.user_album_list_mine);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.user_album_list_mine_titlebar); //修改title文字

        boolean userIsLogined = this.CheckUserLogin();

        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(0);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(userIsLogined){
            LoadData(PageIndex,PageSize);
        }
    }

    private boolean CheckUserLogin(){
        TextView txtUserOp = (TextView) findViewById(R.id.titlebar_btnUserOp);
        boolean userIsLogined = super.UserCheckIsLogined(UserAlbumListOfMineActivity.this);
        if(userIsLogined){
            //ToastObject.Show(this,Integer.toString(super.GetNowUserId(UserAlbumCreateActivity.this)));

        }else{
            Intent intent = new Intent(UserAlbumListOfMineActivity.this, UserLoginActivity.class);
            startActivity(intent);
            //this.finish();
            //txtUserOp.setEnabled(false);
        }
        return userIsLogined;
    }

    private void LoadData(int pageIndex,int pageSize){
        ////////////////////////////////////取得相册数据/////////////////////////
        String userAlbumGetListOfMineUrl = getString(R.string.config_user_album_getlist_ofmine_url);
        int userId = super.GetNowUserId(this);//Integer.parseInt(getString(R.string.config_siteid));
        userAlbumGetListOfMineUrl = userAlbumGetListOfMineUrl.replace("{userid}",Integer.toString(userId));
        UserAlbumListOfMineHandler userAlbumListOfMineHandler = new UserAlbumListOfMineHandler();
        UserAlbumData userAlbumData = new UserAlbumData(userAlbumGetListOfMineUrl,userAlbumListOfMineHandler);
        userAlbumData.setPageIndex(pageIndex);
        userAlbumData.setPageSize(pageSize);
        userAlbumData.RequestFromHttp(UserAlbumDataOperateType.GetListOfMine);
    }


    private class UserAlbumListOfMineHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch(httpClientStatus){
                case START_GET:
                    break;

                case FINISH_GET:
                    if(userAlbumCollectionsOfMine != null && userAlbumListAdapterOfMine != null && userAlbumCollectionsOfMine.size()>0){
                        userAlbumCollectionsOfMine.addAll((UserAlbumCollections)msg.obj);
                        userAlbumListAdapterOfMine.notifyDataSetChanged();
                    }else{

                        pullToRefreshView = (PullToRefreshView)findViewById(R.id.main_pull_refresh_view);
                        pullToRefreshView.setBackgroundColor(Color.parseColor("#333333"));
                        gvOfMine = (GridView) findViewById(R.id.gvUserAlbumListOfMine);
                        gvOfMine.setBackgroundColor(Color.parseColor("#333333"));
                        userAlbumCollectionsOfMine = (UserAlbumCollections)msg.obj;
                        userAlbumListAdapterOfMine = new UserAlbumListAdapter(UserAlbumListOfMineActivity.this,R.layout.user_album_type_list_item, userAlbumCollectionsOfMine);
                        gvOfMine.setAdapter(userAlbumListAdapterOfMine);
                        gvOfMine.setOnItemClickListener(new GridViewItemClick());
                        pullToRefreshView.setOnHeaderRefreshListener(UserAlbumListOfMineActivity.this);
                        pullToRefreshView.setOnFooterRefreshListener(UserAlbumListOfMineActivity.this);
                    }

                    pullToRefreshView.setLastUpdated(new Date().toLocaleString());

                    break;

                case ERROR_GET:
                    ToastObject.Show(UserAlbumListOfMineActivity.this, "加载失败");
                    break;

                default:
                    System.out.println("nothing to do");
                    break;
            }
        }
    }


    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        pullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                pullToRefreshView.onFooterRefreshComplete();
                PageIndex++;
                LoadData(PageIndex,PageSize);
            }
        },1000);
    }
    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        pullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                pullToRefreshView.onHeaderRefreshComplete("更新于:"+new Date().toLocaleString());
//				mPullToRefreshView.onHeaderRefreshComplete();
                userAlbumCollectionsOfMine.clear();
                LoadData(PageIndex,PageSize);
            }
        },1000);

    }


    private class GridViewItemClick implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id){
            //点击操作 
            //Intent intent = new Intent();
            ImagePositionsOfMine = position;//图片的位置 
            BaseGen.USER_ALBUM_PIC_LIST_SHOW_MODULE = 3;
            //ToastObject.Show(UserAlbumListOfMineActivity.this, Integer.toString(ImagePositionsOfMine));
            Intent intent = new Intent(UserAlbumListOfMineActivity.this, UserAlbumPicListActivity.class);
            //intent.setClass(UserAlbumListOfMineActivity.this,UserAlbumPicListActivity.class);
            startActivity(intent);
        }
    }
}
