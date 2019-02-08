package com.ultimatesoftil.adapters;

/**
 * Created by mike on 17 Dec 2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ultimatesoftil.trackeru.R;

import java.util.ArrayList;

import com.ultimatesoftil.models.User;
import com.ultimatesoftil.utilities.PicassoClient;

/**
 * Created by mike on 14 Dec 2017.
 */

public class CustomAdapter2 extends BaseAdapter {
    Context c;
    ArrayList<User> circlesinfo;
    LayoutInflater inflater;


    public CustomAdapter2(Context c, ArrayList<User> circlesinfo) {
        this.c = c;
        this.circlesinfo = circlesinfo;
    }





    @Override
    public int getCount() {
        return circlesinfo.size();
    }

    @Override
    public Object getItem(int i) {
        return circlesinfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {

        if (inflater== null)
        {
            inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } if(convertview==null)
        {
            convertview= inflater.inflate(R.layout.list_group,viewGroup,false);

        }




        MyHolder2 holder= new MyHolder2(convertview);
        holder.status.setText("offline");
        holder.username.setText(circlesinfo.get(i).getUsername());
      //  PicassoClient.downloadimg(c, circlesinfo.get(i).getPicLink(), holder.img2);






        return convertview;
    }
}


