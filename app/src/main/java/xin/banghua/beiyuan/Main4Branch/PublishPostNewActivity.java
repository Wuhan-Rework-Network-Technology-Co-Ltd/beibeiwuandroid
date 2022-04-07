package xin.banghua.beiyuan.Main4Branch;

import static com.donkingliang.imageselector.ImageSelectorActivity.IMAGE_SELECTOR_REQUEST_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageSelector;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.util.CheckPermission;
import xin.banghua.beiyuan.R;

public class PublishPostNewActivity extends AppCompatActivity {
    private static final String TAG = "PublishPostActivity";

    AppCompatActivity mContext;

    List<String> pictures = new ArrayList<>();
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    PictureAdapter pictureAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_post);
        mContext = this;

        ButterKnife.bind(this);


        CheckPermission.verifyPermissionCameraAndStorage(mContext);


        pictureAdapter = new PictureAdapter(pictures);
        recyclerview.setAdapter(pictureAdapter);
        recyclerview.setLayoutManager(new GridLayoutManager(mContext,3));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: 动态图片地址");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //从图片选择器回来
            case IMAGE_SELECTOR_REQUEST_CODE:
                if (data != null) {
                    ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                    boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
                    boolean isFull = data.getBooleanExtra(ImageSelector.IS_FULL, false);
                    Log.d("isCameraImage", "是否是拍照图片：" + isCameraImage);
                    if (images == null || images.size() <= 0) {
                        Toast.makeText(mContext, "取消设置", Toast.LENGTH_SHORT).show();
                    }
                    for (String image : images){
                        Log.d(TAG, "onActivityResult: 图片地址"+image);
                        pictures.add(image);
                        pictureAdapter.setPictures(pictures);
                    }
                }
                break;
        }
    }

    public class PictureAdapter extends RecyclerView.Adapter<ViewHolder>{
        List<String> pictures = new ArrayList<>();

        public PictureAdapter(List<String> pictures) {
            this.pictures = pictures;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item0,parent,false);
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
            int currentPosition = position;
            if (this.pictures.size() == position){
                holder.delete_img.setVisibility(View.GONE);
                Glide.with(mContext).load(R.drawable.plus).into(holder.picture_img);
                holder.picture_img.setOnClickListener(v -> {
                    ImageSelector.builder()
                            .onlyImage(true)
                            .useCamera(true) // 设置是否使用拍照
                            .setSingle(false)  //设置是否单选
                            .setMaxSelectCount(9)
                            .setViewImage(true) //是否点击放大图片查看,，默认为true
                            .start(mContext, IMAGE_SELECTOR_REQUEST_CODE); // 打开相册
                });
            }else {
                String currentItem = this.pictures.get(position);
                Glide.with(mContext).load(currentItem).into(holder.picture_img);
                holder.delete_img.setVisibility(View.VISIBLE);
                holder.delete_img.setOnClickListener(v -> {
                    this.pictures.remove(currentPosition);
                    notifyDataSetChanged();
                });
                holder.picture_img.setClickable(false);
            }
        }

        @Override
        public int getItemCount() {
            return this.pictures.size()+1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView picture_img;
        ImageView delete_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            picture_img = itemView.findViewById(R.id.picture_img);
            delete_img = itemView.findViewById(R.id.delete_img);
        }
    }
}