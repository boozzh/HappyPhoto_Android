package cn.happyz.happyphoto.Gen.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.happyz.happyphoto.DefaultGen;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.R;

/**
 * Created by zcmzc on 13-12-24.
 */
public class UserInfoGen extends BaseGen {

    private ImageButton btnBack;
    private Button btnLogout;
    private TextView txtUserOp;
    private TextView textViewOfUserPoint;
    private ImageButton ibtnUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.user_info);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.user_info_title); //修改title文字

        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(0);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoGen.this, DefaultGen.class);
                startActivity(intent);
                finish();
            }
        });

        txtUserOp = (TextView) findViewById(R.id.titlebar_btnUserOp);
        ibtnUserInfo = (ImageButton) findViewById(R.id.ibtnUserInfo);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                sp.edit().clear().commit();

                txtUserOp.setVisibility(View.VISIBLE);
                ibtnUserInfo.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(UserInfoGen.this, DefaultGen.class);
                startActivity(intent);
                finish();
            }
        });

        textViewOfUserPoint = (TextView) findViewById(R.id.textViewOfUserPoint);
        int nowUserPoint = super.GetNowUserPoint(this);
        textViewOfUserPoint.setText(Integer.toString(nowUserPoint));
    }
}
