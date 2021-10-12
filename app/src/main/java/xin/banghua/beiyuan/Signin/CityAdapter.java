package xin.banghua.beiyuan.Signin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import xin.banghua.beiyuan.bean.AddrBean;

/**
 * Created by Xu Wei on 2017/4/26.
 */

public class CityAdapter extends BaseAdapter {
    private Context context;
    private List<AddrBean.ProvinceBean.CityBean> cityBeanList = new ArrayList<>();

    public void setCityBeanList(List<AddrBean.ProvinceBean.CityBean> cityBeanList) {
        this.cityBeanList = cityBeanList;
        notifyDataSetChanged();
    }

    public CityAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return cityBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return cityBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = new TextView(context);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setText(cityBeanList.get(position).getCityName());
        tv.setTextColor(android.graphics.Color.RED);
        return tv;
    }
}
