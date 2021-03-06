package cn.happyz.happyphoto.Gen.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
 * Created by zcmzc on 14-2-1.
 */
public class ActivityListOfMineVotedGen extends BaseGen implements PullToRefreshView.OnHeaderRefreshListener,PullToRefreshView.OnFooterRefreshListener {

    private ImageButton btnBack;
    PullToRefreshView pullToRefreshView;
    int PageSize = 18;
    int PageIndex = 1;
    public static ActivityCollections activityCollectionsOfMineVoted;
    ActivityListOfMineVotedAdapter activityListOfMineVotedAdapter;
    private ListView listViewOfActivityList;
    public static int activityPositionsOfMineVoted;

    public static int nowUserId;
    public static String nowUserName;
    public static String nowUserPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_list_mine_voted);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.activity_list_of_mine_voted_title);

        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listViewOfActivityList = (ListView) findViewById(R.id.listViewOfActivityList);
        pullToRefreshView = (PullToRefreshView)findViewById(R.id.main_pull_refresh_view);

        nowUserId = super.GetNowUserId(this);
        nowUserName = super.GetNowUserName(this);
        nowUserPass = super.GetNowUserPass(this);

        if(nowUserId > 0){
            LoadData(PageIndex, PageSize);
        }
    }


    private void LoadData(int pageIndex,int pageSize){
        int nowUserId = super.GetNowUserId(this);
        String nowUserName = super.GetNowUserName(this);
        String nowUserPass = super.GetNowUserPass(this);

        String activityGetListOfMineVotedUrl = getString(R.string.config_activity_get_list_of_mine_voted_url);
        activityGetListOfMineVotedUrl = activityGetListOfMineVotedUrl.replace("{user_id}", Integer.toString(nowUserId));
        activityGetListOfMineVotedUrl = activityGetListOfMineVotedUrl.replace("{site_id}", getString(R.string.config_siteid));
        activityGetListOfMineVotedUrl = activityGetListOfMineVotedUrl.replace("{user_name}", nowUserName);
        activityGetListOfMineVotedUrl = activityGetListOfMineVotedUrl.replace("{user_pass}", nowUserPass);


        ActivityOfMineVotedHandler activityOfMineJoinedHandler = new ActivityOfMineVotedHandler();
        ActivityData activityData = new ActivityData(activityGetListOfMineVotedUrl,activityOfMineJoinedHandler);
        activityData.setPageIndex(pageIndex);
        activityData.setPageSize(pageSize);
        activityData.GetDataFromHttp(ActivityDataOperateType.GetListOfMineVoted);
    }

    private class ActivityOfMineVotedHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];
            switch(httpClientStatus){
                case START_GET:
                    ToastObject.Show(ActivityListOfMineVotedGen.this, getString(R.string.message_load_begin));
                    break;
                case FINISH_GET:
                    activityCollectionsOfMineVoted = (ActivityCollections)msg.obj;
                    activityListOfMineVotedAdapter = new ActivityListOfMineVotedAdapter(ActivityListOfMineVotedGen.this,R.layout.activity_list_mine_voted_item, activityCollectionsOfMineVoted);
                    listViewOfActivityList.setAdapter(activityListOfMineVotedAdapter);
                    //listViewOfActivityList.setOnItemClickListener(new ListViewItemClick());
                    pullToRefreshView.setOnHeaderRefreshListener(ActivityListOfMineVotedGen.this);
                    pullToRefreshView.setOnFooterRefreshListener(ActivityListOfMineVotedGen.this);
                    pullToRefreshView.setLastUpdated(new Date().toLocaleString());
                    break;
                case FINISH_GET_BUT_NO_DATA:
                    ToastObject.Show(ActivityListOfMineVotedGen.this, getString(R.string.activity_list_of_mine_voted_no_data));
                    break;
                case FINISH_GET_BUT_USER_ERROR:
                    ToastObject.Show(ActivityListOfMineVotedGen.this, getString(R.string.message_load_user_error));
                    break;
                case ERROR_GET:
                    ToastObject.Show(ActivityListOfMineVotedGen.this, getString(R.string.message_load_failure));
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
                if(activityCollectionsOfMineVoted.size() == PageSize){ //只有当前页的数据等于每页显示数时，才进行加载
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
                if(activityCollectionsOfMineVoted != null){
                    activityCollectionsOfMineVoted.clear();
                }
                LoadData(PageIndex,PageSize);
            }
        },1000);
    }
}
