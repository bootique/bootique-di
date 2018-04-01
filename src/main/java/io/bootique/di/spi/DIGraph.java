

package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;

import java.util.*;

/**
 * The implementation here is basically an adjacency list, but a {@link Map} is
 * used to map each vertex to its list of adjacent vertices.
 *
 * @param <V>
 *            A type of a vertex.
 */
class DIGraph<V> {

	/**
	 * {@link LinkedHashMap} is used for supporting insertion order.
	 */
	private Map<V, List<V>> neighbors = new LinkedHashMap<>();

	public DIGraph() {
	}

	/**
	 * Add a vertex to the graph. Nothing happens if vertex is already in graph.
	 */
	public void add(V vertex) {
		if (neighbors.containsKey(vertex)) {
			return;
		}

		neighbors.put(vertex, new ArrayList<V>());
	}

    /**
     * Add vertexes to the graph.
     */
    public void addAll(Collection<V> vertexes) {
        for (V vertex : vertexes) {
            this.add(vertex);
        }
    }

	/**
	 * Add an edge to the graph; if either vertex does not exist, it's added.
	 * This implementation allows the creation of multi-edges and self-loops.
	 */
	public void add(V from, V to) {
		this.add(from);
		this.add(to);
		neighbors.get(from).add(to);
	}

	/**
	 * True iff graph contains vertex.
	 */
	public boolean contains(V vertex) {
		return neighbors.containsKey(vertex);
	}

	/**
	 * Remove an edge from the graph. Nothing happens if no such edge.
	 *
	 * @throws IllegalArgumentException
	 *             if either vertex doesn't exist.
	 */
	public void remove(V from, V to) {
		if (!(this.contains(from) && this.contains(to))) {
			throw new IllegalArgumentException("Nonexistent vertex");
		}

		neighbors.get(from).remove(to);
	}

	/**
	 * Return (as a Map) the out-degree of each vertex.
	 */
	public Map<V, Integer> outDegree() {
		Map<V, Integer> result = new LinkedHashMap<>();

		for (Map.Entry<V, List<V>> entry : neighbors.entrySet()) {
			result.put(entry.getKey(), entry.getValue().size());
		}

		return result;
	}

	/**
	 * Return (as a Map) the in-degree of each vertex.
	 */
	public Map<V, Integer> inDegree() {
		Map<V, Integer> result = new LinkedHashMap<>();

		for (V v : neighbors.keySet()) {
			result.put(v, 0);
		}

		for (V from : neighbors.keySet()) {
			for (V to : neighbors.get(from)) {
				result.put(to, result.get(to) + 1);
			}
		}

		return result;
	}

	/**
	 * Return (as a List) the topological sort of the vertices. Throws an exception if cycles are detected.
	 */
	public List<V> topSort() {
		Map<V, Integer> degree = inDegree();
		Deque<V> zeroDegree = new ArrayDeque<>();
		LinkedList<V> result = new LinkedList<>();

		for (Map.Entry<V, Integer> entry : degree.entrySet()) {
			if (entry.getValue() == 0) {
				zeroDegree.push(entry.getKey());
			}
		}

		while (!zeroDegree.isEmpty()) {
			V v = zeroDegree.pop();
			result.push(v);

			for (V neighbor : neighbors.get(v)) {
				degree.put(neighbor, degree.get(neighbor) - 1);
				if (degree.get(neighbor) == 0) {
					zeroDegree.push(neighbor);
				}
			}
		}

		// Check that we have used the entire graph (if not, there was a cycle)
		if (result.size() != neighbors.size()) {
			throw new DIRuntimeException("Dependency cycle detected in DI container");
		}

		return result;
	}

	/**
	 * String representation of graph.
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();

		for (Map.Entry<V, List<V>> entry : neighbors.entrySet()) {
			s.append("\n    " + entry.getKey() + " -> " + entry.getValue());
		}

		return s.toString();
	}

	public int size() {
		return neighbors.size();
	}
}