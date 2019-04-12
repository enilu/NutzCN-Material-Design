package net.wendal.nutzbook.model.entity;

import net.wendal.nutzbook.R;

public enum TabType {


    duanzi(R.string.tab_duanzi),
    news(R.string.tab_news),
    nb(R.string.tab_nb),
    pic(R.string.tab_pic);

    private int nameId;

    TabType(int nameId) {
        this.nameId = nameId;
    }

    public int getNameId() {
        return nameId;
    }

}
