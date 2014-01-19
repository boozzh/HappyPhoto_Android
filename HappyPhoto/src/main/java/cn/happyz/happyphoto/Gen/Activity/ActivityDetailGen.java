package cn.happyz.happyphoto.Gen.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.happyz.happyphoto.DataProvider.Activity.Activity;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.R;

/**
 * Created by zcmzc on 14-1-15.
 */
public class ActivityDetailGen extends BaseGen {

    private ImageButton btnBack;
    private WebView webViewOfActivityDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_detail);
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

        LoadData();
    }

    private void LoadData(){
        if(ActivityListGen.activityCollectionsOfListAll != null && ActivityListGen.activityCollectionsOfListAll.size()>0){
            Activity activity = ActivityListGen.activityCollectionsOfListAll.get(ActivityListGen.activityPositionsOfListAll);
            if(activity != null){
                webViewOfActivityDetail = (WebView) findViewById(R.id.webViewOfActivityDetail);
                webViewOfActivityDetail.getSettings().setJavaScriptEnabled(true);
                String encoding = "UTF-8";
                String mimeType = "text/html";
                final String activityContent = activity.getActivityContent();
                webViewOfActivityDetail.loadDataWithBaseURL("file://", activityContent,mimeType, encoding, "about:blank");
            }
        }
    }

}
