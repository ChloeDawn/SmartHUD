package net.insomniakitten.smarthud.inventory; 
 
/*
 *  Copyright 2017 InsomniaKitten
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ComparableNonNullList<E> extends NonNullList<E> {

    private final NonNullList<E> delegate;
    private final E type;

    public static <E> ComparableNonNullList<E> create() {
        return new ComparableNonNullList<>();
    }

    protected ComparableNonNullList(NonNullList<E> delegate, @Nullable E type) {
        super(delegate, type);
        this.delegate = delegate;
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    protected ComparableNonNullList() {
        this(NonNullList.create(), null);
    }

    @Nonnull
    public E get(int ordinal) {
        return delegate.get(ordinal);
    }

    @Nonnull
    public E set(int ordinal, E object) {
        Validate.notNull(object);
        return delegate.set(ordinal, object);
    }

    public void add(int ordinal, E object) {
        Validate.notNull(object);
        delegate.add(ordinal, object);
    }

    @Nonnull
    public E remove(int ordinal) {
        return delegate.remove(ordinal);
    }

    public int size() {
        return delegate.size();
    }

    public void clear() {
        if (type == null) {
            super.clear();
        } else {
            for (int i = 0; i < this.size(); ++i) {
                this.set(i, type);
            }
        }
    }

}
