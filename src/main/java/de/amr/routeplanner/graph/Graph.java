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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Armin Reichert
 * 
 * @param V vertex class (subclass of {@link Vertex})
 */
public class Graph<V extends Vertex> {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private final Set<V> vertexSet = new HashSet<>();

	public void addVertex(V vertex) {
		if (vertex(vertex.id()).isPresent()) {
			LOGGER.error(() -> "Vertex with ID '%s' already exists.".formatted(vertex.id()));
		} else {
			vertexSet.add(vertex);
		}
	}

	public Optional<V> vertex(String id) {
		return vertices().filter(v -> v.id().equals(id)).findAny();
	}

	public boolean contains(V vertex) {
		return vertexSet.contains(vertex);
	}

	public Stream<V> vertices() {
		return vertexSet.stream();
	}

	public Stream<Edge> edges() {
		return vertices().flatMap(Vertex::outgoingEdges);
	}

	public void addEdge(V either, V other, float cost) {
		addDirectedEdge(either, other, cost);
		addDirectedEdge(other, either, cost);
	}

	public void addDirectedEdge(V source, V target, float cost) {
		source.addOutgoingEdge(target, cost);
	}

	/**
	 * Computes the shortest path from the given source vertex to all vertices of the graph.
	 * <p>
	 * TODO: I am not sure if using "visited" is really needed
	 * 
	 * @see https://cs.au.dk/~gerth/papers/fun22.pdf
	 * 
	 * @param source the source vertex
	 */
	public void computeShortestPathsFrom(Vertex source) {
		vertices().forEach(v -> {
			v.setCost(Float.POSITIVE_INFINITY);
			v.setParent(null);
			v.setVisited(false);
		});
		var q = new MinVertexPQ();
		q.update(source, 0);
		while (!q.isEmpty()) {
			var u = q.extractMinCostVertex();
			if (!u.isVisited()) {
				u.setVisited(true);
				u.outgoingEdges().forEach(edge -> {
					var v = edge.to(); // edge = (u, v)
					var altCost = u.cost() + edge.cost(); // cost of path (source, ..., u, v)
					if (v.cost() > altCost) {
						traceNewPathFound(u, v, v.cost(), altCost);
						q.update(v, altCost);
						v.setParent(u);
					}
				});
			}
		}
	}

	private void traceNewPathFound(Vertex u, Vertex v, float oldCost, float newCost) {
		if (oldCost == Float.POSITIVE_INFINITY) {
			LOGGER.trace(() -> "Found path to %s (%.1f km) via %s".formatted(v, newCost, u));
		} else {
			LOGGER.trace(() -> "Found shorter path to %s (%.1f km instead of %.1f km) via %s instead via %s".formatted(v,
					newCost, oldCost, u, v.parent()));
		}
	}
}