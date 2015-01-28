package com.redheap.dirtyscopes;

import oracle.adf.controller.TaskFlowId;

import oracle.adfinternal.controller.state.PageFlowStackEntry;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


public class PageFlowScopeKey implements ScopeKey {

    private final TaskFlowId taskFlow;
    private final String instance;
    private final String entry;

    public PageFlowScopeKey(final PageFlowStackEntry pageFlow, final String entry) {
        this.taskFlow = pageFlow.getTaskFlowDefinitionId();
        this.instance = pageFlow.getInstanceToken();
        this.entry = entry;
    }

    @Override
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        return b.append("instance", instance).append("taskflow", taskFlow).append("key", entry).toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || object.getClass() != getClass()) {
            return false;
        }
        if (object == this) {
            return true;
        }
        PageFlowScopeKey other = (PageFlowScopeKey) object;
        EqualsBuilder e = new EqualsBuilder();
        // e.append(taskFlow, other.taskFlow).append(instance, other.instance).append(entry, other.entry);
        e.append(instance, other.instance).append(entry, other.entry);
        return e.isEquals();
    }

    @Override
    public int hashCode() {
        //return new HashCodeBuilder().append(taskFlow).append(instance).append(entry).toHashCode();
        return new HashCodeBuilder().append(instance).append(entry).toHashCode();
    }

}
