package xin.banghua.beiyuan.match;


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.faceunity.nama.ui.CircleImageView;

import java.util.Random;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;

@ProviderTag(messageContent = MatchMessage.class,showPortrait = false,centerInHorizontal = true,showSummaryWithName = false)
public class MatchMessageProvider extends IContainerItemProvider.MessageProvider<MatchMessage> {

    @Override
    public void bindView(View view, int i, MatchMessage messageContent, UIMessage uiMessage) {
        final ViewHolder viewHolder = (MatchMessageProvider.ViewHolder) view.getTag();
        UserInfoList userInfoList = JSON.parseObject(messageContent.userInfoListString,UserInfoList.class);
        Glide.with(view.getContext()).load(userInfoList.getPortrait()).into(viewHolder.portrait_one);
        Glide.with(view.getContext()).load(Common.userInfoList.getPortrait()).into(viewHolder.portrait_two);
        viewHolder.age_tv.setText("ta的年龄："+userInfoList.getAge());
        viewHolder.property_tv.setText("ta的属性："+userInfoList.getProperty());
        viewHolder.matched_degree.setText("匹配度"+new Random().nextInt(95)%(95-85+1) + 85);
    }

    @Override
    public Spannable getContentSummary(MatchMessage messageContent) {
        return new SpannableString("[匹配卡]");
    }

    @Override
    public void onItemClick(View view, int i, MatchMessage messageContent, UIMessage uiMessage) {

    }

    class ViewHolder {
        CircleImageView portrait_one;
        CircleImageView portrait_two;
        TextView matched_degree;
        TextView property_tv;
        TextView age_tv;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.match_message_provider, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.portrait_one = view.findViewById(R.id.portrait_one);
        viewHolder.portrait_two = view.findViewById(R.id.portrait_two);
        viewHolder.matched_degree = view.findViewById(R.id.matched_degree);
        viewHolder.property_tv = view.findViewById(R.id.property_tv);
        viewHolder.age_tv = view.findViewById(R.id.age_tv);
        view.setTag(viewHolder);
        return view;
    }
}