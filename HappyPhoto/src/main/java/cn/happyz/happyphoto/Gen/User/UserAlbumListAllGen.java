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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import cn.happyz.happyphoto.DataProvider.User.UserAlbumCollections;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumData;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumDataOperateType;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumListAdapter;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumTypeCollections;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumTypeData;
import cn.happyz.happyphoto.DefaultGen;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.Plugins.PullToRefresh.PullToRefreshView;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ToastObject;

/**
 * Created by zcmzc on 13-12-8.
 */
public class UserAlbumListAllGen extends BaseGen implements PullToRefreshView.OnHeaderRefreshListener,PullToRefreshView.OnFooterRefreshListener {
    private ImageButton ibtnMenuTitleBarWithMenu;
    PullToRefreshView pullToRefreshView;
    int PageSize = 18;
    int PageIndex = 1;
    private LinearLayout llUserAlbumListLeftMenu;
    public static UserAlbumCollections userAlbumCollectionsOfShow;
    UserAlbumListAdapter userAlbumListAdapterOfShow;
    GridView gvUserAlbumListOfAll;
    Button btnUserAlbumListShowAll;


    /**
     * 点击的照片在数组中的索引位置
     */
    public static int ImagePositionsOfAll;
    public static int ImagePositionsOfType;
    private UserAlbumListSideDrawer userAlbumListSideDrawer;
    private int nowUserAlbumTypeId = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.user_album_list_all);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_with_menu);
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBarWithMenu);
        tvTitleBarTitle.setText(R.string.user_album_list_all_title); //修改title文字

        gvUserAlbumListOfAll = (GridView) findViewById(R.id.gvUserAlbumListOfAll);
        gvUserAlbumListOfAll.setBackgroundColor(Color.parseColor("#333333"));

        pullToRefreshView = (PullToRefreshView)findViewById(R.id.main_pull_refresh_view);
        pullToRefreshView.setBackgroundColor(Color.parseColor("#333333"));


        userAlbumListSideDrawer = new UserAlbumListSideDrawer(this);
        userAlbumListSideDrawer.setLeftBehindContentView(R.layout.user_album_list_side_menu);
        ibtnMenuTitleBarWithMenu = (ImageButton) findViewById(R.id.ibtnMenuTitleBarWithMenu);
        ibtnMenuTitleBarWithMenu.setVisibility(0);
        ibtnMenuTitleBarWithMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAlbumListSideDrawer.toggleLeftDrawer();
            }
        });

        btnUserAlbumListShowAll = (Button) findViewById(R.id.btnUserAlbumListShowAll);
        btnUserAlbumListShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userAlbumCollectionsOfShow != null){
                    userAlbumCollectionsOfShow.clear();
                    if(userAlbumListAdapterOfShow != null){
                        userAlbumListAdapterOfShow.notifyDataSetChanged();
                    }
                }
                nowUserAlbumTypeId = 0;
                PageIndex = 1;
                LoadData(nowUserAlbumTypeId, PageIndex, PageSize);
            }
        });

        if(DefaultGen.globalUserAlbumTypeCollections != null && DefaultGen.globalUserAlbumTypeCollections.size()>0){
            //userAlbumTypeCollections 有可能从其他的view中取得
            LoadUserAlbumTypeList();
        }else{
            ////////////////////////////////////绑定相册分类/////////////////////////
            String userAlbumTypeGetListUrl = getString(R.string.config_user_album_type_getlist_url);
            int siteId = Integer.parseInt(getString(R.string.config_siteid));
            userAlbumTypeGetListUrl = userAlbumTypeGetListUrl.replace("{siteid}",Integer.toString(siteId));
            UserAlbumTypeGetListHandler userAlbumTypeGetListHandler = new UserAlbumTypeGetListHandler();
            UserAlbumTypeData userAlbumTypeData = new UserAlbumTypeData(userAlbumTypeGetListUrl,userAlbumTypeGetListHandler);
            userAlbumTypeData.RequestFromHttp();
        }


    }


    private void LoadData(int userAlbumTypeId, int pageIndex,int pageSize){
        for(int i=0;i<llUserAlbumListLeftMenu.getChildCount();i++){
            View view = llUserAlbumListLeftMenu.getChildAt(i);
            view.setEnabled(false);
        }
        ////////////////////////////////////取得相册数据/////////////////////////
        String userAlbumGetListOfAllUrl = getString(R.string.config_user_album_getlist_ofall_url);
        String userAlbumGetListOfOneTypeUrl = getString(R.string.config_user_album_getlist_of_one_type_url);

        UserAlbumListOfShowHandler userAlbumListOfShowHandler = new UserAlbumListOfShowHandler();

        if(userAlbumTypeId<=0){
            UserAlbumData userAlbumData = new UserAlbumData(userAlbumGetListOfAllUrl,userAlbumListOfShowHandler);
            userAlbumData.setPageIndex(pageIndex);
            userAlbumData.setPageSize(pageSize);
            userAlbumData.RequestFromHttp(UserAlbumDataOperateType.GetListOfAll);
        }else{
            userAlbumGetListOfOneTypeUrl = userAlbumGetListOfOneTypeUrl.replace("{useralbumtypeid}",Integer.toString(userAlbumTypeId));
            UserAlbumData userAlbumData = new UserAlbumData(userAlbumGetListOfOneTypeUrl,userAlbumListOfShowHandler);
            userAlbumData.setPageIndex(pageIndex);
            userAlbumData.setPageSize(pageSize);
            userAlbumData.RequestFromHttp(UserAlbumDataOperateType.GetListOfType);
        }
    }

    private void LoadUserAlbumTypeList(){
        llUserAlbumListLeftMenu = (LinearLayout) findViewById(R.id.llUserAlbumListLeftMenu);
        if(DefaultGen.globalUserAlbumTypeCollections != null && DefaultGen.globalUserAlbumTypeCollections.size()>0){
            for(int i=0;i< DefaultGen.globalUserAlbumTypeCollections.size();i++){
                Button button = new Button(UserAlbumListAllGen.this);
                button.setTag(DefaultGen.globalUserAlbumTypeCollections.get(i).getUserAlbumTypeId());
                button.setText(DefaultGen.globalUserAlbumTypeCollections.get(i).getUserAlbumTypeName());
                button.setBackgroundColor(Color.parseColor("#0A1027"));
                button.setTextColor(Color.parseColor("#FFFFFF"));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(userAlbumCollectionsOfShow != null){
                            userAlbumCollectionsOfShow.clear();
                            if(userAlbumListAdapterOfShow != null){
                                userAlbumListAdapterOfShow.notifyDataSetChanged();
                            }
                        }

                        PageIndex = 1;
                        nowUserAlbumTypeId = Integer.parseInt(view.getTag().toString());
                        LoadData(nowUserAlbumTypeId,PageIndex,PageSize);
                    }
                });
                llUserAlbumListLeftMenu.addView(button);
            }
        }
        LoadData(nowUserAlbumTypeId, PageIndex,PageSize);
    }

    private class UserAlbumListOfShowHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];
            switch(httpClientStatus){
                case START_GET:
                    ToastObject.Show(UserAlbumListAllGen.this, getString(R.string.message_load_begin));
                    break;
                case FINISH_GET:
                    for(int i=0;i<llUserAlbumListLeftMenu.getChildCount();i++){
                        View view = llUserAlbumListLeftMenu.getChildAt(i);
                        view.setEnabled(true);
                    }

                    if(userAlbumCollectionsOfShow != null && userAlbumListAdapterOfShow != null && userAlbumCollectionsOfShow.size()>0){
                        userAlbumCollectionsOfShow.addAll((UserAlbumCollections)msg.obj);
                        userAlbumListAdapterOfShow.notifyDataSetChanged();
                    }else{
                        userAlbumCollectionsOfShow = (UserAlbumCollections)msg.obj;
                        userAlbumListAdapterOfShow = new UserAlbumListAdapter(UserAlbumListAllGen.this,R.layout.user_album_type_list_item, userAlbumCollectionsOfShow);
                        gvUserAlbumListOfAll.setAdapter(userAlbumListAdapterOfShow);
                        gvUserAlbumListOfAll.setOnItemClickListener(new GridViewItemClick());
                        pullToRefreshView.setOnHeaderRefreshListener(UserAlbumListAllGen.this);
                        pullToRefreshView.setOnFooterRefreshListener(UserAlbumListAllGen.this);
                    }

                    pullToRefreshView.setLastUpdated(new Date().toLocaleString());
                    break;

                case ERROR_GET:
                    ToastObject.Show(UserAlbumListAllGen.this, getString(R.string.message_load_failure));
                    break;

                default:
                    System.out.println("nothing to do");
                    break;
            }
        }
    }

    /**
     * 取得相册分类列表的回调处理类
     */
    private class UserAlbumTypeGetListHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];
            switch(httpClientStatus){
                case START_GET:
                    break;
                case FINISH_GET:
                    DefaultGen.globalUserAlbumTypeCollections = (UserAlbumTypeCollections)msg.obj;
                    LoadUserAlbumTypeList();
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
                if(userAlbumCollectionsOfShow.size() == PageSize){ //只有当前页的数据等于每页显示数时，才进行加载
                    PageIndex++;
                    LoadData(nowUserAlbumTypeId, PageIndex,PageSize);
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
                if(userAlbumCollectionsOfShow != null){
                    userAlbumCollectionsOfShow.clear();
                }
                LoadData(nowUserAlbumTypeId, PageIndex,PageSize);
            }
        },1000);
    }


    private class GridViewItemClick implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id){
            //点击操作 
            //Intent intent = new Intent();
            ImagePositionsOfAll = position;//图片的位置 
            BaseGen.USER_ALBUM_PIC_LIST_SHOW_MODULE = 1;
            //ToastObject.Show(UserAlbumListOfMineGen.this, Integer.toString(ImagePositionsOfMine));
            Intent intent = new Intent(UserAlbumListAllGen.this, UserAlbumPicListGen.class);
            //intent.setClass(UserAlbumListOfMineGen.this,UserAlbumPicListGen.class);
            startActivity(intent);
        }
    }

}
