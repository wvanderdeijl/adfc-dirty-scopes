package com.redheap.dirtyscopes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ScopeDiffs {

    private final List<ScopeKey> changed;
    private final List<ScopeKey> newKeys;

    public ScopeDiffs(List<ScopeKey> changed, List<ScopeKey> newKeys) {
        this.changed = new ArrayList<ScopeKey>(changed == null ? Collections.<ScopeKey>emptyList() : changed);
        this.newKeys = new ArrayList<ScopeKey>(newKeys == null ? Collections.<ScopeKey>emptyList() : newKeys);
    }

    public List<ScopeKey> getChanged() {
        return Collections.unmodifiableList(changed);
    }

    public List<ScopeKey> getNewKeys() {
        return Collections.unmodifiableList(newKeys);
    }

    public boolean isEmpty() {
        return changed.isEmpty() && newKeys.isEmpty();
    }

    @Override
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        for (final ScopeKey c : changed) {
            b.append("changed", c);
        }
        for (final ScopeKey n : newKeys) {
            b.append("new", n);
        }
        return b.toString();

    }

}
