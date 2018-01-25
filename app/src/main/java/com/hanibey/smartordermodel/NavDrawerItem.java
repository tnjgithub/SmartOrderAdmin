package com.hanibey.smartordermodel;

import android.widget.ImageView;

/**
 * Created by Tanju on 17.12.2017.
 */

public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private int iconId;


    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title, int iconId) {
        this.showNotify = showNotify;
        this.title = title;
        this.iconId = iconId;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
