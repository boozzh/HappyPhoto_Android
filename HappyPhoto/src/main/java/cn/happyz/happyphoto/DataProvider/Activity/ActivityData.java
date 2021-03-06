package cn.happyz.happyphoto.DataProvider.Activity;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.happyz.happyphoto.DataProvider.BaseData;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ThreadPoolUtils;

/**
 * Created by homezc on 14-1-14.
 */
public class ActivityData extends BaseData implements Runnable {
    private String HttpUrl = null;
    private Handler MyHandler = null;
    private ActivityDataOperateType MyOperateType;

    private int _pageIndex = 1;
    private int _pageSize = 12;

    public void setPageIndex(int pageIndex) {
        _pageIndex = pageIndex;
    }

    public void setPageSize(int pageSize) {
        _pageSize = pageSize;
    }

    public ActivityData(String httpUrl, Handler handler) {
        HttpUrl = httpUrl;
        MyHandler = handler;
    }

    @Override
    public void run() {
        if (MyOperateType == ActivityDataOperateType.GetList) {
            HttpUrl += "&p=" + _pageIndex + "&ps=" + _pageSize;
            String result = super.RunGet(HttpUrl, MyHandler);
            if (result != null) {
                try {
                    ActivityCollections activityCollections = new ActivityCollections();
                    Activity activity;
                    JSONObject jsonObject = new JSONObject(result).getJSONObject("activity");
                    JSONArray jsonArray = jsonObject.getJSONArray("activity_list");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                        activity = new Activity();
                        activity.setActivityId(jsonObject2.getInt("ActivityID"));
                        activity.setActivitySubject(jsonObject2.getString("ActivitySubject"));
                        activity.setTitlePic(jsonObject2.getString("TitlePic"));
                        activity.setActivityContent(jsonObject2.getString("ActivityContent"));
                        activityCollections.add(activity);
                    }

                    Message msg = MyHandler.obtainMessage();
                    msg.what = HttpClientStatus.FINISH_GET.ordinal();
                    msg.obj = activityCollections;

                    MyHandler.sendMessage(msg);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                }
            }
        } else if (MyOperateType == ActivityDataOperateType.GetListOfEnd) {
            HttpUrl += "&p=" + _pageIndex + "&ps=" + _pageSize;
            String result = super.RunGet(HttpUrl, MyHandler);
            if (result != null) {
                try {
                    ActivityCollections activityCollections = new ActivityCollections();
                    Activity activity;
                    JSONObject jsonObject = new JSONObject(result).getJSONObject("activity");
                    JSONArray jsonArray = jsonObject.getJSONArray("activity_list");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                        activity = new Activity();
                        activity.setActivityId(jsonObject2.getInt("ActivityID"));
                        activity.setActivitySubject(jsonObject2.getString("ActivitySubject"));
                        activity.setTitlePic(jsonObject2.getString("TitlePic"));
                        activity.setActivityContent(jsonObject2.getString("ActivityContent"));
                        activityCollections.add(activity);
                    }

                    Message msg = MyHandler.obtainMessage();
                    msg.what = HttpClientStatus.FINISH_GET.ordinal();
                    msg.obj = activityCollections;

                    MyHandler.sendMessage(msg);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                }
            }
        } else if (MyOperateType == ActivityDataOperateType.GetListOfMineJoined) {
            HttpUrl += "&p=" + _pageIndex + "&ps=" + _pageSize;
            String result = super.RunGet(HttpUrl, MyHandler);
            if (result != null) {
                if (result.equals("")) { //查询正确，但没有数据
                    MyHandler.sendEmptyMessage(HttpClientStatus.FINISH_GET_BUT_NO_DATA.ordinal());
                } else if (result == "-10") { //会员验证失败
                    MyHandler.sendEmptyMessage(HttpClientStatus.FINISH_GET_BUT_USER_ERROR.ordinal());
                } else if (result.length() > 0) {
                    try {
                        ActivityCollections activityCollections = new ActivityCollections();
                        Activity activity;
                        JSONObject jsonObject = new JSONObject(result).getJSONObject("activity");
                        JSONArray jsonArray = jsonObject.getJSONArray("activity_list");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                            activity = new Activity();
                            activity.setActivityId(jsonObject2.getInt("ActivityID"));
                            activity.setActivitySubject(jsonObject2.getString("ActivitySubject"));
                            activity.setTitlePic(jsonObject2.getString("TitlePic"));
                            activity.setActivityContent(jsonObject2.getString("ActivityContent"));
                            activityCollections.add(activity);
                        }
                        Message msg = MyHandler.obtainMessage();
                        msg.what = HttpClientStatus.FINISH_GET.ordinal();
                        msg.obj = activityCollections;
                        MyHandler.sendMessage(msg);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                    }
                }
            }
        } else if (MyOperateType == ActivityDataOperateType.GetListOfMineVoted) {
            HttpUrl += "&p=" + _pageIndex + "&ps=" + _pageSize;
            String result = super.RunGet(HttpUrl, MyHandler);
            if (result != null) {
                if (result.equals("")) { //查询正确，但没有数据
                    MyHandler.sendEmptyMessage(HttpClientStatus.FINISH_GET_BUT_NO_DATA.ordinal());
                } else if (result == "-10") { //会员验证失败
                    MyHandler.sendEmptyMessage(HttpClientStatus.FINISH_GET_BUT_USER_ERROR.ordinal());
                } else if (result.length() > 0) {
                    try {
                        ActivityCollections activityCollections = new ActivityCollections();
                        Activity activity;
                        JSONObject jsonObject = new JSONObject(result).getJSONObject("activity");
                        JSONArray jsonArray = jsonObject.getJSONArray("activity_list");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                            activity = new Activity();
                            activity.setActivityId(jsonObject2.getInt("ActivityID"));
                            activity.setActivitySubject(jsonObject2.getString("ActivitySubject"));
                            activity.setTitlePic(jsonObject2.getString("TitlePic"));
                            activity.setActivityContent(jsonObject2.getString("ActivityContent"));
                            activityCollections.add(activity);
                        }
                        Message msg = MyHandler.obtainMessage();
                        msg.what = HttpClientStatus.FINISH_GET.ordinal();
                        msg.obj = activityCollections;
                        MyHandler.sendMessage(msg);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                    }
                }
            }
        }
    }

    public void GetDataFromHttp(ActivityDataOperateType activityDataOperateType) {
        this.MyOperateType = activityDataOperateType;
        ThreadPoolUtils.execute(this);
    }
}
