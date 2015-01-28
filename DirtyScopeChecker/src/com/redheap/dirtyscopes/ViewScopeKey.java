package com.redheap.dirtyscopes;

import oracle.adf.controller.metadata.ActivityId;

import oracle.adfinternal.controller.state.ViewPortContextImpl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
        b.append("viewPort", viewPortId).append("region", clientId).append("activity", viewActivityId);
        b.append("key", entry);
        return b.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || object.getClass() != getClass()) {
            return false;
        }
        if (object == this) {
            return true;
        }
        ViewScopeKey other = (ViewScopeKey) object;
        EqualsBuilder e = new EqualsBuilder();
        //        if (viewActivityId != null && other.viewActivityId != null) {
        //            e.append(viewActivityId, other.viewActivityId);
        //        }
        //        e.append(clientId, other.clientId);
        e.append(viewPortId, other.viewPortId).append(entry, other.entry);
        return e.isEquals();
    }

    @Override
    public int hashCode() {
        //return new HashCodeBuilder().append(clientId).append(viewActivityId).append(viewActivityId).append(entry).toHashCode();
        return new HashCodeBuilder().append(viewActivityId).append(entry).toHashCode();
    }

}
