package cn.happyz.happyphoto.Gen.User;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import cn.happyz.happyphoto.DataProvider.User.UserAlbum;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumPicCollections;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumPicData;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumPicDataOperateType;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.AsyncImageLoader;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ImageObject;
import cn.happyz.happyphoto.Tools.ToastObject;

/**
 * Created by zcmzc on 14-1-1.
 */
public class UserAlbumPicListGen extends BaseGen implements GestureDetector.OnGestureListener {

    private ImageButton btnBack;

    private static ViewFlipper vfUserAlbumPicListOfList;
    private static final int FLING_MIN_DISTANCE = 120;//移动最小距离
    private static final int FLING_MIN_VELOCITY = 200;//移动最大速度
    private static final int ID_DELETE =1;
    //private static final int ID_SETWALL=2;
    private static final int ID_SHARE=3;
    private static final int ID_ZOOM=4;
    private static final int ID_REDUCE=5;
    //private static final int ID_ROTATE=6;
    //private static int rotate=0;
    private int position;
    GestureDetector gestureDetector;
    private ImageButton ibtnDownloadPic;
    private ImageButton ibtnBackToUserAlbumPicList;
    UserAlbumPicCollections uapcOfList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.user_album_pic_list);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.user_album_pic_titlebar);

        ibtnDownloadPic = (ImageButton) findViewById(R.id.ibtnDownloadPic);
        ibtnDownloadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //保存图片
                DownloadPic();
            }
        });
        ibtnBackToUserAlbumPicList = (ImageButton) findViewById(R.id.ibtnBackToUserAlbumPicList);
        ibtnBackToUserAlbumPicList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //保存图片
                finish();
            }
        });

        //取得数据
        gestureDetector = new GestureDetector(this, this);
        vfUserAlbumPicListOfList = (ViewFlipper) findViewById(R.id.vfUserAlbumPicListOfList);
        //int imagePositionsOfMine = UserAlbumListOfMineGen.ImagePositionsOfMine;

        //来源页面，有多个来源页面时，进行来源的区分
        UserAlbum userAlbum = null;
        if(BaseGen.USER_ALBUM_PIC_LIST_SHOW_MODULE == 1){ //全部数据
            userAlbum = UserAlbumListAllGen.userAlbumCollectionsOfShow.get(UserAlbumListAllGen.ImagePositionsOfAll);
        }else if(BaseGen.USER_ALBUM_PIC_LIST_SHOW_MODULE == 2){ //分类数据
            userAlbum = UserAlbumListAllGen.userAlbumCollectionsOfShow.get(UserAlbumListAllGen.ImagePositionsOfType);
        }else if(BaseGen.USER_ALBUM_PIC_LIST_SHOW_MODULE == 3){ //个人数据
            userAlbum = UserAlbumListOfMineGen.userAlbumCollectionsOfMine.get(UserAlbumListOfMineGen.ImagePositionsOfMine);
        }
        if(userAlbum != null){
            int userAlbumId = userAlbum.getUserAlbumId();
            if(userAlbumId > 0){
                String userAlbumPicGetListUrl = getString(R.string.config_user_album_pic_getlist_url);
                UserAlbumPicListHandler userAlbumPicListHandler = new UserAlbumPicListHandler();
                UserAlbumPicData userAlbumPicData = new UserAlbumPicData(userAlbumPicGetListUrl,userAlbumPicListHandler);
                userAlbumPicData.setUserAlbum(userAlbum);
                userAlbumPicData.RequestFromHttp(UserAlbumPicDataOperateType.GetList);
            }
        }
    }

    private void DownloadPic(){
        if(vfUserAlbumPicListOfList.getChildCount()>0 && uapcOfList.size() == vfUserAlbumPicListOfList.getChildCount()){
            final int nowIndex = Integer.parseInt(vfUserAlbumPicListOfList.getCurrentView().getTag().toString());
            String userAlbumPicUrl = getString(R.string.config_site_url) + "/" + uapcOfList.get(nowIndex).getUserAlbumPicUrl();
            if(!"".equals(userAlbumPicUrl)){
                ToastObject.Show(UserAlbumPicListGen.this,getString(R.string.user_album_pic_list_begin_save_pic));
                AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
                asyncImageLoader.loadDrawable(userAlbumPicUrl, new AsyncImageLoader.ImageCallback() {
                    public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                        if(imageDrawable != null){
                            Bitmap bitmap = ImageObject.DrawableToBitmap(imageDrawable);
                            ContentResolver cr = getContentResolver();
                            String fileName = Integer.toString(uapcOfList.get(nowIndex).getUserAlbumId()); //Long.toString(Calendar.getInstance().getTimeInMillis());
                            String url = MediaStore.Images.Media.insertImage(cr, bitmap, fileName, "");
                            if(url != null){
                                ToastObject.Show(UserAlbumPicListGen.this,getString(R.string.user_album_pic_list_success_save_pic));
                            }else {
                                ToastObject.Show(UserAlbumPicListGen.this,getString(R.string.user_album_pic_list_fail_save_pic));
                            }
                        }
                    }
                });
            }
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    /*用户按下触摸屏、快速移动后松开，由1个MotionEventACTION_DOWN,*多个ACTION_MOVE,1个ACTION_UP触发*/
    //主要方法
    public boolean onFling(MotionEvent e1,MotionEvent e2,float velocityX,  float velocityY){
        if (e1.getX() - e2.getX() > 120) {
            //最后一张图的时候，不处理
            ImageView iv = (ImageView)vfUserAlbumPicListOfList.getCurrentView();
            int nowPosition = Integer.parseInt(iv.getTag().toString());

            if(nowPosition < vfUserAlbumPicListOfList.getChildCount()-1){
                this.vfUserAlbumPicListOfList.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
                this.vfUserAlbumPicListOfList.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
                this.vfUserAlbumPicListOfList.showNext();
                return true;
            }else{
                ToastObject.Show(this,getString(R.string.user_album_pic_list_lasttips));
            }

        } else if (e1.getX() - e2.getX() < -120) {
            //第一张图的时候，不处理
            ImageView iv = (ImageView)vfUserAlbumPicListOfList.getCurrentView();
            int nowPosition = Integer.parseInt(iv.getTag().toString());
            if(nowPosition > 0){
                this.vfUserAlbumPicListOfList.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
                this.vfUserAlbumPicListOfList.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
                this.vfUserAlbumPicListOfList.showPrevious();
                return true;
            }
        }

        return false;
    }
    //////////////////////////////////////////////////////////////下面 方法没用，但是这里必须实现
    public void onLongPress(MotionEvent e){

    }
    /*用户按下触摸屏，并拖动，由1个MotionEventACTION_DOWN,多个ACTION_MOVE 触发*/
    public boolean onScroll(MotionEvent e1,MotionEvent e2,float distanceX,float distanceY){
        return false;
    }
    /*用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEventACTION_DOWN触发注意和onDown()的区别，强调的是没有松开或者拖动的状态*/
    public void onShowPress(MotionEvent e){

    }
    /*用户（轻触触摸屏后）松开，由一个1个MotionEventACTION_UP触发*/
    public boolean onSingleTapUp(MotionEvent e){
        return false;
    }
    public boolean onDown(MotionEvent e){
        return false;
    }
    //菜单的方法
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, ID_DELETE,1,"删除");
        menu.add(0,ID_SHARE,3,"分享");
        menu.add(0,ID_ZOOM,4,"放大");
        menu.add(0,ID_REDUCE,5,"缩小");
        return super.onCreateOptionsMenu(menu);
    }
    //菜单点击事件
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()== ID_DELETE)
            Delete();
        if(item.getItemId()==ID_SHARE)
            Share();
        if(item.getItemId()==ID_ZOOM)
            Zoom();
        if(item.getItemId()==ID_REDUCE)
            Reduce();
        return super.onOptionsItemSelected(item);
    }

    //放大图片的方法
    private void Zoom()  {
        //int Width= ivUserAlbumPicOfList.getWidth()*4/3;
        //int Height= ivUserAlbumPicOfList.getHeight()*4/3;
        //if(Width>=1000||Height>=1000){
       //     ToastObject.Show(this, "已经不能在放大了");
        //}else{
        //    ivUserAlbumPicOfList.setLayoutParams(new LinearLayout.LayoutParams(Width, Height));
        //}
    }

    //缩小图片的方法
    private void Reduce(){
        //int Width= ivUserAlbumPicOfList.getWidth()*3/4;
        //int Height= ivUserAlbumPicOfList.getHeight()*3/4;
        //if(Width<=100||Height<=100){
        //    ToastObject.Show(this, "已经不能在缩小了");
        //}else{
        //    ivUserAlbumPicOfList.setLayoutParams(new LinearLayout.LayoutParams(Width, Height));
        //}
    }

    private void Delete()
    {
        //String path =  GridViewTestActivity.pathsrcs.get(GridViewTestActivity.ImagePositions );
        //System.out.println(path);
        //final File file=new File(path);
        new AlertDialog.Builder(this).setMessage("是否删除？").setPositiveButton("",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){

            //file.delete();
            //Toast.makeText(ShowImage.this,"删除成功！", Toast.LENGTH_LONG).show();
            //Intent intent=new Intent(ShowImage.this,GridViewTestActivity.class);
            //startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
    //图片分享的方法
    private void Share()  {
        //String path =  GridViewTestActivity.pathsrcs.get(GridViewTestActivity.ImagePositions );
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        //File file=new File(path);
        //shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        //shareIntent.setType("idddmagesss/jpeg");
        startActivity(Intent.createChooser(shareIntent, getTitle()));
    }


    private class UserAlbumPicListHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch(httpClientStatus){
                case START_GET:
                    break;

                case FINISH_GET:
                    uapcOfList = (UserAlbumPicCollections)msg.obj;
                    if(uapcOfList != null && uapcOfList.size()>0){

                        //加载图片
                        //position = 0;
                        LoadImage(uapcOfList);

                        //ivUserAlbumPicOfList.setOnTouchListener(UserAlbumPicListGen.this);
                        //ivUserAlbumPicOfList.setLongClickable(true);

                    }
                    break;

                case ERROR_GET:
                    ToastObject.Show(UserAlbumPicListGen.this, "加载失败");
                    break;

                default:
                    System.out.println("nothing to do");
                    break;
            }
        }
    }

    private void LoadImage(UserAlbumPicCollections uapcOfList){
        position = 0;
        for(int i=0;i<uapcOfList.size();i++){
            final ImageView iv = new ImageView(UserAlbumPicListGen.this);
            iv.setTag(position);
            position++;
            String userAlbumPicCompressUrl = getString(R.string.config_site_url) + "/" + uapcOfList.get(i).getUserAlbumPicCompressUrl();
            if(!"".equals(userAlbumPicCompressUrl)){
                AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
                asyncImageLoader.loadDrawable(userAlbumPicCompressUrl, new AsyncImageLoader.ImageCallback() {
                    public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                        //
                        iv.setImageDrawable(imageDrawable);
                        //iv.setId();

                        //ToastObject.Show(UserAlbumPicListGen.this,Integer.toString(ivUserAlbumPicOfList.getWidth()) + "," + Integer.toString(ivUserAlbumPicOfList.getHeight()));
                    }
                });
            }
            vfUserAlbumPicListOfList.addView(iv);
        }
    }

}