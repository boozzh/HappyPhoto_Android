package cn.happyz.happyphoto.DataProvider.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.happyz.happyphoto.Gen.Activity.ActivityDetailGen;
import cn.happyz.happyphoto.Gen.Activity.ActivityListGen;
import cn.happyz.happyphoto.Gen.Activity.ActivityListOfMineJoinedGen;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.AsyncImageLoader;
import cn.happyz.happyphoto.Tools.FormatObject;
import cn.happyz.happyphoto.Tools.HttpClientStatus;

/**
 * Created by zcmzc on 14-2-1.
 */
public class ActivityListOfMineJoinedAdapter extends ArrayAdapter<Activity> {
    private ActivityCollections _activityCollections;
    private Context _context;
    private int _resource;

    public ActivityListOfMineJoinedAdapter(Context context,int resource,ActivityCollections activityCollections) {
        super(context, resource, activityCollections);
        this._context = context;
        this._resource = resource;
        this._activityCollections = activityCollections;
    }

    /**
     *
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return this.LoadData(position,convertView,parent);
    }

    private View LoadData(int position, View convertView, ViewGroup parent){
        ActivityListOfMineJoinedGen.activityPositionsOfMineJoined = position;
        LayoutInflater layoutInflater = LayoutInflater.from(_context);
        final LinearLayout linearLayout = (LinearLayout)layoutInflater.inflate(_resource, null);
        if(linearLayout != null)
        {
            linearLayout.setPadding(10,10,10,10);

            String titlePic = _activityCollections.get(position).getTitlePic();
            if(!"".equals(titlePic)){
                final ImageView ivTitlePicOfListItem = new ImageView(linearLayout.getContext());
                ViewGroup.LayoutParams imageParam = new ViewGroup.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT);
                ivTitlePicOfListItem.setLayoutParams(imageParam);
                ivTitlePicOfListItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                final FrameLayout frameLayout1 = new FrameLayout(linearLayout.getContext());
                FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FormatObject.DipToPx(linearLayout.getContext(), 150));
                frameLayout1.setBackgroundColor(Color.parseColor("#efefef"));
                frameLayout1.setPadding(
                        FormatObject.DipToPx(linearLayout.getContext(),2)
                        ,FormatObject.DipToPx(linearLayout.getContext(),2)
                        ,FormatObject.DipToPx(linearLayout.getContext(),2)
                        ,FormatObject.DipToPx(linearLayout.getContext(),2));
                params1.gravity = Gravity.CENTER;
                frameLayout1.setLayoutParams(params1);
                final FrameLayout frameLayout2 = new FrameLayout(linearLayout.getContext());
                FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT);
                frameLayout2.setBackgroundColor(Color.parseColor("#333333"));
                params2.gravity = Gravity.CENTER;
                frameLayout2.setLayoutParams(params2);
                AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
                asyncImageLoader.loadDrawable(titlePic, new AsyncImageLoader.ImageCallback() {
                    public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                        ivTitlePicOfListItem.setImageDrawable(imageDrawable);

                        ivTitlePicOfListItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(linearLayout.getContext(), ActivityDetailGen.class);
                                linearLayout.getContext().startActivity(intent);
                            }
                        });

                        frameLayout2.addView(ivTitlePicOfListItem);
                        frameLayout1.addView(frameLayout2);
                        linearLayout.addView(frameLayout1);

                        LinearLayout linearLayoutOfButton = new LinearLayout(linearLayout.getContext());
                        int weight = 1;

                        Button buttonViewMyPhoto = new Button(linearLayout.getContext());
                        buttonViewMyPhoto.setText(R.string.activity_view_user_album);
                        buttonViewMyPhoto.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, weight));
                        buttonViewMyPhoto.setBackgroundColor(Color.parseColor("#efefef"));
                        buttonViewMyPhoto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //查看参赛作品
                            }
                        });

                        Button buttonCancel = new Button(linearLayout.getContext());
                        buttonCancel.setText(R.string.activity_cancel);

                        buttonCancel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, weight));
                        buttonCancel.setBackgroundColor(Color.parseColor("#efefef"));
                        buttonCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //取消报名
                                int userId = ActivityListOfMineJoinedGen.nowUserId;
                                String userName = ActivityListOfMineJoinedGen.nowUserName;
                                String userPass = ActivityListOfMineJoinedGen.nowUserPass;

                                String activityUserDeleteUrl = linearLayout.getContext().getString(R.string.config_activity_user_delete_url);

                                int acitivtyId = _activityCollections.get(ActivityListOfMineJoinedGen.activityPositionsOfMineJoined).getActivityId();

                                activityUserDeleteUrl = activityUserDeleteUrl.replace("{user_id}", Integer.toString(userId));
                                activityUserDeleteUrl = activityUserDeleteUrl.replace("{site_id}", linearLayout.getContext().getString(R.string.config_siteid));
                                activityUserDeleteUrl = activityUserDeleteUrl.replace("{user_name}", userName);
                                activityUserDeleteUrl = activityUserDeleteUrl.replace("{user_pass}", userPass);
                                activityUserDeleteUrl = activityUserDeleteUrl.replace("{activity_id}", Integer.toString(acitivtyId));

                                ActivityUserDeleteHandler activityUserDeleteHandler = new ActivityUserDeleteHandler();
                                ActivityUserData activityUserData = new ActivityUserData(activityUserDeleteUrl,activityUserDeleteHandler);
                                activityUserData.GetDataFromHttp(ActivityUserDataOperateType.Delete);

                                String activityAlbumDeleteUrl = linearLayout.getContext().getString(R.string.config_activity_album_delete_url);

                                activityAlbumDeleteUrl = activityAlbumDeleteUrl.replace("{user_id}", Integer.toString(userId));
                                activityAlbumDeleteUrl = activityAlbumDeleteUrl.replace("{site_id}", linearLayout.getContext().getString(R.string.config_siteid));
                                activityAlbumDeleteUrl = activityAlbumDeleteUrl.replace("{user_name}", userName);
                                activityAlbumDeleteUrl = activityAlbumDeleteUrl.replace("{user_pass}", userPass);
                                activityAlbumDeleteUrl = activityAlbumDeleteUrl.replace("{activity_id}", Integer.toString(acitivtyId));

                                ActivityAlbumDeleteHandler activityAlbumDeleteHandler = new ActivityAlbumDeleteHandler();
                                ActivityAlbumData activityAlbumData = new ActivityAlbumData(activityAlbumDeleteUrl,activityAlbumDeleteHandler);
                                activityAlbumData.GetDataFromHttp(ActivityAlbumDataOperateType.Delete);

                            }
                        });

                        linearLayoutOfButton.addView(buttonCancel);
                        linearLayoutOfButton.addView(buttonViewMyPhoto);

                        linearLayout.addView(linearLayoutOfButton);
                    }
                });
            }

            TextView tvActivitySubjectOfListItem= new TextView(linearLayout.getContext());
            String activitySubject = _activityCollections.get(position).getActivitySubject();
            tvActivitySubjectOfListItem.setText(activitySubject);
            tvActivitySubjectOfListItem.setTextSize(20);
            tvActivitySubjectOfListItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(linearLayout.getContext(), ActivityDetailGen.class);
                    linearLayout.getContext().startActivity(intent);
                }
            });
            linearLayout.addView(tvActivitySubjectOfListItem);
        }
        return linearLayout;
    }

    private class ActivityUserDeleteHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];
            switch (httpClientStatus) {
                case START_GET:
                    break;
                case FINISH_GET:
                    ActivityListOfMineJoinedGen.activityCollectionsOfMineJoined.remove(ActivityListGen.activityPositionsOfListAll);
                    ActivityListOfMineJoinedAdapter.this.notifyDataSetChanged();
                    break;
                case ERROR_GET:
                    break;
                default:
                    break;
            }
        }
    }

    private class ActivityAlbumDeleteHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];
            switch (httpClientStatus) {
                case START_GET:
                    break;
                case FINISH_GET:
                    break;
                case ERROR_GET:
                    break;
                default:
                    break;
            }
        }
    }
}
