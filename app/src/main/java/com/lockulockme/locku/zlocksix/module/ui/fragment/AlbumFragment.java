package com.lockulockme.locku.zlocksix.module.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.DeleteImageRequestBean;
import com.lockulockme.locku.base.beans.requestbean.MyAlbumRequestBean;
import com.lockulockme.locku.base.beans.responsebean.MyAlbumResponseBean;
import com.lockulockme.locku.databinding.FgMyspaceBinding;
import com.lockulockme.locku.databinding.LayoutDeletePictureBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlocksix.base.utils.OkGoUtils;
import com.lockulockme.locku.zlocksix.common.BaseFragment;
import com.lockulockme.locku.zlocksix.common.PopupWindowBuilder;
import com.lockulockme.locku.zlocksix.module.ui.activity.ImagePreviewActivity;
import com.lockulockme.locku.zlocksix.module.ui.adapter.MySpacePicturesAdapter;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AlbumFragment extends BaseFragment<FgMyspaceBinding> {

    public static final int CODE_CHOOSE_IMAGE = 1;

    List<String> imageList;


    RxPermissions rxPermissions;


    MySpacePicturesAdapter albumsAdapter;

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container) {
        binding = FgMyspaceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.rvMySpace.setLayoutManager(gridLayoutManager);

    }

    @Override
    protected void lazyFetchData() {
        super.lazyFetchData();
        albumsAdapter = new MySpacePicturesAdapter();
        binding.rvMySpace.setAdapter(albumsAdapter);
        binding.llNetwork.rltCommonNetworkError.setOnClickListener(v -> {
            loadAlbumData();
        });
        loadAlbumData();
    }

    private void showProgress() {
        binding.rlProgress.rltProgress.setBackgroundColor(getResources().getColor(R.color.color_5016143c));
        binding.rlProgress.rltProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        binding.rlProgress.rltProgress.setVisibility(View.GONE);
    }

    private void loadAlbumData() {
        MyAlbumRequestBean albumReq = new MyAlbumRequestBean(50, 1);
        showProgress();
        OkGoUtils.getInstance().getMyAlbums(this, albumReq, new NewJsonCallback<List<MyAlbumResponseBean>>() {
            @Override
            public void onSuc(List<MyAlbumResponseBean> response, String msg) {
                hideProgress();
                MyAlbumResponseBean resp = new MyAlbumResponseBean();
                resp.isAddPhoto = true;
                response.add(0, resp);
                albumsAdapter.setNewInstance(response);
                setClickListener(response);
                binding.rvMySpace.setVisibility(View.VISIBLE);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<MyAlbumResponseBean> response) {
                hideProgress();
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
                binding.rvMySpace.setVisibility(View.GONE);
            }
        });
    }

    private void setClickListener(List<MyAlbumResponseBean> response) {
        albumsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                List<String> albumList = new ArrayList<>();
                for (int i = 1; i < response.size(); i++) {
                    albumList.add(response.get(i).imgUrl);
                }
                if (position == 0) {
                    addPhoto();
                } else {
                    ImagePreviewActivity.StartMe(mContext, albumList, position - 1);
                }
            }
        });
        albumsAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (position != 0) {
                    final PopupWindowBuilder popBuilder = PopupWindowBuilder.newBuilder(getContext())
                            .setContentView(R.layout.layout_delete_picture)
                            .setFocusable(true)
                            .setOutsideTouchable(true)
                            .build();
                    final LayoutDeletePictureBinding popRateBing = LayoutDeletePictureBinding.bind(popBuilder.getRootView());
                    popBuilder.show(binding.getRoot(), Gravity.CENTER, 0, 0);
                    popRateBing.tvDeleteCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popBuilder.dismiss();
                        }
                    });
                    popRateBing.tvDeleteConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DeleteImageRequestBean req = new DeleteImageRequestBean();
                            req.id = albumsAdapter.getItem(position).id;
                            showProgress();
                            OkGoUtils.getInstance().deleteImage(AlbumFragment.this, req, new NewJsonCallback<Void>() {
                                @Override
                                public void onSuc(Void response, String msg) {
                                    hideProgress();
                                    albumsAdapter.remove(albumsAdapter.getItem(position));
                                    albumsAdapter.notifyDataSetChanged();
                                    ToastUtils.toastShow(getString(R.string.delete_image_success));
                                }

                                @Override
                                public void onE(int httpCode, int apiCode, String msg, Void response) {
                                    hideProgress();
                                    ToastUtils.toastShow(getString(R.string.delete_image_failed));
                                }
                            });
                            popBuilder.dismiss();
                        }
                    });
                }
                return true;
            }
        });
    }

    public void addPhoto() {
        rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        selectPictures();
                    } else {
                        ToastUtils.toastShow(getString(R.string.no_camera_or_external_storage_permission));
                    }
                });
    }

    private void selectPictures() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_CHOOSE_IMAGE && resultCode == RESULT_OK) {

        }
    }

    private void compress(String filePath) {
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(new File(filePath)).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {
                if (isSuccess) {
                    showProgress();
                    OkGoUtils.getInstance().uploadAlbums(this, "image", new File(outfile), new NewJsonCallback<Void>() {
                        @Override
                        public void onSuc(Void response, String msg) {
                            ToastUtils.toastShow(getString(R.string.upload_image_success));
                            MyAlbumRequestBean albumReq = new MyAlbumRequestBean(50, 1);
                            OkGoUtils.getInstance().getMyAlbums(this, albumReq, new NewJsonCallback<List<MyAlbumResponseBean>>() {
                                @Override
                                public void onSuc(List<MyAlbumResponseBean> response, String msg) {
                                    MyAlbumResponseBean resp = new MyAlbumResponseBean();
                                    resp.isAddPhoto = true;
                                    response.add(0, resp);
                                    albumsAdapter.setNewInstance(response);
                                    setClickListener(response);
                                    hideProgress();
                                }

                                @Override
                                public void onE(int httpCode, int apiCode, String msg, List<MyAlbumResponseBean> response) {
                                    ToastUtils.toastShow(R.string.upload_photo_failed);
                                    hideProgress();
                                }
                            });
                        }

                        @Override
                        public void onE(int httpCode, int apiCode, String msg, Void response) {
                            ToastUtils.toastShow(getString(R.string.upload_image_error));
                            hideProgress();
                        }
                    });
                } else {
                    t.printStackTrace();
                    ToastUtils.toastShow(getString(R.string.upload_image_error));
                }

            }
        });
    }

}