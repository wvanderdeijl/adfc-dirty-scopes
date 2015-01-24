package com.redheap.dirtyscopes;

import oracle.adf.controller.TaskFlowId;

import oracle.adfinternal.controller.state.PageFlowStackEntry;

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
        return b.append("taskflow", taskFlow).append("instance", instance).append("key", entry).toString();
    }

}
