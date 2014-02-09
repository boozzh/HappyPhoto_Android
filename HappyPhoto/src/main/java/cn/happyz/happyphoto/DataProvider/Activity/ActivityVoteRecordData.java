package cn.happyz.happyphoto.DataProvider.Activity;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.happyz.happyphoto.DataProvider.BaseData;
import cn.happyz.happyphoto.DataProvider.User.UserAlbum;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumCollections;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ThreadPoolUtils;

/**
 * Created by zcmzc on 14-2-5.
 */
public class ActivityVoteRecordData extends BaseData implements Runnable {
    private String HttpUrl = null;
    private Handler MyHandler = null;
    private ActivityVoteRecordDataOperateType MyOperateType;
    private int _pageIndex = 1;
    private int _pageSize = 12;

    public void setPageIndex(int pageIndex) {
        _pageIndex = pageIndex;
    }

    public void setPageSize(int pageSize) {
        _pageSize = pageSize;
    }

    public ActivityVoteRecordData(String httpUrl, Handler handler) {
        HttpUrl = httpUrl;
        MyHandler = handler;
    }

    @Override
    public void run() {
        if (MyOperateType == ActivityVoteRecordDataOperateType.Create) {
            String result = super.RunGet(HttpUrl, MyHandler);
            if (result != null) {
                try {
                    int createResult = -1;
                    JSONObject jsonObject = new JSONObject(result).getJSONObject("activity_vote_record");
                    JSONArray jsonArray = jsonObject.getJSONArray("activity_vote_record_create_result");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                        createResult = jsonObject2.getInt("create_result");
                    }

                    Message msg = MyHandler.obtainMessage();
                    msg.what = HttpClientStatus.FINISH_GET.ordinal();
                    msg.obj = createResult;

                    MyHandler.sendMessage(msg);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                }
            }
        } else if (MyOperateType == ActivityVoteRecordDataOperateType.GetListOfMyVote) {
            HttpUrl += "&p=" + _pageIndex + "&ps=" + _pageSize;
            String result = super.RunGet(HttpUrl, MyHandler);
            if (result != null) {
                if (result.equals("")) { //查询正确，但没有数据
                    MyHandler.sendEmptyMessage(HttpClientStatus.FINISH_GET_BUT_NO_DATA.ordinal());
                } else if (result == "-10") { //会员验证失败
                    MyHandler.sendEmptyMessage(HttpClientStatus.FINISH_GET_BUT_USER_ERROR.ordinal());
                } else if (result.length() > 0) {
                    try {
                        UserAlbumCollections userAlbumCollections = new UserAlbumCollections();
                        UserAlbum userAlbum = null;
                        JSONObject jsonObject = new JSONObject(result).getJSONObject("activity_vote_record");
                        JSONArray jsonArray = jsonObject.getJSONArray("activity_vote_record_list");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                            int userAlbumId = jsonObject2.getInt("UserAlbumID");
                            int siteId = jsonObject2.getInt("SiteID");
                            int userId = jsonObject2.getInt("UserID");
                            String userAlbumName = jsonObject2.getString("UserAlbumName");
                            int userAlbumTypeId = jsonObject2.getInt("UserAlbumTypeId");
                            String coverPicUrl = jsonObject2.getString("CoverPicUrl");

                            if (userAlbumId > 0) {
                                userAlbum = new UserAlbum(
                                        siteId,
                                        userId,
                                        userAlbumName,
                                        userAlbumTypeId
                                );
                                userAlbum.setCoverPicUrl(coverPicUrl);
                                userAlbum.setUserAlbumId(userAlbumId);
                            }
                            userAlbumCollections.add(userAlbum);
                        }

                        Message msg = MyHandler.obtainMessage();
                        msg.what = HttpClientStatus.FINISH_GET.ordinal();
                        msg.obj = userAlbumCollections;

                        MyHandler.sendMessage(msg);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                    }
                }
            }
        }
    }

    public void GetDataFromHttp(ActivityVoteRecordDataOperateType activityVoteRecordDataOperateType) {
        this.MyOperateType = activityVoteRecordDataOperateType;
        ThreadPoolUtils.execute(this);
    }
}
