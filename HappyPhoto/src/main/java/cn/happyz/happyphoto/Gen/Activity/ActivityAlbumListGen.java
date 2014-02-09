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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

import cn.happyz.happyphoto.AppApplication;
import cn.happyz.happyphoto.DataProvider.Activity.Activity;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityAlbumData;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityAlbumDataOperateType;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumCollections;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.Gen.User.UserAlbumPicListGen;
import cn.happyz.happyphoto.Gen.User.UserAlbumPicListShowModule;
import cn.happyz.happyphoto.Plugins.PullToRefresh.PullToRefreshView;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.HttpClientStatus;

/**
 * Created by zcmzc on 14-2-3.
 */
public class ActivityAlbumListGen extends BaseGen implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

    private ImageButton btnBack;
    PullToRefreshView pullToRefreshView;
    public static UserAlbumCollections userAlbumCollectionsOfActivityAlbumList;
    GridView gvOfActivityAlbumList;
    int PageSize = 18;
    int PageIndex = 1;
    ActivityAlbumListAdapter activityAlbumListAdapter;
    private Button btn_view_my_voted;
    /**
     * 点击的照片在数组中的索引位置
     */
    public static int ImagePositionsOfActivityAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_album_list);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.activity_album_list_title); //修改title文字

        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LoadData(PageIndex, PageSize);

        btn_view_my_voted = (Button) findViewById(R.id.btn_view_my_voted);
        btn_view_my_voted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityAlbumListGen.this, ActivityVoteRecordListOfMyVotedGen.class);
                startActivity(intent);
            }
        });

    }

    private void LoadData(int pageIndex, int pageSize) {
        ////////////////////////////////////取得相册数据/////////////////////////
        String activityAlbumListUrl = getString(R.string.config_activity_album_list_url);
        //int userId = super.GetNowUserId(this);//Integer.parseInt(getString(R.string.config_siteid));
        Activity activity = ((AppApplication)getApplication()).getNowSelectActivity();
        if(activity != null){
            int activityId = activity.getActivityId();
            if(activityId>0){
                int siteId = Integer.parseInt(getString(R.string.config_siteid));
                activityAlbumListUrl = activityAlbumListUrl.replace("{site_id}",Integer.toString(siteId));
                activityAlbumListUrl = activityAlbumListUrl.replace("{activity_id}", Integer.toString(activityId));
                ActivityAlbumListHandler activityAlbumListHandler = new ActivityAlbumListHandler();
                ActivityAlbumData activityAlbumData = new ActivityAlbumData(activityAlbumListUrl, activityAlbumListHandler);
                activityAlbumData.setPageIndex(pageIndex);
                activityAlbumData.setPageSize(pageSize);
                activityAlbumData.GetDataFromHttp(ActivityAlbumDataOperateType.GetList);
            }
        }
    }

    private class ActivityAlbumListHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch (httpClientStatus) {
                case START_GET:
                    break;
                case FINISH_GET:
                    if (userAlbumCollectionsOfActivityAlbumList != null && activityAlbumListAdapter != null && userAlbumCollectionsOfActivityAlbumList.size() > 0) {
                        userAlbumCollectionsOfActivityAlbumList.addAll((UserAlbumCollections) msg.obj);
                        activityAlbumListAdapter.notifyDataSetChanged();
                    } else {
                        pullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
                        pullToRefreshView.setBackgroundColor(Color.parseColor("#333333"));
                        gvOfActivityAlbumList = (GridView) findViewById(R.id.gvUserAlbumListOfMine);
                        gvOfActivityAlbumList.setBackgroundColor(Color.parseColor("#333333"));
                        userAlbumCollectionsOfActivityAlbumList = (UserAlbumCollections) msg.obj;
                        activityAlbumListAdapter = new ActivityAlbumListAdapter(ActivityAlbumListGen.this, R.layout.activity_album_list_item, userAlbumCollectionsOfActivityAlbumList);
                        gvOfActivityAlbumList.setAdapter(activityAlbumListAdapter);
                        gvOfActivityAlbumList.setOnItemClickListener(new GridViewItemClick());
                        pullToRefreshView.setOnHeaderRefreshListener(ActivityAlbumListGen.this);
                        pullToRefreshView.setOnFooterRefreshListener(ActivityAlbumListGen.this);
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
            ImagePositionsOfActivityAlbum = position;//图片的位置 
            BaseGen.userAlbumPicListShowModule = UserAlbumPicListShowModule.UserAlbumOfActivityAlbumList;
            //ToastObject.Show(UserAlbumListOfMineGen.this, Integer.toString(ImagePositionsOfMine));
            Intent intent = new Intent(ActivityAlbumListGen.this, UserAlbumPicListGen.class);
            //intent.setClass(UserAlbumListOfMineGen.this,UserAlbumPicListGen.class);
            startActivity(intent);
        }
    }
}
