package com.redheap.dirtyscopes;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import oracle.adf.controller.ViewPortContext;
import oracle.adf.controller.metadata.ActivityId;

import oracle.adfinternal.controller.state.AdfcContext;
import oracle.adfinternal.controller.state.PageFlowStack;
import oracle.adfinternal.controller.state.PageFlowStackEntry;
import oracle.adfinternal.controller.state.ViewPortContextImpl;

import org.apache.commons.lang.StringUtils;

public class ViewPortSnapshot {

    private final String clientId; // taskflow binding name in parent
    private final ActivityId viewActivityId; // taskflowid@viewactivity
    private final String viewPortId; // for example 1dc6ynhmgf_4
    private final HashedScopeMap viewScope;
    private final List<HashedScopeMap> pageFlowScopes;
    private final List<ViewPortSnapshot> children;

    public ViewPortSnapshot(ViewPortContext viewport) {
        final ViewPortContextImpl viewportimpl = (ViewPortContextImpl) viewport;
        clientId = viewportimpl.getClientId();
        viewActivityId = viewportimpl.getViewActivityId();
        viewPortId = viewportimpl.getViewPortId();
        viewScope = new HashedScopeMap(viewportimpl.getViewScope(AdfcContext.getCurrentInstance()));
        final PageFlowStack flowStack = viewportimpl.getPageFlowStack();
        pageFlowScopes = new ArrayList<HashedScopeMap>(flowStack.getSnapshotCount());
        for (int i = 0, n = flowStack.getSnapshotCount(); i < n; i++) {
            final PageFlowStackEntry stackEntry = flowStack.getEntry(i);
            pageFlowScopes.add(new HashedScopeMap(stackEntry.getPageFlowScope(AdfcContext.getCurrentInstance())));
        }

        Collection<String> childIds = viewportimpl.getChildViewPortClientIds();
        children = new ArrayList<ViewPortSnapshot>(childIds.size());
        for (String childId : childIds) {
            children.add(new ViewPortSnapshot(viewportimpl.getChildViewPortByClientId(childId)));
        }
    }

    public void dump(final PrintStream out) {
        dump(out, 0);
    }

    /**
     * Gets the viewport-scope and all pageflow-scopes from the pageflow-stack for the rootviewport (from the unbounded
     * task flow) as well as all nested viewports (regions).
     * @return
     */
    public List<HashedScopeMap> getScopes() {
        final List<HashedScopeMap> retval = new ArrayList<HashedScopeMap>(1 + pageFlowScopes.size());
        getScopes(retval);
        return retval;
    }

    private void getScopes(final List<HashedScopeMap> retval) {
        retval.add(viewScope);
        retval.addAll(pageFlowScopes);
        for (ViewPortSnapshot child : children) {
            child.getScopes(retval);
        }
    }

    private void dump(final PrintStream out, final int level) {
        final String prefix = StringUtils.repeat(".", level * 2);
        out.println(prefix + "viewport: " + viewPortId + " currently at " + viewActivityId + " running in " + clientId);
        out.println(prefix + viewScope.size() + " entries in viewscope " + viewScope.getKey());
        dumpEntries(viewScope, out, prefix + "#");

        // dump pageFlow stack
        out.println(prefix + pageFlowScopes.size() + " level pageflow-stack:");
        for (int i = 0, n = pageFlowScopes.size(); i < n; i++) {
            HashedScopeMap flowScope = pageFlowScopes.get(i);
            final String scopePrefix = prefix + StringUtils.repeat(">", i + 1);
            out.println(scopePrefix + flowScope.size() + " entries in " + flowScope.getKey());
            dumpEntries(flowScope, out, scopePrefix);
        }

        // dump child viewports
        for (final ViewPortSnapshot child : children) {
            child.dump(out, level + 1);
        }
    }

    private void dumpEntries(final HashedScopeMap scope, final PrintStream out, final String prefix) {
        for (final String scopeKey : scope.keySet()) {
            out.println(prefix + scopeKey + "[hash:" + scope.getHex(scopeKey) + "]");
        }
    }

}
