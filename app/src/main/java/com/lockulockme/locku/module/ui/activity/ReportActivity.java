package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.ReportRequestBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.eventbus.ReportEvent;
import com.lockulockme.locku.base.utils.ImeUtils;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.PictureSelectorHelper;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.databinding.AcReportBinding;
import com.lockulockme.locku.module.ui.adapter.ReportAdapter;
import com.lockulockme.lockuchat.utils.MatisseHelper;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends BaseActivity<AcReportBinding> {

    static final String ID_KEY = "intent_key_id";
    static final String POSITION_KEY = "intent_key_position";
    static final String ENTER_KEY = "intent_key_enter";
    private String userStringId;
    private List<String> reportList;
    private ReportAdapter mAdapter;
    private File file;
    private int position = -1;//reportRequestBean的type
    private final int CODE_CHOOSE_IMAGE = 1;
    private int enterType;//从哪儿进入的

    public static void StartMe(Context context, String userStringId) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.putExtra(ID_KEY, userStringId);
        context.startActivity(intent);
    }

    public static void StartMe(Context context, String userStringId, int position) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.putExtra(ID_KEY, userStringId);
        intent.putExtra(POSITION_KEY, position);
        context.startActivity(intent);
    }

    public static void StartMe(Context context, String userStringId, int position, int enterType) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.putExtra(ID_KEY, userStringId);
        intent.putExtra(POSITION_KEY, position);
        intent.putExtra(ENTER_KEY, enterType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setNavTitle(getResources().getString(R.string.report));
        userStringId = getIntent().getStringExtra(ID_KEY);
        position = getIntent().getIntExtra(POSITION_KEY, -1);
        enterType = getIntent().getIntExtra(ENTER_KEY, ReportEvent.NORMAL_TYPE);
        initView();
    }


    private void initView() {
        reportList = new ArrayList<>();
        reportList.add(getString(R.string.report_reason1));
        reportList.add(getString(R.string.report_reason2));
        reportList.add(getString(R.string.report_reason3));
        reportList.add(getString(R.string.report_reason4));
        mAdapter = new ReportAdapter();
        binding.rvReport.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvReport.setAdapter(mAdapter);
        mAdapter.setNewInstance(reportList);
        mAdapter.setCheckStr(reportList.get(0));

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                mAdapter.setCheckStr(mAdapter.getItem(position));
            }
        });

        binding.tvSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etContent.getText().toString().trim())) {
                ToastUtils.toastShow(getString(R.string.report_empty_content));
                return;
            }
            if (file == null) {
                ToastUtils.toastShow((getString(R.string.report_empty_img)));
                return;
            }
            reportUser(binding.etContent.getText().toString());
        });
        binding.ivCert.setOnClickListener(v -> {
            if (file == null) {
                selectPictures();
            }
        });
    }

    private void selectPictures() {
        PictureSelectorHelper.getLocalImage(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            List<File> fileList= MatisseHelper.getFile(selectList,getContentResolver());
            if (fileList != null && fileList.size()>0) {
                compress(fileList.get(0));
            }else {
                ToastUtils.toastShow(getString(R.string.upload_image_error));
            }
        }
    }

    private void compress(File srcFile) {
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(srcFile).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {
                if (isSuccess) {
                    file = new File(outfile);
                    binding.ivCert.setImageURI(Uri.parse(outfile));
                } else {
                    t.printStackTrace();
                    ToastUtils.toastShow(getString(R.string.upload_image_error));
                }

            }
        });
    }


    private void reportUser(String content) {
        showLoading();
        OkGoUtils.getInstance().reportUser(ReportActivity.this, new ReportRequestBean(userStringId, content, String.valueOf(position), String.valueOf(checkType())), file, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                ToastUtils.toastShow(R.string.report_suc);
                hideLoading();
                ImeUtils.hideIme(getWindow().getDecorView());
                EventBus.getDefault().post(new ReportEvent(userStringId, enterType));
                finish();
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                super.onE(httpCode, apiCode, msg, response);
                ImeUtils.hideIme(getWindow().getDecorView());
                hideLoading();
                ToastUtils.toastShow(R.string.network_not_available);
            }
        });
    }

    private int checkType() {
        if (reportList != null) {
            for (int i = 0; i < reportList.size(); i++) {
                if (reportList.get(i).equals(mAdapter.getCheckStr())) {
                    return i + 1;
                }
            }
        }
        return 0;
    }
}
