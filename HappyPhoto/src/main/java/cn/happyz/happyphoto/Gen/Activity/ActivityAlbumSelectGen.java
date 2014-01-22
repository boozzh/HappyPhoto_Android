package cn.happyz.happyphoto.Gen.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

import cn.happyz.happyphoto.DataProvider.Activity.Activity;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityUserData;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityUserDataOperateType;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumCollections;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumData;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumDataOperateType;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumListAdapter;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumListForSelectAdapter;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.Gen.User.UserAlbumPicListGen;
import cn.happyz.happyphoto.Gen.User.UserLoginGen;
import cn.happyz.happyphoto.Plugins.PullToRefresh.PullToRefreshView;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ToastObject;

/**
 * 选择参加比赛的作品
 */
public class ActivityAlbumSelectGen extends BaseGen implements PullToRefreshView.OnHeaderRefreshListener,PullToRefreshView.OnFooterRefreshListener  {

    private ImageButton btnBack;
    PullToRefreshView pullToRefreshView;
    UserAlbumCollections userAlbumCollectionsOfMineForSelect;
    GridView gvOfMine;
    int PageSize = 18;
    int PageIndex = 1;
    UserAlbumListForSelectAdapter userAlbumListForSelectAdapter;

    /**
     * 已经选择的作品id,以|分隔
     */
    public static String selectedUserAlbumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_album_select);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.activity_album_select_title); //修改title文字

        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(0);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        boolean userIsLogin = this.CheckUserLogin();

        if(userIsLogin){
            final int nowUserId = super.GetNowUserId(this);
            final String nowUserName = super.GetNowUserName(this);
            final String nowUserPass = super.GetNowUserPass(this);

            LoadData(PageIndex, PageSize);

            Button activity_album_select_confirm = (Button) findViewById(R.id.activity_album_select_confirm);
            activity_album_select_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //提交报名
                   SubmitActivityUser(nowUserId, nowUserName, nowUserPass);
                }
            });
        }
    }

    private void SubmitActivityUser(int nowUserId, String nowUserName, String nowUserPass){
        if(selectedUserAlbumId != null && !selectedUserAlbumId.equals("")){
            int activityPositionsOfListAll = ActivityListGen.activityPositionsOfListAll;
            if(ActivityListGen.activityCollectionsOfListAll != null && activityPositionsOfListAll>=0){
                Activity activity = ActivityListGen.activityCollectionsOfListAll.get(activityPositionsOfListAll);
                if(activity != null){
                    int activityId = activity.getActivityId();
                    if(activityId > 0){
                        String httpUrl = getString(R.string.config_site_url) + getString(R.string.config_activity_user_create_url);
                        httpUrl = httpUrl.replace("{activity_id}",Integer.toString(activityId));
                        httpUrl = httpUrl.replace("{user_id}",Integer.toString(nowUserId));
                        httpUrl = httpUrl.replace("{site_id}",getString(R.string.config_siteid));
                        httpUrl = httpUrl.replace("{user_name}",nowUserName);
                        httpUrl = httpUrl.replace("{user_pass}",nowUserPass);
                        ActivityUserCreateHandler activityUserCreateHandler = new ActivityUserCreateHandler();
                        ActivityUserData activityUserData = new ActivityUserData(httpUrl,activityUserCreateHandler);
                        activityUserData.GetDataFromHttp(ActivityUserDataOperateType.Create);

                        //ToastObject.Show(ActivityAlbumSelectGen.this,activityId + "," + selectedUserAlbumId);

                    }
                }
            }
        }
    }


    private boolean CheckUserLogin(){
        //TextView txtUserOp = (TextView) findViewById(R.id.titlebar_btnUserOp);
        boolean userIsLogined = super.UserCheckIsLogined(ActivityAlbumSelectGen.this);
        if(userIsLogined){
        }else{
            Intent intent = new Intent(ActivityAlbumSelectGen.this, UserLoginGen.class);
            startActivity(intent);
            this.finish();
        }
        return userIsLogined;
    }

    private void LoadData(int pageIndex,int pageSize){
        ////////////////////////////////////取得相册数据/////////////////////////
        String userAlbumGetListOfMineUrl = getString(R.string.config_user_album_getlist_ofmine_url);
        int userId = super.GetNowUserId(this);//Integer.parseInt(getString(R.string.config_siteid));
        userAlbumGetListOfMineUrl = userAlbumGetListOfMineUrl.replace("{userid}",Integer.toString(userId));
        ActivityAlbumSelectHandler activityAlbumSelectHandler = new ActivityAlbumSelectHandler();
        UserAlbumData userAlbumData = new UserAlbumData(userAlbumGetListOfMineUrl,activityAlbumSelectHandler);
        userAlbumData.setPageIndex(pageIndex);
        userAlbumData.setPageSize(pageSize);
        userAlbumData.RequestFromHttp(UserAlbumDataOperateType.GetListOfMine);
    }

    private class ActivityAlbumSelectHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch(httpClientStatus){
                case START_GET:
                    break;
                case FINISH_GET:
                    if(userAlbumCollectionsOfMineForSelect != null && userAlbumListForSelectAdapter != null && userAlbumCollectionsOfMineForSelect.size()>0){
                        userAlbumCollectionsOfMineForSelect.addAll((UserAlbumCollections)msg.obj);
                        userAlbumListForSelectAdapter.notifyDataSetChanged();
                    }else{
                        pullToRefreshView = (PullToRefreshView)findViewById(R.id.main_pull_refresh_view);
                        pullToRefreshView.setBackgroundColor(Color.parseColor("#333333"));
                        gvOfMine = (GridView) findViewById(R.id.gvUserAlbumListOfMine);
                        gvOfMine.setBackgroundColor(Color.parseColor("#333333"));
                        userAlbumCollectionsOfMineForSelect = (UserAlbumCollections)msg.obj;
                        userAlbumListForSelectAdapter = new UserAlbumListForSelectAdapter(ActivityAlbumSelectGen.this,R.layout.activity_list_all_item, userAlbumCollectionsOfMineForSelect);
                        gvOfMine.setAdapter(userAlbumListForSelectAdapter);
                        gvOfMine.setOnItemClickListener(new GridViewItemClick());
                        pullToRefreshView.setOnHeaderRefreshListener(ActivityAlbumSelectGen.this);
                        pullToRefreshView.setOnFooterRefreshListener(ActivityAlbumSelectGen.this);
                    }

                    pullToRefreshView.setLastUpdated(new Date().toLocaleString());

                    break;

                case ERROR_GET:
                    ToastObject.Show(ActivityAlbumSelectGen.this, "加载失败");
                    break;

                default:
                    System.out.println("nothing to do");
                    break;
            }
        }
    }

    private class ActivityUserCreateHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch(httpClientStatus){
                case START_GET:
                    break;
                case FINISH_GET:
                    ToastObject.Show(ActivityAlbumSelectGen.this,getString(R.string.activity_album_select_submit_activity_user_success));
                    break;
                case ERROR_GET:
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
                //pullToRefreshView.onHeaderRefreshComplete( getString(R.string.pull_to_refresh_update_tips) +new Date().toLocaleString());
//				mPullToRefreshView.onHeaderRefreshComplete();
                //userAlbumCollectionsOfMineForSelect.clear();
                //LoadData(PageIndex,PageSize);
            }
        },1000);

    }


    private class GridViewItemClick implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id){
            //点击操作 
            //Intent intent = new Intent();
            //ImagePositionsOfMine = position;//图片的位置 
            //BaseGen.USER_ALBUM_PIC_LIST_SHOW_MODULE = 3;
            //ToastObject.Show(UserAlbumListOfMineGen.this, Integer.toString(ImagePositionsOfMine));
            //Intent intent = new Intent(ActivityAlbumSelectGen.this, UserAlbumPicListGen.class);
            //intent.setClass(UserAlbumListOfMineGen.this,UserAlbumPicListGen.class);
            //startActivity(intent);
        }
    }
}
