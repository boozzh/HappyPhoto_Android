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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;

import cn.happyz.happyphoto.DataProvider.Activity.ActivityCollections;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityData;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityDataOperateType;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityListAdapter;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.Plugins.PullToRefresh.PullToRefreshView;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ToastObject;

/**
 * Created by homezc on 14-1-14.
 */
public class ActivityListGen extends BaseGen implements PullToRefreshView.OnHeaderRefreshListener,PullToRefreshView.OnFooterRefreshListener {

    private ImageButton btnBack;
    PullToRefreshView pullToRefreshView;
    int PageSize = 18;
    int PageIndex = 1;
    public static ActivityCollections activityCollectionsOfListAll;
    ActivityListAdapter activityListAdapter;
    private ListView listViewOfActivityList;
    public static int activityPositionsOfListAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_list_all);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.activity_list_all_title); //修改title文字

        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(0);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listViewOfActivityList = (ListView) findViewById(R.id.listViewOfActivityList);
        listViewOfActivityList.setBackgroundColor(Color.parseColor("#ffffff"));
        pullToRefreshView = (PullToRefreshView)findViewById(R.id.main_pull_refresh_view);
        //pullToRefreshView.setBackgroundColor(Color.parseColor("#333333"));

        LoadData(PageIndex, PageSize);
    }

    private void LoadData(int pageIndex,int pageSize){
        ////////////////////////////////////取得相册数据/////////////////////////
        String activityGetListOfAllUrl = getString(R.string.config_activity_get_list_of_all_url);

        ActivityOfAllHandler activityOfAllHandler = new ActivityOfAllHandler();
        //userAlbumGetListOfOneTypeUrl = userAlbumGetListOfOneTypeUrl.replace("{useralbumtypeid}",Integer.toString(userAlbumTypeId));
        ActivityData activityData = new ActivityData(activityGetListOfAllUrl,activityOfAllHandler);
        activityData.setPageIndex(pageIndex);
        activityData.setPageSize(pageSize);
        activityData.GetDataFromHttp(ActivityDataOperateType.GetList);

    }

    private class ActivityOfAllHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];
            switch(httpClientStatus){
                case START_GET:
                    ToastObject.Show(ActivityListGen.this, getString(R.string.message_load_begin));
                    break;
                case FINISH_GET:

                    activityCollectionsOfListAll = (ActivityCollections)msg.obj;
                    activityListAdapter = new ActivityListAdapter(ActivityListGen.this,R.layout.activity_list_all_item, activityCollectionsOfListAll);
                    listViewOfActivityList.setAdapter(activityListAdapter);
                    listViewOfActivityList.setOnItemClickListener(new ListViewItemClick());
                    pullToRefreshView.setOnHeaderRefreshListener(ActivityListGen.this);
                    pullToRefreshView.setOnFooterRefreshListener(ActivityListGen.this);
                    pullToRefreshView.setLastUpdated(new Date().toLocaleString());
                    break;

                case ERROR_GET:
                    ToastObject.Show(ActivityListGen.this, getString(R.string.message_load_failure));
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
                if(activityCollectionsOfListAll.size() == PageSize){ //只有当前页的数据等于每页显示数时，才进行加载
                    PageIndex++;
                    LoadData(PageIndex,PageSize);
                }
            }
        },1000);
    }
    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        pullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                pullToRefreshView.onHeaderRefreshComplete(getString(R.string.pull_to_refresh_update_tips)+new Date().toLocaleString());
//				mPullToRefreshView.onHeaderRefreshComplete();
                if(activityCollectionsOfListAll != null){
                    activityCollectionsOfListAll.clear();
                }
                LoadData(PageIndex,PageSize);
            }
        },1000);
    }

    private class ListViewItemClick implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id){
            //点击操作
            //Intent intent = new Intent();
            activityPositionsOfListAll = position;//图片的位置 
            //BaseGen.USER_ALBUM_PIC_LIST_SHOW_MODULE = 1;
            //ToastObject.Show(ActivityListGen.this, Integer.toString(activityPositionsOfListAll));
            Intent intent = new Intent(ActivityListGen.this, ActivityDetailGen.class);
            //intent.setClass(UserAlbumListOfMineGen.this,UserAlbumPicListGen.class);
            startActivity(intent);
        }
    }
}
