package com.lockulockme.lockuchat.adapter.chat;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.attach.QAAnswerMsgAttachment;
import com.lockulockme.lockuchat.attach.QAMsgAttachment;
import com.lockulockme.lockuchat.bean.QAAnswer;
import com.lockulockme.lockuchat.databinding.ItemChatQaBinding;
import com.lockulockme.lockuchat.utils.im.IMMsgHelper;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class QAMsgProvider extends MyBaseItemProvider<IMMessage> {
    @Override
    public int getItemViewType() {
        return MsgType.QA;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_qa;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage imMessage) {
        ItemChatQaBinding binding=ItemChatQaBinding.bind(baseViewHolder.itemView);
        //如果不是最后一条，则隐藏
        ViewGroup.LayoutParams layoutParams = binding.rootQa.getLayoutParams();
        layoutParams.height = 0;
        binding.rootQa.setLayoutParams(layoutParams);
        binding.rootQa.setVisibility(View.GONE);

//        if (getChatMsgAdapter().getItemPosition(imMessage) != getChatMsgAdapter().getItemCount() -1) {
//            layoutParams.height = 0;
//            binding.rootQa.setLayoutParams(layoutParams);
//            binding.rootQa.setVisibility(View.GONE);
//            return;
//        }else {
//            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            binding.rootQa.setVisibility(View.VISIBLE);
//            binding.rootQa.setLayoutParams(layoutParams);
//        }
//
//
//        QAMsgAttachment attachment= (QAMsgAttachment) imMessage.getAttachment();
//        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
//        binding.rvAnswer.setLayoutManager(layoutManager);
//        final QAAnswerAdapter qaAnswerAdapter=new QAAnswerAdapter(attachment.getAnswers());
//        binding.rvAnswer.setAdapter(qaAnswerAdapter);
//
//        binding.tvTitle.setText(attachment.getQuestion());
//        binding.getRoot().setGravity(isLeftMsg(imMessage)? Gravity.LEFT :Gravity.RIGHT);
//
//        qaAnswerAdapter.setOnItemClickListener((adapter, view, position) -> {
//            ViewGroup.LayoutParams layoutParams1 = binding.rootQa.getLayoutParams();
//            layoutParams1.height = 0;
//            binding.rootQa.setLayoutParams(layoutParams1);
//            binding.rootQa.setVisibility(View.GONE);
//
//            QAAnswerMsgAttachment answerMsgAttachment=new QAAnswerMsgAttachment();
//            QAAnswer answer=qaAnswerAdapter.getItem(position);
//            answerMsgAttachment.setAnswer(answer.answer);
//            answerMsgAttachment.setIds(answer.ids);
//            IMMessage qaIMMessage= MessageBuilder.createCustomMessage(imMessage.getSessionId(), SessionTypeEnum.P2P, answerMsgAttachment);
//            IMMsgHelper.storeRemoteExtension(qaIMMessage);
//            Map<String, Object> extensions = qaIMMessage.getRemoteExtension();
//            extensions.put("qaAnswer", "1");
//            qaIMMessage.setRemoteExtension(extensions);
//
//            getMsgController().sendMsg(qaIMMessage);
//        });
    }
}
