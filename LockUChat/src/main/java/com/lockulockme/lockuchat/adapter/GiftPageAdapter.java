package com.lockulockme.lockuchat.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.bean.rsp.GiftBeanRsp;
import com.lockulockme.lockuchat.databinding.ItemGiftPageBinding;
import com.lockulockme.lockuchat.ui.GiftPop;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiftPageAdapter extends BaseQuickAdapter<List<GiftBeanRsp>, BaseViewHolder> {
    private Map<Integer, GiftAdapter> giftAdapterMap = new HashMap<>();
    private GiftPop.OnGiftSelect onGiftSelect;
    public GiftPageAdapter() {
        super(R.layout.item_gift_page);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, List<GiftBeanRsp> giftBeans) {
        ItemGiftPageBinding binding = ItemGiftPageBinding.bind(baseViewHolder.itemView);

        GiftAdapter giftAdapter;
        if (giftAdapterMap.containsKey(baseViewHolder.getAdapterPosition())&&giftAdapterMap.get(baseViewHolder.getAdapterPosition())!=null) {
            giftAdapter = giftAdapterMap.get(baseViewHolder.getAdapterPosition());
        }else {
            giftAdapter = new GiftAdapter();
            giftAdapterMap.put(baseViewHolder.getAdapterPosition(),giftAdapter);
            giftAdapter.setNewInstance(giftBeans);
        }
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(giftAdapter);
        giftAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {

                for (Map.Entry<Integer, GiftAdapter> entry : giftAdapterMap.entrySet()) {
                    entry.getValue().setPos(-1);
                    entry.getValue().notifyDataSetChanged();
                }
                giftAdapter.setPos(position);
                giftAdapter.notifyDataSetChanged();
                if (onGiftSelect!=null) onGiftSelect.onSelect(giftAdapter.getItem(position));
            }
        });
    }


    public void setOnGiftSelect(GiftPop.OnGiftSelect onGiftSelect) {
        this.onGiftSelect = onGiftSelect;
    }
}
