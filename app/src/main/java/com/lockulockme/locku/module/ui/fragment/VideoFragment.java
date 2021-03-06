package com.lockulockme.locku.module.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.lockulockme.locku.common.BaseFragment;
import com.lockulockme.locku.databinding.FgMyspaceBinding;

public class VideoFragment extends BaseFragment<FgMyspaceBinding> {

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container) {
        binding = FgMyspaceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
