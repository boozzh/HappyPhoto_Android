package cn.happyz.happyphoto.DataProvider.User;

/**
 * Created by zcmzc on 13-12-5.
 */
public class User {

    private int _userId;
    private String _userName;
    private String _userPass;
    private int _state;
    private String _nickName;
    private String _nealName;
    private String _email;
    private String _avatar;
    private String _avatarMedium;
    private String _avatarSmall;
    private String _qq;
    private String _idCard;
    private String _address;
    private String _birthday;
    private String _postCode;
    private String _mobile;
    private String _tel;
    private int _userScore;
    private int _userMoney;
    private int _userCharm;
    private int _userExp;
    private int _userPoint;
    private String _sign;
    private String _comeFrom;
    private String _honor;
    private int _fansCount;
    private String _gendar;
    private String _province;
    private String _city;
    private int _hit;
    private int _messageCount;
    private int _userPostCount;
    private int _userPostBestCount;
    private int _userActivityCount;
    private int _userAlbumCount;
    private int _userBestAlbumCount;
    private int _userRecAlbumCount;
    private int _userAlbumCommentCount;
    private String _userLevelName;
    private String _userLevelPic;
    private String _userLevel;
    private int _userGroupId;

    public User(int userId,String userName,String userPass,int state){
        super();
        _userId = userId;
        _userName = userName;
        _userPass = userPass;
        _state = state;
    }

    public int getUserId(){
        return _userId;
    }

    public String getUserName(){
        return _userName;
    }

    public String getUserPass(){return _userPass;}

    public int getState(){
        return _state;
    }
}
