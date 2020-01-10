package com.sate7.sate7launcher;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class AppsAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private Context mContext;
    private List<ResolveInfo> mDatas;
    public AppsAdapter(Context context,List<ResolveInfo> date){
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDatas = date;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null)
        {
            /*if(position < 5){
                convertView = mInflater.inflate(R.layout.row0_special,null);
            }else{
                convertView = mInflater.inflate(R.layout.layout_item,null);
            }*/
            convertView = mInflater.inflate(R.layout.layout_item,null);
            viewHolder=new ViewHolder();
            viewHolder.ico=(ImageView)convertView.findViewById(R.id.app_ico);
            viewHolder.name=(TextView)convertView.findViewById(R.id.app_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ResolveInfo info = (ResolveInfo) mDatas.get(position);
        viewHolder.ico.setImageDrawable(info.activityInfo.loadIcon(mContext.getPackageManager()));
        viewHolder.name.setText(info.activityInfo.loadLabel(mContext.getPackageManager()));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.ico.getLayoutParams();
        if(position < 5){
            params.topMargin = 30;
            viewHolder.ico.setLayoutParams(params);
        }else{
            params.topMargin = 0;
            viewHolder.ico.setLayoutParams(params);
        }
        return convertView;
    }
    private class ViewHolder {

        ImageView ico;
        TextView name;
    }

}
