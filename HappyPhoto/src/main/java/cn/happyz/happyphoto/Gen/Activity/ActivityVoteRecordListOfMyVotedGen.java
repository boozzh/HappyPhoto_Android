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
import cn.happyz.happyphoto.DataProvider.Activity.ActivityAlbumListOfMineAdapter;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityVoteRecordData;
import cn.happyz.happyphoto.DataProvider.Activity.ActivityVoteRecordDataOperateType;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumCollections;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.Gen.User.UserAlbumPicListGen;
import cn.happyz.happyphoto.Gen.User.UserAlbumPicListShowModule;
import cn.happyz.happyphoto.Plugins.PullToRefresh.PullToRefreshView;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.HttpClientStatus;

/**
 * Created by homezc on 14-2-6.
 */
public class ActivityVoteRecordListOfMyVotedGen extends BaseGen implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

    private ImageButton btnBack;
    PullToRefreshView pullToRefreshView;
    public static UserAlbumCollections userAlbumCollectionsOfActivityVoteRecordListOfMyVoted;
    GridView gvOfActivityAlbumList;
    int PageSize = 18;
    int PageIndex = 1;
    ActivityAlbumListOfMineAdapter activityVoteRecordListOfMyVotedAdapter;
    /**
     * 点击的照片在数组中的索引位置
     */
    public static int ImagePositionsOfActivityVoteRecordOfMyVoted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_vote_record_list_of_my_voted);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.activity_vote_record_of_my_voted_title); //修改title文字

        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(0);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LoadData(PageIndex, PageSize);

        //Button btn_activity_album_list_view_mine_voted = (Button) findViewById(R.id.btn_activity_album_list_view_mine_voted);

    }

    private void LoadData(int pageIndex, int pageSize) {
        int nowUserId = super.GetNowUserId(this);
        String nowUserName = super.GetNowUserName(this);
        String nowUserPass = super.GetNowUserPass(this);

        ////////////////////////////////////取得相册数据/////////////////////////
        String activityVoteRecordListOfMyVotedUrl = getString(R.string.config_activity_vote_record_list_of_my_voted_url);

        Activity activity = ((AppApplication)getApplication()).getNowSelectActivity();
        if (activity != null) {
            int activityId = activity.getActivityId();
            int activityRound = 1;

            int siteId = Integer.parseInt(getString(R.string.config_siteid));
            activityVoteRecordListOfMyVotedUrl = activityVoteRecordListOfMyVotedUrl.replace("{site_id}", Integer.toString(siteId));
            activityVoteRecordListOfMyVotedUrl = activityVoteRecordListOfMyVotedUrl.replace("{activity_id}", Integer.toString(activityId));
            activityVoteRecordListOfMyVotedUrl = activityVoteRecordListOfMyVotedUrl.replace("{user_id}", Integer.toString(nowUserId));
            activityVoteRecordListOfMyVotedUrl = activityVoteRecordListOfMyVotedUrl.replace("{user_name}", nowUserName);
            activityVoteRecordListOfMyVotedUrl = activityVoteRecordListOfMyVotedUrl.replace("{user_pass}", nowUserPass);
            activityVoteRecordListOfMyVotedUrl = activityVoteRecordListOfMyVotedUrl.replace("{activity_round}", Integer.toString(activityRound));


            ActivityVoteRecordListOfMyVotedHandler activityVoteRecordListOfMyVotedHandler = new ActivityVoteRecordListOfMyVotedHandler();
            ActivityVoteRecordData activityVoteRecordData = new ActivityVoteRecordData(activityVoteRecordListOfMyVotedUrl, activityVoteRecordListOfMyVotedHandler);
            activityVoteRecordData.setPageIndex(pageIndex);
            activityVoteRecordData.setPageSize(pageSize);
            activityVoteRecordData.GetDataFromHttp(ActivityVoteRecordDataOperateType.GetListOfMyVote);
        }

    }

    private class ActivityVoteRecordListOfMyVotedHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch (httpClientStatus) {
                case START_GET:
                    break;
                case FINISH_GET:
                    if (userAlbumCollectionsOfActivityVoteRecordListOfMyVoted != null
                            && activityVoteRecordListOfMyVotedAdapter != null
                            && userAlbumCollectionsOfActivityVoteRecordListOfMyVoted.size() > 0) {
                        userAlbumCollectionsOfActivityVoteRecordListOfMyVoted.addAll((UserAlbumCollections) msg.obj);
                        activityVoteRecordListOfMyVotedAdapter.notifyDataSetChanged();
                    } else {
                        pullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
                        pullToRefreshView.setBackgroundColor(Color.parseColor("#333333"));
                        gvOfActivityAlbumList = (GridView) findViewById(R.id.gvUserAlbumListOfMine);
                        gvOfActivityAlbumList.setBackgroundColor(Color.parseColor("#333333"));
                        userAlbumCollectionsOfActivityVoteRecordListOfMyVoted = (UserAlbumCollections) msg.obj;
                        activityVoteRecordListOfMyVotedAdapter = new ActivityAlbumListOfMineAdapter(
                                ActivityVoteRecordListOfMyVotedGen.this,
                                R.layout.activity_vote_record_list_of_my_voted_item,
                                userAlbumCollectionsOfActivityVoteRecordListOfMyVoted);
                        gvOfActivityAlbumList.setAdapter(activityVoteRecordListOfMyVotedAdapter);
                        gvOfActivityAlbumList.setOnItemClickListener(new GridViewItemClick());
                        pullToRefreshView.setOnHeaderRefreshListener(ActivityVoteRecordListOfMyVotedGen.this);
                        pullToRefreshView.setOnFooterRefreshListener(ActivityVoteRecordListOfMyVotedGen.this);
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
            ImagePositionsOfActivityVoteRecordOfMyVoted = position;//图片的位置 
            BaseGen.userAlbumPicListShowModule = UserAlbumPicListShowModule.UserAlbumOfActivityVoteRecordListOfMyVoted;
            //ToastObject.Show(UserAlbumListOfMineGen.this, Integer.toString(ImagePositionsOfMine));
            Intent intent = new Intent(ActivityVoteRecordListOfMyVotedGen.this, UserAlbumPicListGen.class);
            //intent.setClass(UserAlbumListOfMineGen.this,UserAlbumPicListGen.class);
            startActivity(intent);
        }
    }
}
