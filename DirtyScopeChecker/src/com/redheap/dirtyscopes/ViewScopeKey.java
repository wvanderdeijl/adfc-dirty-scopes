package com.redheap.dirtyscopes;

import oracle.adf.controller.metadata.ActivityId;

import oracle.adfinternal.controller.state.ViewPortContextImpl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


public class ViewScopeKey implements ScopeKey {

    private final String clientId; // taskflow binding name in parent
    private final ActivityId viewActivityId; // taskflowid@viewactivity
    private final String viewPortId; // for example 1dc6ynhmgf_4
    private final String entry;

    public ViewScopeKey(final ViewPortContextImpl viewPort, final String entry) {
        this.clientId = viewPort.getClientId();
        this.viewActivityId = viewPort.getViewActivityId();
        this.viewPortId = viewPort.getViewPortId();
        this.entry = entry;
    }

    @Override
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        b.append("activity", viewActivityId).append("region", clientId).append("viewPort", viewPortId);
        b.append("key", entry);
        return b.toString();
    }

}
