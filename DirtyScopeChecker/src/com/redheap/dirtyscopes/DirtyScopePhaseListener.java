package com.redheap.dirtyscopes;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.config.ControllerConfig;
import oracle.adf.controller.config.ControllerProperty;
import oracle.adf.share.ADFContext;

import oracle.adfinternal.controller.state.AdfcContext;

import org.apache.commons.lang.StringUtils;

public class DirtyScopePhaseListener implements PhaseListener {

    public DirtyScopePhaseListener() {
        super();
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        if (PhaseId.RESTORE_VIEW.equals(event.getPhaseId())) {
            ADFContext.getCurrent().getRequestScope().put(ScopesSnapshot.class.getName(), new ScopesSnapshot());
        }
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        // RichPhaseListener.renderSucceeded will always mark dirty before this
        // invoked from DocumentRenderer.encodeAll
    }

    public void dump() {
        System.out.println("clustering: " + isReplicatingCluster());
        System.out.println("dirty: " + isScopesDirty());
        ViewPortSnapshot ss = new ViewPortSnapshot(ControllerContext.getInstance().getCurrentRootViewPort());
        ss.dump(System.out);
        System.out.println(StringUtils.repeat("*", 50));

        oracle.adfinternal.controller.state.SessionBasedScopes adfScopes =
            AdfcContext.getCurrentInstance().getSessionBasedScopes();
        System.out.println(adfScopes);
    }

    /**
     * Checks if adf-scope-ha-support is enabled in adf-config.xml that indicates that ADF Controller should replicate
     * its scopes (viewScopes and pageFlowScopes) to the other nodes in a cluster.
     * @return {@code true} if {@code adf-scope-ha-support} is set to true in adf-config.xml, otherwise {@code false}
     */
    public static boolean isReplicatingCluster() {
        return Boolean.TRUE.equals(ControllerConfig.getProperty(ControllerProperty.ADF_SCOPE_HA_SUPPORT));
    }

    /**
     * Checks if the ADF Controller scopes have been marked dirty by the framework or an explicit call to
     * oracle.adf.controller.ControllerContext#markScopeDirty.
     * @return {@code true} if the ADF Controller scopes have been marked dirty and will be replicated to other nodes
     * in the cluster at the end of the request, otherwise {@code false}
     */
    public static boolean isScopesDirty() {
        // RequestState is deprected in 12c so you could use
        // oracle.adfinternal.controller.state.AdfcContext.getCurrentInstance().getRequestState().isScopesDirty()
        return oracle.adfinternal.controller.state.RequestState.getInstance().isScopesDirty();
    }

}
