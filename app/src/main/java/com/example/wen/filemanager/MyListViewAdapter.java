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
 * Created by wen on 2017/2/28.
 */

public class MyListViewAdapter extends BaseAdapter{
    private List<HashMap<String,Object>> dataList=null;
    private Context context;

    public MyListViewAdapter(Context context,List<HashMap<String,Object>> dataList){
        this.dataList=dataList;
        this.context=context;
    }

    public void setData(List<HashMap<String,Object>> dataList){
        this.dataList=dataList;
        notifyDataSetChanged();
    }

    public void changeImage2(int position) {
        for (HashMap<String,Object> m:dataList) {
            m.put("image2_item",R.mipmap.icon_check);
        }
        dataList.get(position).put("image2_item",R.mipmap.icon_check_selected);
        notifyDataSetChanged();
    }
    public void changeImage2Next(){
        for (HashMap<String,Object> m:dataList) {
            if(((String)m.get("type")).equals("Folder")){
                m.put("image2_item",R.mipmap.icon_arrow);
            }else{
                m.put("image2_item",null);
            }
        }
        notifyDataSetChanged();
    }
    public void changeImage2At(int position){
        HashMap<String,Object> m=dataList.get(position);
        if(R.mipmap.icon_check==(int)m.get("image2_item")){
            m.put("image2_item",R.mipmap.icon_check_selected);
        }else{
            m.put("image2_item",R.mipmap.icon_check);
        }
        notifyDataSetChanged();
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
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_listview,null);
            viewHolder.image1_item=(ImageView)convertView.findViewById(R.id.image1_item);
            viewHolder.file_name=(TextView)convertView.findViewById(R.id.file_name);
            viewHolder.file_count=(TextView)convertView.findViewById(R.id.file_count);
            viewHolder.image2_item=(ImageView)convertView.findViewById(R.id.image2_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        HashMap<String,Object> map=dataList.get(position);
        viewHolder.image1_item.setImageResource((int)map.get("image1_item"));
        viewHolder.file_name.setText((String)map.get("file_name"));
        viewHolder.file_count.setText((String)map.get("file_count"));
        if(map.get("image2_item")!=null){
            viewHolder.image2_item.setImageResource((int)map.get("image2_item"));
        }else{
            viewHolder.image2_item.setImageDrawable(null);
        }
        return convertView;
    }

    private class ViewHolder{
        private ImageView image1_item;
        private TextView file_name;
        private TextView file_count;
        private ImageView image2_item;
    }
}
