package com.redheap.dirtyscopes;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import oracle.adf.controller.config.ControllerConfig;
import oracle.adf.controller.config.ControllerProperty;
import oracle.adf.share.ADFContext;

import oracle.adfinternal.view.faces.lifecycle.PageRenderListener;

public class DirtyScopeListener implements PhaseListener, PageRenderListener {

    public DirtyScopeListener() {
        super();
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        ADFContext.getCurrent().getRequestScope().put(ScopesSnapshot.class.getName(), new ScopesSnapshot());
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        // we cannot do the checking after render phase, as RichPhaseListener.renderSucceeded (called from
        // DocumentRenderer.encodeAll) always marks the scopes as dirty. That's why we also implement PageRenderListener
        // so we have a hook just before RichPhaseListener.renderSucceeded but still after rendering.
    }

    @Override
    public void renderStarting(FacesContext context) {
    }

    @Override
    public void renderEnding(FacesContext context) {
        // gets invoked before DocumentRenderer.encodeAll invokes RichPhaseListener.renderSucceeded which
        // always marks the scopes dirty (consider that a bug)
        Object old = ADFContext.getCurrent().getRequestScope().get(ScopesSnapshot.class.getName());
        if (!(old instanceof ScopesSnapshot)) {
            throw new IllegalStateException("unable to find saved state from start of request");
        }
        ScopesSnapshot beginShot = (ScopesSnapshot) old;
        ScopesSnapshot endShot = new ScopesSnapshot();

        System.out.println("*** renderEnding");
        System.out.println("BEGIN REQUEST:\n" + beginShot);
        System.out.println("END REQUEST:\n" + endShot);
        System.out.println("DIFFERENCES:\n" + endShot.compare(beginShot));
        System.out.println("clustering: " + DirtyScopeListener.isReplicatingCluster());
        System.out.println("dirty: " + DirtyScopeListener.isScopesDirty());
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
        // Code below also compiles in 11g, RequestState is deprected in 12c so there you could use
        // oracle.adfinternal.controller.state.AdfcContext.getCurrentInstance().getRequestState().isScopesDirty()
        return oracle.adfinternal.controller.state.RequestState.getInstance().isScopesDirty();
    }

}
