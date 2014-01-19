package cn.happyz.happyphoto.DataProvider.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import cn.happyz.happyphoto.Gen.Activity.ActivityAlbumSelectGen;
import cn.happyz.happyphoto.Gen.Activity.ActivityDetailGen;
import cn.happyz.happyphoto.Gen.Activity.ActivityListGen;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.AsyncImageLoader;
import cn.happyz.happyphoto.Tools.FormatObject;
import cn.happyz.happyphoto.Tools.ToastObject;

/**
 * Created by zcmzc on 14-1-15.
 */
public class ActivityListAdapter extends ArrayAdapter<Activity> {
    private ActivityCollections _activityCollections;
    private Context _context;
    private int _resource;

    public ActivityListAdapter(Context context,int resource,ActivityCollections activityCollections) {
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
        ActivityListGen.activityPositionsOfListAll = position;

        LayoutInflater layoutInflater = LayoutInflater.from(_context);
        //convertView = layoutInflater.inflate(_resource, null);
        final LinearLayout linearLayout = (LinearLayout)layoutInflater.inflate(_resource, null);
        if(linearLayout != null)
        {
            //linearLayout.setBackgroundColor(Color.parseColor("#333333"));
            linearLayout.setPadding(10,10,10,10);

            String titlePic = _activityCollections.get(position).getTitlePic();
            if(!"".equals(titlePic)){
                final ImageView ivTitlePicOfListItem = new ImageView(linearLayout.getContext());
                //ivTitlePicOfListItem.setTag(position);
                ViewGroup.LayoutParams imageParam = new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
                ivTitlePicOfListItem.setLayoutParams(imageParam);
                ivTitlePicOfListItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                final FrameLayout frameLayout1 = new FrameLayout(linearLayout.getContext());
                FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FormatObject.DipToPx(linearLayout.getContext(), 150));
                frameLayout1.setBackgroundColor(Color.parseColor("#efefef"));
                frameLayout1.setPadding(
                        FormatObject.DipToPx(linearLayout.getContext(),2)
                        ,FormatObject.DipToPx(linearLayout.getContext(),2)
                        ,FormatObject.DipToPx(linearLayout.getContext(),2)
                        ,FormatObject.DipToPx(linearLayout.getContext(),2));
                params1.gravity = Gravity.CENTER;
                frameLayout1.setLayoutParams(params1);
                final FrameLayout frameLayout2 = new FrameLayout(linearLayout.getContext());
                FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
                frameLayout2.setBackgroundColor(Color.parseColor("#333333"));
                //frameLayout2.setPadding(3,3,3,3);
                params2.gravity = Gravity.CENTER;
                frameLayout2.setLayoutParams(params2);
                AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
                asyncImageLoader.loadDrawable(titlePic, new AsyncImageLoader.ImageCallback() {
                    public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                        ivTitlePicOfListItem.setImageDrawable(imageDrawable);

                        ivTitlePicOfListItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int nowPosition = Integer.parseInt(view.getTag().toString());
                                ActivityListGen.activityPositionsOfListAll = nowPosition;
                                Intent intent = new Intent(linearLayout.getContext(), ActivityDetailGen.class);
                                linearLayout.getContext().startActivity(intent);
                            }
                        });

                        frameLayout2.addView(ivTitlePicOfListItem);
                        frameLayout1.addView(frameLayout2);
                        linearLayout.addView(frameLayout1);

                        Button button = new Button(linearLayout.getContext());
                        button.setText(R.string.activity_join);
                        button.setBackgroundColor(Color.parseColor("#efefef"));
                        //button.setTag(ivTitlePicOfListItem.getTag());
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //int nowPosition = Integer.parseInt(view.getTag().toString());
                                //ToastObject.Show(linearLayout.getContext(), Integer.toString(nowPosition));

                                Intent intent = new Intent(linearLayout.getContext(), ActivityAlbumSelectGen.class);
                                linearLayout.getContext().startActivity(intent);
                            }
                        });
                        linearLayout.addView(button);
                    }
                });
            }

            //TextView tvUserAlbumIdOfListItem=(TextView)convertView.findViewWithTag("tvUserAlbumIdOfListItem");
            TextView tvActivitySubjectOfListItem= new TextView(linearLayout.getContext());
            //TextView tvUserAlbumNameOfListItem=(TextView)convertView.findViewById(R.id.tvUserAlbumNameOfListItem);
            //int userAlbumId = _userAlbumCollections.get(position).getUserAlbumId();
            String activitySubject = _activityCollections.get(position).getActivitySubject();
            tvActivitySubjectOfListItem.setText(activitySubject);
            tvActivitySubjectOfListItem.setTextSize(20);
            //tvActivitySubjectOfListItem.setTag(position);
            tvActivitySubjectOfListItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //int nowPosition = Integer.parseInt(view.getTag().toString());
                    //ActivityListGen.activityPositionsOfListAll = nowPosition;
                    Intent intent = new Intent(linearLayout.getContext(), ActivityDetailGen.class);
                    linearLayout.getContext().startActivity(intent);
                }
            });
            //tvUserAlbumIdOfListItem.setTextColor(Color.parseColor("#ffffff"));
            linearLayout.addView(tvActivitySubjectOfListItem);
            //tvUserAlbumNameOfListItem.setText(userAlbumName);
        }
        return linearLayout;
    }
}
