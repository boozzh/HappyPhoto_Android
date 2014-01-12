package cn.happyz.happyphoto.DataProvider.User;

/**
 * Created by zcmzc on 13-12-21.
 */
public class UserAlbumType {
    private int _userAlbumTypeId;
    private String _userAlbumTypeName;
    private int _state;

    public UserAlbumType(int userAlbumTypeId,String userAlbumTypeName){
        super();
        _userAlbumTypeId = userAlbumTypeId;
        _userAlbumTypeName = userAlbumTypeName;
        //_userAlbumIntro = userAlbumIntro;
        //_state = state;
    }

    public void setUserAlbumTypeId(int userAlbumTypeId){
        _userAlbumTypeId = userAlbumTypeId;
    }
    public int getUserAlbumTypeId(){
        return _userAlbumTypeId;
    }
    public String getUserAlbumTypeName(){
        return _userAlbumTypeName;
    }
    public int getState(){
        return _state;
    }
}
