package com.redheap.dirtyscopes;

import java.util.Collection;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.ViewPortContext;
import oracle.adf.controller.internal.ViewPortContextFwk;

import oracle.adfinternal.controller.state.AdfcContext;
import oracle.adfinternal.controller.state.ChildViewPortContextImpl;
import oracle.adfinternal.controller.state.PageFlowScope;
import oracle.adfinternal.controller.state.PageFlowStack;
import oracle.adfinternal.controller.state.PageFlowStackEntry;
import oracle.adfinternal.controller.state.RootViewPortContextImpl;
import oracle.adfinternal.controller.state.SessionBasedScopeMap;
import oracle.adfinternal.controller.state.ViewPortContextImpl;
import oracle.adfinternal.controller.state.ViewScope;

import org.apache.commons.lang.StringUtils;

public class DirtyScopePhaseListener implements PhaseListener {

    public DirtyScopePhaseListener() {
        super();
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    private void processViewPort(ViewPortContextImpl vp, int level) {

        // each ViewPort has a single ViewScope and a stack of PageFlowScopes ending with the PageFlowScope
        // for the ViewScope

        final String prefix = StringUtils.repeat("..", level);

        //System.out.println(vp.getClientId()); // taskflow-binding name (or null for UTF)
        //System.out.println(vp.getViewActivityId()); // taskflowId@viewId
        //System.out.println(vp.getViewPortId()); // for example: 1dc6ynhmgf_4
        final ViewScope viewScope = vp.getViewScope(AdfcContext.getCurrentInstance());
        System.out.println(prefix + "viewScope: " + viewScope.getSessionKey());
        PageFlowScope flowScope = vp.getPageFlowScopeMap(AdfcContext.getCurrentInstance());
        System.out.println(prefix + "flowScope: " + flowScope.getSessionKey());

        // inspect taskflow call stack. PageFlowScope associated with ViewScope should be deepest
        // level (highest number).
        PageFlowStack flowStack = vp.getPageFlowStack();
        int count = flowStack.getSnapshotCount();
        System.out.println(prefix + "flowStack: " + count + " items");
        for (int i=0; i<count; i++) {
            PageFlowStackEntry entry = flowStack.getEntry(i);
            PageFlowScope stackFlowScope = entry.getPageFlowScope(AdfcContext.getCurrentInstance());
            //System.out.println(prefix + "stackItem " + i + ": " + entry);
            System.out.println(prefix + "stackItem " + i + ": " + stackFlowScope.getSessionKey());
        }

        Collection<String> childViewPortClientIds = vp.getChildViewPortClientIds();
        for (String id : vp.getChildViewPortClientIds()) {
            ViewPortContextFwk childViewPort = vp.getChildViewPortByClientId(id);
            processViewPort((ViewPortContextImpl) childViewPort, level+1);
        }
     }

    @Override
    public void afterPhase(PhaseEvent event) {
        if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
            ViewPortContext context = ControllerContext.getInstance().getCurrentRootViewPort();
            RootViewPortContextImpl r = (RootViewPortContextImpl)context;
            Collection<String> childViewPortClientIds = r.getChildViewPortClientIds();
            String childViewPortClientId = childViewPortClientIds.iterator().next();
            Collection<String> childViewPortIds = r.getChildViewPortIds();
            String childViewPortId = childViewPortIds.iterator().next();

            oracle.adfinternal.controller.state.SessionBasedScopes scopes = AdfcContext.getCurrentInstance().getSessionBasedScopes();
            System.out.println(StringUtils.repeat("=", 50));
            processViewPort(r, 0);
            System.out.println(StringUtils.repeat("=", 50));
            System.out.println(StringUtils.repeat("*", 50));
            ViewPortSnapshot ss = new ViewPortSnapshot(ControllerContext.getInstance().getCurrentRootViewPort());
            ss.dump(System.out);
            System.out.println(StringUtils.repeat("*", 50));

            String key_2 = r.getViewScope(AdfcContext.getCurrentInstance()).getSessionKey();
            System.out.println("utf view scope key: "+ key_2);
            final PageFlowScope rootFlowMap = r.getPageFlowScopeMap(AdfcContext.getCurrentInstance());
            String id_2 = rootFlowMap.getInstanceId();
            String key_3 = rootFlowMap.getSessionKey();
            System.out.println("utf pageflow scope key: " + key_3);

            ViewPortContextFwk childViewPort = r.getChildViewPortByClientId(childViewPortClientId);
            String id = childViewPort.getViewPortId();

            oracle.adfinternal.controller.state.ChildViewPortContextImpl childImpl =
                (ChildViewPortContextImpl) childViewPort;
            PageFlowStack pfStack = childImpl.getPageFlowStack();
            pfStack.getClass().getName();
            // TODO: should we iterate pageFlowStack?
            oracle.adfinternal.controller.state.PageFlowStack pfs = pfStack;
            int count = pfs.getSnapshotCount();
            PageFlowStackEntry entry_2 = pfs.getEntry(0);
            PageFlowStackEntry entry = pfStack.peek();
            // pfStack.get

            ViewScope childViewScope = childImpl.getViewScope(AdfcContext.getCurrentInstance());
            String key = childViewScope.getSessionKey();
            System.out.println("child view scope key: " + key);
            PageFlowScope flowScope = childImpl.getPageFlowScopeMap(AdfcContext.getCurrentInstance());
            String instanceId = flowScope.getInstanceId();
            String sessionKey = flowScope.getSessionKey();
            System.out.println("child pageflow scope key: " + sessionKey);

            // see oracle.adfinternal.controller.util.diagnostic.AdfcDiagnosticDump.executeDump
            // starts from RootViewPortContext
            // looks at ViewPort.getPageFlowStack
            // iterates mChildViewPortsByViewPortId
            // for each child does .getViewScope (and perhaps getPageFlowSomething)

            RootViewPortContextImpl contextImpl = r.getRootViewPortImpl();
            ViewPortContextFwk contextFwk = r.getRootViewPort();
            final AdfcContext adfcContext = AdfcContext.getCurrentInstance();
            final String name = adfcContext.getClass().getName(); // oracle.adfinternal.controller.state.AdfcContext
            oracle.adfinternal.controller.state.SessionBasedScopes adfScopes = adfcContext.getSessionBasedScopes();
            String name_2 = adfScopes.getClass().getName(); // oracle.adfinternal.controller.state.SessionBasedScopes
            SessionBasedScopeMap scope = adfScopes.getScope(childViewPortId);
            //String name_3 = scope.getClass().getName();
            System.out.println(context);
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        if (PhaseId.RESTORE_VIEW.equals(event.getPhaseId())) {
        }
    }

}
