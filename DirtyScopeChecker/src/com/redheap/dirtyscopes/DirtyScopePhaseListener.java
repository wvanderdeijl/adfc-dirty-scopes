package com.redheap.dirtyscopes;

import java.util.Collection;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.ViewPortContext;
import oracle.adf.controller.internal.ViewPortContextFwk;

import oracle.adfinternal.controller.state.AdfcContext;
import oracle.adfinternal.controller.state.RootViewPortContextImpl;

public class DirtyScopePhaseListener implements PhaseListener {

    public DirtyScopePhaseListener() {
        super();
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
            ViewPortContext context = ControllerContext.getInstance().getCurrentRootViewPort();
            RootViewPortContextImpl r = (RootViewPortContextImpl)context;
            Collection<String> childViewPortClientIds = r.getChildViewPortClientIds();
            Collection<String> childViewPortIds = r.getChildViewPortIds();
            RootViewPortContextImpl contextImpl = r.getRootViewPortImpl();
            ViewPortContextFwk contextFwk = r.getRootViewPort();
            final AdfcContext adfcContext = AdfcContext.getCurrentInstance();
            final String name = adfcContext.getClass().getName();
            System.out.println(context);
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        if (PhaseId.RESTORE_VIEW.equals(event.getPhaseId())) {
        }
    }

}
