package cn.happyz.happyphoto.Gen.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;

import cn.happyz.happyphoto.DataProvider.Activity.ActivityCollections;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityData;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityDataOperateType;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.Plugins.PullToRefresh.PullToRefreshView;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ToastObject;

/**
 * Created by home zc on 14-2-9.
 */
public class ActivityListOfEndGen extends BaseGen implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

    private ImageButton btnBack;
    private Button btnMyJoinedActivity;
    private Button btnMyVotedActivity;
    PullToRefreshView pullToRefreshView;
    int PageSize = 18;
    int PageIndex = 1;
    ActivityCollections activityCollections;
    ActivityListAdapter activityListAdapter;
    private ListView listViewOfActivityList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_list_all);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.activity_list_of_end_title); //修改title文字

        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnMyJoinedActivity = (Button) findViewById(R.id.btnMyJoinedActivity);
        btnMyJoinedActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ActivityListOfEndGen.this, ActivityListOfMineJoinedGen.class);
                startActivity(intent);
            }
        });

        btnMyVotedActivity = (Button) findViewById(R.id.btnMyVotedActivity);
        btnMyVotedActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ActivityListOfEndGen.this, ActivityListOfMineVotedGen.class);
                startActivity(intent);
            }
        });

        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listViewOfActivityList = (ListView) findViewById(R.id.listViewOfActivityList);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        //pullToRefreshView.setBackgroundColor(Color.parseColor("#333333"));

        LoadData(PageIndex, PageSize);
    }

    private void LoadData(int pageIndex, int pageSize) {
        String activityGetListOfAllUrl = getString(R.string.config_activity_get_list_of_end_url);
        ActivityListHandler activityListHandler = new ActivityListHandler();
        ActivityData activityData = new ActivityData(activityGetListOfAllUrl, activityListHandler);
        activityData.setPageIndex(pageIndex);
        activityData.setPageSize(pageSize);
        activityData.GetDataFromHttp(ActivityDataOperateType.GetListOfEnd);
    }

    private class ActivityListHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];
            switch (httpClientStatus) {
                case START_GET:
                    ToastObject.Show(ActivityListOfEndGen.this, getString(R.string.message_load_begin));
                    break;
                case FINISH_GET:
                    activityCollections = (ActivityCollections) msg.obj;
                    activityListAdapter = new ActivityListAdapter(ActivityListOfEndGen.this, R.layout.activity_list_all_item, activityCollections);
                    listViewOfActivityList.setAdapter(activityListAdapter);
                    pullToRefreshView.setOnHeaderRefreshListener(ActivityListOfEndGen.this);
                    pullToRefreshView.setOnFooterRefreshListener(ActivityListOfEndGen.this);
                    pullToRefreshView.setLastUpdated(new Date().toLocaleString());
                    break;

                case ERROR_GET:
                    ToastObject.Show(ActivityListOfEndGen.this, getString(R.string.activity_list_of_end_no_data));
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
                if (activityCollections.size() == PageSize) { //只有当前页的数据等于每页显示数时，才进行加载
                    PageIndex++;
                    LoadData(PageIndex, PageSize);
                }
            }
        }, 1000);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        pullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                pullToRefreshView.onHeaderRefreshComplete(getString(R.string.pull_to_refresh_update_tips) + new Date().toLocaleString());
                if (activityCollections != null) {
                    activityCollections.clear();
                }
                LoadData(PageIndex, PageSize);
            }
        }, 1000);
    }
}