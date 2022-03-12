package xin.banghua.beiyuan.custom_ui;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClickableAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private OnItemClickListener mItemClickListener;
    private OnLongClickListener mLongClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setLongClickListener(OnLongClickListener longClickListener) {
        mLongClickListener = longClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnLongClickListener{
        boolean onLongClick(int position);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        //设置点击和长按事件
        if (mItemClickListener != null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(i);
                }
            });
        }
        if (mLongClickListener != null){
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return mLongClickListener.onLongClick(i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
