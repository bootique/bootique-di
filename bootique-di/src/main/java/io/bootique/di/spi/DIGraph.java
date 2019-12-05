/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.di.spi;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The implementation here is basically an adjacency list, but a {@link Map} is
 * used to map each vertex to its list of adjacent vertices.
 *
 * This object is not thread safe.
 *
 * @param <V> A type of a vertex.
 */
class DIGraph<V> {

    /**
     * {@link LinkedHashMap} is used for supporting insertion order.
     */
    private final Map<V, List<V>> neighbors = new LinkedHashMap<>();

    DIGraph() {
    }

    /**
     * Add a vertex to the graph. Nothing happens if vertex is already in graph.
     */
    void add(V vertex) {
        neighbors.putIfAbsent(vertex, new ArrayList<>());
    }

    /**
     * Add vertexes to the graph.
     */
    void addAll(Collection<V> vertexes) {
        for (V vertex : vertexes) {
            this.add(vertex);
        }
    }

    /**
     * Add an edge to the graph; if either vertex does not exist, it's added.
     * This implementation allows the creation of multi-edges and self-loops.
     */
    void add(V from, V to) {
        neighbors.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        this.add(to);
    }

    /**
     * Return (as a Map) the in-degree of each vertex.
     */
    private Map<V, Integer> inDegree() {
        Map<V, Integer> result = new LinkedHashMap<>();

        neighbors.forEach((from, neighbors) -> {
            neighbors.forEach(to -> result.compute(to, (k, old) -> {
                if(old == null) {
                    return 1;
                }
                return old + 1;
            }));
            result.putIfAbsent(from, 0);
        });

        return result;
    }

    /**
     * Return (as a List) the topological sort of the vertices. Throws an exception if cycles are detected.
     */
    List<V> topSort() {
        Map<V, Integer> degree = inDegree();
        Deque<V> zeroDegree = new ArrayDeque<>();
        LinkedList<V> result = new LinkedList<>();

        degree.forEach((k, v) -> {
            if(v == 0) {
                zeroDegree.push(k);
            }
        });

        while (!zeroDegree.isEmpty()) {
            V v = zeroDegree.pop();
            result.push(v);

            neighbors.get(v).forEach(neighbor ->
                degree.compute(neighbor, (k, oldValue) -> {
                    int newValue = --oldValue;
                    if(newValue == 0) {
                        zeroDegree.push(k);
                    }
                    return newValue;
                })
            );
        }

        // Check that we have used the entire graph (if not, there was a cycle)
        if (result.size() != neighbors.size()) {
            Set<V> remainingKeys = new HashSet<>(neighbors.keySet());
            remainingKeys.removeIf(result::contains);
            throw new IllegalStateException("Cycle detected in list for keys: " + remainingKeys);
        }

        return result;
    }

    /**
     * String representation of graph.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        for (Map.Entry<V, List<V>> entry : neighbors.entrySet()) {
            s.append("\n    ").append(entry.getKey()).append(" -> ").append(entry.getValue());
        }

        return s.toString();
    }

}