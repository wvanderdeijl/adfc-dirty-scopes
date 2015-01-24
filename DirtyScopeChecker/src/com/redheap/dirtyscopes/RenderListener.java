package com.redheap.dirtyscopes;

import javax.faces.context.FacesContext;

import oracle.adf.share.ADFContext;

import oracle.adfinternal.view.faces.lifecycle.PageRenderListener;

public class RenderListener implements PageRenderListener {

    public RenderListener() {
        super();
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
        System.out.println("clustering: " + DirtyScopePhaseListener.isReplicatingCluster());
        System.out.println("dirty: " + DirtyScopePhaseListener.isScopesDirty());

    }
}
