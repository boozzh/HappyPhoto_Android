package cn.happyz.happyphoto.Gen.User;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.happyz.happyphoto.AppApplication;
import cn.happyz.happyphoto.DataProvider.User.UserAlbum;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumCollections;
import cn.happyz.happyphoto.Gen.Activity.ActivityAlbumSelectGen;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.AsyncImageLoader;
import cn.happyz.happyphoto.Tools.FormatObject;

/**
 *
 */
public class UserAlbumListForSelectAdapter extends ArrayAdapter<UserAlbum> {
    private UserAlbumCollections _userAlbumCollections;
    private Context _context;
    private int _resource;

    public UserAlbumListForSelectAdapter(Context context, int resource, UserAlbumCollections userAlbumCollections) {
        super(context, resource, userAlbumCollections);
        this._context = context;
        this._resource = resource;
        this._userAlbumCollections = userAlbumCollections;
    }

    /**
     *
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return this.LoadData(position, convertView, parent);
    }

    private View LoadData(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(_context);
        //convertView = layoutInflater.inflate(_resource, null);
        final LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(_resource, null);
        if (linearLayout != null) {
            linearLayout.setBackgroundColor(Color.parseColor("#333333"));
            linearLayout.setPadding(5, 5, 5, 5);

            String coverPicUrl = _context.getString(R.string.config_site_url) + "/" + _userAlbumCollections.get(position).getCoverPicUrl();
            final int nowPosition = position;
            if (!"".equals(coverPicUrl)) {
                final ImageView ivCoverPicUrlOfListItem = new ImageView(linearLayout.getContext());
                ViewGroup.LayoutParams imageParam = new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                ivCoverPicUrlOfListItem.setLayoutParams(imageParam);
                ivCoverPicUrlOfListItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                final FrameLayout frameLayout1 = new FrameLayout(linearLayout.getContext());
                int displayWidth = ((AppApplication)_context.getApplicationContext()).getDisplayWidth();
                //每行三个
                int imageWidth = displayWidth/3 - FormatObject.DipToPx(linearLayout.getContext(), 12); //px

                FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(imageWidth,imageWidth);
                frameLayout1.setBackgroundColor(Color.parseColor("#efefef"));
                //frameLayout1.setPadding(
                //        FormatObject.DipToPx(linearLayout.getContext(), 2)
                //        , FormatObject.DipToPx(linearLayout.getContext(), 2)
                //        , FormatObject.DipToPx(linearLayout.getContext(), 2)
                //        , FormatObject.DipToPx(linearLayout.getContext(), 2));
                params1.gravity = Gravity.CENTER;
                frameLayout1.setLayoutParams(params1);
                final FrameLayout frameLayout2 = new FrameLayout(linearLayout.getContext());
                FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                frameLayout2.setBackgroundColor(Color.parseColor("#333333"));
                //frameLayout2.setPadding(3,3,3,3);
                params2.gravity = Gravity.CENTER;
                frameLayout2.setLayoutParams(params2);
                AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
                asyncImageLoader.loadDrawable(coverPicUrl, new AsyncImageLoader.ImageCallback() {
                    public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                        ivCoverPicUrlOfListItem.setImageDrawable(imageDrawable);
                        frameLayout2.addView(ivCoverPicUrlOfListItem);
                        frameLayout1.addView(frameLayout2);
                        linearLayout.addView(frameLayout1);

                        ivCoverPicUrlOfListItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int userAlbumId = _userAlbumCollections.get(nowPosition).getUserAlbumId();
                                String addToString = '|' + Integer.toString(userAlbumId);
                                int paddingValue;
                                if (ActivityAlbumSelectGen.selectedUserAlbumId != null && ActivityAlbumSelectGen.selectedUserAlbumId.indexOf(addToString) >= 0) {
                                    //已经有这个作品了，进行取消操作
                                    ActivityAlbumSelectGen.selectedUserAlbumId =
                                            ActivityAlbumSelectGen.selectedUserAlbumId.replace(addToString, "");
                                    frameLayout1.setPadding(0,0,0,0);
                                } else {
                                    //还没有这个作品，进行添加操作
                                    if (ActivityAlbumSelectGen.selectedUserAlbumId == null) {
                                        ActivityAlbumSelectGen.selectedUserAlbumId = '|' + Integer.toString(userAlbumId);
                                    } else {
                                        ActivityAlbumSelectGen.selectedUserAlbumId += '|' + Integer.toString(userAlbumId);
                                    }
                                    paddingValue = 6;
                                    frameLayout1.setPadding(
                                            FormatObject.DipToPx(linearLayout.getContext(), paddingValue)
                                            , FormatObject.DipToPx(linearLayout.getContext(), paddingValue)
                                            , FormatObject.DipToPx(linearLayout.getContext(), paddingValue)
                                            , FormatObject.DipToPx(linearLayout.getContext(), paddingValue));
                                }


                            }
                        });
                    }
                });
            }

            //TextView tvUserAlbumIdOfListItem=(TextView)convertView.findViewWithTag("tvUserAlbumIdOfListItem");
            //TextView tvUserAlbumIdOfListItem= new TextView(linearLayout.getContext());
            //TextView tvUserAlbumNameOfListItem=(TextView)convertView.findViewById(R.id.tvUserAlbumNameOfListItem);
            //int userAlbumId = _userAlbumCollections.get(position).getUserAlbumId();
            //String userAlbumName = _userAlbumCollections.get(position).getUserAlbumName();
            //tvUserAlbumIdOfListItem.setText(Integer.toString(userAlbumId));
            //tvUserAlbumIdOfListItem.setTextColor(Color.parseColor("#ffffff"));
            //linearLayout.addView(tvUserAlbumIdOfListItem);
            //tvUserAlbumNameOfListItem.setText(userAlbumName);
        }
        return linearLayout;
    }
}
