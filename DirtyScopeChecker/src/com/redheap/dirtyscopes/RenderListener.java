package com.redheap.dirtyscopes;

import javax.faces.context.FacesContext;

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
        System.out.println("*** renderEnding");
        new DirtyScopePhaseListener().dump();
    }
}
