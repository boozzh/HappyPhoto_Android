package cn.happyz.happyphoto.DataProvider.User;

import android.os.Handler;
import android.os.Message;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.happyz.happyphoto.DataProvider.BaseData;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ThreadPoolUtils;

/**
 * Created by zcmzc on 13-11-19.
 */
public class UserData extends BaseData implements Runnable {
    private String _httpUrl = null;
    private Handler _handler = null;
    private UserDataOperateType _userDataOperateType = UserDataOperateType.Null;

    public UserData(String httpUrl, Handler handler){
        _httpUrl = httpUrl;
        _handler = handler;
    }

    @Override
    public void run() {

        if(this._userDataOperateType == UserDataOperateType.Login){

            String result = super.RunGet(_httpUrl, _handler);

            if(result != null){
                try {
                    UserCollections userCollections = new UserCollections();

                    JSONObject jsonObject = new JSONObject(result).getJSONObject("user");
                    JSONArray jsonArray = jsonObject.getJSONArray("userlist");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                        Integer userId = jsonObject2.getInt("UserId");
                        User user = null;
                        if(userId>0){
                            user = new User(
                                    jsonObject2.getInt("UserId"),
                                    jsonObject2.getString("UserName"),
                                    jsonObject2.getString("UserPass"),
                                    jsonObject2.getInt("State")
                            );
                            user.setUserPoint(jsonObject2.getInt("UserPoint"));
                        }else{
                            user = new User(
                                    userId,
                                    "",
                                    "",
                                    -1
                            );
                        }
                        userCollections.add(user);
                    }

                    Message msg = _handler.obtainMessage();
                    msg.what = HttpClientStatus.FINISH_GET.ordinal();
                    msg.obj = userCollections;

                    _handler.sendMessage(msg);
                } catch (Exception ex){
                    ex.printStackTrace();
                    _handler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                }

            }
        }
    }

    public void RequestFromHttp(UserDataOperateType userDataOperateType){
        this._userDataOperateType = userDataOperateType;
        ThreadPoolUtils.execute(this);
    }

    public void SaveUser(User user){
    }

    public void SaveUserCollections(UserCollections userCollections){

    }
}
