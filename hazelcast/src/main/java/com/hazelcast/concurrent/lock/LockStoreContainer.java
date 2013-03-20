/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.concurrent.lock;

import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConcurrencyUtil.ConstructorFunction;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @mdogan 2/12/13
 */
public class LockStoreContainer {

    private final LockService lockService;
    private final int partitionId;
    private final ConcurrentMap<ILockNamespace, LockStoreImpl> lockStores = new ConcurrentHashMap<ILockNamespace, LockStoreImpl>();

    private final ConstructorFunction<ILockNamespace, LockStoreImpl> lockStoreConstructor
                = new ConstructorFunction<ILockNamespace, LockStoreImpl>() {
        public LockStoreImpl createNew(ILockNamespace key) {
            return new LockStoreImpl(key, 1, 0);
        }
    };

    public LockStoreContainer(LockService lockService, int partitionId) {
        this.lockService = lockService;
        this.partitionId = partitionId;
    }

    public LockStoreImpl createLockStore(ILockNamespace namespace, int backupCount, int asyncBackupCount) {
        final LockStoreImpl ls = new LockStoreImpl(namespace, backupCount, asyncBackupCount);
        final LockStoreImpl current;
        if ((current = lockStores.putIfAbsent(namespace, ls)) != null) {
            if (current.getBackupCount() != ls.getBackupCount()
                    || current.getAsyncBackupCount() != ls.getAsyncBackupCount()) {
                throw new IllegalStateException("LockStore for namespace[" + namespace + "] is already created!");
            }
            return current;
        }
        return ls;
    }

    public void destroyLockStore(ILockNamespace namespace) {
        final LockStoreImpl lockStore = lockStores.remove(namespace);
        if (lockStore != null) {
            lockStore.clear();
        }
    }

    public LockStoreImpl getLockStore(ILockNamespace namespace) {
        return ConcurrencyUtil.getOrPutIfAbsent(lockStores, namespace, lockStoreConstructor);
    }

    Collection<LockStoreImpl> getLockStores() {
        return Collections.unmodifiableCollection(lockStores.values());
    }

    void clear() {
        for (LockStoreImpl lockStore : lockStores.values()) {
            lockStore.clear();
        }
        lockStores.clear();
    }

    int getMaxBackupCount() {
        int max = 0;
        for (LockStoreImpl ls : lockStores.values()) {
            max = Math.max(max, ls.getTotalBackupCount());
        }
        return max;
    }

    int getPartitionId() {
        return partitionId;
    }

    void put(LockStoreImpl ls) {
        lockStores.put(ls.getNamespace(), ls);
    }
}
