package io.agora.chatroom.gift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.agora.chatroom.R;


public class GiftSentAdapter extends RecyclerView.Adapter<GiftSentAdapter.ViewHolder>{
    private static final String TAG = "Cannot invoke method length() on null object";
    List<GiftList> giftLists = new ArrayList<>();

    Context mContext;
    public GiftSentAdapter(Context context,List<GiftList> giftLists) {
        this.mContext = context;
        this.giftLists = giftLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_unselected_white,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }



    public void setGiftLists(List<GiftList> giftLists){
        this.giftLists = giftLists;
        notifyDataSetChanged();
    }
    public void swapData(List<GiftList> userInfoLists){
        int oldSize = this.giftLists.size();
        int newSize = userInfoLists.size();
        this.giftLists = userInfoLists;
        notifyItemRangeInserted(oldSize , newSize);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int currentPosition = position;
        GiftList currentItem = this.giftLists.get(position);
        if (currentItem.getNum()>1){
            holder.gift_name.setText(currentItem.getGift_name()+" x"+currentItem.getNum());
        }else {
            holder.gift_name.setText(currentItem.getGift_name());
        }

        holder.gift_price.setText(new BigDecimal(currentItem.getGift_price()).multiply(new BigDecimal(currentItem.getNum()))+"乐园币");
        Glide.with(mContext).load(currentItem.getGift_image()).into(holder.gift_iv);
    }

    @Override
    public int getItemCount() {
        return giftLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout gift_layout;
        ImageView gift_iv;
        TextView gift_name;
        TextView gift_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gift_layout = itemView.findViewById(R.id.gift_layout);
            gift_iv = itemView.findViewById(R.id.gift_iv);
            gift_name = itemView.findViewById(R.id.gift_name);
            gift_price = itemView.findViewById(R.id.gift_price);
        }
    }
}

