package cn.happyz.happyphoto.Gen.User;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import cn.happyz.happyphoto.DataProvider.User.UserAlbum;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumData;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumDataOperateType;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumPic;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumPicCollections;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumPicData;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumPicDataOperateType;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumType;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumTypeCollections;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumTypeData;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumTypeListAdapter;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.MainActivity;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.FileObject;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ImageObject;
import cn.happyz.happyphoto.Tools.ToastObject;

/**
 * Created by zcmzc on 13-12-20.
 */
public class UserAlbumCreateActivity extends BaseGen {


    private Button btnSelectPicFromGallery;
    private Button btnTakeNewPhoto;
    boolean hasSDCard = super.CheckExternalStorage();
    private int nowPhotoNumber = 1;
    Bitmap sourcePic;
    Bitmap thumbPic;
    private TextView txtUserAlbumName;
    private TextView txtUserAlbumIntro;
    private boolean checkUserAlbumType = false;
    public UserAlbumPicCollections uapcOfUserAlbumCreate;
    private LinearLayout linearLayout;
    private ProgressDialog progressDialog;
    private int loadImageByCamera = 1;
    private int loadImageByGallery = 2;
    private int userLogin = 3;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.user_album_create);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.user_album_create_titlebar); //修改title文字

        this.CheckUserLogin();

        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(0);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(MainActivity.globalUserAlbumTypeCollections != null && MainActivity.globalUserAlbumTypeCollections.size()>0){
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

        //判断web端是否有变化
        //int userAlbumTypeUpdated = Integer.parseInt(getString(R.string.updated_user_album_type));
        //userAlbumTypeData.Get(siteId, userAlbumTypeUpdated);
        /////////////////////////////////////////////////////////////////////////

        btnTakeNewPhoto = (Button) findViewById(R.id.btnTakeNewPhoto);
        btnTakeNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //step 1. check SDCard
                if(hasSDCard){

                    File picFile = FileObject.Create(
                            Environment.getExternalStorageDirectory().getPath()+getString(R.string.config_user_album_upload_pic_filepath),
                            getString(R.string.config_user_album_upload_pic_source_filename)
                                    + Integer.toString(nowPhotoNumber) + "." + getString(R.string.config_user_album_upload_pic_fileex));

                    Uri sourcePicUri = Uri.fromFile(picFile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, sourcePicUri);
                    startActivityForResult(intent, loadImageByCamera);
                }else{
                    Toast.makeText(UserAlbumCreateActivity.this, getString(R.string.message_takephoto_nosdcard), Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnSelectPicFromGallery = (Button) findViewById(R.id.btnSelectPicFromGallery);
        btnSelectPicFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, loadImageByGallery);
            }
        });

        txtUserAlbumName = (TextView) findViewById(R.id.txtUserAlbumName);
        txtUserAlbumName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtUserAlbumName.getText().toString().startsWith(getString(R.string.user_album_create_user_album_name_default_value))){
                    txtUserAlbumName.setText("");
                }
            }
        });

        txtUserAlbumIntro = (TextView) findViewById(R.id.txtUserAlbumIntro);
        txtUserAlbumIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtUserAlbumIntro.getText().toString().startsWith(getString(R.string.user_album_create_user_album_intro_default_value))){
                    txtUserAlbumIntro.setText("");
                }
            }
        });

    }

    private void LoadUserAlbumTypeList(){
        if(MainActivity.globalUserAlbumTypeCollections != null && MainActivity.globalUserAlbumTypeCollections.size()>0){
            checkUserAlbumType = true;
            Spinner sp = (Spinner) findViewById(R.id.spUserAlbumType);
            UserAlbumTypeListAdapter userAlbumTypeListAdapter = new UserAlbumTypeListAdapter(UserAlbumCreateActivity.this,R.layout.user_album_type_list_item,MainActivity.globalUserAlbumTypeCollections);
            userAlbumTypeListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            sp.setAdapter(userAlbumTypeListAdapter);
        }
    }


    private void CheckUserLogin(){
        TextView txtUserOp = (TextView) findViewById(R.id.titlebar_btnUserOp);
        boolean userIsLogined = super.UserCheckIsLogined(UserAlbumCreateActivity.this);
        if(userIsLogined){
            //ToastObject.Show(this,Integer.toString(super.GetNowUserId(UserAlbumCreateActivity.this)));
            txtUserOp.setText(R.string.user_album_create_user_album_button_confirm_text);
            txtUserOp.setEnabled(true);
            txtUserOp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //确认上传作品
                    UserAlbumCreateActivity.this.ConfirmToCreateUserAlbum();
                    view.setEnabled(false);
                }
            });
        }else{
            Intent intent = new Intent(UserAlbumCreateActivity.this, UserLoginActivity.class);
            startActivityForResult(intent, userLogin);
            this.finish();
            //txtUserOp.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            if(requestCode == userLogin){
                this.CheckUserLogin();
                return;
            }

            String filePath = Environment.getExternalStorageDirectory()+ getString(R.string.config_user_album_upload_pic_filepath);
            String sourceFileName = "";
            String sourceFilePath = "";
            sourceFileName =
                    getString(R.string.config_user_album_upload_pic_source_filename)
                            + Integer.toString(nowPhotoNumber) + "."+getString(R.string.config_user_album_upload_pic_fileex)
            ;

            if(requestCode == loadImageByCamera){
                sourceFilePath = filePath + sourceFileName;
            }else if(requestCode == loadImageByGallery && null != data){
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                sourceFilePath = cursor.getString(columnIndex);
                //sourceFilePath = sourceFileName;
                cursor.close();
            }

            if(!sourceFileName.equals("") && !(sourceFilePath != null ? sourceFilePath.equals("") : false)){

                /////////////////////////////////////////////////////////////////////
                ///////////////////////处理缩略图//////////////////////////
                /////////////////////////////////////////////////////////////////////

                BitmapFactory.Options optionsOfThumb = new BitmapFactory.Options();
                optionsOfThumb.inJustDecodeBounds = true;
                sourcePic = BitmapFactory.decodeFile(sourceFilePath , optionsOfThumb);
                optionsOfThumb.inSampleSize = ImageObject.CalculateInSampleSize(optionsOfThumb, 150);
                optionsOfThumb.inJustDecodeBounds = false;
                int degreeOfThumb = ImageObject.GetDegree(sourceFilePath);

                thumbPic = BitmapFactory.decodeFile(sourceFilePath, optionsOfThumb);
                //int degreeOfThumbPic = ImageObject.GetDegree(sourceFilePath);
                thumbPic = ImageObject.Rotate(degreeOfThumb, thumbPic);
                String thumbFileName =
                        getString(R.string.config_user_album_upload_pic_thumb_filename)
                                + Integer.toString(nowPhotoNumber) + "."+getString(R.string.config_user_album_upload_pic_fileex)
                        ;
                File fileThumb = FileObject.Create(filePath, thumbFileName);
                try{
                    FileOutputStream outThumbFile = new FileOutputStream(fileThumb);
                    if (thumbPic.compress(Bitmap.CompressFormat.JPEG, 100, outThumbFile))
                    {
                        outThumbFile.flush();
                        outThumbFile.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                /////////////////////////////////////////////////////////////////////
                ///////////////////////处理原图//////////////////////////
                /////////////////////////////////////////////////////////////////////
                BitmapFactory.Options optionsOfSource = new BitmapFactory.Options();
                optionsOfSource.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(sourceFilePath , optionsOfSource);
                optionsOfSource.inSampleSize = ImageObject.CalculateInSampleSize(optionsOfSource, 800);
                optionsOfSource.inJustDecodeBounds = false;
                sourcePic = BitmapFactory.decodeFile(sourceFilePath, optionsOfSource);
                int degreeOfSource = ImageObject.GetDegree(sourceFilePath);

                sourcePic = ImageObject.Rotate(degreeOfSource, sourcePic);

                File fileSource = FileObject.Create(filePath, sourceFileName);
                try{
                    FileOutputStream outSourceFile = new FileOutputStream(fileSource);
                    if (sourcePic.compress(Bitmap.CompressFormat.JPEG, 100, outSourceFile))
                    {
                        outSourceFile.flush();
                        outSourceFile.close();

                        nowPhotoNumber++;
                        ///////////////////////////////添加到相册图片集合对象//////////////////////////////
                        int userId = super.GetNowUserId(UserAlbumCreateActivity.this);
                        UserAlbumPic userAlbumPic = new UserAlbumPic(filePath+sourceFileName);
                        userAlbumPic.setUserId(userId);
                        this.AddToHorizontalScrollView(userAlbumPic);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        ImageObject.DestoryBitmap(sourcePic);
        ImageObject.DestoryBitmap(thumbPic);
        super.onDestroy();
    }


    /**
     * 确认提交相册数据
     */
    private void ConfirmToCreateUserAlbum(){
        //检查标题
        String userAlbumName = txtUserAlbumName.getText().toString().trim();
        if(userAlbumName == null || userAlbumName.equals("") || userAlbumName.startsWith(getString(R.string.user_album_create_user_album_name_default_value))){
            //标题不能为空，也不能等于默认值
            ToastObject.Show(UserAlbumCreateActivity.this,getString(R.string.message_user_album_create_useralbumname_is_null));
            TextView txtUserOp = (TextView) findViewById(R.id.titlebar_btnUserOp);
            txtUserOp.setEnabled(true);
            return;
        }
        if(!checkUserAlbumType){
            //未读取到作品分类
            ToastObject.Show(UserAlbumCreateActivity.this,getString(R.string.message_user_album_create_useralbumtype_is_null));
            TextView txtUserOp = (TextView) findViewById(R.id.titlebar_btnUserOp);
            txtUserOp.setEnabled(true);
            return;
        }
        if(uapcOfUserAlbumCreate == null || uapcOfUserAlbumCreate.size()<=0){
            ToastObject.Show(UserAlbumCreateActivity.this,getString(R.string.message_user_album_create_useralbumpic_is_null));
            TextView txtUserOp = (TextView) findViewById(R.id.titlebar_btnUserOp);
            txtUserOp.setEnabled(true);
            return;
        }
        String userAlbumIntro = txtUserAlbumIntro.getText().toString().trim();
        if(userAlbumIntro.startsWith(getString(R.string.user_album_create_user_album_intro_default_value))){
            userAlbumIntro = "";
        }

        Spinner sp = (Spinner) findViewById(R.id.spUserAlbumType);
        UserAlbumType userAlbumType = (UserAlbumType)sp.getSelectedItem();
        int userAlbumTypeId = userAlbumType.getUserAlbumTypeId();
        int userId = super.GetNowUserId(UserAlbumCreateActivity.this);
        int siteId = Integer.parseInt(getString(R.string.config_siteid));

        String httpAlbumUploadUrl = getString(R.string.config_user_album_create_url);
        UserAlbumCreateHandler userAlbumCreateHandler = new UserAlbumCreateHandler();
        UserAlbumData userAlbumData = new UserAlbumData(httpAlbumUploadUrl,userAlbumCreateHandler);
        UserAlbum userAlbum = new UserAlbum(siteId, userId, userAlbumName, userAlbumTypeId);
        userAlbum.setUserAlbumIntro(userAlbumIntro);
        userAlbumData.setUserAlbum(userAlbum);
        UserAlbumDataOperateType userAlbumDataOperateType = UserAlbumDataOperateType.Create;
        userAlbumData.RequestFromHttp(userAlbumDataOperateType);

    }

    //Dialog中确定按钮的监听器
    private class ProgressDialogButtonListener implements android.content.DialogInterface.OnClickListener{
        public void onClick(DialogInterface dialog, int which) {
            //点击“确定按钮”取消对话框
            //dialog.cancel();
            if(uapcOfUserAlbumCreate.size() == 0){
                //ToastObject.Show(UserAlbumCreateActivity.this, getString(R.string.message_upload_success));
                //progressDialog.setMessage(getString(R.string.message_upload_success));
                //转到我的作品
                Intent intent = new Intent(UserAlbumCreateActivity.this, UserAlbumListOfMineActivity.class);
                startActivity(intent);
                //finish();
            }
        }

    }

    /**
     * 创建（上传）相册图片
     * @param userAlbumId
     */
    private void CreateUserAlbumPic(int userAlbumId, UserAlbumPicCollections userAlbumPicCollections){

        String httpAlbumUploadUrl = getString(R.string.config_user_album_pic_create_url);
        String picFilePath = Environment.getExternalStorageDirectory()+ getString(R.string.config_user_album_upload_pic_filepath);

        if(userAlbumPicCollections.size()>0){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            UserAlbumPic userAlbumPic = userAlbumPicCollections.get(0);
            if(userAlbumPic != null){
                userAlbumPic.setUserAlbumId(userAlbumId);
                UserAlbumPicCreateHandler userAlbumPicCreateHandler = new UserAlbumPicCreateHandler();
                UserAlbumPicData userAlbumPicData = new UserAlbumPicData(httpAlbumUploadUrl,userAlbumPicCreateHandler);
                userAlbumPicData.setUserAlbumPic(userAlbumPic);
                userAlbumPicData.RequestFromHttp(UserAlbumPicDataOperateType.Create);
            }
        }
    }

    /**
     * 创建相册的回调处理类
     */
    private class UserAlbumCreateHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];
            switch(httpClientStatus){
                case START_POST:
                    ToastObject.Show(UserAlbumCreateActivity.this, getString(R.string.message_upload_begin));
                    break;
                case FINISH_POST:
                    //传照片
                    int userAlbumId = Integer.parseInt(msg.obj.toString());
                    if(userAlbumId>0){
                        progressDialog = new ProgressDialog(UserAlbumCreateActivity.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setTitle(getString(R.string.user_album_create_progressdialog_title));
                        progressDialog.setMessage(getString(R.string.user_album_create_progressdialog_content));
                        progressDialog.setIndeterminate(false);// false代表根据程序进度确切的显示进度
                        progressDialog.setMax(uapcOfUserAlbumCreate.size());
                        //progressDialog.setButton(ProgressDialog.BUTTON_NEUTRAL, getString(R.string.user_album_create_progressdialog_button_text), new ProgressDialogButtonListener());
                        progressDialog.setCancelable(false); // 设置ProgressDialog 是否可以按退回按键取消
                        progressDialog.setCanceledOnTouchOutside(false);
                        //Button btnOfProgressDialog = progressDialog.getButton(ProgressDialog.BUTTON_NEUTRAL);
                        //btnOfProgressDialog.setVisibility(View.INVISIBLE);
                        //btnOfProgressDialog.setEnabled(false);
                        progressDialog.show();

                        UserAlbumCreateActivity.this.CreateUserAlbumPic(userAlbumId, uapcOfUserAlbumCreate);
                    }
                    //ToastObject.Show(UserAlbumCreateActivity.this, getString(R.string.message_upload_success));
                    break;
                case ERROR_POST:
                    ToastObject.Show(UserAlbumCreateActivity.this, getString(R.string.message_upload_failure));
                    break;
                default:
                    System.out.println("nothing to do");
                    break;
            }
        }
    }

    private class UserAlbumPicCreateHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];
            switch(httpClientStatus){
                case START_POST:
                    //ToastObject.Show(UserAlbumCreateActivity.this, getString(R.string.message_upload_begin));
                    break;
                case FINISH_POST:
                    progressDialog.incrementProgressBy(1);
                    //Button btnOfProgressDialog = progressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE);
                    //btnOfProgressDialog.setEnabled(false);
                    UserAlbumPic userAlbumPic = (UserAlbumPic) msg.obj;
                    uapcOfUserAlbumCreate.remove(userAlbumPic);
                    if(uapcOfUserAlbumCreate.size()>0){
                        int userAlbumId = userAlbumPic.getUserAlbumId();
                        UserAlbumCreateActivity.this.CreateUserAlbumPic(userAlbumId, uapcOfUserAlbumCreate);
                    }else{
                        //全部完成
                        //btnOfProgressDialog.setEnabled(true);
                        progressDialog.setMessage(getString(R.string.message_upload_success));
                        //转到我的作品
                        Intent intent = new Intent(UserAlbumCreateActivity.this, UserAlbumListOfMineActivity.class);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();
                    }
                    break;
                case ERROR_POST:
                    ToastObject.Show(UserAlbumCreateActivity.this, getString(R.string.message_upload_failure));
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
                    MainActivity.globalUserAlbumTypeCollections = (UserAlbumTypeCollections)msg.obj;
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

    /**
     * 将照片加入到滑动视图
     */
    private void AddToHorizontalScrollView(UserAlbumPic userAlbumPic){
        if(uapcOfUserAlbumCreate == null){
            uapcOfUserAlbumCreate = new UserAlbumPicCollections();
        }
        if(uapcOfUserAlbumCreate.size()==0){
            //第一张图片设为封面图
            Integer isCover = 1;
            userAlbumPic.setIsCover(isCover);
        }else{
            Integer isCover = 0;
            userAlbumPic.setIsCover(isCover);
        }
        uapcOfUserAlbumCreate.add(userAlbumPic);

        linearLayout = (LinearLayout) findViewById(R.id.llSelectedPics);

        //////////////////////////////////////////////////////////////
        RelativeLayout rl = new RelativeLayout(UserAlbumCreateActivity.this);
        rl.setRight(10);
        rl.setId(100);

        ImageView iv = new ImageView(UserAlbumCreateActivity.this);
        iv.setMaxWidth(150);
        iv.setMaxHeight(200);
        iv.setImageBitmap(thumbPic);
        iv.setId(1);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp1.addRule(RelativeLayout.ALIGN_RIGHT, RelativeLayout.TRUE);
        lp1.setMargins(10,10,10,10);
        // btn1 位于父 View 的顶部，在父 View 中水平居中
        rl.addView(iv, lp1);

        Button btn = new Button(UserAlbumCreateActivity.this);
        btn.setText(getString(R.string.user_album_create_user_album_button_cancel_text));
        //btn.setBackgroundColor(Color.parseColor("#CA0000"));
        //btn.setTextColor(Color.parseColor("#FFFFFF"));
        btn.setTextAppearance(UserAlbumCreateActivity.this, R.style.ButtonStyle);
        btn.setOnClickListener(new CancelUserAlbumPicListener(userAlbumPic));

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.addRule(RelativeLayout.BELOW, 1);
        lp2.addRule(RelativeLayout.ALIGN_LEFT, 1);
        rl.addView(btn, lp2);

        /////////////////////////////////////////////////////////////

        linearLayout.addView(rl);
    }

    /**
     * 点击取消按钮时的监听类
     */
    private class CancelUserAlbumPicListener implements View.OnClickListener {
        private UserAlbumPic _userAlbumPic;
        public CancelUserAlbumPicListener(UserAlbumPic userAlbumPic){
            _userAlbumPic = userAlbumPic;
        }

        @Override
        public void onClick(View view) {
            RelativeLayout rlParent = (RelativeLayout)view.getParent();
            LinearLayout ll = (LinearLayout)rlParent.getParent();
            ll.removeView(rlParent);
            //从照片相册对象集合删除
            uapcOfUserAlbumCreate.remove(_userAlbumPic);
        }
    }
}
