package cn.happyz.happyphoto.Gen.Activity;

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

import cn.happyz.happyphoto.AppApplication;
import cn.happyz.happyphoto.DataProvider.Activity.Activity;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityAlbumData;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityAlbumDataOperateType;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityAlbumListOfMineAdapter;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumCollections;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.Plugins.PullToRefresh.PullToRefreshView;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.HttpClientStatus;

/**
 * Created by homezc on 14-2-6.
 */
public class ActivityAlbumListOfMineGen extends BaseGen implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

    private ImageButton btnBack;
    PullToRefreshView pullToRefreshView;
    UserAlbumCollections userAlbumCollections;
    GridView gvOfActivityAlbumList;
    int PageSize = 18;
    int PageIndex = 1;
    ActivityAlbumListOfMineAdapter activityAlbumListOfMineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_album_list_of_mine);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.activity_album_list_of_mine_title); //修改title文字

        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(0);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LoadData(PageIndex, PageSize);
    }

    private void LoadData(int pageIndex, int pageSize) {
        ////////////////////////////////////取得相册数据/////////////////////////
        String activityAlbumListOfMineUrl = getString(R.string.config_activity_album_list_of_mine_url);
        int nowUserId = super.GetNowUserId(this);
        String nowUserName = super.GetNowUserName(this);
        String nowUserPass = super.GetNowUserPass(this);
        Activity activity = ((AppApplication)getApplication()).getNowSelectActivity();
        if(activity != null && nowUserId>0 ){
            int activityId = activity.getActivityId();
            if(activityId>0){
                int siteId = Integer.parseInt(getString(R.string.config_siteid));
                activityAlbumListOfMineUrl = activityAlbumListOfMineUrl.replace("{site_id}",Integer.toString(siteId));
                activityAlbumListOfMineUrl = activityAlbumListOfMineUrl.replace("{activity_id}", Integer.toString(activityId));
                activityAlbumListOfMineUrl = activityAlbumListOfMineUrl.replace("{user_id}", Integer.toString(nowUserId));
                activityAlbumListOfMineUrl = activityAlbumListOfMineUrl.replace("{user_name}", nowUserName);
                activityAlbumListOfMineUrl = activityAlbumListOfMineUrl.replace("{user_pass}", nowUserPass);

                ActivityAlbumListOfMineHandler activityAlbumListOfMineHandler = new ActivityAlbumListOfMineHandler();
                ActivityAlbumData activityAlbumData = new ActivityAlbumData(activityAlbumListOfMineUrl, activityAlbumListOfMineHandler);
                activityAlbumData.setPageIndex(pageIndex);
                activityAlbumData.setPageSize(pageSize);
                activityAlbumData.GetDataFromHttp(ActivityAlbumDataOperateType.GetListOfMine);
            }
        }
    }

    private class ActivityAlbumListOfMineHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch (httpClientStatus) {
                case START_GET:
                    break;
                case FINISH_GET:
                    if (userAlbumCollections != null && activityAlbumListOfMineAdapter != null && userAlbumCollections.size() > 0) {
                        userAlbumCollections.addAll((UserAlbumCollections) msg.obj);
                        activityAlbumListOfMineAdapter.notifyDataSetChanged();
                    } else {
                        pullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
                        pullToRefreshView.setBackgroundColor(Color.parseColor("#333333"));
                        gvOfActivityAlbumList = (GridView) findViewById(R.id.gvUserAlbumListOfMine);
                        gvOfActivityAlbumList.setBackgroundColor(Color.parseColor("#333333"));
                        userAlbumCollections = (UserAlbumCollections) msg.obj;
                        activityAlbumListOfMineAdapter = new ActivityAlbumListOfMineAdapter(
                                ActivityAlbumListOfMineGen.this,
                                R.layout.activity_album_list_of_mine_item,
                                userAlbumCollections);
                        gvOfActivityAlbumList.setAdapter(activityAlbumListOfMineAdapter);
                        gvOfActivityAlbumList.setOnItemClickListener(new GridViewItemClick());
                        pullToRefreshView.setOnHeaderRefreshListener(ActivityAlbumListOfMineGen.this);
                        pullToRefreshView.setOnFooterRefreshListener(ActivityAlbumListOfMineGen.this);
                    }
                    pullToRefreshView.setLastUpdated(new Date().toLocaleString());
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
                LoadData(PageIndex, PageSize);
            }
        }, 1000);
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
        }, 1000);

    }


    private class GridViewItemClick implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            //点击操作 
            //Intent intent = new Intent();
            //ImagePositionsOfActivityAlbum = position;//图片的位置 
            //BaseGen.userAlbumPicListShowModule = UserAlbumPicListShowModule.UserAlbumOfActivityAlbumList;
            //ToastObject.Show(UserAlbumListOfMineGen.this, Integer.toString(ImagePositionsOfMine));
            //Intent intent = new Intent(ActivityAlbumListOfMineGen.this, UserAlbumPicListGen.class);
            //intent.setClass(UserAlbumListOfMineGen.this,UserAlbumPicListGen.class);
            //startActivity(intent);
        }
    }
}
