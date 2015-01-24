package com.redheap.dirtyscopes;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.ViewPortContext;

import oracle.adfinternal.controller.state.AdfcContext;
import oracle.adfinternal.controller.state.PageFlowScope;
import oracle.adfinternal.controller.state.PageFlowStack;
import oracle.adfinternal.controller.state.PageFlowStackEntry;
import oracle.adfinternal.controller.state.ViewPortContextImpl;
import oracle.adfinternal.controller.state.ViewScope;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ScopesSnapshot extends AbstractMap<ScopeKey, byte[]> {

    private Map<ScopeKey, byte[]> entries = new LinkedHashMap<ScopeKey, byte[]>(100);

    private static final OutputStream NULL_STREAM = new NullOutputStream();

    public ScopesSnapshot(final ViewPortContext rootViewPort) {
        addViewPort((ViewPortContextImpl) rootViewPort);
    }

    public ScopesSnapshot() {
        this(ControllerContext.getInstance().getCurrentRootViewPort());
    }

    private void addViewPort(final ViewPortContextImpl viewPort) {
        // add all PageFlowScopes from pageFlow stack (pageflow for given viewPort and all calling pageFlows)
        final PageFlowStack flowStack = viewPort.getPageFlowStack();
        // do not use flowStack.getSnapshotCount() as that includes the popped entries after taskflow return
        for (int i = 0, n = flowStack.size(); i < n; i++) {
            addPageFlowScope(flowStack.getEntry(i));
        }
        // add ViewScope for given viewPort
        addViewScope(viewPort);
        // add all child viewscopes/viewports
        for (final String childId : viewPort.getChildViewPortClientIds()) {
            addViewPort((ViewPortContextImpl) viewPort.getChildViewPortByClientId(childId));
        }
    }

    private void addViewScope(final ViewPortContextImpl viewPort) {
        ViewScope viewScope = viewPort.getViewScope(AdfcContext.getCurrentInstance());
        for (Map.Entry<String, Object> entry : viewScope.entrySet()) {
            addEntry(new ViewScopeKey(viewPort, entry.getKey()), entry.getValue());
        }
    }

    private void addPageFlowScope(final PageFlowStackEntry pageFlow) {
        PageFlowScope flowScope = pageFlow.getPageFlowScope(AdfcContext.getCurrentInstance());
        for (Map.Entry<String, Object> entry : flowScope.entrySet()) {
            addEntry(new PageFlowScopeKey(pageFlow, entry.getKey()), entry.getValue());
        }
    }

    private void addEntry(final ScopeKey key, final Object obj) {
        try {
            entries.put(key, digest(obj));
        } catch (NotSerializableException e) {
            throw new RuntimeException("unable to serialize " + key, e);
        }
    }

    private byte[] digest(Object object) throws NotSerializableException {
        try {
            // MD5 produces smallest hashes (= lowest memory consumption)
            final DigestOutputStream dos = new DigestOutputStream(NULL_STREAM, MessageDigest.getInstance("MD5"));
            final ObjectOutputStream oos = new ObjectOutputStream(dos);
            oos.writeObject(object);
            oos.close();
            return dos.getMessageDigest().digest();
        } catch (NotSerializableException e) {
            throw e;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Map.Entry<ScopeKey, byte[]>> entrySet() {
        return Collections.unmodifiableSet(entries.entrySet());
    }

    @Override
    public String toString() {
        //return ToStringBuilder.reflectionToString(entries, ToStringStyle.MULTI_LINE_STYLE);
        ToStringBuilder b = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        for (Map.Entry<ScopeKey, byte[]> e : entries.entrySet()) {
            b.append(e.getKey().toString(), DatatypeConverter.printHexBinary(e.getValue()));
        }
        return b.toString();

    }

    private static class NullOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
        }
    }

}
