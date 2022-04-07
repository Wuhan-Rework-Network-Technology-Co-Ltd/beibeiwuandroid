package xin.banghua.beiyuan.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zhaoss.weixinrecorded.util.MyVideoEditor;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.publish.ScriptSingleEffectActivity;

public class ScriptKAdapter extends RecyclerView.Adapter<ScriptKAdapter.ViewHolder>{
    private static final String TAG = "ScriptAdapter";
    private MyVideoEditor myVideoEditor = new MyVideoEditor();
    List<ScriptList> scriptLists = new ArrayList<>();

    Context mContext;

    public ScriptKAdapter(List<ScriptList> scriptLists,Context mContext) {
        this.scriptLists = scriptLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.script_k_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public void setScriptLists(List<ScriptList> scriptLists){
        this.scriptLists = scriptLists;
    }
    public void swapData(List<ScriptList> scriptLists){
        int oldSize = this.scriptLists.size();
        int newSize = scriptLists.size();
        this.scriptLists = scriptLists;
        notifyItemRangeInserted(oldSize , newSize);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: 刷新");

        ScriptList currentItem = this.scriptLists.get(position);

        holder.script_layout.setOnClickListener(v -> {
            String oldPath = Common.getAccthmentPath("Video","script"+currentItem.getId());
            Log.d(TAG, "onBindViewHolder: 点击了剧本");
            holder.script_layout.setClickable(false);
            if (!(new File(oldPath)).exists()) { // 如果不存在.
                Log.d(TAG, "onBindViewHolder: 1点击了剧本");
                Common.downloadAttachment("Video", "script"+currentItem.getId(), Common.getOssResourceUrl(currentItem.getScript_video()), task -> {
                    Log.d(TAG, "getBaseDownloadTask: 合音新下载视频地址"+task.getPath()+"|"+task.getTargetFilePath());
                    Log.d(TAG, "onBindViewHolder: 2点击了剧本");
                    //调整尺寸
                    //String newScaleVideo = myVideoEditor.executeScaleVideo(task.getPath(),Common.screen_width,Common.screen_height);
                    currentItem.setScript_video(task.getPath());
                    Intent intent = new Intent(mContext, ScriptSingleEffectActivity.class);
                    intent.putExtra("scriptList", (Serializable) currentItem);
                    mContext.startActivity(intent);

                    holder.script_layout.setClickable(true);
                });
            }else {
                Log.d(TAG, "onBindViewHolder: 3点击了剧本");
                Log.d(TAG, "onBindViewHolder: 合音已下载视频地址"+oldPath);
                currentItem.setScript_video(oldPath);
                Intent intent = new Intent(mContext, ScriptSingleEffectActivity.class);
                intent.putExtra("scriptList", (Serializable) currentItem);
                mContext.startActivity(intent);

                holder.script_layout.setClickable(true);
            }
        });
        Glide.with(mContext).load(currentItem.getScript_cover()).into(holder.script_cover_img);
        holder.script_singer_tv.setText(currentItem.getScript_singer());
        holder.script_name_tv.setText(currentItem.getScript_name());
        holder.script_description_tv.setText(currentItem.getScript_description());
    }

    @Override
    public int getItemCount() {
        return scriptLists.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout script_layout;
        ImageView script_cover_img;
        TextView script_name_tv;
        TextView script_singer_tv;
        TextView script_description_tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            script_layout = itemView.findViewById(R.id.script_layout);
            script_cover_img = itemView.findViewById(R.id.script_cover_img);
            script_name_tv = itemView.findViewById(R.id.script_name_tv);
            script_singer_tv = itemView.findViewById(R.id.script_singer_tv);
            script_description_tv = itemView.findViewById(R.id.script_description_tv);
        }
    }
}

