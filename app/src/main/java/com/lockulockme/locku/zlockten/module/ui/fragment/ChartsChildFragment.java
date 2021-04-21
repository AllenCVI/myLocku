package com.lockulockme.locku.zlockten.module.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.PageRequestBean;
import com.lockulockme.locku.base.beans.responsebean.RankItemResponseBean;
import com.lockulockme.locku.databinding.FgChartsChildBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlockten.base.utils.GlideUtils;
import com.lockulockme.locku.zlockten.base.utils.OkGoUtils;
import com.lockulockme.locku.zlockten.common.BaseFragment;
import com.lockulockme.locku.zlockten.module.ui.activity.SheDetailActivity;
import com.lockulockme.locku.zlockten.module.ui.adapter.RankAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChartsChildFragment extends BaseFragment<FgChartsChildBinding> {

    public static int GOD_TYPE = 1;
    public static int MALE_TYPE = 2;
    static final String TYPE_KEY = "bundle_key_type";
    private int type;
    private final int page = 1;
    private final int size = 50;
    private RankAdapter mAdapter;
    private List<TextView> names;
    private List<ImageView> images;
    private List<ImageView> countrys;
    private List<TextView> scores;
    private List<LinearLayout> lls;
    private boolean loading = false;

    public static ChartsChildFragment getInstance(int type) {
        ChartsChildFragment fragment = new ChartsChildFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE_KEY, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void updateData() {
        if (mAdapter == null)
            return;
        if (mAdapter.getData() == null || mAdapter.getData().size() == 0) {
            if (!loading) {
                loadData();
            }
        }
    }

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container) {
        binding = FgChartsChildBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAdapter();
        initHead();
        loadData();
    }

    private void initAdapter() {
        type = getArguments().getInt(TYPE_KEY);
        mAdapter = new RankAdapter(null, type);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        binding.recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            remoteInfo(mAdapter.getItem(position).stringId);
        });
        binding.llNetwork.rltCommonNetworkError.setOnClickListener(v -> {
            loading = false;
            loadData();
        });
    }

    private void remoteInfo(String id) {
        if (type == MALE_TYPE)
            return;
        SheDetailActivity.StartMe(mContext, id);
    }

    private void initHead() {
        names = new ArrayList<>();
        images = new ArrayList<>();
        scores = new ArrayList<>();
        countrys = new ArrayList<>();
        lls = new ArrayList<>();
        names.add(binding.llHeadview.tvNickname1);
        names.add(binding.llHeadview.tvNickname2);
        names.add(binding.llHeadview.tvNickname3);
        images.add(binding.llHeadview.ivHeadimg1);
        images.add(binding.llHeadview.ivHeadimg2);
        images.add(binding.llHeadview.ivHeadimg3);
        scores.add(binding.llHeadview.tvScore1);
        scores.add(binding.llHeadview.tvScore2);
        scores.add(binding.llHeadview.tvScore3);
        countrys.add(binding.llHeadview.ivCountry1);
        countrys.add(binding.llHeadview.ivCountry2);
        countrys.add(binding.llHeadview.ivCountry3);
        lls.add(binding.llHeadview.ll1);
        lls.add(binding.llHeadview.ll2);
        lls.add(binding.llHeadview.ll3);
    }


    private void loadData() {
        if (loading) return;
        loading = true;
        binding.progress.setVisibility(View.VISIBLE);
        OkGoUtils.getInstance().getRankList(this, new PageRequestBean(page, size), type, new NewJsonCallback<List<RankItemResponseBean>>() {
            @Override
            public void onSuc(List<RankItemResponseBean> response, String msg) {
                convertData(response);
                loading = false;
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<RankItemResponseBean> response) {
                super.onE(httpCode, apiCode, msg, response);
                loading = false;
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
                binding.llContent.setVisibility(View.GONE);
                binding.progress.setVisibility(View.GONE);
            }
        });
    }

    private void convertData(List<RankItemResponseBean> response) {
        List<RankItemResponseBean> list = response;
        if (list == null || list.size() == 0)
            return;
        if (list.size() > 3) {
            convertHeadData(list.subList(0, 3));
            mAdapter.setNewInstance(list.subList(3, list.size()));
        } else {
            convertHeadData(list);
        }
        binding.llContent.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.GONE);
        binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
    }

    private void convertHeadData(List<RankItemResponseBean> list) {
        for (int i = 0; i < list.size(); i++) {
            RankItemResponseBean item = list.get(i);
            names.get(i).setText(item.userName);
            GlideUtils.loadCircleImg(mContext, item.icon, images.get(i), R.mipmap.img_charts_head);
            GlideUtils.loadCircleImg(mContext, item.countryUrl, countrys.get(i), R.mipmap.country_placeholder);
            scores.get(i).setText(type == MALE_TYPE ? "+" + item.consumePrice : "+" + item.scores);
            lls.get(i).setOnClickListener(v -> remoteInfo(item.stringId));
            images.get(i).setOnClickListener(v -> remoteInfo(item.stringId));
        }
    }


}
