/*
MIT License

Copyright (c) 2022 Armin Reichert

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package de.amr.routeplanner.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Armin Reichert
 * 
 * @param V vertex class (subclass of {@link Vertex})
 */
public class Graph<V extends Vertex> {

	private final Map<String, V> vertexByID = new HashMap<>();

	public void addVertex(String id, V vertex) {
		if (vertex(id).isPresent()) {
			throw new IllegalArgumentException("Vertex with ID '%s' already exists.".formatted(id));
		}
		vertexByID.put(id, vertex);
	}

	public Optional<V> vertex(String id) {
		return Optional.ofNullable(vertexByID.get(id));
	}

	public boolean contains(V vertex) {
		return vertexByID.values().contains(vertex);
	}

	public Stream<V> vertices() {
		return vertexByID.values().stream();
	}

	public int numVertices() {
		return vertexByID.size();
	}

	public Stream<Edge> edges() {
		return vertices().flatMap(Vertex::outgoingEdges);
	}

	public int numEdges() {
		return (int) edges().count();
	}

	public void addEdge(V either, V other, float cost) {
		addDirectedEdge(either, other, cost);
		addDirectedEdge(other, either, cost);
	}

	public void addDirectedEdge(V from, V to, float cost) {
		if (edge(from, to).isPresent()) {
			throw new IllegalArgumentException("Duplicate edge (%s, %s)".formatted(from, to));
		}
		from.addOutgoingEdge(to, cost);
	}

	public Optional<Edge> edge(V from, V to) {
		return from.outgoingEdges().filter(e -> e.to().equals(to)).findAny();
	}

	/**
	 * Computes the shortest path from the given source vertex to all vertices of the graph.
	 * <p>
	 * TODO: Not sure if using "visited" and if removing vertices from queue are needed
	 * 
	 * @see https://cs.au.dk/~gerth/papers/fun22.pdf
	 * 
	 * @param source the source vertex
	 */
	public void computeShortestPathsFrom(Vertex source) {
		var q = new VertexPQ();
		vertices().forEach(v -> {
			v.setCost(Float.POSITIVE_INFINITY);
			v.setParent(null);
			v.setVisited(false);
		});
		source.setCost(0);
		q.insert(source);
		while (!q.isEmpty()) {
			var u = q.extractMin();
			if (!u.isVisited()) {
				u.setVisited(true);
				u.outgoingEdges().forEach(edge -> {
					var v = edge.to(); // edge = (u, v)
					var altCost = u.cost() + edge.cost(); // cost of path (source, ..., u, v)
					if (v.cost() > altCost) {
						traceNewPathFound(u, v, altCost);
						q.remove(v); // if vertex not in queue, does nothing
						v.setCost(altCost);
						v.setParent(u);
						q.insert(v);
					}
				});
			}
		}
	}

	protected void traceNewPathFound(Vertex u, Vertex v, float newCost) {
		// subclass may override
	}
}