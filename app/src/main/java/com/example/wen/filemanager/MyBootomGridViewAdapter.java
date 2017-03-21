package com.example.wen.filemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wen on 2017/3/1.
 */

public class MyBootomGridViewAdapter extends BaseAdapter {

    private List<HashMap<String,Object>> dataList=null;
    private Context context;

    public MyBootomGridViewAdapter(Context context,List<HashMap<String,Object>> dataList){
        this.dataList=dataList;
        this.context=context;
    }

    public void setData(List<HashMap<String,Object>> dataList){
        this.dataList=dataList;
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyBootomGridViewAdapter.ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new MyBootomGridViewAdapter.ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_bootom_gridview,null);
            viewHolder.image_bootom=(ImageView)convertView.findViewById(R.id.image_bootom);
            viewHolder.text_bootom=(TextView)convertView.findViewById(R.id.text_bootom);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(MyBootomGridViewAdapter.ViewHolder) convertView.getTag();
        }
        HashMap<String,Object> map=dataList.get(position);
        viewHolder.image_bootom.setImageResource((int)map.get("image_bootom"));
        viewHolder.text_bootom.setText((String)map.get("text_bootom"));

        return convertView;
    }
    private class ViewHolder{
        private ImageView image_bootom;
        private TextView text_bootom;
    }
}
