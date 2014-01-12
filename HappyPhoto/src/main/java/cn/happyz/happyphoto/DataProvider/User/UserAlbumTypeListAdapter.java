package cn.happyz.happyphoto.DataProvider.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import cn.happyz.happyphoto.R;

/**
 * Created by zcmzc on 13-12-21.
 */
public class UserAlbumTypeListAdapter extends ArrayAdapter<UserAlbumType> {
    private UserAlbumTypeCollections _userAlbumTypeCollections;
    private Context _context;
    private int _resource;

    public UserAlbumTypeListAdapter(Context context,int resource,UserAlbumTypeCollections userAlbumTypeCollections) {
        super(context, resource, userAlbumTypeCollections);
        this._context = context;
        this._resource = resource;
        this._userAlbumTypeCollections = userAlbumTypeCollections;
    }

    /**
     *
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return this.LoadData(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.LoadData(position,convertView,parent);
    }

    private View LoadData(int position, View convertView, ViewGroup parent){
        LayoutInflater layoutInflater = LayoutInflater.from(_context);
        convertView = layoutInflater.inflate(_resource, null);
        if(convertView != null)
        {
            TextView txtUserAlbumTypeId=(TextView)convertView.findViewById(R.id.txtUserAlbumTypeId);
            TextView txtUserAlbumTypeName=(TextView)convertView.findViewById(R.id.txtUserAlbumTypeName);
            int userAlbumTypeId = _userAlbumTypeCollections.get(position).getUserAlbumTypeId();
            String userAlbumTypeName = _userAlbumTypeCollections.get(position).getUserAlbumTypeName();
            txtUserAlbumTypeId.setText(Integer.toString(userAlbumTypeId));
            txtUserAlbumTypeName.setText(userAlbumTypeName);
        }
        return convertView;
    }
}
