package com.lockulockme.locku.zlockfour.base.utils;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class CustomGlideModule extends AppGlideModule {

    public boolean isManifestParsingEnabled() {
        return false;
    }
}
