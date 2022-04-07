package xin.banghua.beiyuan.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.agora.chatroom.util.NiceZoomImageView;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Main4Branch.ImagerPagerActivity;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder>{
    private static final String TAG = "PictureAdapter";
    List<String> pictures = new ArrayList<>();


    LuntanList luntanList;

    Context mContext;
    public PictureAdapter(Context context,LuntanList luntanList) {
        this.mContext = context;
        this.luntanList = luntanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (pictures.size()==1){
            view = LayoutInflater.from(parent.getContext()).inflate(io.agora.chatroom.R.layout.picture_item3,parent,false);
        }else if(pictures.size()==2 || pictures.size()==4){
            view = LayoutInflater.from(parent.getContext()).inflate(io.agora.chatroom.R.layout.picture_item2,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(io.agora.chatroom.R.layout.picture_item1,parent,false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public void setPictures(List<String> pictures){
        this.pictures = pictures;
        notifyDataSetChanged();
    }
    public void swapData(List<String> pictures){
        int oldSize = this.pictures.size();
        int newSize = pictures.size();
        this.pictures = pictures;
        notifyItemRangeInserted(oldSize , newSize);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: 刷新");
        Log.d(TAG, "onBindViewHolder: 图片地址"+pictures.get(position));

        int currentPosition = position;
        Glide.with(mContext).load(Common.getOssResourceUrl(pictures.get(position))).into(holder.picture_img);

        holder.picture_img.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ImagerPagerActivity.class);
            intent.putExtra("luntanlist", luntanList);
            intent.putExtra("image_index", currentPosition);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return this.pictures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        NiceZoomImageView picture_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            picture_img = itemView.findViewById(io.agora.chatroom.R.id.picture_img);
        }
    }
}


