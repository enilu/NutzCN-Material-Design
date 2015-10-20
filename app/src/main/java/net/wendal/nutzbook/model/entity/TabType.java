package net.wendal.nutzbook.model.entity;

import net.wendal.nutzbook.R;

public enum TabType {


    ask(R.string.tab_ask),

    news(R.string.tab_news),

    share(R.string.tab_share),

    job(R.string.tab_job),

    nb(R.string.tab_nb),

    shortit(R.string.tab_shortit);

    private int nameId;

    TabType(int nameId) {
        this.nameId = nameId;
    }

    public int getNameId() {
        return nameId;
    }

}
