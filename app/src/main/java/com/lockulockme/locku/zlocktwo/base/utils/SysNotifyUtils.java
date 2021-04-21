package com.lockulockme.locku.zlocktwo.base.utils;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;

public class SysNotifyUtils {

    public static  CharSequence getClickableContentFromHtml(String html, OnContentClickListener onHtmlClickListener) {
        Spanned spanned = Html.fromHtml(html);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spanned);
        String newSpannable = filterLastLineFeed(spannableStringBuilder.toString());
        spannableStringBuilder = new SpannableStringBuilder(newSpannable);
        URLSpan[] urlSpans = spannableStringBuilder.getSpans(0, spanned.length(), URLSpan.class);
        for (final URLSpan span : urlSpans) {
            setLinkClickEnabled(spannableStringBuilder, span, onHtmlClickListener);
        }
        return spannableStringBuilder;
    }

    private static void setLinkClickEnabled(final SpannableStringBuilder spannableStringBuilder,
                                            final URLSpan urlSpan, OnContentClickListener onHtmlClickListener) {
        int start = spannableStringBuilder.getSpanStart(urlSpan);
        int end = spannableStringBuilder.getSpanEnd(urlSpan);
        int flags = spannableStringBuilder.getSpanFlags(urlSpan);
        ClickableSpan clickableSpan = new ClickableSpan() {

            public void onClick(View view) {
                if (onHtmlClickListener != null) {
                    onHtmlClickListener.onClickContent(urlSpan.getURL());
                }
            }
        };
        spannableStringBuilder.setSpan(clickableSpan, start, end, flags);
    }

    public static String filterLastLineFeed(String str)
    {
        if (TextUtils.isEmpty(str)) {
            return str;
        }

        if (str.length() == 1) {
            char ch = str.charAt(0);
            if ((ch == '\r') || (ch == '\n')) {
                return "";
            }
            return str;
        }

        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);

        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
            if (str.charAt(lastIdx - 1) == '\n') {
                lastIdx--;
            }
        } else if (last == '\r') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
            if (str.charAt(lastIdx - 1) == '\n') {
                lastIdx--;
            }
        } else {
            lastIdx++;

        }
        return str.substring(0, lastIdx);
    }

    public interface OnContentClickListener {
        void onClickContent(String url);
    }
}
