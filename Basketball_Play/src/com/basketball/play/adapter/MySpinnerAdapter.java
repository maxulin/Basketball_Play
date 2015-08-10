package com.basketball.play.adapter;
import java.util.List;

import com.basketball.play.bean.SpinnerBean;
import com.basketball.play.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
/**
 * 自定义适配器类
 * @author jiangqq  <a href=http://blog.csdn.net/jiangqq781931404></a>
 *
 */
public class MySpinnerAdapter extends BaseAdapter {
    private List<SpinnerBean> mList;
    private Context mContext;
 
    public MySpinnerAdapter(Context pContext, List<SpinnerBean> pList) {
        this.mContext = pContext;
        this.mList = pList;
    }
 
    @Override
    public int getCount() {
        return mList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
    /**
     * 下面是重要代码
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater _LayoutInflater=LayoutInflater.from(mContext);
        convertView=_LayoutInflater.inflate(R.layout.spinner_item, null);
        if(convertView!=null)
        {
            TextView _TextView1=(TextView)convertView.findViewById(R.id.spinner_item);
            _TextView1.setText(mList.get(position).getRole());
        }
        return convertView;
    }
}