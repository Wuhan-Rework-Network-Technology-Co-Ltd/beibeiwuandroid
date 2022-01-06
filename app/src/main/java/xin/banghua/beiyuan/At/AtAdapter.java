package xin.banghua.beiyuan.At;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.Common;


public class AtAdapter extends RecyclerView.Adapter<AtAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "AtAdapter";

    private List<AtList> atLists;
    private List<AtList> atListsFull;

    private Context mContext;

    private AtEdittext atEdittext;

    Integer current_timestamp = Integer.valueOf((int) (System.currentTimeMillis()/1000));


    public AtAdapter(Context mContext, List<AtList> atLists,AtEdittext atEdittext) {
        this.mContext = mContext;
        this.atLists = atLists;
        this.atListsFull = new ArrayList<>(atLists);
        this.atEdittext = atEdittext;
    }
    public void swapData(List<AtList> atList) {

        this.atLists = atList;
        this.atListsFull = new ArrayList<>(atList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_at,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AtList currentItem = atLists.get(position);

        String id = currentItem.getmUserID();
        String name = currentItem.getmUserNickName();

        Glide.with(mContext).load(Common.getOssResourceUrl(currentItem.getmUserPortrait()))
                .into(holder.userPortrait);

        holder.userNickName.setText(currentItem.getmUserNickName());

        holder.at_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atEdittext.handleResult(id,name);
            }
        });

    }

    @Override
    public int getItemCount() {
        return atLists.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    //过滤器
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AtList> filteredList = new ArrayList();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(atListsFull);
            }else {
                String filterPattern = constraint.toString().trim();
                for (AtList item : atListsFull){
                    if (item.getmUserNickName().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            atLists.clear();
            atLists.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userPortrait;
        TextView userNickName;
        ImageView vip_gray;
        ImageView vip_diamond;
        ImageView vip_black;
        ImageView vip_white;

        LinearLayout at_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userPortrait = itemView.findViewById(R.id.portrait_civ);
            userNickName = itemView.findViewById(R.id.nickname_tv);
            vip_gray = itemView.findViewById(R.id.vip_gray);
            vip_diamond = itemView.findViewById(R.id.vip_diamond);
            vip_black = itemView.findViewById(R.id.vip_black);
            vip_white = itemView.findViewById(R.id.vip_white);

            at_layout = itemView.findViewById(R.id.at_layout);
        }
    }
}
