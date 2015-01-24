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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import oracle.adfinternal.controller.state.SessionBasedScopeMap;

public class HashedScopeMap extends AbstractMap<String, byte[]> {

    private final String key;
    private final Map<String, byte[]> digests;

    private static final OutputStream NULL_STREAM = new NullOutputStream();

    public HashedScopeMap(final SessionBasedScopeMap scopeMap) {
        this.key = scopeMap.getSessionKey();
        this.digests = new HashMap<String, byte[]>(scopeMap.size());
        for (final Map.Entry<String, Object> entry : scopeMap.entrySet()) {
            try {
                digests.put(entry.getKey(), digest(entry.getValue()));
            } catch (NotSerializableException e) {
                // TODO: proper exception
                throw new RuntimeException("scope " + key + " contains unserializable element at " + entry.getKey(), e);
            }
        }
    }

    public String getKey() {
        return key;
    }

    public String getHex(final String key) {
        final byte[] bytes = get(key);
        return bytes == null ? null : DatatypeConverter.printHexBinary(bytes);
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
    public Set<Map.Entry<String, byte[]>> entrySet() {
        return Collections.unmodifiableSet(digests.entrySet());
    }

    private static class NullOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
        }
    }

}
