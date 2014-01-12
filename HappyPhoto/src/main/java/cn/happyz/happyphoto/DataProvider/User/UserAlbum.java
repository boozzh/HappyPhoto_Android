package cn.happyz.happyphoto.DataProvider.User;

/**
 * Created by zcmzc on 13-12-13.
 */
public class UserAlbum {

    private int _userAlbumId;
    private String _userAlbumName;
    private String _userAlbumIntro;
    private int _userAlbumTypeId;
    private int _userId;
    private int _siteId;
    private int _state;
    private String _coverPicUrl;

    public UserAlbum(int siteId, int userId,String userAlbumName,int userAlbumTypeId){
        super();
        //_userAlbumId = userAlbumId;
        _siteId = siteId;
        _userId = userId;
        _userAlbumTypeId = userAlbumTypeId;
        _userAlbumName = userAlbumName;
        //_userAlbumIntro = userAlbumIntro;
        //_state = state;
    }

    public int getSiteId(){
        return _siteId;
    }

    public int getUserId(){
        return _userId;
    }

    public int getUserAlbumId(){
        return _userAlbumId;
    }

    public void setUserAlbumId(int userAlbumId){
        _userAlbumId = userAlbumId;
    }

    public String getCoverPicUrl(){
        return _coverPicUrl;
    }

    public void setCoverPicUrl(String coverPicUrl){
        _coverPicUrl = coverPicUrl;
    }

    public String getUserAlbumName(){
        return _userAlbumName;
    }

    public void setUserAlbumName(String userAlbumName){
        _userAlbumName = userAlbumName;
    }

    public int getUserAlbumTypeId(){
        return _userAlbumTypeId;
    }

    public void setUserAlbumTypeId(int userAlbumTypeId){
        _userAlbumTypeId = userAlbumTypeId;
    }

    public String getUserAlbumIntro(){
        return _userAlbumIntro;
    }

    public void setUserAlbumIntro(String userAlbumIntro){
        _userAlbumIntro = userAlbumIntro;
    }

    public int getState(){
        return _state;
    }
}
