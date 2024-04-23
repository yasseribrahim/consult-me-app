package com.consult.me.app.persenters.links;

import com.consult.me.app.models.Link;
import com.consult.me.app.persenters.BaseCallback;

import java.util.List;

public interface LinksCallback extends BaseCallback {
    default void onGetLinksComplete(List<Link> links) {
    }

    default void onSaveLinkComplete() {
    }

    default void onDeleteLinkComplete(Link link) {
    }

    default void onGetLinkComplete(Link link) {
    }
}
