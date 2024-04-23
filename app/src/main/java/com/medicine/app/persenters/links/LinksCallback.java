package com.medicine.app.persenters.links;

import com.medicine.app.models.Link;
import com.medicine.app.persenters.BaseCallback;

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
