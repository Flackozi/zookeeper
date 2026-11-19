/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zookeeper.server.watch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class WatchManagerOptimizedProjectTest {

    private final WatchManagerOptimized manager = new WatchManagerOptimized();

    @AfterEach
    void tearDown() {
        manager.shutdown();
    }

    @Test
    void watchFiresOnlyOnceUntilRegisteredAgain() {
        List<WatchedEvent> events = new ArrayList<>();
        Watcher watcher = events::add;
        String path = "/config";

        assertTrue(manager.addWatch(path, watcher));
        manager.triggerWatch(path, EventType.NodeDataChanged, 1L, null);
        manager.triggerWatch(path, EventType.NodeDataChanged, 2L, null);

        assertEquals(1, events.size());
        assertEquals(1L, events.get(0).getZxid());

        assertTrue(manager.addWatch(path, watcher));
        manager.triggerWatch(path, EventType.NodeDataChanged, 3L, null);

        assertEquals(2, events.size());
        assertEquals(3L, events.get(1).getZxid());
    }
}
