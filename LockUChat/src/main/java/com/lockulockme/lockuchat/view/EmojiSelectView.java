package com.lockulockme.lockuchat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.databinding.ItemEmojiBinding;
import com.lockulockme.lockuchat.databinding.ItemEmojiPagerBinding;
import com.lockulockme.lockuchat.databinding.ItemEmojiPointBinding;
import com.lockulockme.lockuchat.databinding.LayoutEmojiBinding;

import java.util.ArrayList;
import java.util.List;

public class EmojiSelectView extends LinearLayout {
    private int startEmojiValue = 0x1F601;
    private int endEmojiValue = 0x1F64F;
    private LayoutEmojiBinding binding;
    private List<List<Integer>> emojiPageValues;
    private PointAdapter pointAdapter;

    private OnEmojiSelectListener listener;

    public EmojiSelectView(Context context) {
        super(context);
        initEmoji(context);
    }

    public EmojiSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initEmoji(context);
    }

    public EmojiSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEmoji(context);
    }

    private void initEmoji(Context context) {
        List<Integer> emojiValues=new ArrayList<>();
        for (int i = startEmojiValue; i < endEmojiValue+1; i++) {
            emojiValues.add(i);
        }
        emojiPageValues = new ArrayList<>();
        for (int i = 0; i < emojiValues.size(); i++) {
            if (i%27==0){
                List<Integer> itemList=new ArrayList<>();
                for (int j=i;j<i+27;j++){
                    if (j<emojiValues.size()){
                        itemList.add(emojiValues.get(j));
                    }else {
                        itemList.add(0);
                    }
                }
                itemList.add(-1);
                emojiPageValues.add(itemList);
            }
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_emoji,this);
//        binding = LayoutEmojiBinding.inflate(inflater,this,false);
        binding=LayoutEmojiBinding.bind(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        EmojiPagerAdapter emojiPageAdapter=new EmojiPagerAdapter(emojiPageValues);
        binding.viewPager.setAdapter(emojiPageAdapter);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        binding.rvPos.setLayoutManager(layoutManager);
        pointAdapter = new PointAdapter(emojiPageValues);
        binding.rvPos.setAdapter(pointAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                selectPosition=position;
                pointAdapter.notifyDataSetChanged();
            }
        });

    }


    public class EmojiPagerAdapter extends BaseQuickAdapter<List<Integer>, BaseViewHolder> {
        public EmojiPagerAdapter(List<List<Integer>> data) {
            super(R.layout.item_emoji_pager,data);
        }
        @Override
        protected void convert(BaseViewHolder baseViewHolder, List<Integer> emojis) {
            ItemEmojiPagerBinding binding=ItemEmojiPagerBinding.bind(baseViewHolder.itemView);
            GridLayoutManager layoutManager=new GridLayoutManager(getContext(),7);
            binding.rvEmoji.setLayoutManager(layoutManager);
            EmojiShowAdapter showAdapter=new EmojiShowAdapter(emojis);
            binding.rvEmoji.setAdapter(showAdapter);
            showAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    if (listener!=null){
                        int value=showAdapter.getItem(position);
                        if (value==-1){
                            listener.onSelectEmoji("/delete");
                        } else if (value>0){
                            listener.onSelectEmoji(unicode2Emoji(value));
                        }

                    }
                }
            });
        }

    }

    public class EmojiShowAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {
        public EmojiShowAdapter(List<Integer> data) {
            super(R.layout.item_emoji,data);
        }
        @Override
        protected void convert(BaseViewHolder baseViewHolder, Integer emoji) {
            ItemEmojiBinding binding=ItemEmojiBinding.bind(baseViewHolder.itemView);
            if (emoji==-1){
                binding.tvEmoji.setText("");
                binding.tvEmoji.setBackgroundResource(R.drawable.ic_delete);
            }else if (emoji==0){
                binding.tvEmoji.setText("");
                binding.tvEmoji.setBackgroundResource(R.color.transparent);
            }else {
                binding.tvEmoji.setText(unicode2Emoji(emoji));
                binding.tvEmoji.setBackgroundResource(R.color.transparent);
            }

        }

    }
    public static String unicode2Emoji(int unicode) {
        return new String(Character.toChars(unicode));
    }
    private int selectPosition;

    public class PointAdapter extends BaseQuickAdapter<List<Integer>, BaseViewHolder> {
        public PointAdapter(List<List<Integer>> data) {
            super(R.layout.item_emoji_point,data);
        }
        @Override
        protected void convert(BaseViewHolder baseViewHolder, List<Integer> emojis) {
            ItemEmojiPointBinding binding=ItemEmojiPointBinding.bind(baseViewHolder.itemView);
            binding.ivPoint.setSelected(selectPosition==baseViewHolder.getAdapterPosition());
        }
    }

    public void setListener(OnEmojiSelectListener listener) {
        this.listener = listener;
    }
}
