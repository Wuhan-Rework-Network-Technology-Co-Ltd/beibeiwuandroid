package xin.banghua.mediapicker.Video;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;


import java.util.ArrayList;
import java.util.List;

import xin.banghua.mediapicker.FileProcessing;

/**
 * Created by Alhazmy13 on 8/15/16.
 * MediaPicker
 */
class VideoProcessing {
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static List<String> processMultiVideos(Context context, Intent data) {
        List<String> listOfImgs = new ArrayList<>();
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) && (null == data.getData())) {
            ClipData clipdata = data.getClipData();
            for (int i = 0; i < clipdata.getItemCount(); i++) {
                Uri selectedImage = clipdata.getItemAt(i).getUri();
                String selectedImagePath = FileProcessing.getPath(context, selectedImage);
                listOfImgs.add(selectedImagePath);
            }
        }
        return listOfImgs;
    }
}
