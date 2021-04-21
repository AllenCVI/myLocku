package com.lockulockme.locku.module.ui.fragment;

import android.widget.Filter;

import com.lockulockme.locku.base.beans.UserInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class AttentionFilter extends Filter {

    private List<UserInfo> list;
    private List<UserInfo> resultList;
    private onFilterResult onFilterResult;

    public AttentionFilter() {
    }

    public void setOnFilterResult(onFilterResult onFilterResult) {
        this.onFilterResult = onFilterResult;
    }

    public void setList(List<UserInfo> list) {
        this.list = list;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults filterResults = new FilterResults();
        if (list == null) {
            return filterResults;
        }
        resultList = new ArrayList<>();
        for (Iterator<UserInfo> iterator = list.iterator(); iterator.hasNext(); ) {
            UserInfo userInfo = iterator.next();
            if (userInfo.name.trim().toLowerCase().contains(charSequence.toString().trim().toLowerCase())) {
                resultList.add(userInfo);
            }
        }
        filterResults.values = resultList;
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence arg0, FilterResults results) {
        if (onFilterResult != null) {
            onFilterResult.onResult((List<UserInfo>) results.values);
        }
    }

    public interface onFilterResult {
        void onResult(List<UserInfo> list);
    }
}
