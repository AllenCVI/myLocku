package com.lockulockme.locku.base.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;

import com.lockulockme.locku.common.R;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureSelectorHelper {
    public static void getLocalImage(Activity activity){
        PictureSelectionModel pictureSelectionModel= PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.picture_WeChat_style)
                .imageEngine(PictureSelectorGlide.getInstance())
                .isAndroidQTransform(true)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)
                .isSingleDirectReturn(true)
                .isCompress(true)
                .minimumCompressSize(380)
                .compressQuality(80);// 图片压缩后输出质量
        pictureSelectionModel.forResult(PictureConfig.CHOOSE_REQUEST);
    }

    public static void getLocalImage(Fragment fragment){
        PictureSelectionModel pictureSelectionModel= PictureSelector.create(fragment)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.picture_WeChat_style)
                .imageEngine(PictureSelectorGlide.getInstance())
                .isAndroidQTransform(true)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)
                .isSingleDirectReturn(true)
                .isCompress(true)
                .minimumCompressSize(380)
                .compressQuality(80);// 图片压缩后输出质量
        pictureSelectionModel.forResult(PictureConfig.CHOOSE_REQUEST);
    }

    public static List<File> getFile(List<LocalMedia> mediaList, ContentResolver resolver) {
        List<File> files = new ArrayList<>();
        for (LocalMedia media : mediaList) {
            String path;
            if (media.isCut() && !media.isCompressed()) {
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                path = media.getCompressPath();
            } else {
                path = media.getPath();
            }
            File file = null;
            if (PictureMimeType.isContent(path) && !media.isCut() && !media.isCompressed()) {
                Uri uri = Uri.parse(path);
                file = new File(getUriPath(uri, resolver));
            } else {
                file = new File(path);
            }
            files.add(file);
        }
        return files;
    }

    private static String getUriPath(Uri uri,
                                    ContentResolver resolver) {
        String[] pathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = resolver.query(uri, pathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(pathColumn[0]);
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }
}
